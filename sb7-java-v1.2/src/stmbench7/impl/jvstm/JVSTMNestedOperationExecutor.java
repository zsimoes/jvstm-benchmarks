package stmbench7.impl.jvstm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import jvstm.ParallelTask;
import jvstm.Transaction;
import jvstm.TransactionalTask;
import stmbench7.BenchThread;
import stmbench7.OperationExecutor;
import stmbench7.Parameters;
import stmbench7.ThreadRandom;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

public class JVSTMNestedOperationExecutor implements OperationExecutor
{

	private static final ThreadLocal<Integer> lastLocalOperationTimestamp = new ThreadLocal<Integer>()
	{
		@Override
		protected Integer initialValue()
		{
			return 0;
		}
	};

	public static final ExecutorService nestedParPool = Executors.newCachedThreadPool(new ThreadFactory()
	{

		@Override
		public Thread newThread(Runnable r)
		{
			Thread t = new Thread(r);
			t.setDaemon(true);
			t.setName("NestedWorker" + t.getId());
			return t;
		}
	});

	private final Operation[] op;
	private final int size;
	private List<Integer> results;

	private boolean[] readOnly;
	private boolean topLevelReadOnly = true;
	private boolean[] idNull;
	private int lastOperationTimestamp = 0;
	private boolean wasReadOnly;

	public JVSTMNestedOperationExecutor(Operation[] op)
	{
		this.op = op;
		this.size = op.length;
		readOnly = new boolean[size];
		Arrays.fill(readOnly, Boolean.FALSE);
		idNull = new boolean[size];
		Arrays.fill(idNull, Boolean.FALSE);

		for (int i = 0; i < size; i++)
		{
			if (op[i].getOperationId() == null)
			{
				idNull[i] = true;
			}

			switch (op[i].getOperationId().getType())
			{
			case OPERATION_RO:
			case SHORT_TRAVERSAL_RO:
			case TRAVERSAL_RO:
				readOnly[i] = true;
				break;
			case OPERATION:
			case SHORT_TRAVERSAL:
			case TRAVERSAL:
			case STRUCTURAL_MODIFICATION:
				readOnly[i] = false;
				topLevelReadOnly = false;
				break;
			default:
				throw new RuntimeError("Unexpected operation type");
			}
		}
	}

	public Operation[] getOperations()
	{
		return op;
	}

	public List<Integer> getResults()
	{
		return results;
	}

	@Override
	public int execute() throws OperationFailedException
	{
		int result = 0;

		Transaction topLevelTx = null;
		long start = System.nanoTime();
		boolean conflictNoted = false;
		boolean txNoted = false;

		while (true)
		{
			try
			{
				boolean opSuccess = true;

				topLevelTx = Transaction.begin(topLevelReadOnly);

				if (!txNoted)
				{
					JVSTMStats.noteTransaction(topLevelReadOnly, BenchThread.ID.get());
					txNoted = true;
				}

				try
				{
					ThreadRandom.saveState();
					List<TransactionalTask<Integer>> callables = new ArrayList<TransactionalTask<Integer>>();
					for (int i = 0; i < size; i++)
					{
						if (idNull[i] == true)
							throw new RuntimeError("Operation ID is null");

						callables.add(new Worker(op[i], readOnly[i]));
					}

					try
					{
						long startTime = System.currentTimeMillis();
						results = topLevelTx.manageNestedParallelTxs(callables, nestedParPool);
						startTime = System.currentTimeMillis() - startTime;
						System.out.println("Round Run time: " + startTime);
					} catch (RuntimeException re)
					{
						if (re.getCause() instanceof OperationFailedException)
						{
							throw (OperationFailedException) re.getCause();
						}
					}

					return result;
				} catch (jvstm.CommitException ce)
				{
					opSuccess = false;
					throw ce;
				} finally
				{
					wasReadOnly = !topLevelTx.isWriteTransaction();
					if (opSuccess)
					{ // commit unless a CommitException occurred
						// during performOperation()
						Transaction.commit();
						if (Parameters.sequentialReplayEnabled)
						{
							lastLocalOperationTimestamp.set(lastLocalOperationTimestamp.get() + 1);
							lastOperationTimestamp = topLevelTx.getNumber();
						}
					}
				}

			} catch (jvstm.CommitException ce)
			{
				ThreadRandom.restoreState();
				if (topLevelReadOnly)
					throw new Error("Read-Only Transactions should never fail!");

				if (!conflictNoted)
				{
					JVSTMStats.noteConflict(BenchThread.ID.get());
					conflictNoted = true;
				}

				jvstm.Transaction.abort();
				JVSTMStats.noteRestart(BenchThread.ID.get());
			}
		}
	}

	public class Worker extends ParallelTask<Integer>
	{

		private final Operation nestedOp;
		private boolean readOnly;

		public Worker(Operation nestedOp, boolean readOnly)
		{
			this.nestedOp = nestedOp;
			this.readOnly = readOnly;
		}

		@Override
		public Integer execute() throws Throwable
		{
			boolean conflictNoted = false;
			boolean txNoted = false;
			
			try
			{
				if (!txNoted)
				{
					JVSTMStats.noteNestedTransaction(readOnly);
					txNoted = true;
				}

				try
				{
					return nestedOp.performOperation();
				} catch (OperationFailedException o)
				{
					System.err.println("Nested Fail");
					o.setSource(this.nestedOp.getOperationId());
					throw o;
				} 
				//ignore sequential replay for now. Later, copy code from JVSTMOperationExecutor.java
			} catch (jvstm.CommitException ce)
			{
				if (readOnly)
					throw new Error("Read-Only Transactions should never fail!");

				if (!conflictNoted)
				{
					JVSTMStats.noteConflict(BenchThread.ID.get());
					conflictNoted = true;
				}
				System.err.println("Nested Conflict");
				JVSTMStats.noteRestart(BenchThread.ID.get());
				throw ce;
			}
		}

		@Override
		protected boolean isReadOnly()
		{
			return readOnly;
		}

	}

	@Override
	public int getLastOperationTimestamp()
	{
		return lastOperationTimestamp;
	}
}

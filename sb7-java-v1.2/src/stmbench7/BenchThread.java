package stmbench7;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import stmbench7.annotations.NonAtomic;
import stmbench7.core.Operation;
import stmbench7.core.RuntimeError;
import stmbench7.core.OperationFailedException;
import stmbench7.impl.jvstm.JVSTMNestedOperationExecutor;

/**
 * A single thread of the STMBench7 benchmark. Executes operations assigned to
 * it one by one, randomly choosing the next operation and respecting the
 * expected ratios of operations' counts.
 */
@NonAtomic
public class BenchThread implements Runnable
{

	public static final ThreadLocal<Short> ID = new ThreadLocal<Short>();

	protected volatile boolean stop = false;
	protected double[] operationCDF;
	protected OperationExecutor[] operations;
	protected final short myThreadNum;

	public int[] successfulOperations, failedOperations;
	public int[][] operationsTTC, operationsHighTTCLog;

	public class ReplayLogEntry implements Comparable<ReplayLogEntry>
	{
		public final short threadNum;
		public final int timestamp, result;
		public final boolean failed;
		public final int opNum;

		public ReplayLogEntry(int timestamp, int result, boolean failed, int opNum)
		{
			this.threadNum = myThreadNum;
			this.timestamp = timestamp;
			this.result = result;
			this.failed = failed;
			this.opNum = opNum;
		}

		public int compareTo(ReplayLogEntry entry)
		{
			return timestamp - entry.timestamp;
		}
	}

	public ArrayList<ReplayLogEntry> replayLog;

	public BenchThread(Setup setup, double[] operationCDF, short myThreadNum)
	{
		this.operationCDF = operationCDF;

		int numOfOperations = OperationId.values().length;
		operationsTTC = new int[numOfOperations][Parameters.MAX_LOW_TTC + 1];
		operationsHighTTCLog = new int[numOfOperations][Parameters.HIGH_TTC_ENTRIES];
		successfulOperations = new int[numOfOperations];
		failedOperations = new int[numOfOperations];
		this.myThreadNum = myThreadNum;

		createOperations(setup);

		if (Parameters.sequentialReplayEnabled)
			replayLog = new ArrayList<ReplayLogEntry>();
	}

	protected BenchThread(Setup setup, double[] operationCDF)
	{
		this.operationCDF = operationCDF;
		operations = new OperationExecutor[OperationId.values().length];
		createOperations(setup);
		myThreadNum = 0;
	}

	public void run()
	{
		if (Parameters.nesting == true)
		{
			System.err.println("Running nested. Parameters: t" + Parameters.numTopLevel + "  n" + Parameters.numNested);
			runNested();
		} else
		{
			runLinear();
		}
	}

	protected void runNested()
	{
		ID.set(this.myThreadNum);
		while (!stop)
		{
			int operationNumber = getNextOperationNumber();
			OperationExecutor currentExecutor = nestedExecutors.get(operationNumber);

			// OperationType type =
			// OperationId.values()[operationNumber].getType();
			// if( (type != OperationType.SHORT_TRAVERSAL) ) continue;
			// (type != OperationType.SHORT_TRAVERSAL_RO) &&
			// (type != OperationType.OPERATION) )
			// continue;

			// System.out.println(i + " > "
			// + OperationId.values()[operationNumber]);

			int result = 0;
			boolean failed = false;

			try
			{
				long startTime = System.currentTimeMillis();

				// needed after executing nested ops: result list, opSuccess
				// list
				result = currentExecutor.execute();

				long endTime = System.currentTimeMillis();
				System.err.println("Nested Executor finished: " + (endTime - startTime));

				for (Operation op : ((JVSTMNestedOperationExecutor) currentExecutor).getOperations())
				{
					int operationID = op.getOperationId().ordinal();
					// CHANGE
					successfulOperations[operationID]++;
					int ttc = (int) (endTime - startTime);
					if (ttc <= Parameters.MAX_LOW_TTC)
						operationsTTC[operationID][ttc]++;
					else
					{
						double logHighTtc = (Math.log(ttc) - Math.log(Parameters.MAX_LOW_TTC + 1))
								/ Math.log(Parameters.HIGH_TTC_LOG_BASE);
						int intLogHighTtc = Math.min((int) logHighTtc, Parameters.HIGH_TTC_ENTRIES - 1);
						operationsHighTTCLog[operationID][intLogHighTtc]++;
					}
				}
			} catch (OperationFailedException e)
			{
				// System.out.println("failed");
				// CHANGE
				failedOperations[e.getSource().ordinal()]++;
				//System.err.println("NESTED OP FAILED: " + e.getSource().ordinal() + " (BenchThread.runNested() outer catch() block)");
				failed = true;
			}

			// CHANGE
			if (Parameters.sequentialReplayEnabled)
			{
				ReplayLogEntry newEntry = new ReplayLogEntry(currentExecutor.getLastOperationTimestamp(), result,
						failed, operationNumber);
				replayLog.add(newEntry);
				// System.out.println("ts: " + newEntry.timestamp);
			}
		}
		System.err.println("Thread #" + myThreadNum + " finished.");
		// i = 0;
		// for (ReplayLogEntry entry : replayLog)
		// System.out.println(i++ + " % " + OperationId.values()[entry.opNum]
		// + " -- " + entry.timestamp);
	}

	protected void runLinear()
	{
		ID.set(this.myThreadNum);
		while (!stop)
		{
			// if (i++ > 55) continue;
			int operationNumber = getNextOperationNumber();

			OperationType type = OperationId.values()[operationNumber].getType();
			// if( (type != OperationType.SHORT_TRAVERSAL) ) continue;
			// (type != OperationType.SHORT_TRAVERSAL_RO) &&
			// (type != OperationType.OPERATION) )
			// continue;

			// System.out.println(i + " > "
			// + OperationId.values()[operationNumber]);

			OperationExecutor currentExecutor = operations[operationNumber];
			int result = 0;
			boolean failed = false;

			try
			{
				long startTime = System.currentTimeMillis();

				result = currentExecutor.execute();

				long endTime = System.currentTimeMillis();
				System.err.println("Sequential Executor finished: " + (endTime - startTime));
				// System.out.println("success");

				successfulOperations[operationNumber]++;
				int ttc = (int) (endTime - startTime);
				if (ttc <= Parameters.MAX_LOW_TTC)
					operationsTTC[operationNumber][ttc]++;
				else
				{
					double logHighTtc = (Math.log(ttc) - Math.log(Parameters.MAX_LOW_TTC + 1))
							/ Math.log(Parameters.HIGH_TTC_LOG_BASE);
					int intLogHighTtc = Math.min((int) logHighTtc, Parameters.HIGH_TTC_ENTRIES - 1);
					operationsHighTTCLog[operationNumber][intLogHighTtc]++;
				}
			} catch (OperationFailedException e)
			{
				// System.out.println("failed");
				failedOperations[operationNumber]++;
				failed = true;
			}

			if (Parameters.sequentialReplayEnabled)
			{
				ReplayLogEntry newEntry = new ReplayLogEntry(currentExecutor.getLastOperationTimestamp(), result,
						failed, operationNumber);
				replayLog.add(newEntry);
				// System.out.println("ts: " + newEntry.timestamp);
			}
		}
		System.err.println("Thread #" + myThreadNum + " finished.");
		// i = 0;
		// for (ReplayLogEntry entry : replayLog)
		// System.out.println(i++ + " % " + OperationId.values()[entry.opNum]
		// + " -- " + entry.timestamp);
	}

	public void stopThread()
	{
		stop = true;
	}

	protected void createOperations(Setup setup)
	{
		if (Parameters.nesting)
		{
			createOperationsNested(setup);
		} else
		{
			createOperationsLinear(setup);
		}
	}

	protected void createOperationsLinear(Setup setup)
	{

		int numOfOperations = OperationId.values().length;
		operations = new OperationExecutor[numOfOperations];

		for (OperationId operationDescr : OperationId.values())
		{
			Class<? extends Operation> operationClass = operationDescr.getOperationClass();
			int operationIndex = operationDescr.ordinal();

			try
			{
				Constructor<? extends Operation> operationConstructor = operationClass.getConstructor(Setup.class);
				Operation operation = operationConstructor.newInstance(setup);

				operations[operationIndex] = OperationExecutorFactory.instance.createOperationExecutor(operation);
				assert (operation.getOperationId().getOperationClass().equals(operationClass));
			} catch (Exception e)
			{
				throw new RuntimeError("Error while creating operation " + operationDescr, e);
			}
		}
	}

	private Map<Integer, OperationExecutor> nestedExecutors = null;

	protected void createOperationsNested(Setup setup)
	{
		nestedExecutors = new HashMap<>();

		int numOfOperations = OperationId.values().length;
		int nesting = Parameters.numNested;
		int partition = numOfOperations / nesting;
		int rest = numOfOperations % nesting;

		boolean leftover = false;
		int leftoverStartIndex = 0;
		if (rest > 0)
		{
			leftover = true;
			leftoverStartIndex = numOfOperations - rest;
			leftoverStartIndex--;
		}
		operations = new OperationExecutor[nesting];

		// create well-defined partitions:
		int linearIndex = 0;
		for (int i = 0; i < partition * nesting; i += partition, linearIndex++)
		{
			List<Operation> partitionOps = new ArrayList<Operation>();

			// leftovers:
			OperationId operationDescr = null;
			if (leftover && leftoverStartIndex < numOfOperations)
			{
				operationDescr = OperationId.values()[leftoverStartIndex++];
				Operation op = instantiateOperation(setup, operationDescr);
				partitionOps.add(op);
			}

			// rest of the partition:
			for (int j = i; j < i + partition; j++)
			{
				try
				{
					// add each op to a given partition
					operationDescr = OperationId.values()[j];
					Operation op = instantiateOperation(setup, operationDescr);
					partitionOps.add(op);
				} catch (Exception e)
				{
					throw new RuntimeError("Error while creating operation " + operationDescr, e);
				}
			}

			// skip factory methods for now, because NestedOperationExecutors do
			// not implement the standard executor iterface. Refactor later.
			JVSTMNestedOperationExecutor exec = new JVSTMNestedOperationExecutor(
					partitionOps.toArray(new Operation[partitionOps.size()]));
			operations[linearIndex] = exec;

			for (Operation op : exec.getOperations())
			{
				nestedExecutors.put(op.getOperationId().ordinal(), exec);
			}
		}
	}

	public Operation instantiateOperation(Setup setup, OperationId opId)
	{
		Class<? extends Operation> operationClass = opId.getOperationClass();

		try
		{
			Constructor<? extends Operation> operationConstructor = operationClass.getConstructor(Setup.class);
			Operation operation = operationConstructor.newInstance(setup);
			assert (operation.getOperationId().getOperationClass().equals(operationClass));
			return operation;
		} catch (Exception e)
		{
			throw new RuntimeError("Error while creating operation " + opId, e);
		}
	}

	protected int getNextOperationNumber()
	{
		double whichOperation = ThreadRandom.nextDouble();
		int operationNumber = 0;
		while (whichOperation >= operationCDF[operationNumber])
			operationNumber++;
		return operationNumber;
	}
}

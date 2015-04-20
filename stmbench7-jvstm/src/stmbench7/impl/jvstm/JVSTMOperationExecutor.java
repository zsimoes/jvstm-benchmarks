package stmbench7.impl.jvstm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import jvstm.Transaction;
import jvstm.scheduler.SerializedOperationException;
import jvstm.scheduler.WorkDealing;
import jvstm.util.NestedWorkUnit;
import stmbench7.BenchThread;
import stmbench7.OperationExecutor;
import stmbench7.Parameters;
import stmbench7.ThreadRandom;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

public class JVSTMOperationExecutor implements OperationExecutor {

    private static final ThreadLocal<Integer> lastLocalOperationTimestamp = new ThreadLocal<Integer>() {
	@Override
	protected Integer initialValue() {
	    return 0;
	}
    };

    public static final ExecutorService nestedParPool = Executors.newCachedThreadPool(new ThreadFactory() {
	@Override
	public Thread newThread(Runnable r) {
	    Thread t = new Thread(r);
	    t.setDaemon(true);
	    return t;
	}
    });

    private final Operation op;

    private final ThreadLocal<WorkDealing> runner = new ThreadLocal<WorkDealing>();

    protected int operationNumber;

    private boolean readOnly;
    private boolean idNull = false;
    private int lastOperationTimestamp = 0;
    private boolean wasReadOnly;

    public JVSTMOperationExecutor(Operation op) {
	this.op = op;

	if (op.getOperationId() == null) {
	    idNull = true;
	    return;
	}

	this.operationNumber = op.getOperationId().ordinal();

	switch (op.getOperationId().getType()) {
	case OPERATION_RO:
	case SHORT_TRAVERSAL_RO:
	case TRAVERSAL_RO:
	    readOnly = true;
	    break;
	case OPERATION:
	case SHORT_TRAVERSAL:
	case TRAVERSAL:
	case STRUCTURAL_MODIFICATION:
	    readOnly = false;
	    break;
	default:
	    throw new RuntimeError("Unexpected operation type");
	}
    }

    @Override
    public int execute() throws OperationFailedException {
	int result;
	if (idNull == true)
	    result = op.performOperation();
	else
	    result = txExecute();
	this.runner.set(null);
	return result;
    }

    private int txExecute() throws OperationFailedException {
	boolean conflictNoted = false;
	boolean txNoted = false;
	Transaction tx = null;
	long start = System.nanoTime();
	while (true) {

	    try {
		boolean opSuccess = true; // default only changes when
					  // CommitException occurs eagerly

		if (Parameters.useSchd) {
		    try {
			tx = Transaction.begin(readOnly, operationNumber, this, this.runner.get());
		    } catch (SerializedOperationException soe) {
			return -42;
		    }
		} else {
		    tx = Transaction.begin(readOnly);
		}
		if (!txNoted) {
		    JVSTMStats.noteTransaction(readOnly, BenchThread.ID.get());
		    txNoted = true;
		}
		try {
		    ThreadRandom.saveState();
		    int result = 0;
		    if (Parameters.dummyNest) {
			List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
			callables.add(new Worker(1));
			try {
			    result = tx.manageNestedParallelTxs(callables, nestedParPool).get(0);
			} catch (RuntimeException re) {
			    if (re.getCause() instanceof OperationFailedException) {
				throw (OperationFailedException) re.getCause();
			    }
			}
		    } else {
			result = op.performOperation();
		    }
		    return result;
		} catch (jvstm.CommitException ce) {
		    opSuccess = false;
		    throw ce;
		} finally {
		    wasReadOnly = !tx.isWriteTransaction();
		    if (opSuccess) { // commit unless a CommitException occurred
				     // during performOperation()
			Transaction.commit();
			if (Parameters.sequentialReplayEnabled) {
			    lastLocalOperationTimestamp.set(lastLocalOperationTimestamp.get() + 1);
			    lastOperationTimestamp = tx.getNumber();
			}
		    }
		}
	    } catch (jvstm.CommitException ce) {
		ThreadRandom.restoreState();
		if (readOnly)
		    throw new Error("Read-Only Transactions should never fail!");

		if (!conflictNoted) {
		    JVSTMStats.noteConflict(BenchThread.ID.get());
		    conflictNoted = true;
		}

		if (Parameters.useSchd) {
		    jvstm.Transaction.abort(operationNumber, ce.getTxNumber());
		} else {
		    jvstm.Transaction.abort();
		}
		JVSTMStats.noteRestart(BenchThread.ID.get());
	    }
	}
    }

    public class Worker extends NestedWorkUnit<Integer> {

	private final int depth;

	public Worker(int depth) {
	    this.depth = depth;
	}

	@Override
	public Integer execute() throws Throwable {
	    if (depth == Parameters.fakeDepth) {
		return op.performOperation();
	    } else {
		List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
		callables.add(new Worker(depth + 1));
		return Transaction.current().manageNestedParallelTxs(callables, nestedParPool).get(0);
	    }
	}

	@Override
	protected boolean isReadOnly() {
	    return readOnly;
	}

    }

    @Override
    public int getLastOperationTimestamp() {
	return lastOperationTimestamp;
    }

    @Override
    public int getLastLocalOperationTimestamp() {
	return lastLocalOperationTimestamp.get();
    }

    @Override
    public boolean isOperationReadOnly() {
	return wasReadOnly;
    }

    @Override
    public int getOperationNumber() {
	return operationNumber;
    }

    @Override
    public Operation getOp() {
	return op;
    }

    @Override
    public void setRunner(WorkDealing runner) {
	this.runner.set(runner);
    }

    @Override
    public int getTaskId() {
	return operationNumber;
    }
}

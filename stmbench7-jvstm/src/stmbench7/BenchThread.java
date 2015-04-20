package stmbench7;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

import jvstm.scheduler.Scheduler;
import jvstm.scheduler.WorkDealing;
import stmbench7.annotations.NonAtomic;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

/**
 * A single thread of the STMBench7 benchmark. Executes operations assigned to
 * it one by one, randomly choosing the next operation and respecting the
 * expected ratios of operations' counts.
 */
@NonAtomic
public class BenchThread implements Runnable, WorkDealing {

    public static final ThreadLocal<Short> ID = new ThreadLocal<Short>();

    public int executedTxs = 0;
    public volatile boolean working = false;
    protected volatile boolean stop = false;
    protected double[] operationCDF;
    protected OperationExecutor[] operations;
    protected final short myThreadNum;

    public int[] successfulOperations, failedOperations;
    public int[][] operationsTTC, operationsHighTTCLog;

    protected ConcurrentLinkedQueue<Object> offeredWork = new ConcurrentLinkedQueue<Object>();

    public class ReplayLogEntry implements Comparable<ReplayLogEntry> {
	public final short threadNum;
	public final int timestamp, result;
	public final boolean failed;
	public final int opNum;
	public final boolean readOnly;
	public final int localTs;

	public ReplayLogEntry(int timestamp, int result, boolean failed, int opNum, boolean readOnly, int localTs) {
	    this.threadNum = myThreadNum;
	    this.timestamp = timestamp;
	    this.result = result;
	    this.failed = failed;
	    this.opNum = opNum;
	    this.readOnly = readOnly;
	    this.localTs = localTs;
	}

	@Override
	public int compareTo(ReplayLogEntry entry) {
	    int globalOrder = timestamp - entry.timestamp;
	    if (globalOrder != 0) {
		return globalOrder;
	    }

	    // If we get here, both ReplayLogEntries have the
	    // same timestamp, meaning that both correspond to
	    // the same version of the transactional system.

	    if (entry == this)
		return 0;

	    // If both entries were created by the same BenchThread,
	    // we use the execution order local to the thread to order them.
	    if (threadNum == entry.threadNum) {
		return localTs - entry.localTs;
	    }

	    // At least one of the entries is read-only
	    // and is ordered last.
	    return readOnly ? 1 : -1;
	}
    }

    public ArrayList<ReplayLogEntry> replayLog;

    public BenchThread(Setup setup, double[] operationCDF, short myThreadNum,
    /* schd: */OperationExecutor[] ops) {
	this.operationCDF = operationCDF;

	int numOfOperations = OperationId.values().length;
	operationsTTC = new int[numOfOperations][Parameters.MAX_LOW_TTC + 1];
	operationsHighTTCLog = new int[numOfOperations][Parameters.HIGH_TTC_ENTRIES];
	successfulOperations = new int[numOfOperations];
	failedOperations = new int[numOfOperations];
	operations = ops; /* schd: new OperationExecutor[numOfOperations]; */
	this.myThreadNum = myThreadNum;

	// schd: done outside, not here anymore! createOperations(setup);

	if (Parameters.sequentialReplayEnabled)
	    replayLog = new ArrayList<ReplayLogEntry>();
    }

    protected BenchThread(Setup setup, double[] operationCDF) {
	this.operationCDF = operationCDF;
	operations = new OperationExecutor[OperationId.values().length];
	createOperations(setup);
	myThreadNum = 0;
    }

    @Override
    public void run() {
	ID.set(this.myThreadNum);
	ThreadRandom.random.set(new RandomState(ThreadRandom.INITIAL_SEED));
	int i = 0;
	while (!stop) {
	    // if (i++ > 55) continue;

	    // OperationType type =
	    // OperationId.values()[operationNumber].getType();
	    // if( (type != OperationType.SHORT_TRAVERSAL) )
	    // continue;
	    // (type != OperationType.SHORT_TRAVERSAL_RO) &&
	    // (type != OperationType.OPERATION) )
	    // continue;

	    // System.out.println(i + " > "
	    // + OperationId.values()[operationNumber]);

	    int operationNumber;
	    OperationExecutor currentExecutor;
	    Object possibleWork = offeredWork.poll();
	    if (possibleWork != null) {
		currentExecutor = (OperationExecutor) possibleWork;
		operationNumber = currentExecutor.getOperationNumber();
	    } else {
		// operationNumber = getNextOperationNumber();
		// currentExecutor = operations[operationNumber];
		currentExecutor = Scheduler.getScheduler().<OperationExecutor> getNextScheduledTask();
		if (currentExecutor == null) {
		    // Benchmark.barrier.countDown();
		    return;
		}
		operationNumber = currentExecutor.getOperationNumber();
	    }

	    int result = 0;
	    boolean failed = false;

	    try {
		long startTime = System.currentTimeMillis();

		currentExecutor.setRunner(this);
		// working = true;
		result = currentExecutor.execute();
		// working = false;
		// if (result != -42) {
		// executedTxs++;
		// }

		long endTime = System.currentTimeMillis();
		// System.out.println("success");

		successfulOperations[operationNumber]++;
		int ttc = (int) (endTime - startTime);
		if (ttc <= Parameters.MAX_LOW_TTC)
		    operationsTTC[operationNumber][ttc]++;
		else {
		    double logHighTtc = (Math.log(ttc) - Math.log(Parameters.MAX_LOW_TTC + 1))
			    / Math.log(Parameters.HIGH_TTC_LOG_BASE);
		    int intLogHighTtc = Math.min((int) logHighTtc, Parameters.HIGH_TTC_ENTRIES - 1);
		    operationsHighTTCLog[operationNumber][intLogHighTtc]++;
		}
	    } catch (OperationFailedException e) {
		// System.out.println("failed");
		failedOperations[operationNumber]++;
		failed = true;
	    }

	    if (Parameters.sequentialReplayEnabled) {
		ReplayLogEntry newEntry = new ReplayLogEntry(currentExecutor.getLastOperationTimestamp(), result, failed,
			operationNumber, currentExecutor.isOperationReadOnly(), currentExecutor.getLastLocalOperationTimestamp());
		replayLog.add(newEntry);
		// System.out.println("ts: " +
		// newEntry.timestamp);
	    }

	}
	// i = 0;
	// for (ReplayLogEntry entry : replayLog)
	// System.out.println(i++ + " % " +
	// OperationId.values()[entry.opNum]
	// + " -- " + entry.timestamp);
    }

    protected void createOperations(Setup setup) {
	int i = 0;
	for (OperationId operationDescr : OperationId.values()) {
	    Class<? extends Operation> operationClass;
	    int operationIndex;

	    if (!Parameters.limitOps) {
		operationClass = operationDescr.getOperationClass();
		operationIndex = operationDescr.ordinal();
	    } else {
		if (Parameters.opsUsed.equals("Ta")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T2a;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("Tb")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T2b;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("Tc")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T2c;
		    operationClass = operationDescr.getOperationClass();
		} else {
		    if (i == 0) {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T2a;
			operationClass = operationDescr.getOperationClass();
			i = 1;
		    } else if (i == 1) {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T2b;
			operationClass = operationDescr.getOperationClass();
			i = 2;
		    } else {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T2c;
			operationClass = operationDescr.getOperationClass();
			i = 0;
		    }
		}
	    }

	    try {
		Constructor<? extends Operation> operationConstructor = operationClass.getConstructor(Setup.class);
		Operation operation = operationConstructor.newInstance(setup);

		operations[operationIndex] = OperationExecutorFactory.instance.createOperationExecutor(operation);
		assert (operation.getOperationId().getOperationClass().equals(operationClass));
	    } catch (Exception e) {
		throw new RuntimeError("Error while creating operation " + operationDescr, e);
	    }
	}
	// Benchmark.barrier.countDown();
    }

    public void stopThread() {
	stop = true;
    }

    // schd: unused for now
    protected int getNextOperationNumber() {
	double whichOperation = ThreadRandom.nextDouble();
	int operationNumber = 0;
	while (whichOperation >= operationCDF[operationNumber])
	    operationNumber++;
	return operationNumber;
    }

    @Override
    public void offerWork(Object work) {
	offeredWork.add(work);
    }
}

package stmbench7.scheduler;

import jvstm.CommitException;
import stmbench7.OperationExecutor;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.ThreadRandom;

/**
 * Manages queue of operations to be executed using a scheduling policy.
 * 
 * @author david
 * 
 */
public class OperationScheduler implements Scheduler {

    // Don't forget that the following are not thread-safe

    // Set of currently computing operations
    private final DoublyLinkedList<OperationExecutor> _computingOperations = new DoublyLinkedList<OperationExecutor>();

    // Queue of operations awaiting execution
    private DoublyLinkedList<OperationExecutor> _queuedOperations = new DoublyLinkedList<OperationExecutor>();

    private DoublyLinkedList<OperationExecutor> _copyOperations = null;

    // Scheduling policy
    private SchedulingPolicy _policy;

    public void setSchedulingPolicy(SchedulingPolicy policy) {
	_policy = policy;
    }

    public SchedulingPolicy getSchedulingPolicy() {
	return _policy;
    }

    // Structures for operation generation
    private double[] _operationCDF;
    private OperationExecutor[] _operations;

    // Singleton Pattern
    private static OperationScheduler _scheduler = new OperationScheduler();

    private OperationScheduler() {
	_policy = new SchedulingPolicy();
    }

    public static OperationScheduler getScheduler() {
	return _scheduler;
    }
    
    public static void remakeScheduler() {
    	_scheduler = new OperationScheduler();
    	_profiler = new Profiler();
    }

    // Profiler - collects statistics on executed operations
    private static Profiler _profiler = new Profiler();

    public static Profiler getProfiler() {
	return _profiler;
    }

    /**
     * Threads that wish to perform work should check in here
     * 
     * @return The operation id of the operation to execute
     */
    @Override
    public OperationExecutor getNextOperation() {

	OperationExecutor next = _policy.decideNext(_queuedOperations, _computingOperations);
	if (next == null)
	    return null;
	// Allow lock free implementation in policy

	synchronized (_computingOperations) {
	    _computingOperations.add(next);
	    return next;
    }
	}

    /**
     * This method should be called when an operation finishes computing.
     */
    @Override
    public void finishComputation(OperationExecutor operation) {

	synchronized (_computingOperations) {

	    // Assertions
	    // int n1 = _computingOperations.size() + _queuedOperations.size();

	    _computingOperations.remove(operation);

	    // Assertions
	    // int n2 = _computingOperations.size() + _queuedOperations.size();
	    // assert n2 == n1 - 1 : "Mismatch " + n1 + " " + n2;
	}
    }

    @Override
    public void replenishOperations(int number) {
	synchronized (_queuedOperations) {
	    if (_copyOperations != null) {
		_queuedOperations = new DoublyLinkedList<OperationExecutor>(_copyOperations);
		return;
	    }
	    // Generate next n operations
	    for (int i = 0; i < number; i++) {

		// See getNextOperationNumber in BenchThread
		double whichOperation = ThreadRandom.nextDouble();
		int operationNumber = 0;
		while (whichOperation >= _operationCDF[operationNumber])
		    operationNumber++;

		OperationExecutor executor = _operations[operationNumber];
		_queuedOperations.add(executor);
		_profiler.addSubmittedOperation(executor.getOp().getOperationId());

	    }

	    _copyOperations = new DoublyLinkedList<OperationExecutor>(_queuedOperations);
	}
    }

    // See BenchThread constructor: we need these structures to generate next
    // tasks
    @Override
    public void setupStructures(double[] operationCDF, OperationExecutor[] ops) {
	_operationCDF = operationCDF;
	_operations = ops;
    }

    /**
     * For debugging purposes Note: No longer thread-safe
     */
    @Deprecated
    public void printWorkingOperations() {
	System.out.println("Computing: " + _computingOperations);
	System.out.println("Queued: " + _queuedOperations);
    }

    @Override
    public void conflictTx(OperationId abortingTx, CommitException ce) {
	OperationId conflicter = OperationScheduler.getProfiler().getRegisteredOperation(2);
	if (conflicter != null)
	    OperationScheduler.getProfiler().addConflict(abortingTx, conflicter);
	OperationScheduler.getProfiler().addAbort(abortingTx);
    }

    @Override
    public void commitTx(int commitNumber, OperationId committingTx) {
	OperationScheduler.getProfiler().addCommit(committingTx);
	OperationScheduler.getProfiler().registerOperation(commitNumber, committingTx);
    }

}

package stmbench7.scheduler;

import java.util.AbstractList;

import stmbench7.OperationExecutor;


/**
 * Interface for scheduling policies.
 * 
 * @author david
 *
 */
public class SchedulingPolicy {

	/**
	 * Decides what operation to execute next, based on the set of currently running
	 * operations and queued operations. Should alter queuedOperations destructively.
	 * Should return null if queue is empty.
	 * 
	 * @param queuedOperations
	 * @param runningOperations
	 * @return Operation number of next operation to execute.
	 */
	public OperationExecutor decideNext( DoublyLinkedList<OperationExecutor> queuedOperations, AbstractList<OperationExecutor> runningOperations )
	{
		// Default behavior: LIFO
		OperationExecutor next;
synchronized( queuedOperations ) {
		try {
			next = queuedOperations.remove(queuedOperations.size()-1);
		} catch( IndexOutOfBoundsException ioe ) {
			next = null;
		}
}
		//printDecision( next );
		return next;
	}


	// For debugging purposes (deprecated because it's no longer thread-safe)
	@Deprecated
	public void printDecision( OperationExecutor operation ) {
		OperationScheduler.getScheduler().printWorkingOperations();
		System.out.println( "Going to put " + operation + " into execution." );
		System.out.println();
	}

}

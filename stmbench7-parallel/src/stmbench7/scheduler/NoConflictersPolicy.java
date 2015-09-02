
package stmbench7.scheduler;

import java.util.AbstractList;
import java.util.Set;

import stmbench7.OperationExecutor;
import stmbench7.OperationId;


/**
 * Simple Scheduling Policy which doesn't allow execution of operations
 * which previously incurred on conflicts until the context allows
 * Note: As of now this policy doesn't work at all!
 * 
 * @author david
 *
 */
public class NoConflictersPolicy extends SchedulingPolicy {

	@Override
	public OperationExecutor decideNext( DoublyLinkedList<OperationExecutor> queuedOperations, AbstractList<OperationExecutor> runningOperations )
	{
		Set<OperationId> conflicts;
		boolean opposition;


		while( true ) {

synchronized( queuedOperations ) {
			int i, size = queuedOperations.size();
			if( size == 0 ) return null;
			for( i = 0; i < size; i++ ) {
				OperationExecutor opEx = queuedOperations.get(i);
				
				opposition = false;
				// Does the operation have no conflicts, or no potential enemies in the pool?
				if( (conflicts = OperationScheduler.getProfiler().conflicts(opEx.getOp().getOperationId())).isEmpty() ) {
					queuedOperations.remove(i);
					return opEx;
				} else {
					// Check for enemies
					// Loop order?
				synchronized( runningOperations ) {
					for( OperationExecutor enemy: runningOperations ) {
						if( conflicts.contains(enemy.getOp().getOperationId())) {
							opposition = true;
							break;
						}	
					}
				}
					// If no enemies are found, we're good to go
					if( opposition == false ) {
						queuedOperations.remove(i);
						return opEx;
					}
					else continue;
				}
			}
}

			// If none available, wait
			// Note that we already own this monitor
			Thread.yield();
		}
	}
}


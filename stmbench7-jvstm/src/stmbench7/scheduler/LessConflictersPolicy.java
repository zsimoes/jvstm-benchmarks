
package stmbench7.scheduler;

import java.util.AbstractList;
import java.util.Set;

import stmbench7.OperationExecutor;
import stmbench7.OperationId;


/**
 * Simple Scheduling Policy which doesn't allow execution of operations
 * which previously incurred on conflicts until the context allows
 * 
 * @author david
 *
 */
public class LessConflictersPolicy extends SchedulingPolicy {

	
	@Override
	public OperationExecutor decideNext( DoublyLinkedList<OperationExecutor> queuedOperations, AbstractList<OperationExecutor> runningOperations )
	{
		Set<OperationId> conflicts;
		int numConflicts, minConflicts = Integer.MAX_VALUE;
		int minConflicterIndex = 0;
		OperationExecutor opEx;

synchronized( queuedOperations ) {
		int i, size = queuedOperations.size();
		if( size == 0 ) {
			return null;
		}
		for( i = 0; i < size; i++ ) {

			opEx = queuedOperations.get(i);
			conflicts = OperationScheduler.getProfiler().conflicts(opEx.getOp().getOperationId());

			numConflicts = 0;
		synchronized( runningOperations ) {
			for( OperationExecutor enemy: runningOperations )
				if( conflicts.contains(enemy.getOp().getOperationId()))
					numConflicts++;
		}
			
			if( numConflicts == 0 ) {
				queuedOperations.remove(i);
				return opEx;
			}
			else if( numConflicts < minConflicts ) {
				minConflicterIndex = i;
				minConflicts = numConflicts;
			}
		}

		opEx = queuedOperations.remove(minConflicterIndex);
		return opEx;
		
}
	}

}


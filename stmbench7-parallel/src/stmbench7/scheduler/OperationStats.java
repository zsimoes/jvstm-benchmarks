package stmbench7.scheduler;


import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import stmbench7.OperationId;

/**
 * Represents collectable statistics for operations
 * 
 * @author david
 *
 */
// We could probably relax synchronization constrains in order to obtain faster performance,
// as this is just statistical data
public class OperationStats {

	private AtomicInteger _executions = new AtomicInteger(0);
	private AtomicInteger _commits = new AtomicInteger(0);
	private AtomicInteger _aborts = new AtomicInteger(0);

	// TODO: Average execution time?

	private ConcurrentHashMap<OperationId,AtomicInteger> _conflicts =
		new ConcurrentHashMap<OperationId,AtomicInteger>();

	/**
	 * Statistics reader methods
	 */

	public int getExecutions() {
		return _executions.get();
	}

	public int getCommits() {
		return _commits.get();
	}

	public int getAborts() {
		return _aborts.get();
	}

	// This method returns both the conflicting operations and the number of times
	// they conflicted
	public Set<Map.Entry<OperationId,AtomicInteger>> getConflicts() {
		return _conflicts.entrySet();
	}

	public Set<OperationId> getConflictingTasks() {
		return _conflicts.keySet();
	}

	/**
	 * Statistics update methods
	 */

	public void addCommit() {
		_commits.incrementAndGet();
		_executions.incrementAndGet();
	}

	public void addAbort() {
		_aborts.incrementAndGet();
		_executions.incrementAndGet();
	}

	public void addConflict( OperationId conflictingOperation ) {
		AtomicInteger num = _conflicts.get(conflictingOperation);
		if( num != null ) {
			// Increment conflict count
			num.incrementAndGet();
		} else {
			num = _conflicts.putIfAbsent( conflictingOperation, new AtomicInteger(1) );
			if( num != null ) num.incrementAndGet();
		}
	}

	/**
	 * Statistics printing
	 */

	public void printStatistics() {
		System.out.println( "* Operation was executed " + _executions.get() + " times, of which " +
				_commits.get() + " were commits and " + _aborts.get() + " were aborts." );
		if( !this._conflicts.entrySet().isEmpty() ) {
			int conflicts = 0;
			System.out.println( "* Conflict list:" );
			for( Map.Entry<OperationId, AtomicInteger> me: getConflicts() ) {
				conflicts += me.getValue().get();
				System.out.println( '\t' + me.getKey().toString() + ": " + me.getValue().get() + " times" );
			}
			System.out.println( "Total number of conflicts: " + conflicts );
			System.out.println( "* End list" );
		}
	}

}



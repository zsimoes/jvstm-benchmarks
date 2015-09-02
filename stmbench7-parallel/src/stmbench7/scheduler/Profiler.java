package stmbench7.scheduler;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import stmbench7.OperationId;

//import jvstm.Transaction;

/**
 * This class collects runtime statistics on operations, such as
 * which operations got aborted, how many times, with what other
 * operations did they conflict, etc.
 * TODO: Make serializable
 * 
 * @author david
 *
 */
public class Profiler {

	private ConcurrentHashMap<OperationId,OperationStats> _stats =
		new ConcurrentHashMap<OperationId,OperationStats>();
	private int _submittedTasks = 0;
	
	// Translates transaction numbers (reported by CommitExceptions) to Operations
	private ConcurrentHashMap<Integer,OperationId> _speculatedTasks =
		new ConcurrentHashMap<Integer,OperationId>(); 

	public Profiler() {
		// Nothing special
	}
	
	// TODO: Unregister for garbage collection
	public void registerOperation( Integer txNum, OperationId operation ) {
		_speculatedTasks.put(txNum, operation);
	}
	
	public OperationId getRegisteredOperation( Integer txNum ) {
		return _speculatedTasks.get(txNum);
	}

	public void addSubmittedOperation( OperationId operation ) {
		// This can be a simple integer as this is always called from within a lock
		// If this changes, this should be changed to AtomicInteger (eventhough it
		// is just for statistics)
		_submittedTasks++;
	}

	public void addAbort( OperationId operation ) {
		OperationStats ets = _stats.get(operation);
		if( ets != null ) {
			ets.addAbort();
		} else {
			OperationStats newEts = new OperationStats();
			newEts.addAbort();
			ets = _stats.putIfAbsent(operation, newEts);
			if( ets != null ) {
				ets.addAbort();
			}
		}
	}

	public void addConflict(OperationId operation, OperationId conflicter ) {
		OperationStats ets = _stats.get(operation);
		if( ets != null ) {
			ets.addConflict(conflicter);
		} else {
			OperationStats newEts = new OperationStats();
			newEts.addConflict(conflicter);
			ets = _stats.putIfAbsent(operation, newEts);
			if( ets != null ) {
				ets.addConflict(conflicter);
			}
		}
	}

	public void addCommit(OperationId operation) {
		OperationStats ets = _stats.get(operation);
		if( ets != null ) {
			ets.addCommit();
		} else {
			OperationStats newEts = new OperationStats();
			newEts.addCommit();
			ets = _stats.putIfAbsent(operation, newEts);
			if( ets != null ) ets.addCommit();
		}
	}

	public Set<OperationId> conflicts( OperationId operation ) {
		OperationStats ets = _stats.get(operation);
		if( ets == null ) return new HashSet<OperationId>(); // Could return null here
		else return ets.getConflictingTasks();
	}
	
	public void printStatistics() {
		System.out.println( "Application run profiling:" );
		System.out.println( "* Submitted operations: " + _submittedTasks );
		for( Map.Entry<OperationId,OperationStats> me: _stats.entrySet() ) {
			System.out.println( me.getKey() + ":" );
			me.getValue().printStatistics();
		}
		// Print Transaction to operation map
		//for( Map.Entry<Transaction,OperationId> me: _speculatedTasks.entrySet() ) {
		//	System.out.println( "Transaction " + me.getKey() + ": " + me.getValue() );
		//}
	}

}

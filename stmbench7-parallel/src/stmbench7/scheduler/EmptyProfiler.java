package stmbench7.scheduler;

import java.util.HashSet;
import java.util.Set;

import stmbench7.OperationId;


/**
 * Empty profiler for obtaining data on the overhead of actually
 * having a running profiler.
 * 
 * @author david
 *
 */
public class EmptyProfiler extends Profiler {
	
	public EmptyProfiler() {}

	@Override
	public void addSubmittedOperation( OperationId operation ) {}

	@Override
	public void addAbort( OperationId operation ) {}

	@Override
	public void addConflict(OperationId operation, OperationId conflicter ) {}

	@Override
	public void addCommit(OperationId operation) {}

	@Override
	public Set<OperationId> conflicts( OperationId operation ) {
		return new HashSet<OperationId>();
	}
	
	@Override
	public void printStatistics() {
		System.out.println( "No profiling data." );
	}

}

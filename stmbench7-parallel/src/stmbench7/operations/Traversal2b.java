package stmbench7.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import jvstm.ParallelTask;
import jvstm.Transaction;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;
import stmbench7.core.Assembly;
import stmbench7.core.AtomicPart;
import stmbench7.core.BaseAssembly;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.CompositePart;
import stmbench7.impl.jvstm.JVSTMOperationExecutor;
import stmbench7.operations.Traversal1.WorkerAssembly;

/**
 * Traversal T2, variant (b) (see the specification).
 * Simple update, long.
 */
public class Traversal2b extends Traversal2a {

	public Traversal2b(Setup oo7setup) {
		super(oo7setup);
	}
    
	protected int traverse(ComplexAssembly complexAssembly, int depth) {
	    int result = 0;
	    for(Assembly assembly : complexAssembly.getSubAssemblies())
		result += traverse(assembly, depth+1);
	    return result;
	}
	
	protected int traverse(BaseAssembly baseAssembly) {
	    int partsVisited = 0;

	    if (!Parameters.parNest) {
		for (CompositePart component : baseAssembly.getComponents()) {
		    partsVisited += traverse(component);
		}
	    } else {
	    	ArrayList<ParallelTask<Integer>> callables = new ArrayList<ParallelTask<Integer>>();
		    int currentActiveSubThreads =0;
			for (CompositePart component : baseAssembly.getComponents()) {
			    if (component.getId() == 374) {
				continue;
			    }
			    callables.add((ParallelTask<Integer>)new Worker(component));
			    currentActiveSubThreads++;
			    if(currentActiveSubThreads == Parameters.numSubThreads) {
				    for (Integer res : Transaction.current().manageNestedParallelTxs(callables)) {
				    	partsVisited += res;
				    }
				    currentActiveSubThreads=0;
				    callables.clear();
				}
			}
	    }
	    return partsVisited;
	}
	
	@Override
	protected int performOperationInAtomicPart(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
		part.swapXY();
		return 1;
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.T2b;
    }
}
	

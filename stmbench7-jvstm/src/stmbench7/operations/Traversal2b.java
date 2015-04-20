package stmbench7.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import jvstm.Transaction;
import jvstm.util.NestedWorkUnit;
import jvstm.util.UnsafeTask;

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
	    public class Unsafe extends UnsafeTask<Integer> {

		private final CompositePart part;

		public Unsafe(CompositePart part) {
		    this.part = part;
		}

		@Override
		public Integer execute() throws Throwable {
		    return traverse(part);
		}
		
	    }
	protected int traverse(BaseAssembly baseAssembly) {
	    int partsVisited = 0;

	    if (!Parameters.parNest) {
		for (CompositePart component : baseAssembly.getComponents()) {
		    partsVisited += traverse(component);
		}
	    } else {
		List<Callable<Integer>> callables = new ArrayList<Callable<Integer>>();
		for (CompositePart component : baseAssembly.getComponents()) {
		    if (component.getId() == 374) {
			continue;
		    }
		    callables.add(new Worker(component));
		}
		for (Integer res : Transaction.current().manageNestedParallelTxs(callables, JVSTMOperationExecutor.nestedParPool)) {
		    partsVisited += res;
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
	

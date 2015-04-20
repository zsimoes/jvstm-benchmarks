package stmbench7.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jvstm.Transaction;
import jvstm.util.NestedWorkUnit;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;
import stmbench7.annotations.Transactional;
import stmbench7.annotations.Update;
import stmbench7.core.Assembly;
import stmbench7.core.AtomicPart;
import stmbench7.core.ComplexAssembly;
import stmbench7.impl.jvstm.JVSTMOperationExecutor;

/**
 * Traversal T2, variant (a) (see the specification). Simple update, long.
 */
public class Traversal2a extends Traversal1 {

    public Traversal2a(Setup oo7setup) {
	super(oo7setup);
    }

    @Override
    protected int traverse(ComplexAssembly complexAssembly, int depth) {
	int result = 0;
	if (depth == 6 && Parameters.parNest) {
	    List<NestedWorkUnit<Integer>> callables = new ArrayList<NestedWorkUnit<Integer>>();
	    for (Assembly assembly : complexAssembly.getSubAssemblies()) {
		callables.add(new WorkerAssembly(assembly, depth + 1));
	    }
	    for (Integer res : Transaction.current().manageNestedParallelTxs(callables, JVSTMOperationExecutor.nestedParPool)) {
		result += res;
	    }
	} else {
	    for (Assembly assembly : complexAssembly.getSubAssemblies())
		result += traverse(assembly, depth + 1);
	}
	return result;
    }

    @Override
    @Transactional
    @Update
    public int performOperation() {
	return super.performOperation();
    }

    @Override
    protected int performOperationInAtomicPart(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
	if (setOfVisitedPartIds.isEmpty()) {
	    part.swapXY();
	    return 1;
	}

	part.nullOperation();
	return 0;
    }

    @Override
    public OperationId getOperationId() {
	return OperationId.T2a;
    }
}

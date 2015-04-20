package stmbench7.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Callable;

import jvstm.Transaction;
import jvstm.util.NestedWorkUnit;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;
import stmbench7.core.Assembly;
import stmbench7.core.AtomicPart;
import stmbench7.core.BaseAssembly;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.CompositePart;
import stmbench7.core.Connection;
import stmbench7.impl.jvstm.JVSTMOperationExecutor;

/**
 * Traversal T3, variant (c) (see the specification). Simple update, update on
 * index, long.
 */
public class Traversal3c extends Traversal3a {

    public Traversal3c(Setup oo7setup) {
	super(oo7setup);
    }

    @Override
    protected int traverse(ComplexAssembly complexAssembly, int depth) {
	int result = 0;
	if (depth == 3 && Parameters.parNest) {
	    List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
	    callables.add(new Worker(complexAssembly, 1000, 1125));
	    callables.add(new Worker(complexAssembly, 1250, 1375));
	    callables.add(new Worker(complexAssembly, 1500, 1625));
	    callables.add(new Worker(complexAssembly, 1750, 1875));
	    Transaction.current().manageNestedParallelTxs(callables, JVSTMOperationExecutor.nestedParPool);

	    callables.clear();
	    callables.add(new Worker(complexAssembly, 1125, 1250));
	    callables.add(new Worker(complexAssembly, 1375, 1500));
	    callables.add(new Worker(complexAssembly, 1625, 1750));
	    callables.add(new Worker(complexAssembly, 1875, 2000));
	    Transaction.current().manageNestedParallelTxs(callables, JVSTMOperationExecutor.nestedParPool);
	} else {
	    for (Assembly assembly : complexAssembly.getSubAssemblies())
		result += traverse(assembly, depth + 1);
	}
	return result;
    }

    public class Worker extends NestedWorkUnit<Void> {

	private final int min;
	private final int max;
	private final ComplexAssembly complexAssembly;

	public Worker(ComplexAssembly complexAssembly, int min, int max) {
	    this.min = min;
	    this.max = max;
	    this.complexAssembly = complexAssembly;
	}

	@Override
	public Void execute() throws Throwable {
	    for (Assembly assembly3 : complexAssembly.getSubAssemblies()) {
		for (Assembly assembly4 : ((ComplexAssembly) assembly3).getSubAssemblies()) {
		    for (Assembly assembly : ((ComplexAssembly) assembly4).getSubAssemblies()) {
			for (Assembly bAssembly : ((ComplexAssembly) assembly).getSubAssemblies()) {
			    for (CompositePart part : ((BaseAssembly) bAssembly).getComponents()) {
				AtomicPart rootPart = part.getRootPart();
				HashSet<AtomicPart> setOfVisitedPartIds = new HashSet<AtomicPart>();
				traverse(rootPart, setOfVisitedPartIds);
			    }
			}
		    }
		}
	    }
	    return null;
	}

	protected int traverse(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
	    if (part == null)
		return 0;
	    if (setOfVisitedPartIds.contains(part))
		return 0;

	    int result = 0;
	    setOfVisitedPartIds.add(part);
	    int date = part.getFinalBuildDate();
	    if (date >= min && date < max) {
		result = performOperationInAtomicPart(part, setOfVisitedPartIds);
	    }

	    for (Connection connection : part.getToConnections())
		result += traverse(connection.getDestination(), setOfVisitedPartIds);

	    return result;
	}

    }

    // @Override
    // protected int traverse(BaseAssembly baseAssembly) {
    // List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
    // callables.add(new Worker(baseAssembly, 1000, 1250));
    // callables.add(new Worker(baseAssembly, 1250, 1500));
    // callables.add(new Worker(baseAssembly, 1500, 1750));
    // callables.add(new Worker(baseAssembly, 1750, 2000));
    // Transaction.current().manageNestedParallelTxs(callables);
    //
    // return 0;
    // }

    // @Override
    // protected int traverse(CompositePart component) {
    // List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
    // callables.add(new Worker(component, 1000, 1250));
    // callables.add(new Worker(component, 1250, 1500));
    // callables.add(new Worker(component, 1500, 1750));
    // callables.add(new Worker(component, 1750, 2000));
    // Transaction.current().manageNestedParallelTxs(callables);
    //
    // return 0;
    // }

    @Override
    protected int performOperationInAtomicPart(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
	updateBuildDate(part);
	updateBuildDate(part);
	updateBuildDate(part);
	updateBuildDate(part);
	return 4;
    }

    @Override
    public OperationId getOperationId() {
	return OperationId.T3c;
    }
}

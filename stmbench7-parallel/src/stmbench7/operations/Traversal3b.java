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
import stmbench7.core.Connection;
import stmbench7.impl.jvstm.JVSTMOperationExecutor;

/**
 * Traversal T3, variant (b) (see the specification). Simple update, update on
 * index, long.
 */
public class Traversal3b extends Traversal3a {

    public Traversal3b(Setup oo7setup) {
	super(oo7setup);
    }

    @Override
    protected int traverse(ComplexAssembly complexAssembly, int depth) {
	int result = 0;
	if (depth == 4 && Parameters.parNest) {
	    List<ParallelTask<Void>> callables = new ArrayList<ParallelTask<Void>>();
	    int currentActiveSubThreads = 0;
	    callables.add(new Worker(complexAssembly, 1000, 1125));
	    currentActiveSubThreads++;
	    if(currentActiveSubThreads == Parameters.numSubThreads) {
	    	Transaction.current().manageNestedParallelTxs(callables);
		    currentActiveSubThreads=0;
		    callables.clear();
	    }
	    callables.add(new Worker(complexAssembly, 1250, 1375));
	    currentActiveSubThreads++;
	    if(currentActiveSubThreads == Parameters.numSubThreads) {
	    	Transaction.current().manageNestedParallelTxs(callables);
		    currentActiveSubThreads=0;
		    callables.clear();
	    }
	    callables.add(new Worker(complexAssembly, 1500, 1625));
	    currentActiveSubThreads++;
	    if(currentActiveSubThreads == Parameters.numSubThreads) {
	    	Transaction.current().manageNestedParallelTxs(callables);
		    currentActiveSubThreads=0;
		    callables.clear();
	    }
	    callables.add(new Worker(complexAssembly, 1750, 1875));
	    currentActiveSubThreads++;
	    Transaction.current().manageNestedParallelTxs(callables);
		currentActiveSubThreads=0;
	    callables.clear();
	    
	    
	    
	    
	    callables.add(new Worker(complexAssembly, 1125, 1250));
	    currentActiveSubThreads++;
	    if(currentActiveSubThreads == Parameters.numSubThreads) {
	    	Transaction.current().manageNestedParallelTxs(callables);
		    currentActiveSubThreads=0;
		    callables.clear();
	    }
	    callables.add(new Worker(complexAssembly, 1375, 1500));	    
	    currentActiveSubThreads++;
	    if(currentActiveSubThreads == Parameters.numSubThreads) {
	    	Transaction.current().manageNestedParallelTxs(callables);
		    currentActiveSubThreads=0;
		    callables.clear();
	    }
	    callables.add(new Worker(complexAssembly, 1625, 1750));	    
	    currentActiveSubThreads++;
	    if(currentActiveSubThreads == Parameters.numSubThreads) {
	    	Transaction.current().manageNestedParallelTxs(callables);
		    currentActiveSubThreads=0;
		    callables.clear();
	    }
	    callables.add(new Worker(complexAssembly, 1875, 2000));
	    currentActiveSubThreads++;
	    Transaction.current().manageNestedParallelTxs(callables);
		currentActiveSubThreads=0;

	    // int[] specials = {1249, 1250, 1251, 1499, 1500, 1501, 1749, 1750,
	    // 1751};
	    // traverse(complexAssembly, specials);

	} else {
	    for (Assembly assembly : complexAssembly.getSubAssemblies())
		result += traverse(assembly, depth + 1);
	}

	return result;
    }

    protected int traverse(ComplexAssembly complexAssembly, int[] specials) {
	for (Assembly assembly4 : complexAssembly.getSubAssemblies()) {
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
	return 0;
    }

    protected int traverse(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds, int[] specials) {
	if (part == null)
	    return 0;
	if (setOfVisitedPartIds.contains(part))
	    return 0;

	setOfVisitedPartIds.add(part);
	int date = part.getFinalBuildDate();
	for (int i = 0; i < specials.length; i++) {
	    if (specials[i] == date) {
		performOperationInAtomicPart(part, setOfVisitedPartIds);
		break;
	    }
	}

	for (Connection connection : part.getToConnections())
	    traverse(connection.getDestination(), setOfVisitedPartIds);

	return 0;
    }

    public class Worker extends ParallelTask<Void> {

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
	    for (Assembly assembly4 : complexAssembly.getSubAssemblies()) {
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

    @Override
    protected int performOperationInAtomicPart(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
	updateBuildDate(part);
	return 1;
    }

    @Override
    public OperationId getOperationId() {
	return OperationId.T3b;
    }
}

package stmbench7.operations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import jvstm.TopLevelTransaction;
import jvstm.Transaction;
import jvstm.util.NestedWorkUnit;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;
import stmbench7.annotations.ReadOnly;
import stmbench7.annotations.Transactional;
import stmbench7.core.Assembly;
import stmbench7.core.AtomicPart;
import stmbench7.core.BaseAssembly;
import stmbench7.core.ComplexAssembly;
import stmbench7.core.CompositePart;
import stmbench7.core.Connection;
import stmbench7.core.Module;

/**
 * Traversal T1 (see the specification). Read-only, long.
 */
public class Traversal1 extends BaseOperation {

    protected Module module;

    public Traversal1(Setup oo7setup) {
	this.module = oo7setup.getModule();
    }

    public class Worker extends NestedWorkUnit<Integer> {

	private final CompositePart part;

	public Worker(CompositePart part) {
	    this.part = part;
	}

	@Override
	public Integer execute() throws Throwable {
	    return traverse(part);
	}
	
    }

    public class WorkerAssembly extends NestedWorkUnit<Integer> {

	private final Assembly assembly;
	private final int depth;

	public WorkerAssembly(Assembly assembly, int depth) {
	    this.assembly = assembly;
	    this.depth = depth;
	}

	@Override
	public Integer execute() throws Throwable {
	    return traverse(assembly, depth);
	}

    }

    @Override
    @Transactional
    @ReadOnly
    public int performOperation() {
	ComplexAssembly designRoot = module.getDesignRoot();
	return traverse(designRoot, 1);
    }

    protected int traverse(Assembly assembly, int depth) {
	if (assembly instanceof BaseAssembly)
	    return traverse((BaseAssembly) assembly);
	else
	    return traverse((ComplexAssembly) assembly, depth);
    }

    protected int traverse(ComplexAssembly complexAssembly, int depth) {
	int partsVisited = 0;

	for (Assembly assembly : complexAssembly.getSubAssemblies())
	    partsVisited += traverse(assembly, depth + 1);
	
	return partsVisited;

    }

    protected int traverse(BaseAssembly baseAssembly) {
	int partsVisited = 0;

	//	List<JVSTMTask<Integer>> callables = new ArrayList<JVSTMTask<Integer>>();
	//	for (CompositePart component : baseAssembly.getComponents()) {
	//	    if (component.getId() == 374)
	//		continue;
	//	    callables.add(new ThreadedWorker(component));
	//	}
	//	for (Integer res : ((TopLevelTransaction) Transaction.current()).manageMultiThreading(callables)) {
	//	    partsVisited += res;
	//	}

	//	if(this instanceof Traversal1) {	
	//	    for (CompositePart component : baseAssembly.getComponents()) {
	//		partsVisited += traverse(component);
	//	    }
	//	}

	for (CompositePart component : baseAssembly.getComponents()) {
//	    if (component.getId() == 374)
//		continue;
	    partsVisited += traverse(component);
	}

	//	if (!Parameters.parNest) {
	//	    for (CompositePart component : baseAssembly.getComponents()) {
	//		if (component.getId() == 374)
	//		    continue;
	//		partsVisited += traverse(component);
	//	    }
	//	} else {
	//	    List<NestedWorkUnit<Integer>> callables = new ArrayList<NestedWorkUnit<Integer>>();
	//	    for (CompositePart component : baseAssembly.getComponents()) {
	//		if (component.getId() == 374)
	//		    continue;
	//		callables.add(new Worker(component));
	//	    }
	//	    for (Integer res : Transaction.current().manageNestedParallelTxs(callables)) {
	//		partsVisited += res;
	//	    }
	//	}

	return partsVisited;
    }

    //    public class ThreadedWorker extends JVSTMTask<Integer> {
    //
    //	private final CompositePart component;
    //
    //	public ThreadedWorker(CompositePart component) {
    //	    this.component = component;
    //	}
    //
    //	@Override
    //	public Integer execute() throws Throwable {
    //	    return traverse(component);
    //	}
    //
    //    }

    protected int traverse(CompositePart component) {
	AtomicPart rootPart = component.getRootPart();
	HashSet<AtomicPart> setOfVisitedPartIds = new HashSet<AtomicPart>();

	return traverse(rootPart, setOfVisitedPartIds);
    }

    protected int traverse(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
	if (part == null)
	    return 0;
	if (setOfVisitedPartIds.contains(part))
	    return 0;

	int result = performOperationInAtomicPart(part, setOfVisitedPartIds);

	setOfVisitedPartIds.add(part);

	for (Connection connection : part.getToConnections())
	    result += traverse(connection.getDestination(), setOfVisitedPartIds);

		return result;
    }

    protected int performOperationInAtomicPart(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
	part.nullOperation();
	return 1;
    }

    @Override
    public OperationId getOperationId() {
	return OperationId.T1;
    }
}

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
import stmbench7.core.AtomicPart;
import stmbench7.core.BaseAssembly;
import stmbench7.core.CompositePart;
import stmbench7.core.Document;
import stmbench7.core.RuntimeError;

/**
 * Traversal T4 (see the specification).
 * Read-only, long.
 */
public class Traversal4 extends Traversal1 {

    public Traversal4(Setup oo7setup) {
    	super(oo7setup);
    }

    @Override
    protected int traverse(BaseAssembly baseAssembly) {
	int partsVisited = 0;

	for (CompositePart component : baseAssembly.getComponents()) {
	    partsVisited += traverse(component);
	}

	return partsVisited;
    }
    
    @Override
    protected int traverse(CompositePart component) {
    	Document documentation = component.getDocumentation();
    	return traverse(documentation);
    }
    
    protected int traverse(Document documentation) {
    	return documentation.searchText('I');
    }

    @Override
    protected int traverse(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
    	throw new RuntimeError("T4: traverse(AtomicPart, HashSet<AtomicPart>) called!");
    }

    @Override
    protected int performOperationInAtomicPart(AtomicPart part, HashSet<AtomicPart> setOfVisitedPartIds) {
    	throw new RuntimeError("T4: performOperationInAtomicPart(..) called!");
    }
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.T4;
    }
}

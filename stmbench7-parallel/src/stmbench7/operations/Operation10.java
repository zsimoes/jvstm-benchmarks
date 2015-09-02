package stmbench7.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jvstm.ParallelTask;
import jvstm.Transaction;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;
import stmbench7.annotations.Transactional;
import stmbench7.annotations.Update;
import stmbench7.backend.LargeSet;
import stmbench7.core.AtomicPart;
import stmbench7.core.OperationFailedException;

/**
 * Operation OP10 (see the specification).
 * Simple update, range query on index.
 */
public class Operation10 extends Query2 {

    public Operation10(Setup oo7setup) {
	super(oo7setup, 1);
    }
    

    public class Worker extends ParallelTask<Void> {

	private final int start;
	private final Iterable<LargeSet<AtomicPart>> parts;

	public Worker(Iterable<LargeSet<AtomicPart>> parts, int start) {
	    this.start = start;
	    this.parts = parts;
	}

	@Override
	public Void execute() throws Throwable {
	    int i = 0;
	    int cnt = 0;
	    for(LargeSet<AtomicPart> partSet : parts) {
		if (i < start) {
		    continue;
		}
		cnt++;
		if (cnt == 5) {
		    return null;
		}
		for(AtomicPart part : partSet) {
		    performOperationInAtomicPart(part);
		}
	    }
	    return null;
	}

    }

    @Override
    @Transactional @Update
    public int performOperation() throws OperationFailedException {
	if (!Parameters.parNest) {
	    return super.performOperation();
	}
	    return super.performOperation();
//	Iterable<LargeSet<AtomicPart>> partSets = partBuildDateIndex.getRange(minAtomicDate, maxAtomicDate);
//	int count = 0;
//
//	List<Callable<Void>> list = new ArrayList<Callable<Void>>();
//	list.add(new Worker(partSets, 0));
//	list.add(new Worker(partSets, 5));
//	Transaction.current().manageNestedParallelTxs(list);
//
//	return count;
    }

    @Override
    protected void performOperationInAtomicPart(AtomicPart atomicPart) {
	atomicPart.swapXY();
    }

    @Override
    public OperationId getOperationId() {
	return OperationId.OP10;
    }
}

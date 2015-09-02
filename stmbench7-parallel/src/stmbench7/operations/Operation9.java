package stmbench7.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jvstm.ParallelTask;
import jvstm.Transaction;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;
import stmbench7.ThreadRandom;
import stmbench7.annotations.ReadOnly;
import stmbench7.annotations.Transactional;
import stmbench7.annotations.Update;
import stmbench7.core.AtomicPart;
import stmbench7.core.OperationFailedException;
import stmbench7.operations.Operation15.Worker;

/**
 * Operation OP9 (see the specification).
 * Simple update, search on index.
 */
public class Operation9 extends Query1 {

	public Operation9(Setup oo7setup) {
		super(oo7setup);
	}
    
	public class Worker extends ParallelTask<Void> {

	    private final int[] array;
	    
	    public Worker(int[] array) {
		this.array = array;
	    }
	    
	    @Override
	    public Void execute() throws Throwable {
		for (int i : array) {
		    AtomicPart part = partIdIndex.get(i);

		    if(part == null) continue;

		    performOperationInAtomicPart(part);
		}
		return null;
	    }
	    
	}

	@Override
	@Transactional @ReadOnly
	public int performOperation() throws OperationFailedException {
	    if (!Parameters.parNest) {
		return super.performOperation();
	    }
	    
		return super.performOperation();

//	    List<Callable<Void>> list = new ArrayList<Callable<Void>>();
//	    int[] array = new int[5];
//	    array[0] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    array[1] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    array[2] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    array[3] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    array[4] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    list.add(new Worker(array));
//	    array = new int[5];
//	    array[0] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    array[1] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    array[2] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    array[3] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    array[4] = ThreadRandom.nextInt(Parameters.MaxAtomicParts) + 1;
//	    list.add(new Worker(array));
//	    Transaction.current().manageNestedParallelTxs(list);
//
//	    return 10;
	}
	
	@Override
	protected void performOperationInAtomicPart(AtomicPart atomicPart) {
		atomicPart.swapXY();
	}
	
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP9;
    }
}

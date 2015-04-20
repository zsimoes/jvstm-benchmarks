package stamp.vacation.pnstm.treemap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadFactory;

import epfl.ConflictException;
import epfl.Transaction;
import epfl.NestedWorker;

public class DeleteCustomerOperation extends Operation {

    final private Manager managerPtr;
    final private int customerId;

    public DeleteCustomerOperation(Manager managerPtr, Random randomPtr, int queryRange) {
	this.managerPtr = managerPtr;
	this.customerId = randomPtr.posrandom_generate() % queryRange + 1;
    }

    private ExecutorService threadPool;

    @Override
    public void doOperation() {
	while (true) {
	    threadPool = Executors.newFixedThreadPool((fakeDepth > 0) ? Operation.numberParallelSiblings * fakeDepth : Operation.numberParallelSiblings, new ThreadFactory() {
		@Override
		public Thread newThread(Runnable r) {
		    Thread t = new Thread(r);
		    t.setDaemon(true);
		    return t;
		}
	    });

	    Transaction tx = Transaction.begin();
	    if (tx == null) {
		throw new ConflictException(); // Should never happen!
	    }
	    try {
		if (Operation.fakeDepth > 0) {
		    List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
		    callables.add(new Nested(1));
		    tx.manageNestedParallelTxs(callables, threadPool).get(0);
		} else {
			int bill = managerPtr.manager_queryCustomerBill(customerId);
			if (bill >= 0) {
			    managerPtr.manager_deleteCustomer(customerId);
			}
		}
		tx.commitTx();
		assert (epfl.Debug.print(3, Thread.currentThread().getId() + "] Finished operation: " + this));
		return;
	    } catch (ConflictException ae) {

	    }
	}
    }

    public class Nested extends NestedWorker<Void> {

	protected int depth;
	
	public Nested(int depth) {
	    this.depth = depth;
	}
	
	@Override
	public Void execute() throws Throwable {
	    if (depth == Operation.fakeDepth) {
		int bill = managerPtr.manager_queryCustomerBill(customerId);
		if (bill >= 0) {
		    managerPtr.manager_deleteCustomer(customerId);
		}
		return null;
	    } else {
		List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
		callables.add(new Nested(depth + 1));
		Transaction.current().manageNestedParallelTxs(callables, threadPool).get(0);
		return null;
	    }
	}
	
    }

}


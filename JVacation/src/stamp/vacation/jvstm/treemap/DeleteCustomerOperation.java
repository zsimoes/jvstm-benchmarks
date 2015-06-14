package stamp.vacation.jvstm.treemap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import jvstm.CommitException;
import jvstm.ParallelTask;
import jvstm.Transaction;
import jvstm.TransactionalTask;

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
	    // Debug.print("[Delete] Started top level tx in thread " +
	    // Thread.currentThread().getId() + " " + tx);
	    try {
		if (Operation.fakeDepth > 0) {
		    List<TransactionalTask<Void>> callables = new ArrayList<TransactionalTask<Void>>();
		    callables.add(new Nested(1));
		    tx.manageNestedParallelTxs(callables, threadPool).get(0);
		} else {
			int bill = managerPtr.manager_queryCustomerBill(customerId);
			if (bill >= 0) {
			    managerPtr.manager_deleteCustomer(customerId);
			}
		}
		tx.commit();
		// Debug.print("[Delete] Committed top level tx in thread " +
		// Thread.currentThread().getId() + " " + tx);
		tx = null;
		return;
	    } catch (CommitException ce) {
		tx.abort();
		// Debug.print("[Delete] Aborted top level tx in thread " +
		// Thread.currentThread().getId() + " " + tx);
		tx = null;
	    } finally {
		if (tx != null) {
		    tx.abort();
		}
	    }
	}
    }

    public class Nested extends ParallelTask<Void> {

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
		List<TransactionalTask<Void>> callables = new ArrayList<TransactionalTask<Void>>();
		callables.add(new Nested(depth + 1));
		Transaction.current().manageNestedParallelTxs(callables, threadPool).get(0);
		return null;
	    }
	}
	
    }

}

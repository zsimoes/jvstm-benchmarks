package stamp.vacation.jvstm.nonest.treemap.fake;

import java.util.ArrayList;
import java.util.List;

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
    
    @Override
    public void doOperation() {
	while (true) {
	    Transaction tx = Transaction.begin();
	    // Debug.print("[Delete] Started top level tx in thread " +
	    // Thread.currentThread().getId() + " " + tx);
	    try {
		if (Operation.fakeDepth > 0) {
		    List<TransactionalTask<Void>> callables = new ArrayList<TransactionalTask<Void>>();
		    callables.add(new Nested(1));
		    tx.manageNestedParallelTxs(callables, Operation.nestedParPool).get(0);
		} else {
		    operation();
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

    public void operation() {
	int bill = managerPtr.manager_queryCustomerBill(customerId);
	if (bill >= 0) {
	    managerPtr.manager_deleteCustomer(customerId);
	}
    }
    
    public class Nested extends ParallelTask<Void> {

	protected int depth;
	
	public Nested(int depth) {
	    this.depth = depth;
	}
	
	@Override
	public Void execute() {
	    if (depth == Operation.fakeDepth) {
		operation();
		return null;
	    } else {
		List<TransactionalTask<Void>> callables = new ArrayList<TransactionalTask<Void>>();
		callables.add(new Nested(depth + 1));
		Transaction.current().manageNestedParallelTxs(callables, Operation.nestedParPool).get(0);
		return null;
	    }
	}
	
    }
    
}

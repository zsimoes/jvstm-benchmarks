package stamp.vacation.pnstm.treemap.copy;

import epfl.ConflictException;
import epfl.Transaction;

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
	    if (tx == null) {
		throw new ConflictException(); // Should never happen!
	    }
	    try {
		int bill = managerPtr.manager_queryCustomerBill(customerId);
		if (bill >= 0) {
		    managerPtr.manager_deleteCustomer(customerId);
		}
		tx.commitTx();
		assert (stanford.Debug.print(3, Thread.currentThread().getId() + "] Finished operation: " + this));
		return;
	    } catch (ConflictException ae) {

	    }
	}
    }

}

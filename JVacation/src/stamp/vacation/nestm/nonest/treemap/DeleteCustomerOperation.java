package stamp.vacation.nestm.nonest.treemap;

import jvstm.CommitException;
import jvstm.Transaction;
import jvstm.TransactionSignaller;

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
		TransactionSignaller.SIGNALLER.signalCommitFail(); // Should never happen!
	    }
	    try {
		int bill = managerPtr.manager_queryCustomerBill(customerId);
		if (bill >= 0) {
		    managerPtr.manager_deleteCustomer(customerId);
		}
		tx.commit();
		return;
	    } catch (CommitException ae) {

	    }
	}
    }

}

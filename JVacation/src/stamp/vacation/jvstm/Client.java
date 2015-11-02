package stamp.vacation.jvstm;

import jvstm.CommitException;
import jvstm.Transaction;

public class Client extends Thread
{
	final int id;
	final Manager managerPtr;
	final Random randomPtr;
	final int numOperation;
	final int numQueryPerTransaction;
	final int queryRange;
	final int percentUser;
	final Operation[] operations;

	/*
	 * ==========================================================================
	 * === client_alloc -- Returns NULL on failure
	 * ==============================
	 * ===============================================
	 */
	public Client(int id, Manager managerPtr, int numOperation, int numQueryPerTransaction, int queryRange,
			int percentUser)
	{
		this.randomPtr = new Random();
		this.randomPtr.random_alloc();
		this.id = id;
		this.managerPtr = managerPtr;
		randomPtr.random_seed(id);
		this.numOperation = numOperation;
		this.numQueryPerTransaction = numQueryPerTransaction;
		this.queryRange = queryRange;
		this.percentUser = percentUser;
		this.setName("Vacation Client");

		operations = new Operation[numOperation];

		// System.out.println("\tClient " + this.id + " - running operations: "
		// + numOperation);

		for (int i = 0; i < numOperation; i++)
		{
			int r = randomPtr.posrandom_generate() % 100;
			int action = selectAction(r, percentUser);

			if (action == Definitions.ACTION_MAKE_RESERVATION)
			{
				operations[i] = new MakeReservationOperation(managerPtr, randomPtr, numQueryPerTransaction, queryRange);
			} else if (action == Definitions.ACTION_DELETE_CUSTOMER)
			{
				operations[i] = new DeleteCustomerOperation(managerPtr, randomPtr, queryRange);
			} else if (action == Definitions.ACTION_UPDATE_TABLES)
			{
				operations[i] = new UpdateTablesOperation(managerPtr, randomPtr, numQueryPerTransaction, queryRange);
			} else
			{
				assert (false);
			}
		}
	}

	/*
	 * ==========================================================================
	 * === selectAction
	 * ==========================================================
	 * ===================
	 */
	public int selectAction(int r, int percentUser)
	{
		if (r < percentUser)
		{
			return Definitions.ACTION_MAKE_RESERVATION;
		} else if ((r & 1) == 1)
		{
			return Definitions.ACTION_DELETE_CUSTOMER;
		} else
		{
			return Definitions.ACTION_UPDATE_TABLES;
		}
	}

	/*
	 * ==========================================================================
	 * === client_run -- Execute list operations on the database
	 * ================
	 * =============================================================
	 */
	@Override
	public void run()
	{
		for (int i = 0; i < numOperation; i++)
		{
			operations[i].doOperation();
		}
	}

}

/*
 * =============================================================================
 * 
 * End of client.c
 * 
 * =============================================================================
 */


package stamp.vacation.jvstm.nonest.treemap.fake;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jvstm.CommitException;
import jvstm.Transaction;
import jvstm.util.NestedWorkUnit;

public class UpdateTablesOperation extends Operation {

    final private Manager managerPtr;
    final private int[] types;
    final private int[] ids;
    final private int[] ops;
    final private int[] prices;
    final private int numUpdate;

    public UpdateTablesOperation(Manager managerPtr, Random randomPtr, int numQueryPerTransaction, int queryRange) {
	this.managerPtr = managerPtr;
	this.types = new int[numQueryPerTransaction];
	this.ids = new int[numQueryPerTransaction];
	this.ops = new int[numQueryPerTransaction];
	this.prices = new int[numQueryPerTransaction];

	int[] baseIds = new int[20];
	for (int i = 0; i < 20; i++) {
	    baseIds[i] = (randomPtr.random_generate() % queryRange) + 1;
	}
	
	this.numUpdate = numQueryPerTransaction;
	int n;
	for (n = 0; n < numUpdate; n++) {
	    types[n] = randomPtr.posrandom_generate() % Definitions.NUM_RESERVATION_TYPE;
	    ids[n] = baseIds[n % 20];
	    ops[n] = randomPtr.posrandom_generate() % 2;
	    if (ops[n] == 1) {
		prices[n] = ((randomPtr.posrandom_generate() % 5) * 10) + 50;
	    }
	}
    }

    @Override
    public void doOperation() {
	while (true) {
	    Transaction tx = Transaction.begin();
	    // Debug.print("[Update] Started top level tx in thread " +
	    // Thread.currentThread().getId() + " " + tx);
	    try {
		if (Operation.fakeDepth > 0) {
		    List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
		    callables.add(new Nested(1));
		    tx.manageNestedParallelTxs(callables, Operation.nestedParPool).get(0);
		} else {
		    operation();
		}
		tx.commit();
		// Debug.print("[Update] Committed top level tx in thread " +
		// Thread.currentThread().getId() + " " + tx);
		tx = null;
		return;
	    } catch (CommitException ce) {
		tx.abort();
		// Debug.print("[Update] Aborted top level tx in thread " +
		// Thread.currentThread().getId() + " " + tx);
		tx = null;
	    } finally {
		if (tx != null) {
		    tx.abort();
		}
	    }
	}
    }

    private void operation() {
	int n;
	for (n = 0; n < numUpdate; n++) {
	    int t = types[n];
	    int id = ids[n];
	    int doAdd = ops[n];
	    if (doAdd == 1) {
		int newPrice = prices[n];
		if (t == Definitions.RESERVATION_CAR) {
		    managerPtr.manager_addCar(id, 100, newPrice);
		} else if (t == Definitions.RESERVATION_FLIGHT) {
		    managerPtr.manager_addFlight(id, 100, newPrice);
		} else if (t == Definitions.RESERVATION_ROOM) {
		    managerPtr.manager_addRoom(id, 100, newPrice);
		} else {
		    assert (false);
		}
	    } else { /* do delete */
		if (t == Definitions.RESERVATION_CAR) {
		    managerPtr.manager_deleteCar(id, 100);
		} else if (t == Definitions.RESERVATION_FLIGHT) {
		    managerPtr.manager_deleteFlight(id);
		} else if (t == Definitions.RESERVATION_ROOM) {
		    managerPtr.manager_deleteRoom(id, 100);
		} else {
		    assert (false);
		}
	    }
	}
    }
    
    public class Nested extends NestedWorkUnit<Void> {

	protected int depth;
	
	public Nested(int depth) {
	    this.depth = depth;
	}
	
	@Override
	public Void execute() throws Throwable {
	    if (depth == Operation.fakeDepth) {
		operation();
		return null;
	    } else {
		List<Callable<Void>> callables = new ArrayList<Callable<Void>>();
		callables.add(new Nested(depth + 1));
		Transaction.current().manageNestedParallelTxs(callables, Operation.nestedParPool).get(0);
		return null;
	    }
	}
	
    }

}

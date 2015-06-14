package stamp.vacation.pnstm.treemap;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import jvstm.CommitException;
import jvstm.ParallelTask;
import jvstm.Transaction;
import jvstm.TransactionSignaller;
import jvstm.TransactionalTask;


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
		TransactionSignaller.SIGNALLER.signalCommitFail(); // Should never happen!
	    }
	    try {
		if (Operation.fakeDepth > 0) {
		    List<TransactionalTask<Void>> callables = new ArrayList<TransactionalTask<Void>>();
		    callables.add(new Nested(1));
		    tx.manageNestedParallelTxs(callables, threadPool).get(0);
		} else {
			if (Operation.nestedParallelismOn && Operation.parallelizeUpdateTables) {
			    updateTables();
			} else {
			    updateTablesNotNested();
			}
		}
		tx.commit();
		return;
	    } catch (CommitException ae) {

	    }
	}
    }

    private class NestedWork extends ParallelTask<Void> {

	private final List<Integer> operations;

	public NestedWork(List<Integer> operations) {
	    this.operations = operations;
	}

	@Override
	public Void execute() throws Throwable {
	    for (int n : operations) {
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
	    return null;
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
		if (Operation.nestedParallelismOn && Operation.parallelizeUpdateTables) {
		    updateTables();
		} else {
		    updateTablesNotNested();
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

    private void updateTables() {
	int n;

	List<NestedWork> workers = new ArrayList<NestedWork>();
	List<Integer> type1 = new ArrayList<Integer>();
	List<Integer> type2 = new ArrayList<Integer>();
	List<Integer> type3 = new ArrayList<Integer>();
	for (n = 0; n < numUpdate; n++) {
	    int t = types[n];
	    if (t == Definitions.RESERVATION_CAR) {
		type1.add(n);
	    } else if (t == Definitions.RESERVATION_FLIGHT) {
		type2.add(n);
	    } else if (t == Definitions.RESERVATION_ROOM) {
		type3.add(n);
	    }
	}

	workers.add(new NestedWork(type1));
	workers.add(new NestedWork(type2));
	workers.add(new NestedWork(type3));

	try {
	    List<Future<Void>> results = threadPool.invokeAll(workers);
	    for (Future<Void> res : results) {
		res.get();
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    throw (RuntimeException) e.getCause();
	} finally {
	    threadPool.shutdownNow();
	}
    }

    private void updateTablesNotNested() {
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

}

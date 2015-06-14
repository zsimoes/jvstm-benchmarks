package stamp.vacation.nestm.treemap;

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

public class MakeReservationOperation extends Operation {

    final private Manager manager;
    final private int[] types;
    final private int[] ids;
    final private int[] maxPrices;
    final private int[] maxIds;
    final private int customerId;
    final private int numQuery;

    public MakeReservationOperation(Manager manager, Random random, int numQueryPerTx, int queryRange) {
	this.manager = manager;
	this.types = new int[numQueryPerTx];
	this.ids = new int[numQueryPerTx];

	this.maxPrices = new int[Definitions.NUM_RESERVATION_TYPE];
	this.maxIds = new int[Definitions.NUM_RESERVATION_TYPE];
	this.maxPrices[0] = -1;
	this.maxPrices[1] = -1;
	this.maxPrices[2] = -1;
	this.maxIds[0] = -1;
	this.maxIds[1] = -1;
	this.maxIds[2] = -1;
	int n;
	this.numQuery = numQueryPerTx;
	this.customerId = random.posrandom_generate() % queryRange + 1;

	int[] baseIds = new int[20];
	for (int i = 0; i < 20; i++) {
	    baseIds[i] = (random.random_generate() % queryRange) + 1;
	}

	for (n = 0; n < numQuery; n++) {
	    types[n] = random.random_generate() % Definitions.NUM_RESERVATION_TYPE;
	    ids[n] = baseIds[n % 20];
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
			if (Operation.nestedParallelismOn) {
			    makeReservation();
			} else {
			    makeReservationNotNested();
			}
		}
		tx.commit();
		
		return;
	    } catch (CommitException ae) {

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
		if (Operation.nestedParallelismOn) {
		    makeReservation();
		} else {
		    makeReservationNotNested();
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

    private class NestedWork extends ParallelTask<Boolean> {

	private final int min;
	private final int max;

	public NestedWork(int min, int max) {
	    this.min = min;
	    this.max = max;
	}

	@Override
	public Boolean execute() throws Throwable {
	    boolean isFound = false;
	    int n;
	    for (n = min; n < max; n++) {
		int t = types[n];
		int id = ids[n];
		int price = -1;
		if (t == Definitions.RESERVATION_CAR) {
		    if (manager.manager_queryCar(id) >= 0) {
			price = manager.manager_queryCarPrice(id);
		    }
		} else if (t == Definitions.RESERVATION_FLIGHT) {
		    if (manager.manager_queryFlight(id) >= 0) {
			price = manager.manager_queryFlightPrice(id);
		    }
		} else if (t == Definitions.RESERVATION_ROOM) {
		    if (manager.manager_queryRoom(id) >= 0) {
			price = manager.manager_queryRoomPrice(id);
		    }
		} else {
		    assert (false);
		}
		if (price > maxPrices[t]) {
		    maxPrices[t] = price;
		    maxIds[t] = id;
		    isFound = true;
		}
	    }
	    return isFound;
	}

    }

    private void makeReservationNotNested() {
	boolean isFound = false;
	int n;
	for (n = 0; n < numQuery; n++) {
	    int t = types[n];
	    int id = ids[n];
	    int price = -1;
	    if (t == Definitions.RESERVATION_CAR) {
		if (manager.manager_queryCar(id) >= 0) {
		    price = manager.manager_queryCarPrice(id);
		}
	    } else if (t == Definitions.RESERVATION_FLIGHT) {
		if (manager.manager_queryFlight(id) >= 0) {
		    price = manager.manager_queryFlightPrice(id);
		}
	    } else if (t == Definitions.RESERVATION_ROOM) {
		if (manager.manager_queryRoom(id) >= 0) {
		    price = manager.manager_queryRoomPrice(id);
		}
	    } else {
		assert (false);
	    }
	    if (price > maxPrices[t]) {
		maxPrices[t] = price;
		maxIds[t] = id;
		isFound = true;
	    }
	}

	if (isFound) {
	    manager.manager_addCustomer(customerId);
	}
	if (maxIds[Definitions.RESERVATION_CAR] > 0) {
	    manager.manager_reserveCar(customerId, maxIds[Definitions.RESERVATION_CAR]);
	}
	if (maxIds[Definitions.RESERVATION_FLIGHT] > 0) {
	    manager.manager_reserveFlight(customerId, maxIds[Definitions.RESERVATION_FLIGHT]);
	}
	if (maxIds[Definitions.RESERVATION_ROOM] > 0) {
	    manager.manager_reserveRoom(customerId, maxIds[Definitions.RESERVATION_ROOM]);
	}
    }

    private void makeReservation() {
	boolean isFound = false;
	int n;
	int queriesPerTx = numQuery / Operation.numberParallelSiblings;
	List<NestedWork> workers = new ArrayList<NestedWork>();
	for (n = 0; n < numQuery; n += queriesPerTx) {
	    workers.add(new NestedWork(n, n + queriesPerTx));
	} /* for n */

	try {
	    List<Future<Boolean>> results = threadPool.invokeAll(workers);
	    for (Future<Boolean> res : results) {
		if (res.get()) {
		    isFound = true;
		}
	    }
	} catch (InterruptedException e) {
	    e.printStackTrace();
	} catch (ExecutionException e) {
	    throw (RuntimeException) e.getCause();
	} finally {
	    threadPool.shutdownNow();
	}

	if (isFound) {
	    manager.manager_addCustomer(customerId);
	}
	if (maxIds[Definitions.RESERVATION_CAR] > 0) {
	    manager.manager_reserveCar(customerId, maxIds[Definitions.RESERVATION_CAR]);
	}
	if (maxIds[Definitions.RESERVATION_FLIGHT] > 0) {
	    manager.manager_reserveFlight(customerId, maxIds[Definitions.RESERVATION_FLIGHT]);
	}
	if (maxIds[Definitions.RESERVATION_ROOM] > 0) {
	    manager.manager_reserveRoom(customerId, maxIds[Definitions.RESERVATION_ROOM]);
	}
    }

}

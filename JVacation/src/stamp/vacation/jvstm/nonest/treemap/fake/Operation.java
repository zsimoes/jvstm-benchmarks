package stamp.vacation.jvstm.nonest.treemap.fake;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public abstract class Operation {

    public static final ExecutorService nestedParPool = Executors.newCachedThreadPool(new ThreadFactory() {
	@Override
	public Thread newThread(Runnable r) {
	    Thread t = new Thread(r);
	    t.setDaemon(true);
	    return t;
	}
    });
    
    public static boolean nestedParallelismOn;
    public static int numberParallelSiblings;
    public static boolean parallelizeUpdateTables;
    public static int fakeDepth = 0;

    public abstract void doOperation();

}

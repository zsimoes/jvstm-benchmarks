package stamp.vacation.jvstm.treemap;

public abstract class Operation {

    public static boolean nestedParallelismOn;
    public static int numberParallelSiblings;
    public static boolean parallelizeUpdateTables;
    public static int fakeDepth = 0;

    public abstract void doOperation();

}

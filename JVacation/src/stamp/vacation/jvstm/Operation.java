package stamp.vacation.jvstm;

public abstract class Operation {

    public static boolean nestedParallelismOn;
    public static int numberParallelSiblings;
    public static boolean parallelizeUpdateTables;
    public static boolean readOnly;
    public static boolean unsafe;

    public abstract void doOperation();

}

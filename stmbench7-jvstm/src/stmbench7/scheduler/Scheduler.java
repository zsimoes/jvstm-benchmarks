package stmbench7.scheduler;

import jvstm.CommitException;
import stmbench7.OperationExecutor;
import stmbench7.OperationId;

public interface Scheduler {

    public void setupStructures(double[] operationCDF, OperationExecutor[] ops);

    public void replenishOperations(int number);

    public OperationExecutor getNextOperation();

    public void finishComputation(OperationExecutor operation);

    public void conflictTx(OperationId abortingTx, CommitException ce);

    public void commitTx(int commitNumber, OperationId committingTx);

}

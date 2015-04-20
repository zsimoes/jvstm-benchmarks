package stmbench7;

import jvstm.scheduler.ScheduledTask;
import jvstm.scheduler.WorkDealing;
import stmbench7.annotations.NonAtomic;
import stmbench7.annotations.ThreadLocal;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;

/**
 * An interface representing a class that executes a given operation. Can set up
 * a transaction and handle aborts.
 */
@NonAtomic
@ThreadLocal
public interface OperationExecutor extends ScheduledTask {

    int execute() throws OperationFailedException;

    int getLastOperationTimestamp();

    boolean isOperationReadOnly();

    int getLastLocalOperationTimestamp();

    // schd: scheduling requirement
    public int getOperationNumber();

    public Operation getOp();

    public void setRunner(WorkDealing runner);
}

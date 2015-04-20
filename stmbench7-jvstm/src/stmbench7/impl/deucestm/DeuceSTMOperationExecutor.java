package stmbench7.impl.deucestm;

import jvstm.scheduler.WorkDealing;
import stmbench7.OperationExecutor;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;

public class DeuceSTMOperationExecutor implements OperationExecutor {

    protected int operationNumber;
    private final Operation op;

    public DeuceSTMOperationExecutor(Operation op) {
	this.op = op;
    }

    @Override
    @org.deuce.Atomic
    public int execute() throws OperationFailedException {
	return op.performOperation();
    }

    @Override
    public int getLastOperationTimestamp() {
	return 0;
    }

    @Override
    public boolean isOperationReadOnly() {
	// TODO Auto-generated method stub
	return false;
    }

    @Override
    public int getLastLocalOperationTimestamp() {
	// TODO Auto-generated method stub
	return 0;
    }

    @Override
    public int getOperationNumber() {
	return operationNumber;
    }

    @Override
    public Operation getOp() {
	return op;
    }

    @Override
    public void setRunner(WorkDealing runner) {
	// TODO Auto-generated method stub

    }

    @Override
    public int getTaskId() {
	return operationNumber;
    }
}

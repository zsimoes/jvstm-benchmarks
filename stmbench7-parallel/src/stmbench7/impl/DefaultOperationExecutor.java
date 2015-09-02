package stmbench7.impl;

import stmbench7.OperationExecutor;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;

/**
 * Default implementation of an OperationExecutor. Does not provide any
 * thread-safety.
 */
public class DefaultOperationExecutor implements OperationExecutor {

    private static int clock = 0;

    protected int operationNumber;
    private final Operation op;
    private int lastTimestamp = 0;

    public DefaultOperationExecutor(Operation op) {
	this.op = op;
    }

    @Override
    public int execute() throws OperationFailedException {
	lastTimestamp = clock++;
	return op.performOperation();
    }

    @Override
    public int getLastOperationTimestamp() {
	return lastTimestamp;
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

    public int getTaskId() {
	return operationNumber;
    }
}

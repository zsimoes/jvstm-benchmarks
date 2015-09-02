package stmbench7.locking;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import stmbench7.OperationExecutor;
import stmbench7.core.Operation;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;

public class CGLockingOperationExecutor implements OperationExecutor {

    private static final Lock globalReadLock, globalWriteLock;
    private static int globalCounter = 0;

    static {
	ReentrantReadWriteLock globalLock = new ReentrantReadWriteLock(true);
	globalReadLock = globalLock.readLock();
	globalWriteLock = globalLock.writeLock();
    }

    protected int operationNumber;

    private final Operation op;
    private final Lock globalLock;
    private int lastOperationTimestamp;

    public CGLockingOperationExecutor(Operation op) {
	this.op = op;
	if (op.getOperationId() == null) {
	    globalLock = null;
	    return;
	}

	switch (op.getOperationId().getType()) {
	case OPERATION_RO:
	case SHORT_TRAVERSAL_RO:
	case TRAVERSAL_RO:
	    globalLock = globalReadLock;
	    break;
	case OPERATION:
	case SHORT_TRAVERSAL:
	case TRAVERSAL:
	case STRUCTURAL_MODIFICATION:
	    globalLock = globalWriteLock;
	    break;
	default:
	    throw new RuntimeError("Unexpected operation type");
	}
    }

    @Override
    public int execute() throws OperationFailedException {
	try {
	    if (globalLock != null)
		globalLock.lock();
	    return op.performOperation();
	} finally {
	    lastOperationTimestamp = globalCounter++;
	    if (globalLock != null)
		globalLock.unlock();
	}
    }

    @Override
    public int getLastOperationTimestamp() {
	return lastOperationTimestamp;
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

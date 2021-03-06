package stmbench7.core;

import stmbench7.OperationId;
import stmbench7.annotations.Immutable;
import stmbench7.annotations.ThreadLocal;

/**
 * STMBench7 benchmark specific exception. Indicates that a benchmark operation
 * failed. Note that operation failures are unavoidable in the benchmark: their
 * number is indicated in the results shown at the end of the benchmark.
 */
@Immutable
@ThreadLocal
public class OperationFailedException extends Exception {

	OperationId source;
	
	public void setSource(OperationId opId) {
		this.source = opId;
	}
	
	public OperationId getSource() {
		return source;
	}
	
	private static final long serialVersionUID = -4829600105999291994L;

	public OperationFailedException(String message, Object reportingObject) {
		super(message + " [" + reportingObject.toString() + "]");
	}

	public OperationFailedException(String message) {
		super(message);
	}
	
	public OperationFailedException(String message, OperationId source) {
		super(message);
		this.source = source;
	}

	public OperationFailedException(String message, Throwable cause) {
		super(message, cause);
	}
	
	
	public OperationFailedException(String message, Throwable cause, OperationId source) {
		super(message, cause);
		this.source = source;
	}

	
	public OperationFailedException(OperationId source) {
		super();
		this.source = source;
	}

	public OperationFailedException() {
		super();
	}
}

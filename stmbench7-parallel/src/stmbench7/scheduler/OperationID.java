package stmbench7.scheduler;

/**
 * Operation value type.
 * 
 * @author david
 *
 */
@Deprecated
public class OperationID {

	private int _operationNumber;
	
	public OperationID( int operationNumber ) {
		_operationNumber = operationNumber;
	}
	
	public int getOperationNumber() {
		return _operationNumber;
	}
	
	@Override
	public String toString() {
		return "Operation #" + _operationNumber;
	}
	
	@Override
	public int hashCode() {
		return ((Integer)_operationNumber).hashCode();
	}
	
	@Override
	public boolean equals( Object obj ) {
		if( obj instanceof OperationID )
			return this.equals((OperationID)obj);
		else return false;
	}
	
	public boolean equals( OperationID operation ) {
		return this._operationNumber == operation._operationNumber;
	}
	
}

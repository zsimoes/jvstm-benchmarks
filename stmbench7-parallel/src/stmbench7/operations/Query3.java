package stmbench7.operations;

import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;

/**
 * Query Q3 / Operation OP3 (see the specification).
 * Read-only, range query on index.
 */
public class Query3 extends Query2 {

    public Query3(Setup oo7setup) {
    	super(oo7setup, 10);
    }
    
    public void changeSetup(Setup oo7setup) {
    	super.changeSetup(oo7setup, 10);
    }
    
    @Override
    public OperationId getOperationId() {
    	return OperationId.OP3;
    }
}

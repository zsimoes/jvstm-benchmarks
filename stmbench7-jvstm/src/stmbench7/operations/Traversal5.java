package stmbench7.operations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import jvstm.Transaction;
import jvstm.util.NestedWorkUnit;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;
import stmbench7.annotations.Transactional;
import stmbench7.annotations.Update;
import stmbench7.core.BaseAssembly;
import stmbench7.core.CompositePart;
import stmbench7.core.Document;
import stmbench7.core.RuntimeError;
import stmbench7.impl.jvstm.JVSTMOperationExecutor;

/**
 * Traversal T5 (see the specification).
 */
public class Traversal5 extends Traversal4 {

    public Traversal5(Setup oo7setup) {
	super(oo7setup);
    }

    @Override
    @Transactional
    @Update
    public int performOperation() {
	return super.performOperation();
    }

    public class Worker extends NestedWorkUnit<Void> {

	private final CompositePart part;

	public Worker(CompositePart part) {
	    this.part = part;
	}

	@Override
	public Void execute() throws Throwable {
	    traverse(part);
	    return null;
	}

    }

    @Override
    protected int traverse(BaseAssembly baseAssembly) {
	if (!Parameters.parNest) {
	    int partsVisited = 0;

	    for (CompositePart component : baseAssembly.getComponents()) {
		partsVisited += traverse(component);
	    }

	    return partsVisited;
	}

	List<Callable<Void>> list = new ArrayList<Callable<Void>>();
	for (CompositePart component : baseAssembly.getComponents()) {
	    list.add(new Worker(component));
	}
	Transaction.current().manageNestedParallelTxs(list, JVSTMOperationExecutor.nestedParPool);

	return 0;
    }

    @Override
    protected int traverse(Document documentation) {
	int result;

	if (documentation.textBeginsWith("I am"))
	    result = documentation.replaceText("I am", "This is");
	else if (documentation.textBeginsWith("This is"))
	    result = documentation.replaceText("This is", "I am");
	else
	    throw new RuntimeError("T5: illegal document text: " + documentation.getText());

	if (result == 0)
	    throw new RuntimeError("T5: concurrent modification!");
	return result;
    }

    @Override
    public OperationId getOperationId() {
	return OperationId.T5;
    }
}

package stmbench7.correctness.opacity;

import java.util.ArrayList;

import stmbench7.BenchThread;
import stmbench7.OperationExecutor;
import stmbench7.OperationId;
import stmbench7.Parameters;
import stmbench7.Setup;
import stmbench7.ThreadRandom;
import stmbench7.BenchThread.ReplayLogEntry;
import stmbench7.annotations.NonAtomic;
import stmbench7.core.OperationFailedException;
import stmbench7.core.RuntimeError;
import stmbench7.impl.jvstm.JVSTMOperationExecutor;
import stmbench7.scheduler.OperationScheduler;

/**
 * Replays sequentially a concurrent execution. Used to check
 * whether a given concurrent execution ensures opacity, i.e.,
 * whether the synchronization method used in the execution
 * was correctly synchronizing threads during this execution.
 */
@NonAtomic
public class SequentialReplayThread extends BenchThread {

	public SequentialReplayThread(Setup setup, double[] operationCDF,
			ArrayList<ReplayLogEntry> replayLog, OperationExecutor[] ops) {
		super(setup, operationCDF);
		this.setup=setup;
		this.operations=ops;
		this.replayLog = replayLog;
	}
	
	private Setup setup;
	
	public void run() {
		Parameters.parNest= false;
		Parameters.sequentialDebug = true;
		//for(ReplayLogEntry entry : replayLog)
		//	System.err.println(i++ + " @ " + OperationId.values()[entry.opNum] + "\t--\t" + entry.timestamp + "\t" + entry.threadNum + "." + entry.localTs + "\t" + entry.readOnly);
		int opNum = 1, numOfOps = replayLog.size();
		for(ReplayLogEntry entry : replayLog) {
//	    OperationExecutor operation = OperationScheduler.getScheduler().getNextOperation();
			
		OperationExecutor operation =	operations[entry.opNum];
	    ((JVSTMOperationExecutor) operation).changeSetup(setup);
	    
		System.err.print("Operation " + operation + " ID: " +  operation.getOperationNumber() + " type: " + OperationId.values()[operation.getOperationNumber()].toString() + " out of " + numOfOps + "\r");
//			short threadNum = entry.threadNum;
//			ThreadRandom.setVirtualThreadNumber(threadNum);

			//System.out.println(++i);
//			int operationNumber = getNextOperationNumber();

			//System.out.println(entry.threadNum + " -- " + OperationId.values()[entry.opNum] +
			//	" / " + OperationId.values()[operationNumber]);
//			if(operationNumber != entry.opNum) {		
//				throw new RuntimeError("ThreadRandom skew");
//			}

			int result = 0;
			boolean failed = false;

			try {
				// JVSTM: Considering how the benchmark works, it makes no sense
				// 	to save the state of the pseudo-random generator state
				//	and then restore it if the operation fails.
				//ThreadRandom.saveState();
				result = operation.execute();
			}
			catch(OperationFailedException e) {
				failed = true;
				// JVSTM: Read comment above.
				//ThreadRandom.restoreState();
			}

			if(result != entry.result || failed != entry.failed) {
				String opName = OperationId.values()[operation.getOperationNumber()].toString();
				throw new RuntimeError("Different operation result in the sequential execution (" +
						"operation " + opName + "): " +
						"Sequential: result = " + result + ", failed = " + failed + ". " +
						"Concurrent: result = " + entry.result + ", failed = " + entry.failed + ".");
			}
		}
		System.err.println();
	}
}

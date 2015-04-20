package stmbench7;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;

import jvstm.TopLevelTransaction;
import jvstm.Transaction;
import jvstm.scheduler.DoublyLinkedList;
import jvstm.scheduler.ScheduledTask;
import jvstm.scheduler.Scheduler;
import stmbench7.annotations.NonAtomic;
import stmbench7.backend.BackendFactory;
import stmbench7.core.DesignObjFactory;
import stmbench7.core.Operation;
import stmbench7.core.RuntimeError;
import stmbench7.correctness.invariants.CheckInvariantsOperation;
import stmbench7.correctness.opacity.SequentialReplayThread;
import stmbench7.correctness.opacity.StructureComparisonOperation;
import stmbench7.impl.NoSynchronizationInitializer;
import stmbench7.impl.jvstm.JVSTMStats;

/**
 * STMBench7 benchmark, the main program. Run with argument "-h" or "--help" to
 * see the syntax.
 * 
 * TODO: The class got too large and needs some careful refactoring. TODO: An
 * XML output of the benchmark results would be helpful.
 */
@NonAtomic
public class Benchmark {

    public static final String VERSION = "1.0(15.02.2011)";

    class BenchmarkParametersException extends Exception {
	private static final long serialVersionUID = 6341915439489283553L;

	public BenchmarkParametersException(String message, Exception cause) {
	    super(message + ": " + cause.toString());
	}

	public BenchmarkParametersException(String message) {
	    super(message);
	}

	public BenchmarkParametersException() {
	    super("");
	}
    }

    public static void main(String[] args) throws InterruptedException {
	Benchmark benchmark = null;

	try {
	    benchmark = new Benchmark(args, false);
	} catch (BenchmarkParametersException e) {
	    System.err.println(e.getMessage());
	    System.exit(1);
	}

	// benchmark.checkInvariants(true);

	if (Parameters.WarmUpEnabled) {
	    // make a first run to warm up the machine
	    Parameters.numSeconds = Parameters.WarmUpTime;
	    benchmark.start();
	    // benchmark.checkInvariants(false);
	    System.gc();

	    // clean up side effect made in the operations types
	    for (OperationType type : OperationType.values()) {
		type.count = 0;
		type.probability = 0;
	    }
	    // now, repeat the whole stuff
	    try {
		benchmark = new Benchmark(args, true);
		JVSTMStats.cleanUp();
	    } catch (BenchmarkParametersException e) {
		System.err.println(e.getMessage());
		System.exit(1);
	    }

	    // benchmark.checkInvariants(true);
	}

	benchmark.createInitialClone();
	benchmark.start();
	benchmark.checkInvariants(false);
	benchmark.checkOpacity();
	/* disabled to print only commit stats */
	benchmark.showTTCHistograms();
	benchmark.showStats();

    }

    private SynchMethodInitializer synchMethodInitializer;
    private boolean printTTCHistograms = false;
    private double[] operationCDF;
    private BenchThread[] benchThreads;
    private Thread[] threads;
    private Setup setup, setupClone;
    private double elapsedTime;

    private final boolean reexecution;
    public static boolean JVSTM;

    private Benchmark(String[] args, boolean reexecution) throws BenchmarkParametersException, InterruptedException {
	this.reexecution = reexecution;
	parseCommandLineParameters(args);
	if (!reexecution) {
	    printHeader();
	    printRunTimeParametersInformation();
	}
	generateOperationCDF();
	setupStructures();
    }

    private void printHeader() {
	String header = "The STMBench7 Benchmark (Java version)\n" + "A benchmark for comparing synchronization techniques\n"
		+ "Version: " + VERSION + "\n" + "More information: http://lpd.epfl.ch/site/research/tmeval\n"
		+ "Copyright (C) 2006-2008 LPD, I&C, EPFL (http://lpd.epfl.ch)\n"
		+ "Implemented by Michal Kapalka (http://kapalka.eu)\n"
		+ "Updated by Vincent Gramoli for compliance with the VELOX stack";

	// printLine('=');
	// System.out.println(header);
	// printLine('=');
	// System.out.println();
    }

    @SuppressWarnings("unchecked")
    private void parseCommandLineParameters(String[] args) throws BenchmarkParametersException {
	int argNumber = 0;
	String workload = null, synchType = null, stmInitializerClassName = null;

	while (argNumber < args.length) {
	    String currentArg = args[argNumber++];

	    try {
		if (currentArg.equals("--help") || currentArg.equals("-h")) {
		    printSyntax();
		    throw new BenchmarkParametersException();
		} else if (currentArg.equals("--no-rw-traversals"))
		    Parameters.longReadWriteTraversalsEnabled = false;
		else if (currentArg.equals("--no-traversals"))
		    Parameters.longTraversalsEnabled = false;
		else if (currentArg.equals("--no-sms"))
		    Parameters.structureModificationEnabled = false;
		else if (currentArg.equals("--ttc-histograms"))
		    printTTCHistograms = true;
		else if (currentArg.equals("--seq-replay"))
		    Parameters.sequentialReplayEnabled = true;
		else if (currentArg.equals("--ops")) {
		    String optionValue = args[argNumber++];
		    Parameters.numOps = Integer.parseInt(optionValue);
		} else if (currentArg.equals("--schd")) {
		    Parameters.useSchd = true;
		    Transaction.init(OperationId.values().length, args[argNumber++]);
		} else if (currentArg.equals("--limitOps")) {
		    Parameters.limitOps = true;
		    Parameters.opsUsed = args[argNumber++];
		} else if (currentArg.equals("--parNest"))
		    Parameters.parNest = true;
		else if (currentArg.equals("--dummyNest"))
		    Parameters.dummyNest = true;
		else if (currentArg.equals("--fakeDepth"))
		    Parameters.fakeDepth = Integer.parseInt(args[argNumber++]);
		else if (currentArg.equals("--")) {
		    Parameters.stmCommandLineParameters = new String[args.length - argNumber];
		    int stmArgNum = 0;
		    while (argNumber < args.length) {
			Parameters.stmCommandLineParameters[stmArgNum++] = args[argNumber++];
		    }
		    break;
		} else {
		    String optionValue = args[argNumber++];
		    if (currentArg.equals("-t")) {
			Parameters.numThreads = Integer.parseInt(optionValue);
			// barrier = new CountDownLatch(Parameters.numThreads);
		    } else if (currentArg.equals("-l"))
			Parameters.numSeconds = Integer.parseInt(optionValue);
		    else if (currentArg.equals("-w"))
			workload = optionValue;
		    else if (currentArg.equals("-g"))
			synchType = optionValue;
		    else if (currentArg.equals("-s"))
			stmInitializerClassName = optionValue;
		    else if (currentArg.equals("--warm-up")) {
			Parameters.WarmUpEnabled = true;
			try {
			    Parameters.WarmUpTime = Integer.parseInt(optionValue);
			} catch (NumberFormatException e) {
			    argNumber--;
			}
		    } else
			throw new BenchmarkParametersException("Invalid option: " + currentArg);
		}
	    } catch (IndexOutOfBoundsException e) {
		throw new BenchmarkParametersException("Missing value after option: " + currentArg);
	    } catch (NumberFormatException e) {
		throw new BenchmarkParametersException("Number expected after option: " + currentArg);
	    }
	}

	if (workload != null) {
	    if (workload.equals("ro"))
		Parameters.workloadType = Parameters.WorkloadType.READ_ONLY;
	    else if (workload.equals("r"))
		Parameters.workloadType = Parameters.WorkloadType.READ_DOMINATED;
	    else if (workload.equals("rw"))
		Parameters.workloadType = Parameters.WorkloadType.READ_WRITE;
	    else if (workload.equals("w"))
		Parameters.workloadType = Parameters.WorkloadType.WRITE_DOMINATED;
	    else
		throw new BenchmarkParametersException("Invalid workload type: " + workload);
	}

	if (synchType != null) {
	    if (synchType.equals("coarse"))
		Parameters.synchronizationType = Parameters.SynchronizationType.LOCK_COARSE;
	    else if (synchType.equals("medium"))
		Parameters.synchronizationType = Parameters.SynchronizationType.LOCK_MEDIUM;
	    else if (synchType.equals("fine"))
		Parameters.synchronizationType = Parameters.SynchronizationType.LOCK_FINE;
	    else if (synchType.equals("none"))
		Parameters.synchronizationType = Parameters.SynchronizationType.NONE;
	    else if (synchType.equals("stm"))
		Parameters.synchronizationType = Parameters.SynchronizationType.STM;
	    else
		throw new BenchmarkParametersException("Invalid lock granularity: " + synchType);
	}

	Class<? extends SynchMethodInitializer> synchMethodInitializerClass = null;
	switch (Parameters.synchronizationType) {
	case NONE:
	    synchMethodInitializerClass = ImplParameters.noSynchronizationInitializerClass;
	    break;
	case LOCK_COARSE:
	    synchMethodInitializerClass = ImplParameters.coarseGrainedLockingInitializerClass;
	    break;
	case LOCK_MEDIUM:
	    synchMethodInitializerClass = ImplParameters.mediumGrainedLockingInitializerClass;
	    break;
	case LOCK_FINE:
	    synchMethodInitializerClass = ImplParameters.fineGrainedLockingInitializerClass;
	    break;
	case STM:
	    if (stmInitializerClassName != null) {
		try {
		    synchMethodInitializerClass = (Class<? extends SynchMethodInitializer>) Class
			    .forName(stmInitializerClassName);
		} catch (ClassNotFoundException e) {
		    throw new BenchmarkParametersException("Error instantiating the STM initializer class", e);
		}
	    } else if (ImplParameters.defaultSTMInitializerClass != null) {
		synchMethodInitializerClass = ImplParameters.defaultSTMInitializerClass;
	    } else {
		throw new BenchmarkParametersException("STM initializer class name not given"
			+ " and a default class not specified" + " in ImpParameters.defaultSTMInitializerClass");
	    }
	    break;
	}
	try {
	    synchMethodInitializer = synchMethodInitializerClass.newInstance();
	} catch (Exception e) {
	    throw new BenchmarkParametersException("Error instantiating STM initializer class", e);
	}

	if (!reexecution) {
	    setFactoryInstances(synchMethodInitializer);
	}
    }

    private void setFactoryInstances(SynchMethodInitializer synchMethodInitializer) {
	JVSTM = synchMethodInitializer instanceof stmbench7.impl.jvstm.JVSTMInitializer;

	DesignObjFactory.setInstance(synchMethodInitializer.createDesignObjFactory());
	BackendFactory.setInstance(synchMethodInitializer.createBackendFactory());
	OperationExecutorFactory.setInstance(synchMethodInitializer.createOperationExecutorFactory());
	ThreadFactory.setInstance(synchMethodInitializer.createThreadFactory());
    }

    private void printRunTimeParametersInformation() {
	// printSection("Benchmark parameters");
	//
	// System.out.println("Number of threads: " + Parameters.numThreads +
	// "\n" +
	// "Length: " + Parameters.numSeconds + " s\n" +
	// "Workload: " + Parameters.workloadType + "\n" +
	// "Synchronization method: " + Parameters.synchronizationType + "\n" +
	// "Warm-Up " + (Parameters.WarmUpEnabled ? "enabled: " +
	// Parameters.WarmUpTime + " seconds" : "disabled") + "\n" +
	// "Long traversals " + (Parameters.longTraversalsEnabled ? "enabled" :
	// "disabled") + "\n" +
	// "Long Read-Write traversals " +
	// (Parameters.longReadWriteTraversalsEnabled ? "enabled" : "disabled")
	// + "\n" +
	// "Structural modification operations " +
	// (Parameters.structureModificationEnabled ? "enabled" : "disabled") +
	// "\n" +
	// "DesignObjFactory: " + DesignObjFactory.instance.getClass().getName()
	// + "\n" +
	// "BackendFactory: " + BackendFactory.instance.getClass().getName() +
	// "\n" +
	// "OperationExecutorFactory: " +
	// OperationExecutorFactory.instance.getClass().getName() + "\n" +
	// "ThreadFactory: " + ThreadFactory.instance.getClass().getName() +
	// "\n" +
	// "Sequential replay " + (Parameters.sequentialReplayEnabled ?
	// "enabled" : "disabled"));
	//
	// System.out.print("STM-specific parameters:");
	// if (Parameters.stmCommandLineParameters == null) {
	// System.out.print(" none");
	// } else {
	// for (String parameter : Parameters.stmCommandLineParameters) {
	// System.out.print(" " + parameter);
	// }
	// }
	// System.out.println("\n");
    }

    private void generateOperationCDF() {
	double shortTraversalsRatio = Parameters.ShortTraversalsRatio / 100.0, operationsRatio = Parameters.OperationsRatio / 100.0;

	double traversalsRatio, structuralModificationsRatio;
	if (Parameters.longTraversalsEnabled)
	    traversalsRatio = Parameters.TraversalsRatio / 100.0;
	else
	    traversalsRatio = 0;

	if (Parameters.structureModificationEnabled)
	    structuralModificationsRatio = Parameters.StructuralModificationsRatio / 100.0;
	else
	    structuralModificationsRatio = 0;

	double readOnlyOperationsRatio = Parameters.workloadType.readOnlyOperationsRatio / 100.0, updateOperationsRatio = 1.0 - readOnlyOperationsRatio;

	/* Migrated from the original STMBench7 implementation for the JVSTM */
	double roTraversalsRatio = traversalsRatio * readOnlyOperationsRatio;
	double rwTraversalsRatio = (Parameters.longReadWriteTraversalsEnabled ? (traversalsRatio * updateOperationsRatio) : 0);

	double sumRatios = roTraversalsRatio + rwTraversalsRatio + shortTraversalsRatio + operationsRatio
		+ structuralModificationsRatio * updateOperationsRatio;

	roTraversalsRatio /= sumRatios;
	rwTraversalsRatio /= sumRatios;
	shortTraversalsRatio /= sumRatios;
	operationsRatio /= sumRatios;
	structuralModificationsRatio /= sumRatios;

	OperationId[] operations = OperationId.values();
	for (OperationId operation : operations)
	    operation.getType().count++;

	OperationType.TRAVERSAL.probability = rwTraversalsRatio / OperationType.TRAVERSAL.count;
	OperationType.TRAVERSAL_RO.probability = roTraversalsRatio / OperationType.TRAVERSAL_RO.count;
	OperationType.SHORT_TRAVERSAL.probability = shortTraversalsRatio * updateOperationsRatio
		/ OperationType.SHORT_TRAVERSAL.count;
	OperationType.SHORT_TRAVERSAL_RO.probability = shortTraversalsRatio * readOnlyOperationsRatio
		/ OperationType.SHORT_TRAVERSAL_RO.count;
	OperationType.OPERATION.probability = operationsRatio * updateOperationsRatio / OperationType.OPERATION.count;
	OperationType.OPERATION_RO.probability = operationsRatio * readOnlyOperationsRatio / OperationType.OPERATION_RO.count;
	OperationType.STRUCTURAL_MODIFICATION.probability = structuralModificationsRatio * updateOperationsRatio
		/ OperationType.STRUCTURAL_MODIFICATION.count;

	if (!reexecution) {
	    // System.out.println("Operation ratios [%]:");
	    // for (OperationType type : OperationType.values()) {
	    // System.out.println(alignText(type.toString(), 23) + ": " +
	    // formatDouble(type.probability * type.count * 100));
	    // }
	    // System.out.println();
	}

	double[] operationProbabilities = new double[operations.length];
	for (OperationId operation : operations) {
	    double operationProbability = operation.getType().probability;
	    operationProbabilities[operation.ordinal()] = operationProbability;
	}

	operationCDF = new double[operations.length];
	double prevProbValue = 0;
	for (int opNum = 0; opNum < operations.length; opNum++) {
	    operationCDF[opNum] = prevProbValue + operationProbabilities[opNum];
	    prevProbValue = operationCDF[opNum];
	}
	operationCDF[operations.length - 1] = 1.0; // to avoid rounding errors
    }

    OperationExecutor[] operations = new OperationExecutor[OperationId.values().length];

    private void setupStructures() throws InterruptedException {
	setup = new Setup();

	benchThreads = new BenchThread[Parameters.numThreads];
	threads = new Thread[Parameters.numThreads];

	createOperations(setup);

	for (short threadNum = 0; threadNum < Parameters.numThreads; threadNum++) {
	    benchThreads[threadNum] = new BenchThread(setup, operationCDF, threadNum, operations);
	    threads[threadNum] = ThreadFactory.instance.createThread(benchThreads[threadNum]);
	}

	Scheduler.submitTasks(createTasks());
    }

    static DoublyLinkedList<ScheduledTask> result = null;

    private DoublyLinkedList<ScheduledTask> createTasks() {
	if (result == null) {
	    result = new DoublyLinkedList<ScheduledTask>();
	    // Generate next n operations
	    for (int i = 0; i < Parameters.numOps; i++) {

		// See getNextOperationNumber in BenchThread
		double whichOperation = ThreadRandom.nextDouble();
		int operationNumber = 0;
		while (whichOperation >= operationCDF[operationNumber])
		    operationNumber++;

		result.add(operations[operationNumber]);
	    }
	}
	return result;
    }

    private void checkInvariants(boolean initial) throws InterruptedException {
	// if (initial)
	// System.err.println("Checking invariants (initial data structure):");
	// else
	// System.err.println("Checking invariants (final data structure):");

	Operation checkInvariantsOperation = new CheckInvariantsOperation(setup, initial);
	OperationExecutorFactory.executeSequentialOperation(checkInvariantsOperation);
    }

    private void createInitialClone() throws InterruptedException {
	if (!Parameters.sequentialReplayEnabled)
	    return;

	System.err.println("Cloning the initial data structure...");
	ThreadRandom.reset();
	setFactoryInstances(new NoSynchronizationInitializer());
	setupClone = new Setup();
	setFactoryInstances(synchMethodInitializer);
	System.err.println("Cloning completed.");

	System.err.println("Checking if the clone is the same as the data structure...");
	StructureComparisonOperation structureComparisonOperation = new StructureComparisonOperation(setup, setupClone);
	OperationExecutorFactory.executeSequentialOperation(structureComparisonOperation);
	System.err.println("Check OK.");
    }

    private void start() throws InterruptedException {
	ThreadRandom.startConcurrentPhase();

	long startTime = System.currentTimeMillis();

	for (Thread thread : threads)
	    thread.start();

	// Thread.sleep(Parameters.numSeconds * 1000);
	//
	// for (BenchThread thread : benchThreads)
	// thread.stopThread();

	// int totalActive;
	// int runnableActive;
	// do {
	// totalActive = 0;
	// runnableActive = 0;
	// for (int i = 0; i < benchThreads.length; i++) {
	// if (threads[i].isAlive()) {
	// totalActive++;
	// }
	// if (benchThreads[i].working) {
	// runnableActive++;
	// }
	// }
	// samples.add(totalActive);
	// samples.add(runnableActive);
	// barrier.await(sleepInterval, TimeUnit.MILLISECONDS);
	// if (totalActive == 0) {
	// break;
	// }
	// } while (true);

	for (Thread thread : threads)
	    thread.join();

	long endTime = System.currentTimeMillis();
	elapsedTime = (endTime - startTime);
    }

    // protected static final int sleepInterval = 100;
    // protected static final List<Integer> samples = new LinkedList<Integer>();
    // public static CountDownLatch barrier;

    protected void createOperations(Setup setup) {
	int i = 0;
	for (OperationId operationDescr : OperationId.values()) {
	    Class<? extends Operation> operationClass;
	    int operationIndex;

	    if (!Parameters.limitOps) {
		operationClass = operationDescr.getOperationClass();
		operationIndex = operationDescr.ordinal();
	    } else {
		if (Parameters.opsUsed.equals("T2a")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T2a;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("T2b")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T2b;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("T2c")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T2c;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("T3a")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T3a;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("T3b")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T3b;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("T3c")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T3c;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("T5")) {
		    operationIndex = operationDescr.ordinal();
		    operationDescr = OperationId.T5;
		    operationClass = operationDescr.getOperationClass();
		} else if (Parameters.opsUsed.equals("Tx")) {
		    if (i == 0) {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T2a;
			operationClass = operationDescr.getOperationClass();
			i = 1;
		    } else if (i == 1) {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T2b;
			operationClass = operationDescr.getOperationClass();
			i = 2;
		    } else if (i == 2) {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T2c;
			operationClass = operationDescr.getOperationClass();
			i = 3;
		    } else if (i == 3) {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T5;
			operationClass = operationDescr.getOperationClass();
			i = 4;
		    } else if (i == 4) {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T3a;
			operationClass = operationDescr.getOperationClass();
			i = 5;
		    } else if (i == 5) {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T3b;
			operationClass = operationDescr.getOperationClass();
			i = 6;
		    } else {
			operationIndex = operationDescr.ordinal();
			operationDescr = OperationId.T3c;
			operationClass = operationDescr.getOperationClass();
			i = 0;
		    }
		} else {
		    throw new Error("This operation is not available: " + Parameters.opsUsed);
		}
	    }

	    try {
		Constructor<? extends Operation> operationConstructor = operationClass.getConstructor(Setup.class);
		Operation operation = operationConstructor.newInstance(setup);

		operations[operationIndex] = OperationExecutorFactory.instance.createOperationExecutor(operation);
		assert (operation.getOperationId().getOperationClass().equals(operationClass));
	    } catch (Exception e) {
		throw new RuntimeError("Error while creating operation " + operationDescr, e);
	    }
	}
    }

    private void checkOpacity() throws InterruptedException {
	if (!Parameters.sequentialReplayEnabled)
	    return;

	System.err.println("\nReplaying the execution in a single thread...");

	ArrayList<BenchThread.ReplayLogEntry> replayLog = new ArrayList<BenchThread.ReplayLogEntry>();
	for (BenchThread thread : benchThreads)
	    replayLog.addAll(thread.replayLog);
	BenchThread.ReplayLogEntry[] replayLogArray = replayLog.toArray(new BenchThread.ReplayLogEntry[0]);
	Arrays.sort(replayLogArray);
	replayLog = new ArrayList<BenchThread.ReplayLogEntry>();
	for (BenchThread.ReplayLogEntry entry : replayLogArray)
	    replayLog.add(entry);
	replayLogArray = null;

	setFactoryInstances(new NoSynchronizationInitializer());
	ThreadRandom.startSequentialReplayPhase();
	SequentialReplayThread seqThread = new SequentialReplayThread(setupClone, operationCDF, replayLog);
	seqThread.run();
	setFactoryInstances(synchMethodInitializer);

	StructureComparisonOperation structureComparisonOperation = new StructureComparisonOperation(setup, setupClone);
	OperationExecutorFactory.executeSequentialOperation(structureComparisonOperation);
	System.err.println("\nOpacity ensured.\n");
    }

    private void showTTCHistograms() {
	if (!printTTCHistograms)
	    return;

	printSection("TTC histograms");

	OperationId[] operations = OperationId.values();
	for (OperationId operation : operations) {
	    System.out.print("TTC histogram for " + operation + ":");

	    for (int ttc = 0; ttc <= Parameters.MAX_LOW_TTC; ttc++) {
		int count = 0;
		for (BenchThread thread : benchThreads) {
		    count += thread.operationsTTC[operation.ordinal()][ttc];
		}
		System.out.print(" " + ttc + "," + count);
	    }

	    for (int logTtcIndex = 0; logTtcIndex < Parameters.HIGH_TTC_ENTRIES; logTtcIndex++) {
		int count = 0;
		for (BenchThread thread : benchThreads) {
		    count += thread.operationsHighTTCLog[operation.ordinal()][logTtcIndex];
		}

		int ttc = logTtcIndex2Ttc(logTtcIndex);
		System.out.print(" " + ttc + "," + count);
	    }

	    System.out.println();
	}
	System.out.println();
    }

    private int logTtcIndex2Ttc(double logTtcIndex) {
	return (int) ((Parameters.MAX_LOW_TTC + 1) * Math.pow(Parameters.HIGH_TTC_LOG_BASE, logTtcIndex));
    }

    private void showStats() {
	// printSection("Detailed results");

	OperationId[] operations = OperationId.values();
	for (OperationId operation : operations) {
	    // System.out.print("Operation " + alignText(operation.toString(),
	    // 4) + ":   ");

	    int opNumber = operation.ordinal();
	    int successful = 0, failed = 0, maxttc = 0;
	    for (BenchThread thread : benchThreads) {
		successful += thread.successfulOperations[opNumber];
		failed += thread.failedOperations[opNumber];
		maxttc = Math.max(maxttc, computeMaxThreadTTC(thread, opNumber));
	    }

	    // System.out.println("successful = " + successful + "\tmaxttc = " +
	    // maxttc + "\tfailed = " + failed);

	    OperationType operationType = operation.getType();
	    operationType.successfulOperations += successful;
	    operationType.failedOperations += failed;
	    operationType.maxttc = Math.max(operationType.maxttc, maxttc);
	}
	// System.out.println();

	// printSection("Sample errors (operation ratios [%])");

	int totalSuccessful = 0, totalFailed = 0;
	for (OperationType type : OperationType.values()) {
	    totalSuccessful += type.successfulOperations;
	    totalFailed += type.failedOperations;
	}

	double totalError = 0, totalTError = 0;
	for (OperationType type : OperationType.values()) {
	    double expectedRatio = type.probability * type.count * 100.0;
	    double realRatio = (double) type.successfulOperations / (double) totalSuccessful * 100.0;
	    double error = Math.abs(realRatio - expectedRatio);
	    double tRealRatio = (double) (type.successfulOperations + type.failedOperations)
		    / (double) (totalSuccessful + totalFailed) * 100.0;
	    double tError = Math.abs(tRealRatio - expectedRatio);
	    // System.out.println(alignText(type.toString(), 23) + ":  " +
	    // "expected = " + formatDouble(expectedRatio) +
	    // "\tsuccessful = " + formatDouble(realRatio) +
	    // "\terror = " + formatDouble(error) +
	    // "\t(total = " + formatDouble(tRealRatio) +
	    // "\terror = " + formatDouble(tError) + ")");
	    totalError += error;
	    totalTError += tError;
	}
	// System.out.println();
	//
	// printSection("Summary results");

	int total = totalSuccessful + totalFailed;
	for (OperationType type : OperationType.values()) {
	    int totalTypeOperations = type.successfulOperations + type.failedOperations;
	    // System.out.println(alignText(type.toString(), 23) + ":  " +
	    // "successful = " + type.successfulOperations +
	    // "\tmaxttc = " + type.maxttc +
	    // "\tfailed = " + type.failedOperations +
	    // "\ttotal = " + totalTypeOperations);
	}
	// System.out.println();
	//
	// System.out.println("Total sample error: " + formatDouble(totalError)
	// + "%" +
	// " (" + formatDouble(totalTError) + "% including failed)");
	// System.out.println("Total throughput: " +
	// formatDouble(totalSuccessful / (elapsedTime/1000)) + " op/s");
	System.out.print(formatDouble(elapsedTime) + "\n" + Transaction.getReport());// +
							   // Transaction.getReport());
							   // String
							   // sampleResult
							   // = "";
	// int lastSample = 0;
	// for (int i = 0; i < samples.size(); i += 2) {
	// sampleResult += "" + lastSample + " " + samples.get(i) + " " +
	// samples.get(i + 1) + "\n";
	// lastSample += sleepInterval;
	// }
	// System.out.print(sampleResult);
	// String threadsResult = "";
	// for (BenchThread thread : benchThreads) {
	// threadsResult += "\n" + thread.executedTxs;
	// }
	// System.out.println(threadsResult);

	if (JVSTM) {
	    JVSTMStats.printStats();
	}
    }

    private int computeMaxThreadTTC(BenchThread thread, int opNumber) {
	for (int logTtcIndex = Parameters.HIGH_TTC_ENTRIES - 1; logTtcIndex >= 0; logTtcIndex--) {
	    if (thread.operationsHighTTCLog[opNumber][logTtcIndex] > 0) {
		return logTtcIndex2Ttc(logTtcIndex);
	    }
	}

	for (int ttc = Parameters.MAX_LOW_TTC; ttc >= 0; ttc--) {
	    if (thread.operationsTTC[opNumber][ttc] > 0) {
		return ttc;
	    }
	}

	return 0; // operation never completed with success
    }

    private void printSyntax() {
	String syntax = "Syntax:\n" + "java stmbench7.Benchmark [options] [-- stm-specific options]\n\n" + "Options:\n"
		+ "\t-t numThreads -- set the number of threads (default: 1)\n"
		+ "\t-l length     -- set the length of the benchmark, in seconds (default: 10)\n"
		+ "\t-w ro|r|rw|w  -- set the workload: ro = read-only, r = read-dominated (default)\n"
		+ "\t                                   rw = read-write, w = write-dominated\n"
		+ "\t-g coarse|medium|fine|none|stm -- set synchronization method (default: coarse)\n"
		+ "\t-s stmInitializerClass         -- set STM initializer class (default: none)\n"
		+ "\t--no-traversals  -- do not use long traversals\n"
		+ "\t--no-rw-traversals  -- do not use read-write traversals\n"
		+ "\t--no-sms         -- do not use structural modification operations\n"
		+ "\t--warm-up seconds        -- Warms-up de benchmark (default: 60 seconds)\n"
		+ "\t--seq-replay 	-- replay the execution in a single thread\n"
		+ "\t                    (checks for opacity violations)\n"
		+ "\t--ttc-histograms -- print TTC histograms to stdout\n\n"
		+ "Note: the benchmark needs a lot of lot of memory, so the -Xmx option of Java\n" + "might be necessary.";
	System.err.println(syntax);
    }

    private void printSection(String title) {
	printLine('-');
	System.out.println(title);
	printLine('-');
    }

    private void printLine(char ch) {
	StringBuffer line = new StringBuffer(79);
	for (int i = 0; i < 79; i++)
	    line.append(ch);
	System.out.println(line);
    }

    private String alignText(String text, int width) {
	int textLen = text.length();
	int padding = width - textLen;
	if (padding < 0)
	    throw new RuntimeError("alignText: width too small!");

	StringBuffer output = new StringBuffer(width);
	for (int i = 0; i < padding; i++)
	    output.append(' ');
	output.append(text);

	return output.toString();
    }

    private String formatDouble(double number) {
	Formatter formatter = new Formatter();
	formatter.format("%3.2f", number);
	return formatter.toString();
    }
}

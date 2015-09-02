package stmbench7.impl.jvstm;

import stmbench7.Parameters;

public class JVSTMStats {
    private static long[] rwTransactions = new long[Parameters.numTopLevel];
    private static long[] roTransactions = new long[Parameters.numTopLevel];
    private static long nestedRw = 0;
    private static long nestedRo = 0;
    private static long[] conflicts = new long[Parameters.numTopLevel];
    private static long nestedConflicts = 0;
    private static long[] restarts = new long[Parameters.numTopLevel];
    private static long nestedRestarts = 0;
    

    public static void cleanUp() {
	rwTransactions = new long[Parameters.numTopLevel];
	roTransactions = new long[Parameters.numTopLevel];
	conflicts = new long[Parameters.numTopLevel];
	restarts = new long[Parameters.numTopLevel];
    }

    public static void noteTransaction(boolean readOnly, Short id) {
	if (readOnly)
	    noteReadOnlyTransaction(id);
	else
	    noteReadWriteTransaction(id);
    }
    
    public static void noteNestedTransaction(boolean readOnly) {
    	if(readOnly) {
    		nestedRo++;
    	} else {
    		nestedRw++;
    	}
    }

    private static void noteReadWriteTransaction(int id) {
	rwTransactions[id] = rwTransactions[id] + 1;
    }

    private static void noteReadOnlyTransaction(int id) {
	roTransactions[id] = roTransactions[id] + 1;
    }

    public static void noteConflict(int id) {
	conflicts[id] = conflicts[id] + 1;
    }
    
    public static void noteNestedConflict() {
    	nestedConflicts++;
    }

    public static void noteRestart(int id) {
	restarts[id] = restarts[id] + 1;
    }
    
    public static void noteNestedRestart() {
    	nestedRestarts++;
    }

    private static long getRwTransactions() {
	long res = 0;

	for (int i = 0; i < rwTransactions.length; ++i) {
	    res += rwTransactions[i];
	}

	return res + nestedRw;
    }

    private static long getRoTransactions() {
	long res = 0;

	for (int i = 0; i < roTransactions.length; ++i) {
	    res += roTransactions[i];
	}

	return res;
    }

    private static long getConflicts() {
	long res = 0;

	for (int i = 0; i < conflicts.length; ++i) {
	    res += conflicts[i];
	}

	return res;
    }

    private static long getRestarts() {
	long res = 0;

	for (int i = 0; i < restarts.length; ++i) {
	    res += restarts[i];
	}

	return res;
    }

    public static void prlongStats() {
//	System.out.prlongf("RW = %d, RO = %d, Conflicts = %d (%f%%), Restarts = %d (%f%%)\n", getRwTransactions(),
//		getRoTransactions(), getConflicts(), ((getConflicts() * 100.0) / getRwTransactions()), getRestarts(),
//		((getRestarts() * 100.0) / getRwTransactions()));
    }
}

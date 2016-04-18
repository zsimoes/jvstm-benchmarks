#!/bin/bash

# Each script bench.sh has available the following variables:
#
# JAVA: path to the JVM
# JAVA_OPTS: configuration options to pass to the JVM
# CLASSPATH: should already include the application code and JVSTM.
#
# THREADS: the list of threads to test
# nthreads: the current number of threads to test
# SCRIPT_NAME: the name given to this benchmark script
# jvstm_basename: the JVSTM being tested (without .jar extension)
# OUTDIR: output directory within the RESULTS dir in the form of $SCRIPT_NAME/$jvstm_basename
#
# It is expected that this script will write its output to OUTDIR

method="stm"
load="rw"
duration="8"

echo "${JAVA} ${JAVA_OPTS} -Dpolicy=$policy -Dfilename=sb7-throughput-t${nthreads}-w$load -Dfolder=$OUTDIR -cp ${CLASSPATH} stmbench7.Benchmark -g $method -s stmbench7.impl.jvstm.JVSTMInitializer -w $load -l $duration -t ${nthreads} --no-traversals --no-sms > $OUTDIR/notrav-${load}-nosms-${nthreads}.txt 2>&1"

${JAVA} ${JAVA_OPTS} -DmaxThreads=$t -Dpolicy=$policy -Dfilename=sb7-throughput-t${nthreads}-w$load -Dfolder=$OUTDIR -cp ${CLASSPATH} stmbench7.Benchmark -g $method -s stmbench7.impl.jvstm.JVSTMInitializer -w $load -l $duration -t ${nthreads} --no-traversals --no-sms > $OUTDIR/notrav-${load}-nosms-${nthreads}.txt 2>&1


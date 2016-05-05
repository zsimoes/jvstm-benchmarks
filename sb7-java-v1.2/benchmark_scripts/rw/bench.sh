#!/bin/bash

# Each script bench.sh has available the following variables:
#
# JAVA: path to the JVM
# JAVA_OPTS: configuration options to pass to the JVM
# CLASSPATH: should already include the application code and JVSTM.
#
# top: number of top-level threads
# nest: number of nested threads
# SCRIPT_NAME: the name given to this benchmark script
# jvstm_basename: the JVSTM being tested (without .jar extension)
# OUTDIR: output directory within the RESULTS dir in the form of $SCRIPT_NAME/$jvstm_basename
#
# It is expected that this script will write its output to OUTDIR

load="rw"
duration="20"

echo "  Starting stmbench7 -w $load -l $duration -t ${top} -n ${nest} --no-traversals --no-sms // RESULTFILE: $OUTDIR/${load}-${policy}.txt"

${JAVA} ${JAVA_OPTS} -DNoStats=true -DPolicy=$policy -DLogFile=/dev/null -DInterval=100 -DMaxThreads=48 -DInitialConfig="${top},${nest}" -DContention=${load} -DMeasurementType=real -cp ${CLASSPATH} stmbench7.Benchmark -g stm -s stmbench7.impl.jvstm.JVSTMInitializer -w $load -l $duration -t ${top} -n ${nest} --no-traversals --no-sms > "$OUTDIR/${load}-${policy}.txt" 2>/dev/null
echo "  Done."
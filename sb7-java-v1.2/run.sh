#!/bin/bash

. ./benchmark_scripts.conf

jvstm="jvstm.jar"
jvstm_basename=`basename ${jvstm} .jar`

JAVA_OPTS="-Xms1024m -Xmx1800m" 
JAVA="java"

cp ../lib/"$jvstm" lib/jvstm.jar
ls lib
echo "----------------------------"
echo "JVSTM="$jvstm_basename 
echo "----------------------------"
ant clean compile

CLASSPATH="build:lib/jvstm.jar"

THREADS[1]=1
THREADS[2]=2
THREADS[3]=4
THREADS[4]=8
THREADS[5]=16
THREADS[6]=24
THREADS[7]=32
THREADS[8]=48

date1=$(date +"%s")
for bench_script in $BENCH_SCRIPTS_TO_RUN; do
	SCRIPT_NAME=`basename $bench_script`
	OUTDIR=".$RESULTS/$SCRIPT_NAME/$jvstm_basename"
	
	mkdir -p "$OUTDIR"

	# run the bench.sh
	for nthreads in ${THREADS[@]}; do
		echo "##Running $bench_script with $nthreads threads"
		. ./$bench_script/bench.sh
		echo "##done"
	done

	# run the process_results.sh
	. ./$bench_script/process_results.sh
done

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "All done. $(($diff / 60)) minutes elapsed."

# plot the results per benchmark script
#for bench_script in $BENCH_SCRIPTS_TO_RUN; do
#    SCRIPT_NAME=`basename $bench_script`
#
#    . ./$bench_script/plot.sh
#done



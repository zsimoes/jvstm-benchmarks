#!/bin/bash

# $1 - jvstm jar basename
# $2 - jvstm tuning policy
. ./benchmark_scripts.conf

jvstm_basename=$1
jvstm="$1.jar"
policy=$2

JAVA_OPTS="-Xms1024m -Xmx1800m" 
JAVA="java"

cp ../lib/"$jvstm" lib/jvstm.jar
echo "----------------------------"
echo "LeeTM :: JVSTM="$jvstm_basename 
echo "----------------------------"
ant clean compile
rc=$?
if [ $rc -ne "0" ]
then
	echo "---- LeeTM: ANT Compile error (Exit code $rc). Exiting"
	echo
	exit
fi

CLASSPATH="build:lib/jvstm.jar"
RESULTS="../EXHAUSTIVEresults-Lee"

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
	OUTDIR="$RESULTS/$SCRIPT_NAME/$jvstm_basename-$policy"
	
	mkdir -p "$OUTDIR"

	# run the bench.sh
	for nthreads in ${THREADS[@]}; do
		echo "##Running Lee $bench_script with $nthreads threads"
		. ./$bench_script/bench.sh
		#echo "##done"
	done

	# run the process_results.sh
	#. ./$bench_script/process_results.sh
done

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "Lee: All done. $(($diff / 60)) minutes elapsed."

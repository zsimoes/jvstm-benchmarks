#!/bin/bash

. ./defaultconfig.sh

BENCH_SCRIPTS_TO_RUN=`ls -d benchmark_scripts/*`

JAVA_OPTS="-Xms4G -Xmx8G" 
JAVA="java"
load="rw"
duration="20"

echo "----------------------------"
echo "STMBench7 :: JVSTM="${jvstm}".jar"
echo "----------------------------"
ant clean compile
rc=$?
if [ $rc -ne "0" ]
then
	echo "---- STMBench7: ANT Compile error (Exit code $rc). Exiting"
	echo
	exit
fi

CLASSPATH="build:lib/*"
RESULTS="../results-sb7"


date1=$(date +"%s")
for loadType in ${!loads[@]}; do
	echo "Load: $loadType"
	
	OUTDIR="$RESULTS/exhaustive"
	mkdir -p "$OUTDIR"
	FILE="${OUTDIR}/${loadType}.exhaustive"
	#clear file:
	> $FILE
	
	for t in $(seq 1 $maxCores); do
		echo -e "\tTop-Levels: $t"
			for n in $(seq 1 $maxCores); do
				
				#if top-level*nested is greater than the number of cores continue.
				if [ $((n * t)) -gt $maxCores ]
				then
					continue
				fi
				
				echo -e "\t\tNested: $n"
				
				sum=0
				echo -n -e "\t\t\tAttempts "
				for a in $(seq 1 $attempts); do
				
					# run the benchmark
					echo -n -e "$a "
					tempResult=`${JAVA} ${JAVA_OPTS} -DNoStats=true -DPolicy=Default -DLogFile=/dev/null -DInterval=100 -DMaxThreads=$maxCores -DInitialConfig="${t},${n}" -DContention=${load} -DMeasurementType=real -cp ${CLASSPATH} stmbench7.Benchmark -g stm -s stmbench7.impl.jvstm.JVSTMInitializer -w $load -l $duration -t ${t} -n ${n} --no-traversals --no-sms 2>/dev/null | grep "Total throughput" | cut -d\  -f 3`
					
					#I have to use AWK, stupid bash doesn't do float arithmetic
					sum="`echo "$sum $tempResult" | awk '{printf "%.2f", $1+$2}'`"
					
					
				done #attempts
				#average results from each attempt and save to file with coordinates
				echo
				avg="`echo "$sum $attempts" | awk '{printf "%.2f", $1/$2}'`"
				echo -e "\t\tCoords: [$t,$n]  Avg: $avg"
				echo $t $n $avg >> ${FILE}
			done #nested
			echo >> ${FILE}
	done #topLevel
	echo "Round finished: $loadType"
done

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "SB7: All done. $(($diff / 60)) minutes elapsed."



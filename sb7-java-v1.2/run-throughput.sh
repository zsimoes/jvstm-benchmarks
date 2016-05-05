#!/bin/bash

. ./defaultconfig.sh

BENCH_SCRIPTS_TO_RUN=`ls -d benchmark_scripts/*`

JAVA_OPTS="-Xms4G -Xmx8G" 
JAVA="java"

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
#BENCH_SCRIPT_TO_RUN are equivalent to the varous load types for sb7, eg. r, w, rw
for bench_script in $BENCH_SCRIPTS_TO_RUN; do
	SCRIPT_NAME=`basename $bench_script`
		
	configSize=${#configs[@]}
	for c in $(seq 1 ${#configs[@]}); do
		printf "##\t Config: ${configs[$c]} (${c} in ${configSize}) \n "
		configString=${configs[$c]}
		# split config string on ";"
		config=(${configString/;/ })
		top=${config[0]}
		nest=${config[1]}
		
		OUTDIR="$RESULTS/throughput/$SCRIPT_NAME/jvstm-${top}x${nest}"
		mkdir -p "$OUTDIR"
			
		policySize=${#policies[@]}
		let policyCount=1
		for policy in ${policies[@]}; do
			echo -e "##\t\tPolicy: ${policy} (${policyCount} in ${policySize})"
			let policyCount++

			# run the bench.sh
			. ./$bench_script/bench.sh
		done
	done

	# run the process_results.sh
	. ./$bench_script/process_results.sh
done
exit

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "SB7: All done. $(($diff / 60)) minutes elapsed."
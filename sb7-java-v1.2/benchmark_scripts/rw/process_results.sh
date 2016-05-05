#!/bin/bash

# Each script process_results.sh has available the following variables:
#
# configs: the list of thread combinations used to test
# policies: an array with the list of polcies
#
# SCRIPT_NAME: the name given to this benchmark script
# jvstm_basename: the JVSTM being tested (without .jar extension)
# OUTDIR: output directory within the RESULTS dir in the form of $SCRIPT_NAME/$jvstm_basename
#
# It is expected that this script will write its output inside OUTDIR

for c in $(seq 1 ${#configs[@]}); do
	configString=${configs[$c]}
	# split config string on ";"
	config=(${configString/;/ })
	top=${config[0]}
	nest=${config[1]}
	OUTDIR="$RESULTS/$SCRIPT_NAME/jvstm-${top}x${nest}"
	printf "\tConfig: ${top}x${nest} OUTDIR: ${OUTDIR} \n"  >&2
		
	for policy in ${policies[@]}; do

		FILE="$OUTDIR/${load}-${policy}.txt"
		printf "\t\tPolicy: ${policy} FILE: ${FILE} \n" >&2
		#echo "FILE: $FILE"
		TOTAL_ERROR=`grep "sample error" $FILE | cut -d\  -f 4`
		TOTAL_ERROR_INCL_FAILED=`grep "sample error" $FILE | cut -d\( -f 2 | cut -d\  -f1`
		THROUGHPUT=`grep Total\ throughput $FILE | cut -d\  -f 3`
		THROUGHPUT_INCL_FAILED=`grep Total\ throughput $FILE | cut -d\  -f 5 | cut -d\( -f2`
		TIME=`grep Elapsed\ time: $FILE | cut -d\  -f3`

		### Simoes 04/2016 - SB7 no longer provides the following results, commented this section out
		#STM_AUX=`grep Conflicts $FILE`
		#RW=`echo $STM_AUX | cut -d\  -f3 | cut -d, -f1`
		#RO=`echo $STM_AUX | cut -d\  -f6 | cut -d, -f1`
		#CONFLICTS=`echo $STM_AUX | cut -d\  -f9`
		#CONFLICTS_PERCENT=`echo $STM_AUX | cut -d\  -f10 | cut -d, -f1 | cut -d\( -f2 | cut -d\) -f1`
		#RESTARTS=`echo $STM_AUX | cut -d\  -f13 | cut -d, -f1`
		#RESTARTS_PERCENT=`echo $STM_AUX | cut -d\  -f14 | cut -d, -f1 | cut -d\( -f2 | cut -d\) -f1`
		#ABORTS=`echo $STM_AUX | cut -d\  -f17 | cut -d, -f1`
		#echo "$load,$thread,$TOTAL_ERROR,$TOTAL_ERROR_INCL_FAILED,$THROUGHPUT,$THROUGHPUT_INCL_FAILED,$TIME,$RW,$RO,$CONFLICTS,$CONFLICTS_PERCENT,$RESTARTS,$RESTARTS_PERCENT,$ABORTS"
		###
		
		echo "$load,${top}x${nest},$policy,$TOTAL_ERROR,$TOTAL_ERROR_INCL_FAILED,$THROUGHPUT,$THROUGHPUT_INCL_FAILED,$TIME"
	done > "${OUTDIR}/${top}x${nest}-prog.all"
	printf "\tDONE: ${OUTDIR}/${top}x${nest}-prog.all \n" >&2
done 

#! /bin/bash

# $1 jvstm basename - assumed to be in "lib" folder, with a .jar extension - defaults to "jvstm"
# $2 maximum core/thread count - defaults to 48

jvstm=${1:-"jvstm"}
max_cores=${2:-48}

. ./defaultconfig.sh

base_dir="/home/simoes/new_bench"
wd="/home/simoes/new_bench/JVacation"

if [ ! -d "$wd" ]; then
  wd="/home/jsimoes/new_bench/JVacation"
  base_dir="/home/jsimoes/new_bench"
fi

timestamp=`date +%Y-%m-%d`

jvstmlib="$base_dir/lib/${jvstm}.jar"
resultFolder="$base_dir/results-JVacation/${jvstm}-${timestamp}/execution" 
vacNormal="stamp/vacation/jvstm"

echo "----------------------------"
echo "JVacation :: JVSTM="$jvstm_basename 
echo "----------------------------"

mkdir -p $resultFolder
cd $resultFolder
rm -f *.data
cd $wd/src

#rm -f $vacNormal/*.class
#javac -cp $jvstmlib:. $vacNormal/Vacation.java
#rc1=$?

#if [ $rc1 -ne "0" ];
#then
#	echo "---- JVacation: ANT Compile error. Exiting"
#	echo
#	exit
#fi

runjava="java -Xms4096m -Xmx8192m -cp $jvstmlib:."

attempts=3

date1=$(date +"%s")
for contention_key in "${!contention[@]}"; 
do
	contention_value="${contention["$contention_key"]}"
	echo "Contention Type: ${contention_key}"
	
	for policy in ${policies[@]}
	do
		echo -e "\tPolicy: ${policy}"
		file="${resultFolder}/execution-${policy}-${contention_key}.log";
		> "$file";
		
        for c in $(seq 1 ${#configs[@]})
        do
			printf "\t\tConfig: ${configs[$c]} \n"
			configString=${configs[$c]}
			# split config string on ";"
			config=(${configString/;/ })
			top=${config[0]}
			nest=${config[1]}
			sum=0
			for attempt in $(seq 1 $attempts)
			do
				if [ "$configString" == "1;1" ] 
				then
					echo "$runjava -DMaxThreads=${max_cores} -DInitialConfig=$top,$nest -DContention=$contention_key -DPolicy=$policy -DMeasurementType=real -DInterval=100 -DLogFile=/dev/null $vacNormal/Vacation -c $top ${contention_value} -nest false -sib $nest -updatePar false"
					let sum+=`$runjava -DMaxThreads=${max_cores} -DInitialConfig=$top,$nest -DContention=$contention_key -DPolicy=$policy -DMeasurementType=real -DInterval=100 -DLogFile=/dev/null $vacNormal/Vacation -c $top ${contention_value} -nest false -sib $nest -updatePar false`
				else
					let sum+=`$runjava -DMaxThreads=${max_cores} -DInitialConfig=$top,$nest -DContention=$contention_key -DPolicy=$policy -DMeasurementType=real -DInterval=100 -DLogFile=/dev/null $vacNormal/Vacation -c $top ${contention_value} -nest true -sib $nest -updatePar true`
				fi
			done
			result=$((sum / attempts))
			printf " ${top}x${nest} $result\n" >> "$file"
        done #configs
		
	done #policies
done #contentionTypes

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "Done. $(($diff / 60)) minutes elapsed."
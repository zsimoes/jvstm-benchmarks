#! /bin/bash

# $1 jvstm basename - assumed to be in "lib" folder, with a .jar extension - defaults to "jvstm"
# $2 - policy name - defaults to "LinearGD"
# $3 maximum core/thread count - defaults to 48

jvstm=${1:-"jvstm"}
policyName=${2:-"LinearGD"}
max_cores=${3:-48}

. ./defaultconfig.sh

base_dir="/home/simoes/new_bench"
wd="/home/simoes/new_bench/JVacation"

if [ ! -d "$wd" ]; then
  wd="/home/jsimoes/new_bench/JVacation"
  base_dir="/home/jsimoes/new_bench"
fi

jvstmlib="$base_dir/lib/${jvstm}.jar"
vacNormal="stamp/vacation/jvstm"

echo "----------------------------"
echo "JVacation :: JVSTM="$jvstm_basename 
echo "----------------------------"

cd $wd/src

rm -f $vacNormal/*.class
javac -cp $jvstmlib:. $vacNormal/Vacation.java
rc1=$?

if [ $rc1 -ne "0" ] ;
then
	echo "---- JVacation: ANT Compile error. Exiting"
	echo
	exit
fi

runjava="java -Xms16384m -Xmx32768m -cp $jvstmlib:."

nest[1]=48
nest[6]=6
nest[48]=1

timestamp=`date +%Y-%m-%d`
resultFolder="$base_dir/results-JVacation/${jvstm}-${timestamp}/convergence" 
mkdir -p $resultFolder

date1=$(date +"%s")
for policy in ${policies[@]}; do
	#for contention_key in "${!contention[@]}"; do 
	#	contention_value="${contention["$contention_key"]}"
	# do only high contention for now:
	if ["$policy" == "Default"]; then
		continue;
	fi
	contention_key="high_contention"
	contention_value=${contention["$contention_key"]}
			# do only [6;6] for now
			for t in 6
			do
				n=${nest[$t]}
				echo "# $runjava -DMaxThreads=$((t * n)) -DInitialConfig=$t,$n -DPolicy=$policy -DMeasurementType=real -DInterval=100 -DLogFile=\"${resultFolder}/convergence-${policy}-${contention_key}${t}x${n}.log\" $vacNormal/Vacation -c $t $contention_value -nest true -sib $n -updatePar true"
				$runjava -DMaxThreads=$((t * n)) -DInitialConfig=$t,$n -DPolicy=$policy -DMeasurementType=real -DInterval=100 -DLogFile="${resultFolder}/convergence-${policy}-${contention_key}${t}x${n}.log" $vacNormal/Vacation -c $t $contention_value -nest true -sib $n -updatePar true 
			done
	#done
done

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "Done. $(($diff / 60)) minutes elapsed."
#cd $wd
#cd "$base_dir/convergence-results-JVacation/"
#java -jar ../ProcessExhaustiveData.jar -convergence
#gnuplot convergence.gp

#echo "Plots finished - all done."
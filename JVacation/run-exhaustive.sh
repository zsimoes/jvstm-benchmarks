#! /bin/bash 

# $1 - jvstm basename
# $2 - max core count - optional!

jvstm=${1:-"jvstm"}
max_cores=${2:-48}

base_dir="/home/simoes/new_bench"
wd="/home/simoes/new_bench/JVacation"

if [ ! -d "$wd" ]; then
  wd="/home/jsimoes/new_bench/JVacation"
  base_dir="/home/jsimoes/new_bench"
fi

jvstmlib="$base_dir/lib/${jvstm}.jar"

timestamp=`date +%Y-%m-%d`
resultFolder="$base_dir/results-JVacation/${jvstm}-${timestamp}/exhaustive" 

vacNormal="stamp/vacation/jvstm"
vacTree="stamp/vacation/jvstm/treemap"
#noNestNormal="stamp/vacation/jvstm/nonest"
#noNestTree="stamp/vacation/jvstm/nonest/treemap"

echo "----------------------------"
echo "JVacation :: JVSTM="$jvstm_basename 
echo "----------------------------"

mkdir -p $resultFolder
cd $resultFolder
#rm -f *.surfacedata
cd $wd/src

#rm -f $vacNormal/*.class
#rm -f $vacTree/*.class
javac -cp $jvstmlib:. $vacNormal/Vacation.java
rc1=$?
javac -cp $jvstmlib:. $vacTree/Vacation.java
rc2=$?

if [ $rc1 -ne "0" ] || [ $rc2 -ne "0" ];
then
	echo "---- JVacation: ANT Compile error. Exiting"
	echo
	exit
fi

#rm $noNestNormal/*.class
#rm $noNestTree/*.class
#javac -cp $jvstmlib:. $noNestNormal/Vacation.java
#javac -cp $jvstmlib:. $noNestTree/Vacation.java

runjava="java -Xms16384m -Xmx32768m -cp $jvstmlib:."

parameters[1]="-n 2000000 -q 1 -u 98 -r 10485 -t 48"
file[1]="${resultFolder}/high-contention.exhaustive"
parameters[2]="-n 2000000 -q 98 -u 98 -r 10485 -t 48"
file[2]="${resultFolder}/low-contention.exhaustive"

nestBool=true
updatePar=true

printedFirstRun=0

attemps=5

date1=$(date +"%s")
for p in 1 2
do
	echo "${parameters[$p]}   -   ${file[$p]}"
	for t in $(seq 1 $max_cores)
	do
		
		echo "Top-Levels: $t"
			for n in $(seq 1 $max_cores)
			do
					if [ $n -eq 1 ]
					then
						nestBool=false
						updatePar=false
					else
						nestBool=true
						updatePar=true
					fi
					#if top-level*nested is greater than the number of cores continue.
					if [ $((n * t)) -gt $max_cores ]
					then
						continue
					fi
					echo "(avg) java... -Doutput=${file[$p]} Vacation -c $t ${parameters[$p]} -nest $nestBool -sib $n -updatePar $updatePar"
					sum=0
					for a in $(seq 1 $attempts)
					do
						if ((printedFirstRun == 0)); then
							echo "$runjava -DmaxThreads=$((t * n)) -DInitialConfig=$t,$n -Dpolicy=none -DMeasurementType=real -DInterval=100 -DLogFile=/dev/null $vacNormal/Vacation -c $t ${parameters[$p]} -nest $nestBool -sib $n -updatePar $updatePar"
							printedFirstRun=1
						fi
						let sum+=`$runjava -DmaxThreads=$((t * n)) -DInitialConfig=$t,$n -Dpolicy=none -DMeasurementType=real -DInterval=100 -DLogFile=/dev/null $vacNormal/Vacation -c $t ${parameters[$p]} -nest $nestBool -sib $n -updatePar $updatePar`
					done #attempts
					#average results from each attempt and save to file with coordinates
					echo "Coords: [$t,$n]  Avg: $((sum/attempts))"
					echo $t $n $((sum/attempts)) >> ${file[$p]}
			done #nested
			echo >> ${file[$p]}
	done #topLevel
	param=`basename ${file[$p]}`
	echo "Round finished: $param"
done #For Parameters

date2=$(date +"%s")
diff=$(($date2-$date1))

echo "All done. $(($diff / 60)) minutes elapsed."
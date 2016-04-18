#! /bin/bash
# this is a script to run vacation once with user-defined parameters 
# $1 - jvstm basename
# $2 - jvstm tuning policy
# $3 - top-level
# $4 - nested

. ./defaultconfig.sh

x="anything"
if [ -z ${config_set+x} ];
then
	
	if [ $# -eq 0 ]
	then
		echo "No arguments supplied - use either four arguments or a defaultconfig.sh file."
		exit
	fi
	
	jvstm=$1
	policy=$2
	paramTop=$3
	paramNested=$4
	declare -A config
	config[high_contention]="-n 2000000 -q 1 -u 98 -r 10485 -t 48"
	config[low_contention]="-n 2000000 -q 98 -u 98 -r 10485 -t 48"
else
	echo "Using config file."
	echo
fi

echo "JVSTM: $jvstm"
echo "Policy: $policy "
for config_key in "${!config[@]}"; do 
	echo "$config_key: ${config["$config_key"]}"; 
done

echo "Top-levels: $paramTop"
echo "Nested: $paramNested"
echo

base_dir="/home/simoes/new_bench"
wd="/home/simoes/new_bench/JVacation"

if [ ! -d "$wd" ]; then
  wd="/home/jsimoes/new_bench/JVacation"
  base_dir="/home/jsimoes/new_bench"
fi

jvstmlib="$base_dir/lib/${jvstm}.jar"
timestamp=`date +%Y-%m-%d`
resultFolder="$base_dir/results-JVacation/${jvstm}-${timestamp}/overhead" 
#########
vacNormal="stamp/vacation/jvstm"

echo "----------------------------"
echo "JVacation :: JVSTM="$jvstm_basename 
echo "----------------------------"

mkdir -p $resultFolder
cd $resultFolder
#rm -f *.data
cd $wd/src

rm -f $vacNormal/*.class
javac -cp $jvstmlib:. $vacNormal/Vacation.java
rc1=$?

if [ $rc1 -ne "0" ];
then
	echo "JVacation: ANT Compile error. Exiting"
	echo
	exit
fi

runjava="java -Xms1024m -Xmx1800m -cp $jvstmlib:."
file="${resultFolder}/overhead.data"
echo "Type Default Stats Tuning Tuning+Stats" > $file

date1=$(date +"%s")
attempts=10
for config_key in "${!config[@]}"; do 
	config_value="${config["$config_key"]}"
	echo "Running config: $config_key - $config_value"; 
	echo
	
	echo "Running default JVSTM"; 
	sum=0
	for a in $(seq 1 $attempts)
	do
			let sum+=`$runjava -DPolicy=Default -DInterval=100 -DNoStats=true -DMaxThreads=$((paramTop * paramNested)) -DContention=$config_key -DInitialConfig=$paramTop,$paramNested $vacNormal/Vacation -c $paramTop $config_value -nest true -sib $paramNested -updatePar true`
	done
	sum=$((sum/attempts))

	echo "Running JVSTM with statistics collection";
	sum2=0
	for a in $(seq 1 $attempts)
	do
			let sum2+=`$runjava -DPolicy=Default -DInterval=100 -DNoStats=false -DLogFile=/dev/null -DMeasurementType=real  -DMaxThreads=$((paramTop * paramNested)) -DContention=$config_key -DInitialConfig=$paramTop,$paramNested $vacNormal/Vacation -c $paramTop $config_value -nest true -sib $paramNested -updatePar true`
	done
	sum2=$((sum2/attempts))
	
	echo "Running JVSTM with tuning";
	sum3=0
	for a in $(seq 1 $attempts)
	do
			let sum3+=`$runjava -DPolicy=FakeLinearGD -DInterval=100 -DNoStats=true -DMaxThreads=$((paramTop * paramNested)) -DContention=$config_key -DInitialConfig=$paramTop,$paramNested $vacNormal/Vacation -c $paramTop $config_value -nest true -sib $paramNested -updatePar true`
	done
	sum3=$((sum3/attempts))
	
	echo "Running JVSTM with tuning + statistics collection";
	sum4=0
	for a in $(seq 1 $attempts)
	do
			let sum4+=`$runjava -DPolicy=FakeLinearGD -DInterval=100 -DNoStats=false -DLogFile=/dev/null -DMeasurementType=real  -DMaxThreads=$((paramTop * paramNested)) -DContention=$config_key -DInitialConfig=$paramTop,$paramNested $vacNormal/Vacation -c $paramTop $config_value -nest true -sib $paramNested -updatePar true`
	done
	sum4=$((sum4/attempts))
	
	echo "$config_key $sum $sum2 $sum3 $sum4" >> $file
done
date2=$(date +"%s")
diff=$(($date2-$date1))
echo "Done. $(($diff / 60)) minutes elapsed."

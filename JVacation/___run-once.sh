#! /bin/bash
# this is a script to run vacation once with user-defined parameters 
# $1 - jvstm basename
# $2 - jvstm tuning policy
# $3 - n
# $4 - q
# $5 - u
# $6 - r
# $7 - t
# $7 - top-level
# $8 - nested

jvstm=$1
policy=$2
paramN=$3
paramQ=$4
paramU=$5
paramR=$6
paramT=$7
paramTop=$8
paramNested=$9

base_dir="/home/simoes/new_bench"
wd="/home/simoes/new_bench/JVacation"

if [ ! -d "$wd" ]; then
  wd="/home/jsimoes/new_bench/JVacation"
  base_dir="/home/jsimoes/new_bench"
fi

jvstmlib="$base_dir/lib/${jvstm}.jar"
timestamp=`date +%Y-%m-%d`
resultFolder="$base_dir/results-JVacation/${policy}-${timestamp}/execution" 
#########
vacNormal="stamp/vacation/jvstm"
vacTree="stamp/vacation/jvstm/treemap"

echo "----------------------------"
echo "JVacation :: JVSTM="$jvstm_basename 
echo "----------------------------"

mkdir -p $resultFolder
cd $resultFolder
#rm -f *.data
cd $wd/src

rm -f $vacNormal/*.class
rm -f $vacTree/*.class
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

runjava="java -Xms1024m -Xmx1800m -cp $jvstmlib:."

parameters="-n $paramN -q $paramQ -u $paramU -r $paramR -t $paramT"
file="${resultFolder}/N${paramN}Q${paramQ}U${paramU}R${paramR}Top${paramTop}Nested${paramNested}.data"

date1=$(date +"%s")
echo "$runjava -DmaxThreads=$((paramTop * paramNested)) -Dpolicy=$2 $vacNormal/Vacation -c $paramTop $parameters -nest true -sib $paramNested -updatePar true >> $file"
sum=0
attempts=5
for a in $(seq 1 $attempts)
do
		let sum+=`$runjava -Doutput=$file -DmaxThreads=$((paramTop * paramNested)) -DinitialConfig=$paramTop,$paramNested -Dinterval=100 -Dpolicy=$2 $vacNormal/Vacation -c $paramTop $parameters -nest true -sib $paramNested -updatePar true`
done
echo $t $nested $((sum/attempts)) >> $file

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "Done. $(($diff / 60)) minutes elapsed."

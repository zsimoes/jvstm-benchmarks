#! /bin/bash 
# $1 - jvstm basename

jvstm=$1


base_dir="/home/simoes/new_bench"
wd="/home/simoes/new_bench/JVacation"

if [ ! -d "$wd" ]; then
  wd="/home/jsimoes/new_bench/JVacation"
  base_dir="/home/jsimoes/new_bench"
fi

jvstmlib="$base_dir/lib/${jvstm}.jar"
resultFolder="$base_dir/results-JVacation/globaldata/$jvstm"
vacNormal="stamp/vacation/jvstm"
vacTree="stamp/vacation/jvstm/treemap"
#noNestNormal="stamp/vacation/jvstm/nonest"
#noNestTree="stamp/vacation/jvstm/nonest/treemap"

echo "----------------------------"
echo "JVacation :: JVSTM="$jvstm_basename 
echo "----------------------------"

mkdir -p $resultFolder
cd $resultFolder
rm -f *.surfacedata
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

parameters[1]="-n 1024 -q 90 -u 5 -r 10485 -t 4096"
file[1]="${resultFolder}/high-Q_low-U_high-R.surfacedata"
parameters[2]="-n 1024 -q 90 -u 98 -r 10485 -t 4096"
file[2]="${resultFolder}/high-Q_high-U_high-R.surfacedata"
parameters[3]="-n 1024 -q 20 -u 5 -r 10485 -t 4096"
file[3]="${resultFolder}/low-Q_low-U_high-R.surfacedata"
parameters[4]="-n 1024 -q 20 -u 98 -r 10485 -t 4096"
file[4]="${resultFolder}/low-Q_high-U_high-R.surfacedata"


parameters[5]="-n 240000 -q 90 -u 5 -r 2048 -t 24"
file[5]="${resultFolder}/high-Q_low-U_high-N.surfacedata"
parameters[6]="-n 240000 -q 90 -u 98 -r 2048 -t 24"
file[6]="${resultFolder}/high-Q_high-U_high-N.surfacedata"
parameters[7]="-n 240000 -q 20 -u 5 -r 2048 -t 24"
file[7]="${resultFolder}/low-Q_low-U_high-N.surfacedata"
parameters[8]="-n 240000 -q 20 -u 98 -r 2048 -t 24"
file[8]="${resultFolder}/low-Q_high-U_high-N.surfacedata"

parameters[9]="-n 240000 -q 90 -u 5 -r 10485 -t 24"
file[9]="${resultFolder}/high-Q_low-U_high-RN.surfacedata"
parameters[10]="-n 240000 -q 90 -u 98 -r 10485 -t 24"
file[10]="${resultFolder}/high-Q_high-U_high-RN.surfacedata"
parameters[11]="-n 240000 -q 20 -u 5 -r 10485 -t 24"
file[11]="${resultFolder}/low-Q_low-U_high-RN.surfacedata"
parameters[12]="-n 240000 -q 20 -u 98 -r 10485 -t 24"
file[12]="${resultFolder}/low-Q_high-U_high-RN.surfacedata"

parameters[13]="-n 2 -q 90 -u 98 -r 8192 -t 24"
file[13]="${resultFolder}/default-low-contention.surfacedata"
parameters[14]="-n 4 -q 60 -u 90 -r 8192 -t 24"
file[14]="${resultFolder}/default-high-contention.surfacedata"

date1=$(date +"%s")
for p in 13 14
do
		ctr=$((0))
		for t in 1 2 4 8 16 24 32 48
		do
				curClients=${clients[$t]}
				nesting=( 1 2 4 8 16 24 32 48 )
				n_index=0
				nested=${nesting[$n_index]}
				while (( nested * t <= 48 && n_index < 8 ))
				do
						#echo COMMAND
						sum=0
						for a in 1 2 3 4 5
						do
								let sum+=`$runjava -DmaxThreads=$t -Dpolicy=$2 -Doutput=${file[$p]}-t$t $vacNormal/Vacation -c $t ${parameters[$p]} -nest true -sib $nested -updatePar true`
						done #attempts
						##AVERAGE ATTEMPS and save to file with coordinates
						echo "Coords: [$t,$nested]  Avg: $((sum/5))"
						echo $t $nested $((sum/5)) >> ${file[$p]}
						let n_index+=1
						nested=${nesting[$n_index]}
				done #While n*t < MAX
				echo >> ${file[$p]}
		done #For ThreadCount
		param=`basename ${file[$p]}`
		echo "Round finished: $param"
done #For Parameters
echo "Done."

date2=$(date +"%s")
diff=$(($date2-$date1))
echo "First round done. $(($diff / 60)) minutes elapsed."

#date1=$(date +"%s")
#for p in 1 2 3 4
#do
#       ctr=$((0))
#       for t in 1 2 4 8 16 24 32 48
#       do
#               echo "$runjava $vacTree/Vacation -c $t ${parameters[$p]} -nest false -sib 1 -updatePar false >> ${file[$p]}"
#               for a in 1 2 3
#               do
#                       $runjava $vacTree/Vacation -c $t ${parameters[$p]} -nest false -sib 1 -updatePar false >> ${file[$p]}
#               done
#               ctr=$((ctr+1))
#              soFar=$((p-1))
#               soFar=$((soFar * 8))
#               soFar=$((soFar+ctr))
#               echo "[1] done $soFar / 32"
#       done
#done
#date2=$(date +"%s")
#diff=$(($date2-$date1))
#echo "Second round done. $(($diff / 60)) minutes elapsed."

# date1=$(date +"%s")
# for p in 1 2 3 4
# do
        # ctr=$((0))
        # for t in 1 2 4 8 16 24 32 48
        # do
                # echo "$runjava $vacNormal/Vacation -c 1 ${parameters[$p]} -nest true -sib $t -updatePar true >> ${file[$p]}"
                # for a in 1 2 3
                # do
                        # $runjava $vacNormal/Vacation -c 1 ${parameters[$p]} -nest true -sib $t -updatePar true >> ${file[$p]}
                # done
                # ctr=$((ctr+1))
                # soFar=$((p-1))
                # soFar=$((soFar * 8))
                # soFar=$((soFar+ctr))
                # echo "[2] done $soFar / 32"
        # done
# done
# date2=$(date +"%s")
# diff=$(($date2-$date1))
# echo "Third round done. $(($diff / 60)) minutes elapsed."


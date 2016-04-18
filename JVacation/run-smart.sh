#! /bin/bash
# $1 - jvstm basename
# $2 - jvstm tuning policy

jvstm=$1
policy=$2

base_dir="/home/simoes/new_bench"
wd="/home/simoes/new_bench/JVacation"

if [ ! -d "$wd" ]; then
  wd="/home/jsimoes/new_bench/JVacation"
  base_dir="/home/jsimoes/new_bench"
fi

jvstmlib="$base_dir/lib/${jvstm}.jar"
resultFolder="$base_dir/EXHAUSTIVEresults-JVacation/$jvstm-$policy"
vacNormal="stamp/vacation/jvstm"
vacTree="stamp/vacation/jvstm/treemap"
#noNestNormal="stamp/vacation/jvstm/nonest"
#noNestTree="stamp/vacation/jvstm/nonest/treemap"

echo "----------------------------"
echo "JVacation :: JVSTM="$jvstm_basename 
echo "----------------------------"

mkdir -p $resultFolder
cd $resultFolder
rm -f *.data
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

parameters[1]="-n 1024 -q 90 -u 5 -r 2048 -t 24"
file[1]="${resultFolder}/wA.data"
parameters[2]="-n 1024 -q 1 -u 5 -r 2048 -t 24"
file[2]="${resultFolder}/wB.data"
parameters[3]="-n 1024 -q 1 -u 98 -r 2048 -t 24"
file[3]="${resultFolder}/wC.data"
parameters[4]="-n 1024 -q 90 -u 98 -r 2048 -t 24"
file[4]="${resultFolder}/wD.data"

nestClients[1]=1
nestClients[2]=1
nestClients[4]=1
nestClients[8]=1
nestClients[16]=1
nestClients[24]=2
nestClients[32]=4
nestClients[48]=8

date1=$(date +"%s")
for p in 1 2 3 4
do
        ctr=$((0))
        for t in 1 2 4 8 16 24 32 48
        do
                curNestClients=${nestClients[$t]}
                echo "$runjava -Dfilename=vacation-throughput-t$t-p$p -Dfolder=../../results-JVacation $vacNormal/Vacation -Dpolicy=$2 -c $curNestClients ${parameters[$p]} -nest true -sib $((t/curNestClients)) -updatePar true >> ${file[$p]}-t$t"
                for a in 1 2 3
                do
                        $runjava -DmaxThreads=$t -Dpolicy=$2 -Doutput=${file[$p]}-t$t $vacNormal/Vacation -c $curNestClients ${parameters[$p]} -nest true -sib $((t/curNestClients)) -updatePar true >> ${file[$p]}-t$t
                done
                ctr=$((ctr+1))
                soFar=$((p-1))
                soFar=$((soFar * 8))
                soFar=$((soFar+ctr))
                echo "[3] done $soFar / 32"
        done
done

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


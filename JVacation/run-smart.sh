#! /bin/sh

jvstmlib="/home/nmld/workspace/jvstm-lf-gc-notemp/dist/jvstm.jar"
vacNormal="stamp/vacation/jvstm"
vacTree="stamp/vacation/jvstm/treemap"
noNestNormal="stamp/vacation/jvstm/nonest"
noNestTree="stamp/vacation/jvstm/nonest/treemap"

cd src
rm *.data

rm $vacNormal/*.class
rm $vacTree/*.class
javac -cp $jvstmlib:. $vacNormal/Vacation.java
javac -cp $jvstmlib:. $vacTree/Vacation.java

#rm $noNestNormal/*.class
#rm $noNestTree/*.class
#javac -cp $jvstmlib:. $noNestNormal/Vacation.java
#javac -cp $jvstmlib:. $noNestTree/Vacation.java

runjava="java -Xms1024m -Xmx1800m -cp $jvstmlib:."

parameters[1]="-n 480000 -q 90 -u 5 -r 10485 -t 96"
file[1]="wA.data"
parameters[2]="-n 480000 -q 1 -u 5 -r 10485 -t 96"
file[2]="wB.data"
parameters[3]="-n 480000 -q 1 -u 98 -r 10485 -t 96"
file[3]="wC.data"
parameters[4]="-n 480000 -q 90 -u 98 -r 10485 -t 96"
file[4]="wD.data"

nestClients[1]=1
nestClients[2]=1
nestClients[4]=1
nestClients[8]=1
nestClients[16]=1
nestClients[24]=2
nestClients[32]=4
nestClients[48]=8

for p in 1 2 3 4
do
       ctr=$((0))
       for t in 1 2 4 8 16 24 32 48
       do
               echo "$runjava $vacTree/Vacation -c $t ${parameters[$p]} -nest false -sib 1 -updatePar false >> ${file[$p]}"
               for a in 1 2 3
               do
                       $runjava $vacTree/Vacation -c $t ${parameters[$p]} -nest false -sib 1 -updatePar false >> ${file[$p]}
               done
               ctr=$((ctr+1))
               soFar=$((p-1))
               soFar=$((soFar * 8))
               soFar=$((soFar+ctr))
               echo "[1] done $soFar / 32"
       done
done

for p in 1 2 3 4
do
        ctr=$((0))
        for t in 1 2 4 8 16 24 32 48
        do
                echo "$runjava $vacNormal/Vacation -c 1 ${parameters[$p]} -nest true -sib $t -updatePar true >> ${file[$p]}"
                for a in 1 2 3
                do
                        $runjava $vacNormal/Vacation -c 1 ${parameters[$p]} -nest true -sib $t -updatePar true >> ${file[$p]}
                done
                ctr=$((ctr+1))
                soFar=$((p-1))
                soFar=$((soFar * 8))
                soFar=$((soFar+ctr))
                echo "[2] done $soFar / 32"
        done
done

for p in 1 2 3 4
do
        ctr=$((0))
        for t in 1 2 4 8 16 24 32 48
        do
                curNestClients=${nestClients[$t]}
                echo "$runjava $vacNormal/Vacation -c $curNestClients ${parameters[$p]} -nest true -sib $((t/curNestClients)) -updatePar true >> ${file[$p]}"
                for a in 1 2 3
                do
                        $runjava $vacNormal/Vacation -c $curNestClients ${parameters[$p]} -nest true -sib $((t/curNestClients)) -updatePar true >> ${file[$p]}
                done
                ctr=$((ctr+1))
                soFar=$((p-1))
                soFar=$((soFar * 8))
                soFar=$((soFar+ctr))
                echo "[3] done $soFar / 32"
        done
done
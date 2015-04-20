#! /bin/sh

jvstmlib=/home/nmld/early-jvacation-tests/jvstm.jar

cd src
rm stamp/vacation/jvstm/*.class
javac -cp $jvstmlib:. stamp/vacation/jvstm/Vacation.java

## Only top levels
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data

echo "3/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data

echo "6/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data

echo "9/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data

echo "12/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wA.data

echo "15/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data

echo "18/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data

echo "21/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data

echo "24/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data

echo "27/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wB.data

echo "30/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data

echo "33/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data

echo "36/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data

echo "39/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data

echo "42/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wC.data

echo "45/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data

echo "48/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 2 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data

echo "51/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 4 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data

echo "54/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 8 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data

echo "57/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 16 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest false -sib 1 -updatePar false >> wD.data

echo "60/180"

## Nest par without update table
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wA.data

echo "63/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wA.data

echo "66/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wA.data

echo "69/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wA.data

echo "72/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wA.data

echo "75/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wB.data

echo "78/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wB.data

echo "81/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wB.data

echo "84/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wB.data

echo "87/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wB.data

echo "90/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wC.data

echo "93/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wC.data

echo "96/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wC.data

echo "99/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wC.data

echo "102/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wC.data

echo "105/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar false >> wD.data

echo "108/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar false >> wD.data

echo "111/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar false >> wD.data

echo "114/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar false >> wD.data

echo "117/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar false >> wD.data

echo "120/180"

## Nest par WITH update table
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wA.data

echo "123/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wA.data

echo "126/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wA.data

echo "129/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wA.data

echo "132/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wA.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wA.data

echo "135/180"


java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wB.data

echo "138/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wB.data

echo "141/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wB.data

echo "144/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wB.data

echo "147/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wB.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 5 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wB.data

echo "150/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wC.data

echo "153/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wC.data

echo "156/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wC.data

echo "159/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wC.data

echo "162/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wC.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 10 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wC.data

echo "165/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 1 -updatePar true >> wD.data

echo "168/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 2 -updatePar true >> wD.data

echo "171/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 4 -updatePar true >> wD.data

echo "174/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 8 -updatePar true >> wD.data

echo "177/180"

java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wD.data
java -Xms2g -Xmx8g -cp $jvstmlib:. stamp/vacation/jvstm/Vacation -c 1 -n 2000000 -q 90 -u 98 -r 10485 -t 20 -nest true -sib 16 -updatePar true >> wD.data
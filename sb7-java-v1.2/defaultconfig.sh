#!/bin/bash
jvstm="jvstm"
policy="Default"
paramTop=3
paramNested=16
maxCores=48
attempts=3

declare -A contention
contention[high_contention]="-n 2000000 -q 1 -u 98 -r 10485 -t 80"
contention[low_contention]="-n 2000000 -q 98 -u 98 -r 10485 -t 80"

declare -A loads
loads[r]="r"
loads[w]="w"
loads[rw]="rw"

duration="20"

# Associative arrays are compatible in bash 4.0+. Usage example:
# for contention_key in "${!contention[@]}"; do 
# 	contention_value="${contention["$contention_key"]}"
#	yadda yadda...
# done

declare -a policies=("Default" "FakeLinearGD" "RRS" "LinearGD" "FullGD" "HierarchicalGD")

declare -a configs
configs[1]='1;1'
configs[2]='1;2'
configs[3]='1;4'

config_set="true"
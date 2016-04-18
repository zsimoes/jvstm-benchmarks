#!/bin/bash
jvstm="jvstm"
policy="Default"
paramTop=3
paramNested=16

declare -A contention
contention[high_contention]="-n 2000000 -q 1 -u 98 -r 10485 -t 48"
contention[low_contention]="-n 2000000 -q 98 -u 98 -r 10485 -t 48"

# Associative arrays are compatible in bash 4.0+. Usage example:
# for contention_key in "${!contention[@]}"; do 
# 	contention_value="${contention["$contention_key"]}"
#	yadda yadda...
# done

declare -a policies=("Default" "FakeLinearGD" "LinearGD" "FullGD" "HierarchicalGD")

declare -a configs
configs[1]='1;1'
configs[2]='1;2'
configs[3]='1;4'
configs[4]='2;3'
configs[5]='4;3'
configs[6]='8;3'
configs[7]='16;3'

config_set="true"
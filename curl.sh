#!/bin/bash
count=1 
while [[ $count -le 10 ]] 
do 
	curl http://10.228.0.48:8090/hello &
	$count = $count + 1
done

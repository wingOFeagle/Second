#!/bin/bash
FEATURE=BaseData.SecKill.Main.SeckillMain
ports=(66666)
for port in ${ports[@]}
do
	exist=$(ps -ef | grep $FEATURE | grep -v 'grep'| awk '{print $2}')
	if [ "$exist" == "" ];then
		echo "$port is not in use before executing 'kill'."
		echo "$port is not in use before executing 'kill'."
		continue
	fi	
	ps -ef | grep $FEATURE | grep -v 'grep'| awk '{print $2}' |xargs kill -9
	PID=$(ps -ef | grep $FEATURE | grep -v 'grep'| awk '{print $2}')
	if [ "$PID" == "" ];then
		echo "port: $port is successfully killed."
	else
		echo "pid: $PID with port: $port shutdown failed"
	fi
done
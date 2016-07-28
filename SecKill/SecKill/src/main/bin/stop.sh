#!/bin/bash
FEATURE=com.jd.log.LogReportMain.LogReportMain
ports=(6666)
for port in ${ports[@]}
do
	exist=$(ps -ef | grep $FEATURE | grep $port | grep -v 'grep'| awk '{print $2}')
	if [ "$exist" == "" ];then
		echo "$port is not in use before executing 'kill'."
		echo "$port is not in use before executing 'kill'."
		continue
	fi	
	ps -ef | grep $FEATURE | grep $port | grep -v 'grep'| awk '{print $2}' |xargs kill -9
	PID=$(ps -ef | grep $FEATURE | grep $port | grep -v 'grep'| awk '{print $2}')
	if [ "$PID" == "" ];then
		echo "port: $port is successfully killed."
	else
		echo "pid: $PID with port: $port shutdown failed"
	fi
done

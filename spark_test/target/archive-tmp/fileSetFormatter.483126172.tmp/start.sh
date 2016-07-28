#!/bin/bash
BASEDIR=/export/App/logserver.m.jd.local
FEATURE=com.jd.app.pat.log.server.DiscardServer
ports=(6001)
for port in ${ports[@]}
do
	exist=$(ps -ef | grep $FEATURE | grep $port | grep -v 'grep'| awk '{print $2}')
	if [ "$exist" != "" ];then
		echo "port: $port is already on pid: $exist; please stop it first!"
		continue
	fi
	setsid java -server -Xms2048m -Xmx2048m -XX:MaxPermSize=512m -XX:+UseParallelGC -XX:+UseParallelOldGC -DinstanceId=$port -Djava.ext.dirs=$BASEDIR/lib $FEATURE $port &
	usleep 100000
	exist=$(ps -ef | grep $FEATURE | grep $port | grep -v 'grep'| awk '{print $2}')
	if [ "$exist" != "" ];then
		echo "port: $port startup success on pid: $exist"
	else
		echo "port: $port startup failed."
	fi
done

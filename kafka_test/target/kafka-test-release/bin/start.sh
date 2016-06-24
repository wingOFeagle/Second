#!/bin/bash
BASEDIR=/root/file/kafka-test-release
FEATURE=test.test_module.KafkaConsumerProducerMain
PROPERTY=/root/file/kafka-test-release/resources/common.properties
ports=(12345)
for port in ${ports[@]}
do
	exist=$(ps -ef | grep $FEATURE | grep -v 'grep'| awk '{print $2}')
	if [ "$exist" != "" ];then
		echo "port: $port is already on pid: $exist; please stop it first!"
		continue
	fi
	#setsid java -server -Xms2048m -Xmx2048m -XX:MaxPermSize=512m -XX:+UseParallelGC -XX:+UseParallelOldGC -DinstanceId=$port -Djava.ext.dirs=$BASEDIR/lib $FEATURE  &
	setsid java -server -Xms1024m -Xmx1024m -XX:MaxPermSize=512m -XX:+UseParallelGC -XX:+UseParallelOldGC -Djava.ext.dirs=$BASEDIR/lib $FEATURE $PROPERTY &
	usleep 100000
	exist=$(ps -ef | grep $FEATURE | grep -v 'grep'| awk '{print $2}')
	if [ "$exist" != "" ];then
		echo "port: $port startup success on pid: $exist"
	else
		echo "port: $port startup failed."
	fi
done

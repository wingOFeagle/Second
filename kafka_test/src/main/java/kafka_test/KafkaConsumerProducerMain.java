package kafka_test;
/*
package test.test_module;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

*//**
 * @author leicui bourne_cui@163.com
 *//*
public class KafkaConsumerProducerMain
{
	 private static Log log = LogFactory.getLog(KafkaConsumerProducerMain.class);
	 
    public static void main(String[] args) throws UnsupportedEncodingException
    {
    	int NumArgs = 1;
    	try{
    		if (args.length < NumArgs)
    		{
    			log.error("NumArgs is not sufficient");
    			return;
    		}
    		Properties props = new Properties();
    		String strPath = args[0];
    		InputStream in = new BufferedInputStream(new FileInputStream(strPath));
    		props.load(in);
    		String strProducerFilePath = props.getProperty("ProducerFilePath");
    		String strConsumerFilePath = props.getProperty("ConsumerFilePath");
    		String strTopic = props.getProperty("topic");
    		
    		//是否使用简易api
    		String strSimple = props.getProperty("Simple");
    		long maxReads = Long.parseLong(props.getProperty("maxRead"));
    		int partition = Integer.parseInt(props.getProperty("partition"));
    		
    		//broker
    		String brokerhost = props.getProperty("brokerhost");
    		int port = Integer.parseInt(props.getProperty("port"));
    		
    		//redis
    		String RedisHost = props.getProperty("RedisHost");
    		int RedisPort = Integer.parseInt(props.getProperty("RedisPort"));
    		String Password = props.getProperty("password");
    		boolean redisUseful = "1".equals(props.getProperty("redisUseful"));
    		
    		//zookeeper
    		String zkHost = props.getProperty("zkHost");
    		int zkConnectionTimeout = Integer.parseInt(props.getProperty("zkConnectionTimeout"));
    		String zkPath = props.getProperty("zkPath");
    		boolean zkUseful = "1".equals(props.getProperty("zkUseful"));
    		
    		//sql
    		String SqlHost = props.getProperty("SqlHost");
    		int SqlPort = Integer.parseInt(props.getProperty("SqlPort"));
    		String Database = props.getProperty("Database");
    		String User = props.getProperty("User");
    		String Passwd = props.getProperty("Passwd");
    		boolean mysqlUseful = "1".equals(props.getProperty("mysqlUseful"));
    		
    		//storm
    		String kafka_storm_topic = props.getProperty("kafka_storm_topic");
    		
    		//日志文件目录
    		String logFile = props.getProperty("logFile");
			assert(!logFile.isEmpty());
			System.out.print("logFile: " + logFile);
			PropertyConfigurator.configure(logFile);
		
    		String strProduce = props.getProperty("produceful");
        	//kafka生产数据
    		if(strProduce.equals("1"))
        	{
    			KafkaProducer producerThread = new KafkaProducer(strTopic,strProducerFilePath,RedisHost,RedisPort,Password,redisUseful);
    			producerThread.start();
        	}
            
            //kafka消费数据
            if (strSimple.equals("0"))
            {
            	log.warn("enter high level api");
            	KafkaConsumer consumerThread = new KafkaConsumer(strTopic,strConsumerFilePath);
            	consumerThread.start();
            }
            else if(strSimple.equals("1"))
            {
            	log.warn("enter low leve api");
            	//zookeepr & mysql
            	SimpleKafkaConsumer SimpleConsumer = new SimpleKafkaConsumer(zkHost, zkConnectionTimeout, zkPath, SqlHost, SqlPort, Database, User, Passwd,zkUseful,mysqlUseful);
            	SimpleConsumer.InitArgs(maxReads,strTopic,brokerhost,partition,port,SimpleConsumer);
            	SimpleConsumer.run();
            }
            else
            	;
            
            //storm消费并统计数据
            String strStorm = props.getProperty("stormful");
            System.out.println("Main--begin storm");
            if(strStorm.equals("1"))
            	new StormApi(zkHost, strTopic, RedisHost, RedisPort, Password, kafka_storm_topic, redisUseful).run();
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    
    	
    }
}
*/
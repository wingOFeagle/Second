package test.test_module;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * @author leicui bourne_cui@163.com
 */
public class KafkaConsumerProducerDemo
{
    public static void main(String[] args) throws UnsupportedEncodingException
    {
    	int NumArgs = 1;
    	try{
    		if (args.length < NumArgs)
    		{
    			System.out.println("NumArgs is not sufficient");
    			return;
    		}
    		Properties props = new Properties();
    		String strPath = args[0];
    		InputStream in = new BufferedInputStream(new FileInputStream(strPath));
    		props.load(in);
    		String strProducerFilePath = props.getProperty("ProducerFilePath");
    		String strConsumerFilePath = props.getProperty("ConsumerFilePath");
    		String strTopic = props.getProperty("topic");
    		String strSimple = props.getProperty("Simple");
    		long maxReads = Long.parseLong(props.getProperty("maxRead"));
    		int partition = Integer.parseInt(props.getProperty("partition"));
    		String brokerhost = props.getProperty("brokerhost");
    		int port = Integer.parseInt(props.getProperty("port"));
        	//kafka生产数据
        	KafkaProducer producerThread = new KafkaProducer(strTopic,strProducerFilePath);
            producerThread.start();
            //kafka消费数据
            if (strSimple.equals("false"))
            {
            	KafkaConsumer consumerThread = new KafkaConsumer(strTopic,strConsumerFilePath);
            	consumerThread.start();
            }
            else
            {
            	SimpleKafkaConsumer SimpleConsumer = new SimpleKafkaConsumer();
            	SimpleConsumer.InitArgs(maxReads,strTopic,brokerhost,partition,port,SimpleConsumer);
            	SimpleConsumer.run();
            }
    		
    	}catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    
    	
    }
}

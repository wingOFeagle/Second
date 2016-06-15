package test.test_module;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.javaapi.producer.Producer;
/**
 * @author leicui bourne_cui@163.com
 */
public class KafkaProducer extends Thread
{
    private final Producer<Integer, String> producer;
    private final String topic;
    private final Properties props = new Properties();
    public KafkaProducer(String topic,String str_ProduceFilePath)
    {
    	
        //props.put("serializer.class", "kafka.serializer.StringEncoder");
        //props.put("metadata.broker.list", "10.10.239.172:9092");//broker list
        try {   
            InputStream in = new BufferedInputStream(new FileInputStream(str_ProduceFilePath));   
            props.load(in);   
            in.close();   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
        producer = new Producer<Integer, String>(new ProducerConfig(props));
        this.topic = topic;
    }
    @Override
    public void run() 
    {
        int messageNo = 1;
        while (true)
        {
            String messageStr = new String("Message_" + messageNo);
            System.out.println("Send:" + messageStr);
            String key = messageNo + "";
            producer.send(new KeyedMessage(topic,key, messageStr));//topic/key/message
            messageNo++;
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

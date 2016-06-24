package test.test_module;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.javaapi.consumer.ConsumerConnector;
import kafka.message.MessageAndMetadata;
import kafka.consumer.Consumer;;
/**
 * @author leicui bourne_cui@163.com
 */
public class KafkaConsumer extends Thread
{
    private final ConsumerConnector consumer;
    private final String topic;
    public KafkaConsumer(String topic,String strConsumerFilePath)
    {
		consumer = Consumer.createJavaConsumerConnector(createConsumerConfig(strConsumerFilePath));
        this.topic = topic;
    }
    private static ConsumerConfig createConsumerConfig(String strConsumeFilePath)
    {
        Properties props = new Properties();
       /* props.put("zookeeper.connect", KafkaProperties.zkConnect);
        props.put("group.id", KafkaProperties.groupId);
        props.put("zookeeper.session.timeout.ms", "40000");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");*/
        try {   
            InputStream in = new BufferedInputStream(new FileInputStream(strConsumeFilePath));   
            props.load(in);   
            in.close();   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
        return new ConsumerConfig(props);
    }
    @Override
    public void run() 
    {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(1));//只用一个线程来进行消费
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
      //获取其中一个kafka数据流,这里数据流的个数根据topicCountMap来进行指定，即用来进行消费的线程个数；这里只有一个线程，所以去其中一个
        KafkaStream<byte[], byte[]> stream = consumerMap.get(topic).get(0);
        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        while (it.hasNext())
        {
            //System.out.println("receive：" + new String(it.next().message()));
            MessageAndMetadata<byte[], byte[]> message = it.next();  
            String topic = message.topic();  
            System.out.println("topic: " + topic + ",");
            int partition = message.partition(); 
            System.out.println("partition: " + Integer.toString(partition) + ",");
            long offset = message.offset();  
            String key = "";
            if((message.key() != null)&&(message.key().length > 0))
            	key = new String(message.key());  
            String msg = "";
            if((message.message() != null)&&(message.message().length > 0))
            	msg = new String(message.message());  
            // 在这里处理消息,这里仅简单的输出  
            // 如果消息消费失败，可以将已上信息打印到日志中，活着发送到报警短信和邮件中，以便后续处理  
            System.out.println( " thread : " + Thread.currentThread().getName()  
                    + ", topic : " + topic + ", partition : " + partition + ", offset : " + offset + " , key : "  
                    + key + " , mess : " + msg);  
            try {
                sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

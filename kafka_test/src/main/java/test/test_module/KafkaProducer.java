package test.test_module;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import redis.clients.jedis.Jedis;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import kafka.javaapi.producer.Producer;
/**
 * @author leicui bourne_cui@163.com
 */
public class KafkaProducer extends Thread
{
    private final Producer<Integer, String> producer;
    private final String m_topic;
    private final Properties props = new Properties();
    private static RedisApi m_redisapi;
    private boolean m_redisUseful;
    
    public KafkaProducer(String topic,String str_ProduceFilePath,String RedisHost,int RedisPort,String Password,boolean redisUseful)
    {
        //props.put("serializer.class", "kafka.serializer.StringEncoder");
        //props.put("metadata.broker.list", "10.10.239.172:9092");//broker list
    	System.out.println("begin kafkaproducer");
        try {   
            InputStream in = new BufferedInputStream(new FileInputStream(str_ProduceFilePath));   
            props.load(in);   
            in.close();   
        } catch (Exception e) {   
            e.printStackTrace();   
        }   
        producer = new Producer<Integer, String>(new ProducerConfig(props));
        this.m_topic = topic;
        //jedis初始化
        //m_pool = pool;
        m_redisUseful = redisUseful;
        if(m_redisUseful)
        {
        	System.out.println("begin init redis");
        	m_redisapi = new RedisApi(RedisHost, RedisPort,Password);
        }
    }
    //实现jedis数值的更新操作
    public void RedisExec(String message)
    {
    	//System.out.println("Begin RedisExec");
    	Jedis jedis = null;
    	Map<String,String> map = new HashMap<String, String>();
    	try
    	{
    		jedis = m_redisapi.GetJedis();
    		if(!jedis.exists(m_topic))
    		{
    			map.put("topic", m_topic);
    			map.put("totalnum", 0+ "");
    			//统计以某个数字结尾的
    			for(int i = 0;i < 10;i++)
    				map.put("num_with_number" + i,0 + "");
    			jedis.hmset(m_topic,map);
    		}
    		else
    		{
    			//解析下message
    			int len = message.length();
    			if (len > 0)
    			{
    				String LastDigit = message.substring(len - 1);//取最后一位数字
    				if(Character.isDigit(LastDigit.charAt(0)))//如果是数字的话
    				{
    					jedis.hincrBy(m_topic,"totalnum",1);
    					jedis.hincrBy(m_topic, "num_with_number" + LastDigit, 1);
    				}
    				else
    					System.out.println("LastDigit is not a digit:" + LastDigit);
    			}
    			else
    				System.out.println("message len <= 0");
    		}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}finally
    	{
    		//注意释放对象
    		m_redisapi.getM_pool().returnResource(jedis);
    	}
    }
    @Override
    public void run() 
    {
        int messageNo = 1;
        while (true)
        {
            String messageStr = new String("Message_" + messageNo);
            
            String key = messageNo + "";
            producer.send(new KeyedMessage(m_topic,key, messageStr));//topic/key/message
            //计算结果到redis,保存总消息生产量，保存以0-9结尾的的消息的个数
            if(m_redisUseful)
            	RedisExec(messageStr);
            messageNo++;
            if(messageNo%100 == 0)
            {
            	System.out.println("Produce:" + messageNo + "条信息");
            	System.out.println("Produce:" + messageNo + "条信息");
            	System.out.println("Produce:" + messageNo + "条信息");
            }
            try {
                sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}

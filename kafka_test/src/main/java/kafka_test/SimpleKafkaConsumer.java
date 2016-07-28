package kafka_test;


import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.api.PartitionOffsetRequestInfo;
import kafka.cluster.Broker;
import kafka.common.ErrorMapping;
import kafka.common.TopicAndPartition;
import kafka.javaapi.*;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
 











import java.nio.ByteBuffer;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import sql_test.SqlApi;
import zk_test.ZkWatcher;

 
public class SimpleKafkaConsumer extends Thread
{
	private long m_maxReads;
	private String m_topic;
	private int m_partition;
	private int m_port;
	private String m_brokerhost;
	private SimpleKafkaConsumer m_SimpleConsumer;
	private List<String> m_replicaBrokers = new ArrayList<String>();
	
	private ZkClient m_zkClient;
	private String m_zkPath;
	private ZkWatcher m_zkWatcher;
	private SqlApi m_sqlApi;
	private boolean m_mysqlUseful;
	private boolean m_zkUseful;
	private static Log log = LogFactory.getLog(SimpleKafkaConsumer.class);
	
	public SimpleKafkaConsumer(String strZkHost,int nZkConnectionTimeout,String strZkPath,String SqlHost, int SqlPort,String Database,String User, String Passwd,boolean zkUseful,boolean mysqlUseful)
    {
    	log.warn("begin SimpleKafkaConsumer");
        m_replicaBrokers = new ArrayList<String>();
        m_zkPath = strZkPath;
        m_mysqlUseful = mysqlUseful;
        m_zkUseful = zkUseful;
        //初始化zk节点
        if(m_zkUseful)
        {
        	
        	log.warn("begin init zookeeper");
        	m_zkWatcher = new ZkWatcher(strZkHost, nZkConnectionTimeout, strZkPath);
        	m_zkClient = m_zkWatcher.GetzkClient();
        }
        //初始化msql
        if(m_mysqlUseful)
        {
        	log.warn("begin init mysql");
        	m_sqlApi = new SqlApi(SqlHost, SqlPort, Database, User, Passwd);
        }
    }
	
	public void InitArgs(long maxReads,String topic,String brokerhost,int partition,int port,SimpleKafkaConsumer SimpleConsumer)
	{
		m_maxReads = maxReads;
		m_topic = topic;
		m_partition = partition;
		m_brokerhost = brokerhost;
		m_port = port;
		m_SimpleConsumer = SimpleConsumer;
	}
    @Override
    public void run()
    {
    	/*SimpleKafkaConsumer example = new SimpleKafkaConsumer();
        long maxReads = Long.parseLong(args[0]);
        String topic = args[1];
        int partition = Integer.parseInt(args[2]);
        List<String> seeds = new ArrayList<String>();
        seeds.add(args[3]);
        int port = Integer.parseInt(args[4]);*/
        try
        {
        	  List<String> seeds = new ArrayList<String>();
        	  seeds.add(m_brokerhost);
        	  m_SimpleConsumer.execute(m_maxReads, m_topic, m_partition, seeds, m_port);
        } catch (Exception e) {
            System.out.println("Oops:" + e);
             e.printStackTrace();
        }
    }
 
    public void execute(long a_maxReads, String a_topic, int a_partition, List<String> a_seedBrokers, int a_port) throws Exception 
    {
        // find the meta data about the topic and partition we are interested in
        //
        PartitionMetadata metadata = findLeader(a_seedBrokers, a_port, a_topic, a_partition);
        if (metadata == null) 
        {
            log.error("Can't find metadata for Topic and Partition. Exiting");
            return;
        }
        if (metadata.leader() == null) 
        {
            log.error("Can't find Leader for Topic and Partition. Exiting");
            return;
        }
        String leadBroker = metadata.leader().host();
        String clientName = "Client_" + a_topic + "_" + a_partition;
 
        SimpleConsumer consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
        long readOffset = getLastOffset(consumer,a_topic, a_partition, kafka.api.OffsetRequest.EarliestTime(), clientName);
        readOffset = 150;
 
        int numErrors = 0;
        int CycleNum = 100;
        int CNum = 100;
        while (a_maxReads > 0)
        {
            if (consumer == null) 
            {
                consumer = new SimpleConsumer(leadBroker, a_port, 100000, 64 * 1024, clientName);
            }
            //这里的readoffset指定起始的offset
            FetchRequest req = new FetchRequestBuilder()
                    .clientId(clientName)
                    .addFetch(a_topic, a_partition, readOffset, 100000) // Note: this fetchSize of 100000 might need to be increased if large batches are written to Kafka
                    .build();
            FetchResponse fetchResponse = consumer.fetch(req);
 
            if (fetchResponse.hasError()) 
            {
                numErrors++;
                // Something went wrong!
                short code = fetchResponse.errorCode(a_topic, a_partition);
               log.error("Error fetching data from the Broker:" + leadBroker + " Reason: " + code);
                if (numErrors > 5) 
                	break;
                if (code == ErrorMapping.OffsetOutOfRangeCode()) 
                {
                    // We asked for an invalid offset. For simple case ask for the last element to reset
                    readOffset = getLastOffset(consumer,a_topic, a_partition, kafka.api.OffsetRequest.LatestTime(), clientName);
                    continue;
                }
                consumer.close();
                consumer = null;
                leadBroker = findNewLeader(leadBroker, a_topic, a_partition, a_port);
                continue;
            }
            numErrors = 0;
            long numRead = 0;
            //实际上如果生产的太慢的话，这个for循环经常空跑
            for (MessageAndOffset messageAndOffset : fetchResponse.messageSet(a_topic, a_partition)) 
            {
                long currentOffset = messageAndOffset.offset();
                if (currentOffset < readOffset) 
                {
                    log.error("Found an old offset: " + currentOffset + " Expecting: " + readOffset);
                    continue;
                }
                readOffset = messageAndOffset.nextOffset();
                ByteBuffer payload = messageAndOffset.message().payload();
                byte[] bytes = new byte[payload.limit()];
                payload.get(bytes);
                //System.out.println("Consume: " + String.valueOf(messageAndOffset.offset()) + ": " + new String(bytes, "UTF-8"));
                //消费掉CycleNum数据后就存储一下offset的数据
                CycleNum--;
                if(CycleNum%50 == 0)
                	log.warn("CycleNum: " + CycleNum + "\n");
                try
                {
	                if( CycleNum%100 == 0 )
	                {
	                	//System.out.println("CycleNum: " + CycleNum);
	                	//向zk节点写数据
	                	if(m_zkUseful)
	                	{
	                		//System.out.println("Begin write to zookeeper");
	                		//System.out.println("Begin write to zookeeper");
	                		log.warn("Begin write to zookeeper");
	                		String strOffset = Long.toUnsignedString(messageAndOffset.offset());
	                    	byte[] byteOffset = strOffset.getBytes();
	                    	m_zkClient.writeData(m_zkPath,byteOffset);
	                    	log.warn("写入路径" + m_zkPath + "数据：" + strOffset);
	                	}
	                	
	                	//向mysql写数据
	                	if(m_mysqlUseful)
	                	{
	                		//System.out.println("Begin write to mysql");
	                		//System.out.println("Begin write to mysql");
	                		log.warn("Begin write to mysql");
	                		Statement stmt= m_sqlApi.getStatement();
	                    	String sql = String.format("insert into kafka_test(topic,partition_id,offset) values('%s',%d,%d);",a_topic,a_partition,a_partition);
	                    	//System.out.print("sql: " + sql);
	                    	//System.out.print("sql: " + sql);
	                    	log.warn("sql: " + sql);
	                    	//stmt.execute(sql);
	                    	//执行sql语句
	                    	stmt.executeUpdate(sql);
	                	}
	                	CycleNum = CNum;
                	}
                }catch(Exception e){
                	e.printStackTrace();}
                numRead++;
                a_maxReads--;
            }
            //System.out.println("numRead:"+ numRead);
            if(a_maxReads%100 == 0)
            	;//System.out.println("a_maxReads:"+ a_maxReads);
            
       /*     if (numRead == 0) 
            {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }*/
        }
        if (consumer != null) 
        	consumer.close();
    }
 //define where to read from
    public static long getLastOffset(SimpleConsumer consumer, String topic, int partition,long whichTime, String clientName) 
    {
        TopicAndPartition topicAndPartition = new TopicAndPartition(topic, partition);
        Map<TopicAndPartition, PartitionOffsetRequestInfo> requestInfo = new HashMap<TopicAndPartition, PartitionOffsetRequestInfo>();
        requestInfo.put(topicAndPartition, new PartitionOffsetRequestInfo(whichTime, 1));
        kafka.javaapi.OffsetRequest request = new kafka.javaapi.OffsetRequest(
                requestInfo, kafka.api.OffsetRequest.CurrentVersion(), clientName);
        OffsetResponse response = consumer.getOffsetsBefore(request);
        if (response.hasError()) 
        {
            log.error("Error fetching data Offset Data the Broker. Reason: " + response.errorCode(topic, partition) );
            return 0;
        }
        long[] offsets = response.offsets(topic, partition);
        return offsets[0];
    }
    
    private String findNewLeader(String a_oldLeader, String a_topic, int a_partition, int a_port) throws Exception 
    {
        for (int i = 0; i < 3; i++)
        {
            boolean goToSleep = false;
            PartitionMetadata metadata = findLeader(m_replicaBrokers, a_port, a_topic, a_partition);
            if (metadata == null) {
                goToSleep = true;
            } else if (metadata.leader() == null) {
                goToSleep = true;
            } else if (a_oldLeader.equalsIgnoreCase(metadata.leader().host()) && i == 0) {
                // first time through if the leader hasn't changed give ZooKeeper a second to recover
                // second time, assume the broker did recover before failover, or it was a non-Broker issue
                //
                goToSleep = true;
            } else {
                return metadata.leader().host();
            }
            if (goToSleep) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ie) {
                }
            }
        }
        log.error("Unable to find new leader after Broker failure. Exiting");
        throw new Exception("Unable to find new leader after Broker failure. Exiting");
    }
    //找寻topic 与partition对应的  leader
    private PartitionMetadata findLeader(List<String> a_seedBrokers, int a_port, String a_topic, int a_partition) 
    {
        PartitionMetadata returnMetaData = null;
        loop:
        for (String seed : a_seedBrokers) {
            SimpleConsumer consumer = null;
            try {
                consumer = new SimpleConsumer(seed, a_port, 100000, 64 * 1024, "leaderLookup");
                List<String> topics = Collections.singletonList(a_topic);
                TopicMetadataRequest req = new TopicMetadataRequest(topics);
                kafka.javaapi.TopicMetadataResponse resp = consumer.send(req);
 
                List<TopicMetadata> metaData = resp.topicsMetadata();
                for (TopicMetadata item : metaData) 
                {
                	log.info("SimpleApi item.topic: ["+ item.topic() + "]");
                	//store basic info of one topic and partion
                    for (PartitionMetadata part : item.partitionsMetadata()) 
                    {
                        if (part.partitionId() == a_partition) 
                        {
                        	//isr服务
                        	for (Broker isr:part.isr())
                        	{
                        		log.info("isr: host[ " + isr.host() + "]port[" + isr.port() + "]id" + isr.id() + "]");
                        	}
                        	log.info("leader: [host" + part.leader().host() + "]port[" + part.leader().port() + "]id[" + part.leader().id() + "]");
                        	for (Broker replica: part.replicas())
                        	{
                        		log.info("replica: [host" + replica.host() + "]port[" + replica.port() + "]id[" + replica.id() + "]");
                        	}
                            returnMetaData = part;
                            break loop;
                        }
                    }
                }
            } catch (Exception e) {
                log.error("Error communicating with Broker [" + seed + "] to find Leader for [" + a_topic
                        + ", " + a_partition + "] Reason: " + e);
            } finally {
                if (consumer != null) consumer.close();
            }
        }
        if (returnMetaData != null) 
        {
            m_replicaBrokers.clear();
            for (Broker replica : returnMetaData.replicas()) {
                m_replicaBrokers.add(replica.host());
            }
        }
        return returnMetaData;
    }
}

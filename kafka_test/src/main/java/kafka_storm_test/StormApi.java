package kafka_storm_test;


import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.StringScheme;
import storm.kafka.ZkHosts;
import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.spout.Scheme;
import backtype.storm.spout.SchemeAsMultiScheme;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;


public class StormApi extends Thread
{
	private String m_zkHost;
	private String m_topic;
	private String m_redisHost;
	private int m_redisPort;
	private String m_redisPasswd;
	private String m_storm_topic;
	private boolean m_redisUseful;
	private static Log log = LogFactory.getLog(StormApi.class);
	
	public StormApi(String zkHost,String topic,String redisHost,int redisPort,String redisPasswd,String storm_topic,boolean redisUseful)
	{
		m_zkHost = zkHost;
		m_topic = topic;
		m_redisHost = redisHost;
		m_redisPort = redisPort;
		m_redisPasswd = redisPasswd;
		m_storm_topic = storm_topic;
		m_redisUseful = redisUseful;
	}
	
	@Override
	public void run()
	{
		log.warn("Begin storm");
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("storm_kafka",new KafkaSpout(GetSpoutConf()), 1);
		builder.setBolt("classify", (IRichBolt)new ClassifyBolt(), 1).shuffleGrouping("storm_kafka");
		builder.setBolt("count",  (IRichBolt) new CountBolt(m_redisHost,m_redisPort,m_redisPasswd,m_redisUseful,m_storm_topic), 1).shuffleGrouping("classify");

		Config conf = new Config();
		conf.setNumWorkers(1);
		conf.setMaxSpoutPending(100);
		//conf.setNumAckers(0);

		try
		{
			System.setProperty("storm.jar","/data/storm/apache-storm-0.9.6/lib/storm-core-0.9.6.jar");
			StormSubmitter.submitTopology("storm_kafka_test", conf,
					builder.createTopology());
		} catch (AlreadyAliveException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTopologyException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public SpoutConfig GetSpoutConf()
	{
		String zks = "10.10.239.172:2181";
		BrokerHosts zkhost = new ZkHosts(m_zkHost);
    	SpoutConfig spoutConf = new SpoutConfig(zkhost,
    			m_topic,//topic
				"", //zk上的注册地址
				"storm_kafka");//本topology在地址下的目录
    	List<String> zkServers = new ArrayList<String>();  
        if (zks != null && !zks.isEmpty()) {  
            for (String host : zks.split(",")) 
            { 
                zkServers.add(host.split(":")[0]);  
            }  
        }  
        spoutConf.zkServers = zkServers;  
        spoutConf.zkPort = Integer.valueOf(2181);  
        spoutConf.forceFromStart = true;  
        spoutConf.socketTimeoutMs = 60 * 1000;  
        String zkRoot = "/kafka_storm/kafka-api-test";  
        spoutConf.zkRoot=zkRoot;  
    	spoutConf.scheme = new SchemeAsMultiScheme((Scheme) new StringScheme());

		return spoutConf;
	}
}

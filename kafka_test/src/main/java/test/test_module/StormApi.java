package test.test_module;

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


public class StormApi
{
	private String m_zkHost;
	private String m_topic;
	
	public StormApi(String zkHost,String topic)
	{
		m_zkHost = zkHost;
		m_topic = topic;
	}
	
	public void run() throws AlreadyAliveException, InvalidTopologyException
	{
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("storm_kafka", (IRichSpout) new KafkaSpout(GetSpoutConf()), 3);
		builder.setBolt("classify", (IRichBolt) new ClassifyBolt(), 2).setNumTasks(10).shuffleGrouping("storm_kafka");
		builder.setBolt("count", (IRichBolt) new CountBolt(), 1).shuffleGrouping("classify");

		Config conf = new Config();
		conf.setNumWorkers(24);
		conf.setMaxSpoutPending(100);
		conf.setNumAckers(0);

		StormSubmitter.submitTopology("storm_kafka_test", conf,
				builder.createTopology());
	}
	
	public SpoutConfig GetSpoutConf()
	{
    	BrokerHosts brokerHosts = new ZkHosts(m_zkHost);
    	SpoutConfig spoutConf = new SpoutConfig(brokerHosts,
    			m_topic,//topic
				"/storm_kafka" + m_topic, //zk上的注册地址
				m_topic);//本topology在地址下的目录
    	
    	spoutConf.scheme = new SchemeAsMultiScheme((Scheme) new StringScheme());

		//spoutConf.zkServers = config.getZkServers();
		//spoutConf.zkPort = config.getZkPort();
		//clickSpoutConf.forceFromStart = true;
		return spoutConf;
	}

}

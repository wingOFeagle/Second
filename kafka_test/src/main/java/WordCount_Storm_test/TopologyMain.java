package WordCount_Storm_test;

//例2-3 src/main/java/TopologyMain.java
import java.util.ArrayList;
import java.util.List;

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
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class TopologyMain 
{
    public static void main(String[] args) throws InterruptedException, AlreadyAliveException, InvalidTopologyException {
    //定义拓扑
    	System.out.println("enter main!!!!!!!!!");
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader(),2);
        //builder.setSpout("word-reader", new KafkaSpout(GetSpoutConf()), 4);
        builder.setBolt("word-normalizer", new WordNormalizer(),2).setNumTasks(4).shuffleGrouping("word-reader");
        builder.setBolt("word-counter", new WordCounter(),2).fieldsGrouping("word-normalizer", new Fields("word"));

    //配置

        Config conf = new Config();
        conf.put("wordsFile", args[0]);
		conf.setNumWorkers(2);
		conf.setMaxSpoutPending(100);
        StormSubmitter.submitTopology("kafka-storm-word", conf, builder.createTopology());
        Thread.sleep(1000);
        //cluster.shutdown();
    }
	public static SpoutConfig GetSpoutConf()
	{
		String zks = "10.10.239.172:2181";
		BrokerHosts zkhost = new ZkHosts(zks);
    	SpoutConfig spoutConf = new SpoutConfig(zkhost,
    			"kafka-api-test",//topic
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
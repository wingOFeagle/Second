package storm.test;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

public class WordCountTopologyMain {
	public static void main(String[] args) throws InterruptedException
	{
		//定义一个Topology
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("word-reader",new WordReader());
		builder.setBolt("word-normalizer", new WordNormalizer()).shuffleGrouping("word-reader");
		builder.setBolt("word-counter", new WordCounter(),2).fieldsGrouping("word-normalizer", new Fields("word"));
		//配置
		Config conf = new Config();
		conf.put("wordsFile", "/data/storm/apache-storm-0.9.6/file/test");
		conf.setDebug(false);
		//提交Topology
		conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
		//创建一个本地模式cluster
		LocalCluster cluster = new LocalCluster();
		cluster.submitTopology("Getting-Started-Toplogie", conf,builder.createTopology());
		Thread.sleep(1000);
		cluster.shutdown();
	}
}

package WordCount_Storm_test;

//例2-2 src/main/java/bolts/WordNormalizer.java
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordNormalizer implements IRichBolt
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;

	public void cleanup()
	{
	}

	/**
	 * *bolt*从单词文件接收到文本行，并标准化它。 文本行会全部转化成小写，并切分它，从中得到所有单词。
	 */
	public void execute(Tuple input)
	{
		String sentence = input.getString(0);
		System.out.println("sentence: " + sentence);
		String[] words = sentence.split(" ");
		for (String word : words)
		{
			word = word.trim();
			if (!word.isEmpty())
			{
				word = word.toLowerCase();
				// 发布这个单词
				List<Tuple> a = new ArrayList();
				a.add(input);
				//锚定，用来更新消息树
				collector.emit(a, new Values(word));
			}
		}
		// 对元组做出应答
		collector.ack(input);
	}

	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector)
	{
		this.collector = collector;
		// 更好的理解各个概念
		System.out.print("WordNormalizer ComponetId "
				+ context.getThisComponentId() + "of taskid "
				+ context.getThisTaskId());
		System.out.print("WordNormalizer worker port: "
				+ context.getThisWorkerPort());
		Set<String> setComponentIds = context.getComponentIds();
		System.out.println("WordNormalizer setComponents:\n");
		for (String componentId : setComponentIds)
			System.out.println("componentId " + componentId);
		System.out.println("WordNormalizer workTasks:\n");
		List<Integer> listWorkerTasks = context.getThisWorkerTasks();
		for (Integer taskId : listWorkerTasks)
			System.out.println("taskId: " + taskId);
	}

	/**
	 * 这个*bolt*只会发布“word”域
	 */
	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		declarer.declare(new Fields("word"));
	}

	@Override
	public Map<String, Object> getComponentConfiguration()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
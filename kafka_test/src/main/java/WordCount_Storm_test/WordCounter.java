package WordCount_Storm_test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

public class WordCounter implements IRichBolt
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Integer id;
	String name;
	Map<String, Integer> counters;
	private OutputCollector collector;

	/**
	 * 这个spout结束时（集群关闭的时候），我们会显示单词数量
	 */
	@Override
	public void cleanup()
	{
		System.out.println("-- 单词数 【" + name + "-" + id + "】 --");
		for (Map.Entry<String, Integer> entry : counters.entrySet())
		{
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}

	/**
	 * 为每个单词计数
	 */
	@Override
	public void execute(Tuple input)
	{
		String str = input.getString(0);
		/**
		 * 如果单词尚不存在于map，我们就创建一个，如果已在，我们就为它加1
		 */
		if (!counters.containsKey(str))
		{
			counters.put(str, 1);
		} else
		{
			Integer c = counters.get(str) + 1;
			counters.put(str, c);
		}
		System.out.println("here!!!!!!!!!!!!!");
		// 对元组作为应答
		collector.ack(input);
	}

	/**
	 * 初始化
	 */
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector)
	{
		this.counters = new HashMap<String, Integer>();
		this.collector = collector;
		this.name = context.getThisComponentId();
		this.id = context.getThisTaskId();
		// 更好的理解各个概念
		System.out.print("WordCounter ComponetId "
				+ context.getThisComponentId() + "of taskid "
				+ context.getThisTaskId());
		System.out.print("WordCounter worker port: "
				+ context.getThisWorkerPort());
		Set<String> setComponentIds = context.getComponentIds();
		System.out.println("WordCounter setComponents:\n");
		for (String componentId : setComponentIds)
			System.out.println("componentId " + componentId);
		System.out.println("WordCounter workTasks:\n");
		List<Integer> listWorkerTasks = context.getThisWorkerTasks();
		for (Integer taskId : listWorkerTasks)
			System.out.println("taskId: " + taskId);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
	}

	@Override
	public Map<String, Object> getComponentConfiguration()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
package WordCount_Storm_test;

/**
 *  例2-1.src/main/java/spouts/WordReader.java
 */

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class WordReader implements IRichSpout
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	private FileReader fileReader;
	private boolean completed = false;
	private TopologyContext context;

	public boolean isDistributed()
	{
		return false;
	}

	public void ack(Object msgId)
	{
		System.out.println("OK:" + msgId);
	}

	public void close()
	{
	}

	public void fail(Object msgId)
	{
		System.out.println("FAIL:" + msgId);
	}

	/**
	 * 这个方法做的惟一一件事情就是分发文件中的文本行
	 */
	public void nextTuple()
	{
		/**
		 * 
		 * 这个方法会不断的被调用，直到整个文件都读完了，我们将等待并返回。
		 */
		if (completed)
		{
			try
			{
				Thread.sleep(1000);
			} catch (InterruptedException e)
			{
				// 什么也不做
			}
			return;
		}
		String str;
		// 创建reader
		BufferedReader reader = new BufferedReader(fileReader);
		try
		{
			// 读所有文本行
			while ((str = reader.readLine()) != null)
			{
				/**
				 * 按行发布一个新值
				 */
				// messageid用来追踪消息是否被完全处理了
				this.collector.emit(new Values(str), str);
			}
		} catch (Exception e)
		{
			throw new RuntimeException("Error reading tuple", e);
		} finally
		{
			completed = true;
		}
	}

	/**
	 * 我们将创建一个文件并维持一个collector对象
	 */
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector)
	{
		try
		{
			this.context = context;
			// 更好的理解各个概念
			System.out.print("WordReader ComponetId " + context.getThisComponentId()
					+ "of taskid " + context.getThisTaskId());
			System.out.print("WordReader worker port: " + context.getThisWorkerPort());
			Set<String> setComponentIds = context.getComponentIds();
			System.out.println("WordReader setComponents:\n");
			for (String componentId : setComponentIds)
				System.out.println("componentId " + componentId);
			System.out.println("WordReader workTasks:\n");
			List<Integer> listWorkerTasks = context.getThisWorkerTasks();
			for (Integer taskId : listWorkerTasks)
				System.out.println("taskId: " + taskId);

			this.fileReader = new FileReader(conf.get("wordsFile").toString());
		} catch (FileNotFoundException e)
		{
			throw new RuntimeException("Error reading file ["
					+ conf.get("wordFile") + "]");
		}
		this.collector = collector;
	}

	/**
	 * 声明输入域"word"
	 */
	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		declarer.declare(new Fields("line"));
	}

	@Override
	public void activate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void deactivate()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Object> getComponentConfiguration()
	{
		// TODO Auto-generated method stub
		return null;
	}
}

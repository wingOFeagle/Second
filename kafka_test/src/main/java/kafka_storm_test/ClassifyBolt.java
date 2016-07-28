package kafka_storm_test;


import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;



public class ClassifyBolt extends BaseBasicBolt
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(ClassifyBolt.class);
	private OutputCollector collector;

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector) 
	{
		try 
		{
			System.out.println("Enter ClassifyBolt!!!!!!!!!!!!!");
			System.out.println("Enter ClassifyBolt!!!!!!!!!");
			System.out.println("Enter ClassifyBolt!!!!!!!!");
			//每一条消息
			String msg = tuple.getString(0);
			//获取最后一个字母
			int len = msg.length();
			
			if(len > 0)
			{
				String EndNum = msg.substring(len - 1);
				if( Character.isDigit(EndNum.charAt(0)) )//最后一位是数组
				{
					collector.emit(new Values(EndNum,msg));
					System.out.println("storm:" +  EndNum + msg);
				}
			}
			else
			{
				log.error("length 0: " + msg);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	 public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) 
	 {
	       this.collector=collector;
	   }

	public void declareOutputFields(OutputFieldsDeclarer declarer) 
	{
		declarer.declare(new Fields("EndNum", "msg"));
	}
}

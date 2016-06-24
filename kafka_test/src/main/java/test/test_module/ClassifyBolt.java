package test.test_module;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;



public class ClassifyBolt extends BaseBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute(Tuple tuple, BasicOutputCollector collector) 
	{
		try 
		{
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
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) 
	{
		declarer.declare(new Fields("EndNum", "msg"));
	}
}

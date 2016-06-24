package test.test_module;

import java.util.HashMap;
import java.util.Map;

import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;


public class CountBolt extends BaseBasicBolt {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void execute(Tuple tuple, BasicOutputCollector collector) 
	{
		try 
		{
			//每一条消息
			int num = Integer.parseInt(tuple.getString(0));//结尾数字
			Map<Integer, Integer> map = new HashMap<Integer,Integer>();
			
			if(map.containsKey(num))
				map.put(num, map.get(num) + 1);
			else
				map.put(num, map.get(num));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) 
	{
		//declarer.declare(new Fields("endNum", "totalNum"));
	}
}

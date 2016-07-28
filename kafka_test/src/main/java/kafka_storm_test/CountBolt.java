package kafka_storm_test;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis_test.RedisApi;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import backtype.storm.topology.IRichBolt;

public class CountBolt extends BaseBasicBolt
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Log log = LogFactory.getLog(CountBolt.class);
	private RedisApi m_redis;
	private boolean m_redisUseful;
	private String m_kafka_storm_topic;

	public CountBolt(String RedisHost, int RedisPort, String Password,boolean b_redisUseful, String kafka_storm_topic)
	{
		m_redisUseful = b_redisUseful;
		m_kafka_storm_topic = kafka_storm_topic;
		if (m_redisUseful)
			m_redis = new RedisApi(RedisHost, RedisPort, Password);
	}

	@Override
	public void execute(Tuple tuple, BasicOutputCollector collector)
	{
		try
		{
			// 每一条消息
			System.out.println("enter CountBolt-execute");
			int num = Integer.parseInt(tuple.getString(0));// 结尾数字
			if(m_redisUseful)
				RedisExec(Integer.toString(num));
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer)
	{
		// declarer.declare(new Fields("endNum", "totalNum"));
	}

	// 实现jedis数值的更新操作
	public void RedisExec(String LastDigit)
	{
		// System.out.println("Begin RedisExec");
		Jedis jedis = null;
		Map<String, String> map = new HashMap<String, String>();
		try
		{
			jedis = m_redis.GetJedis();
			if (!jedis.exists(m_kafka_storm_topic))
			{
				map.put("topic", m_kafka_storm_topic);
				map.put("totalnum", 0 + "");
				// 统计以某个数字结尾的
				for (int i = 0; i < 10; i++)
					map.put("num_with_number" + i, 0 + "");
				jedis.hmset(m_kafka_storm_topic, map);
			} 
			else
			{
				// 解析下message
				if (Character.isDigit(LastDigit.charAt(0)))// 如果是数字的话
				{
					jedis.hincrBy(m_kafka_storm_topic, "totalnum", 1);
					jedis.hincrBy(m_kafka_storm_topic, "num_with_number"
							+ LastDigit, 1);
				} 
				else
					log.error("LastDigit is not a digit:" + LastDigit);
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			// 注意释放对象
			m_redis.getM_pool().returnResource(jedis);
		}
	}
}

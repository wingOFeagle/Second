package BaseData.SecKill.CommonInfos;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;


/**
 * Redis操作接口
 * 
 * @author 林计钦
 * @version 1.0 2013-6-14 上午08:54:14
 */
public class RedisApi
{
	private static JedisPool m_pool = null;
	private String m_RedisHost = Config.getM_strHostIp();
	private int m_RedisPort = 6379;
	private String m_RedisPasswd = "";
	private static Log log = LogFactory.getLog(RedisApi.class);

	/**
	 * 构建redis连接池
	 * 
	 * @param ip
	 * @param port
	 * @return JedisPool
	 */
	// 构造函数
	public RedisApi()
	{
		System.out.println("Begin ReisApi");
		if (m_pool == null)
		{
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxTotal(10);
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(2);
			// 表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
			config.setMaxWaitMillis(1000 * 1000);
			// 在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
			config.setTestOnBorrow(true);
			int timeout = 1000 * 100;
			m_pool = new JedisPool(config, m_RedisHost, m_RedisPort,timeout,m_RedisPasswd);
		}
	}

	@SuppressWarnings("finally")
	public Jedis GetJedis()
	{
		String value = null;
		Jedis jedis = null;
		try
		{
			jedis = m_pool.getResource();
		} catch (Exception e)
		{
			// 释放redis对象
			m_pool.returnBrokenResource(jedis);
			e.printStackTrace();
		} finally
		{
			return jedis;
		}
	}

	public static JedisPool getM_pool()
	{
		return m_pool;
	}
}
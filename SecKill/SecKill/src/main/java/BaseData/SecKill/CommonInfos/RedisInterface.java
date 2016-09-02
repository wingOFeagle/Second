package BaseData.SecKill.CommonInfos;

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
public class RedisInterface
{
	private static JedisPool m_pool = null;
	private String m_RedisHost = Config.getM_strHostIp();
	private int m_RedisPort = 6379;
	private String m_RedisPasswd = "";
	private static Logger log = Logger.getLogger(RedisInterface.class);

	/**
	 * 构建redis连接池
	 * 
	 * @param ip
	 * @param port
	 * @return JedisPool
	 */
	// 构造函数
	public RedisInterface()
	{
		System.out.println("Begin ReisApi");
		log.info("Begin ReisApi");
		if (m_pool == null)
		{
			JedisPoolConfig config = new JedisPoolConfig();
			// 控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
			// 如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
			config.setMaxTotal(25);//可以有多个连接池对象,可连接的个数至少多于调用线程地个数吧
			// 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
			config.setMaxIdle(4);
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
	public boolean keyInRedis(Jedis jedis,String key)
	{
		boolean bkeyInRedis = false;
		try{
			bkeyInRedis = jedis.exists(key);
		}catch(Exception e)
		{
			log.error("RedisApi: keyInRedis error while query " + key + ","+ e);
		}
		return bkeyInRedis;
	}
	public String GetValueInRedis(Jedis jedis,String key,String field)
	{
		String value = null;
		try{
			if(jedis.hexists(key, field))//如果已经存在了，那么返回
			{
				value = jedis.hget(key, field);
			}
			else//如果不存在那么进行初始化赋值，初始为""
			{
				value = "";
				jedis.hset(key, field, value);
			}
		}catch(Exception e)
		{
			log.error("RedisApi: GetValueInRedis error while query " + key + ","+ field + e);
		}
		return value;
	}
	public void SetKeyValue(Jedis jedis,String key,String field,String value,boolean bSetExpire)
	{
		try{
			//第一次的
			jedis.hset(key, field, value);
			if(bSetExpire && (jedis.ttl(key) == -1))//没有设置过ttl时返回-1，进行判断
				jedis.expire(key, Config.getM_nRedisExpireTLL());
		}
		catch(Exception e)
		{
			log.error("RedisInterface: SetKeyValue" + e);
		}
	}
}
package BaseData.SecKill.Consume;

/**
 * 
 */
/**
 * @author sunguangsheng
 *
 */

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import kafka.serializer.Decoder;
import BaseData.SecKill.BussinessInfo.BusinessInfo;
import BaseData.SecKill.CommonInfos.RedisInterface;
import BaseData.SecKill.CommonInfos.Tokens;

import com.jd.bdp.jdq.auth.Authentication;
import com.jd.bdp.jdq.config.ENV;
import com.jd.bdp.jdq.config.OffsetReset;
import com.jd.bdp.jdq.consumer.JDQConsumerClient;
import com.jd.bdp.jdq.consumer.zk.MessageHandler;
import com.jd.bdp.jdq.control.JDQ_ENV;
import com.jd.bdp.jdw.JdwDataSerializer;
import com.jd.bdp.jdw.avro.JdwData;
import com.jd.info.calc.KeyType;

import ex.JDQException;

public class OrderConsume extends Thread
{

	private static String m_strAppId = Tokens.getM_strOrdersAppId();
	private static String m_strToken = Tokens.getM_strOrdersToken();
	private static Logger log = Logger.getLogger(OrderConsume.class);
	private String m_stropt;
	private String m_strid;
	private String m_strcreateDate;
	private String m_stryn;
	private String m_strtotalPrice;
	private boolean m_bpaydate;
	private boolean m_byn;
	private RedisInterface m_redisInterface;
	private String m_strpaydate;

	public void OrderConsume(RedisInterface redisInterface)
	{
		m_redisInterface = redisInterface;
	}
	
	@Override
	public void run()
	{
		try
		{
			// JDQ_ENV.assignRunningEnv(ENV.OFFLINE);
			System.out.println("OrderConsumer开始执行");
			Consume();
		} catch (JDQException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void HandleMsg(String key, JdwData data, long offset,int partition)
	{
		Jedis jedis = null;
		try{
			log.info("key:" + key 
					+ "|||data:" + data.toString()
					+ "|||offset:" + offset + "|||partition:" + partition
					+ "|||Thread:" + Thread.currentThread().getId());
			//进行处理
			jedis = m_redisInterface.getM_pool().getResource();//获取redis对象
			String value = data.toString();
			JSONObject json = new JSONObject(value);
			m_stropt = json.getString("opt");
			m_bpaydate = false;
			m_byn = false;
			if(m_stropt == "INSERT")
			{
				JSONObject json_cur = json.getJSONObject("cur");
				m_strid = json_cur.getString("id");
				m_strcreateDate = json.getString("createDate");
				m_stryn = json.getString("yn");//初始值为1
				m_strtotalPrice = json.getString("totalPrice");
				//查询redis是否有以orderid为key的记录
				if(!jedis.exists(m_strid))//如果不存在
				{
					m_redisInterface.SetKeyValue(jedis,m_strid, "createDate", m_strcreateDate, true);
					m_redisInterface.SetKeyValue(jedis,m_strid, "totalPrice", m_strtotalPrice, true);
					m_redisInterface.SetKeyValue(jedis,m_strid, "yn", m_stryn, true);
				}
				else
					;//说明update已先于insert而到来，这里什么都不做
			}
			else if (m_stropt == "UPDATE")
			{
				JSONObject json_cur = json.getJSONObject("cur");
				m_strid = json_cur.getString("id");
				m_bpaydate = json_cur.has("paydate");
				m_byn = json_cur.has("yn");
				if(m_bpaydate)
					m_strpaydate = json_cur.getString("paydate");
				if(m_byn)
					m_stryn = json_cur.getString("yn");
				if(m_bpaydate || m_byn)//至少有一个数值发生了变更
				{
					if(jedis.exists(m_strid))//查询redis中是否含有以orderid为key的记录
					{
						if(m_byn && (m_stryn == "0"))//yn发生了改变，且数值为0即订单取消
						{
							if(jedis.hexists(m_strid, "Skuids"))//订单取消时已经有确定的订单了
							{
								String Skuids = jedis.hget(m_strid, "Skuids");
								StringTokenizer tokenizer = new StringTokenizer(Skuids, ",");
								while(tokenizer.hasMoreTokens())
								{
									String Skuid = tokenizer.nextToken();
									String MoneyBalance = jedis.hget(m_strid + "-" + Skuid, "MoneyBalance");
									String amountPayable = jedis.hget(m_strid + "-" + Skuid, "amountPayable");
									String Num = jedis.hget(m_strid + "-" + Skuid, "Num");
									String Createtime = jedis.hget(m_strid + "-" + Skuid, "Createtime");
									//取消以orderid 和 skuid为key对应的订单
									SaveSkuTotalMinus(jedis,Skuid,MoneyBalance,amountPayable,Num);
									SaveSkuTimeToalMinus(jedis,Skuid,MoneyBalance,amountPayable,Num,Createtime);
								}
							}
							
						}
						else//跟新paydate或者yn
						{
							if(m_bpaydate)
								m_redisInterface.SetKeyValue(jedis,m_strid, "paydate", m_strpaydate, false);
							if(m_byn)
								m_redisInterface.SetKeyValue(jedis,m_strid, "yn", m_stryn, false);
						}
					}
					else//update先于insert到来,保存数值
					{
						JSONObject json_src = json.getJSONObject("src");
						m_strcreateDate = json.getString("createDate");
						m_strtotalPrice = json.getString("totalPrice");
						//保存数据
						m_redisInterface.SetKeyValue(jedis,m_strid, "createDate", m_strcreateDate, true);
						m_redisInterface.SetKeyValue(jedis,m_strid, "totalPrice", m_strtotalPrice, true);
						if(m_bpaydate)
							m_redisInterface.SetKeyValue(jedis,m_strid, "paydate", m_strpaydate, true);
						if(m_byn)
							m_redisInterface.SetKeyValue(jedis,m_strid, "yn", m_stryn, true);
					}
				}
				
			}
			else
			{
				log.error("OrderConsume: m_stropt is " + m_stropt + ", it is not INSERT or UPDATE");
			}
			
			BusinessInfo.INFO.put(BusinessInfo.KEY_RECEIVE_ORDER, KeyType.CUSTOM, 1,false);
		}catch(Exception e)
		{
			log.error("OrderConsume: HandleMsg," + e);
		}finally
		{
			RedisInterface.getM_pool().returnResource(jedis);
		}
	
	}
	// 以Skuid为维度，进行存储
	private void SaveSkuTotalMinus(Jedis jedis,String Skuid,String MoneyBalance,String amountPayable,String Num)
	{
		try
		{
			// 进行秒杀订单的存储
			String TotalMoney = m_redisInterface.GetValueInRedis(jedis, Skuid, "TotalMoney");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值
			String TotalNum = m_redisInterface.GetValueInRedis(jedis, Skuid, "TotalNum");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值
			String TotalMoneyNew = String.valueOf(Double.parseDouble(TotalMoney) - Double.parseDouble(MoneyBalance) - Double.parseDouble(amountPayable));
			String TotalNumNew = String.valueOf(Double.parseDouble(TotalNum) - Double.parseDouble(Num));
			// 开始保存
			m_redisInterface.SetKeyValue(jedis,m_strid + "-" + Skuid, "Status", "0", true);
			m_redisInterface.SetKeyValue(jedis,Skuid, "TotalMoney", TotalMoneyNew, true);
			m_redisInterface.SetKeyValue(jedis,Skuid, "TotalNum", TotalNumNew, true);
			/*jedis.hset(m_strid + "-" + Skuid, "Status", "0");// 订单使用,现在m_strStatusNow为"0",订单取消状态
			jedis.hset(Skuid, "TotalMoney", TotalMoneyNew);// 保存数据至Redis
			jedis.hset(Skuid, "TotalNum", TotalNumNew);// 保存数据至Redis
*/		} catch (Exception e)
		{
			log.error("OrderamountConsume: SaveSkuTotal error" + e);
		}
	}
	// 以Skuid+Time,进行存储
	private void SaveSkuTimeToalMinus(Jedis jedis,String Skuid,String MoneyBalance,String amountPayable,String Num,String Createtime)
	{
		try
		{
			Date date = new Date(Createtime);// 更新某个时间段里某个skuid的订单状况
			int year = date.getYear();
			int month = date.getMonth();
			int day = date.getDate();
			int hour = date.getHours();
			// 按照另外一个维度来进行保存
			String time_key = "" + year + month + day + hour;
			String TotalMoney = m_redisInterface.GetValueInRedis(jedis, Skuid + "-" + time_key, "TotalMoney");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值
			String TotalNum = m_redisInterface.GetValueInRedis(jedis, Skuid + "-" + time_key, "TotalNum");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值
			String TotalMoneyNew = String.valueOf(Double.parseDouble(TotalMoney) - Double.parseDouble(MoneyBalance) - Double.parseDouble(amountPayable));
			String TotalNumNew = String.valueOf(Double.parseDouble(TotalNum) - Double.parseDouble(Num));
			// 开始保存
			m_redisInterface.SetKeyValue(jedis,Skuid + "-" + time_key, "TotalMoney", TotalMoneyNew, true);
			m_redisInterface.SetKeyValue(jedis,Skuid + "-" + time_key, "TotalNum", TotalNumNew, true);
		/*	jedis.hset(Skuid + "-" + time_key, "TotalMoney", TotalMoneyNew);// 保存数据至Redis
			jedis.hset(Skuid + "-" + time_key, "TotalNum", TotalNumNew);// 保存数据至Redis
*/		} catch (Exception e)
		{
			log.error("OrderamountConsume: SaveSkuTimeToal error," + e);
		}
	}

	private void Consume() throws JDQException
	{
		Authentication auth = new Authentication(m_strAppId, m_strToken);// 应用ID和Token需要申请
		JDQConsumerClient cousmer = new JDQConsumerClient(auth);
		// cousmer.setGroupId("LogInfoReport_TestGroup");
		cousmer.setAutoCommitTimeOfSecond(5);// 自动会帮助提交记录offset,每隔5秒钟提交一次。
		cousmer.setOffsetReset(OffsetReset.Now);
		// 消费的时候会在后台起线程进行消费（正常情况消费线程会一直消费，没有消息的时候会等待消息不退出），主线程不会退出，当所有的消费线程都退出（出现异常的时候退出）的时候主线程退出
		cousmer.consume(new MessageHandler<String, JdwData>()
		{
			public void doMessageHandler(String key, JdwData data, long offset, int partition)
			{
				HandleMsg(key, data, offset, partition);
			}
		}, new JdwKeyDecoder(), new JdwValueDecoder());
	}

	static class JdwKeyDecoder implements Decoder<String>
	{
		@Override
		public String fromBytes(byte[] bytes)
		{
			String messageKey = null;
			try
			{
				messageKey = new String(bytes, "UTF-8");
			} catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
			return messageKey;
		}
	}

	static class JdwValueDecoder implements Decoder<JdwData>
	{
		private JdwDataSerializer decoder;

		JdwValueDecoder()
		{
			this.decoder = new JdwDataSerializer();
		}

		@Override
		public JdwData fromBytes(byte[] bytes)
		{
			final JdwData messageValue = this.decoder.fromBytes(bytes);
			return messageValue;
		}
	}
	/*
	 * class StrDecoder implements Decoder<String> { public String
	 * fromBytes(byte[] bytes) { String value = null; try { value = new
	 * String(bytes, "utf-8"); // value = new String(bytes, "Unicode"); // value
	 * = new String(bytes, "ASCII"); } catch (UnsupportedEncodingException e) {
	 * throw new RuntimeException(e); } return value; } }
	 */
	/*
	 * static class JdwKeyDecoder2 implements Decoder<byte[]> {
	 * 
	 * @Override public byte[] fromBytes(byte[] bytes) { return bytes; } }
	 */

	/*
	 * static class JdwKeyDecoder1 implements Decoder<JdwData> { private
	 * JdwDataSerializer decoder;
	 * 
	 * JdwKeyDecoder1() { this.decoder = new JdwDataSerializer(); }
	 * 
	 * @Override public JdwData fromBytes(byte[] bytes) { final JdwData
	 * messageValue = this.decoder.fromBytes(bytes); return messageValue; } }
	 */

}
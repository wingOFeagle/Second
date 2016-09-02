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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import redis.clients.jedis.Jedis;
import kafka.serializer.Decoder;
import BaseData.SecKill.BussinessInfo.BusinessInfo;
import BaseData.SecKill.CommonInfos.MapCache;
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

public class OrderamountexpandConsume extends Thread
{
	private static String m_strAppId = Tokens.getM_strOrderamountexpandAppId();
	private static String m_strToken = Tokens.getM_strOrderamountexpandToken();
	private static String m_strGroupId = Tokens.getM_strOrderamountexpandGroupId();
	private static Logger log = Logger.getLogger(OrderamountexpandConsumeOld.class);
	private static Map<String, Boolean> m_mapType = new HashMap<String, Boolean>()
	{// 202,206,207,211,301,302
		{
			put("202", true);
			put("206", true);
			put("207", true);
			put("211", true);
			put("301", true);
			put("302", true);
		}
	};
	private String m_strSkuid, m_strOrderid, m_strType, m_strAmount;
	private String m_strMoneyBalance, m_strNum, m_strCreatetime, m_stramountPayable;
	private String m_StatusInRedis, m_hasUsedInRedis;
	private RedisInterface m_redisInterface;
	private String m_strOrderid_Skuid_key;
	private String m_strType_Amount_key;
	private String m_stryn;
	private boolean m_bCreatetime;

	public OrderamountexpandConsume(RedisInterface redisInterface)
	{
		m_redisInterface = redisInterface;
	}

	@Override
	public void run()
	{
		try
		{
			// JDQ_ENV.assignRunningEnv(ENV.OFFLINE);
			System.out.println("OrderamountConsume开始执行");
			Consume();
		} catch (JDQException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void HandleMsg(String key, JdwData data, long offset, int partition)
	{
		log.info("key:" + key + "|||data:" + data.toString() + "|||offset:" + offset + "|||partition:" + partition + "|||Thread:" + Thread.currentThread().getId());
		BusinessInfo.INFO.put(BusinessInfo.KEY_RECEIVE_ORDERAMOUNT, KeyType.CUSTOM, 1, false);
		// 实际处理消息值
		Jedis jedis = null;
		try
		{
			// 可以建立一个jedis对象进行处理
			// 获取消息体内容
			jedis = m_redisInterface.getM_pool().getResource();
			String value = data.toString();
			InitMsg(value);// 解析消息数据流
			if (MapCache.GetMapCache().containsKey(m_strSkuid) && m_mapType.containsKey(m_strType))// 判断skuid的有效性且type有效
			{
				// 判断是否在redis中
				m_strOrderid_Skuid_key = m_strOrderid + "-" + m_strSkuid;
				if (m_redisInterface.keyInRedis(jedis, m_strOrderid))// Redis中是否有Orderid的记录
				{
					m_stryn = m_redisInterface.GetValueInRedis(jedis, m_strOrderid, "yn");
					if (m_stryn == "1")// 订单表中的订单有效
					{
						m_strCreatetime = m_redisInterface.GetValueInRedis(jedis, m_strOrderid, "createDate");// 用orders表里的常见时间来更新
						m_bCreatetime = true;
						// 具体处理逻辑如下
						HandleOrders(jedis);
					}
					else if (m_stryn == "0")
						m_redisInterface.SetKeyValue(jedis,m_strOrderid_Skuid_key, "Status", m_stryn, true);
						//jedis.hset(m_strOrderid_Skuid_key, "Status", m_stryn);// 更新Status的数值为0
					else
						// 为空值，说明没有yn
						log.error("OrderamountConsume: HandleMsg error");
				}
				else
					HandleOrders(jedis);
			}
		} catch (Exception e)
		{
			log.error("OrderamountConsume" + e);
		} finally
		{
			m_redisInterface.getM_pool().returnResource(jedis);
		}
	}

	private void HandleOrders(Jedis jedis)
	{
		// 具体处理逻辑如下
		if (m_redisInterface.keyInRedis(jedis, m_strOrderid_Skuid_key))// 如果新消息对应的key已经在redis中存在了
		{
			m_hasUsedInRedis = m_redisInterface.GetValueInRedis(jedis, m_strOrderid_Skuid_key, "hasUsed");
			if (m_hasUsedInRedis == "0")// 该订单尚没有使用过
				UpdateSkuOrder_valid(jedis);// 用来更新sku对应订单数值
			else if (m_hasUsedInRedis == "1")// hasUsed已经用过，异常
				log.error("OrderamountConsume: hasUsed is still true while seckill has used");
			else
				log.error("OrderamountConsume: hasUsed is illegal while it is not 0 or 1");
		}
		else
			InitStore(jedis);// 不在redis中,进行初始化赋值
	}

	private void UpdateSkuOrder_valid(Jedis jedis)
	{
		try
		{
			// 因为要用到用户的支付价格以及购买数量
			m_strMoneyBalance = m_redisInterface.GetValueInRedis(jedis, m_strOrderid_Skuid_key, "MoneyBalance");
			m_stramountPayable = m_redisInterface.GetValueInRedis(jedis, m_strOrderid_Skuid_key, "amountPayable");
			m_strNum = m_redisInterface.GetValueInRedis(jedis, m_strOrderid_Skuid_key, "Num");
			m_strCreatetime = m_redisInterface.GetValueInRedis(jedis, m_strOrderid_Skuid_key, "Createtime");
			String ResultInRedis = m_redisInterface.GetValueInRedis(jedis, m_strOrderid_Skuid_key, "Result");// 获取Redis中的Result数值
			String ResultNew = String.valueOf(Double.parseDouble(ResultInRedis) - Double.parseDouble(m_strAmount));// 用原来的值减去优惠值
			String PriceCompare = String.valueOf(Double.parseDouble(ResultNew) / Double.parseDouble(m_strNum));
			m_redisInterface.SetKeyValue(jedis,m_strOrderid_Skuid_key, "Result", ResultNew, true);
			//jedis.hset(m_strOrderid_Skuid_key, "Result", ResultNew);// 保存数据至Redis
			// 从内存中查询skuid对应的信息
			String MemVal = MapCache.GetMapCache().get(m_strSkuid).toString();
			String[] Vals = MemVal.split("|");
			String Promo_price_array = Vals[1];
			for (String Promo_price : Promo_price_array.split(","))
			{
				if (PriceCompare == Promo_price)// 价格是促销价格则保存，否则不做什么
				{
					SaveSkuTotalAdd(jedis, Promo_price);// 以sku的总维度进行redis存储
					SaveSkuTimeToalAdd(jedis, Promo_price);// 以sku+time的维度进行redis存储
					SaveSkuidToRedis(jedis);
					break;
				}
			}
		} catch (Exception e)
		{
			log.error("OrderamountexpandConsume: UpdateSkuOrder_valid " + e);
		}
	}

	private void SaveSkuidToRedis(Jedis jedis)
	{
		if (jedis.hexists(m_strOrderid, "Skuids"))// 如果已经有skuid，说明已经有确定的秒杀订单了
		{
			String Skuids = jedis.hget(m_strOrderid, "Skuids");
			String SkuidsNew = Skuids + "," + m_strSkuid;
			m_redisInterface.SetKeyValue(jedis,m_strOrderid, "Skuids", SkuidsNew, true);
			//jedis.hset(m_strOrderid, "Skuids", SkuidsNew);
		}
		else
		// 还没有确定的秒杀订单
		{
			m_redisInterface.SetKeyValue(jedis,m_strOrderid, "Skuids", m_strSkuid, true);
			//jedis.hset(m_strOrderid, "Skuids", m_strSkuid);
		}
	}

	private void InitMsg(String value)
	{
		try
		{
			// 获取的数值
			JSONObject js_value = new JSONObject(value);
			String opt = js_value.getString("opt");
			JSONObject js_obj = null;
			if (opt == "INSERT")
			{
				js_obj = js_value.getJSONObject("cur");
				// 获取数值
				m_strSkuid = js_value.getString("Skuid");
				m_strOrderid = js_value.getString("Orderid");
				m_strType = js_obj.getString("Type");
				m_strAmount = js_obj.getString("Amount");
				m_strType_Amount_key = m_strType + "-" + "Amount";
			}
			else if (opt == "UPDATE")
				log.error("OrderamountexpandConsume: opt is UPDATE");
			else
				log.error("OrderamountexpandConsume: opt is " + opt + ", not INSERT or UPDATE");

		} catch (Exception e)
		{
			log.error("OrderamountexpandConsume : InitMsg" + e);
		}
	}

	// 初始化保存起来
	private void InitStore(Jedis jedis)
	{
		try
		{
			// 把一些初始化的字段保存起来至redis
			String amount = String.valueOf((-1) * Double.parseDouble(m_strAmount));
			m_redisInterface.SetKeyValue(jedis,m_strOrderid_Skuid_key, "m_strType_Amount_key", m_strAmount, true);
			m_redisInterface.SetKeyValue(jedis,m_strOrderid_Skuid_key, "Status", "1", true);
			m_redisInterface.SetKeyValue(jedis,m_strOrderid_Skuid_key, "hasUsed", "0", true);
			m_redisInterface.SetKeyValue(jedis,m_strOrderid_Skuid_key, "Result", amount, true);
			/*jedis.hset(m_strOrderid_Skuid_key, m_strType_Amount_key, m_strAmount);// 存储
			jedis.hset(m_strOrderid_Skuid_key, "Status", "1");
			jedis.hset(m_strOrderid_Skuid_key, "hasUsed", "0");// 未被使用过
			jedis.hset(m_strOrderid_Skuid_key, "Result", amount);// 初始化的时候，未负值
*/		} catch (Exception e)
		{
			log.error("OrderamountexpandConsume:InitStore error " + e);
		}
	}

	// 以Skuid为维度，进行存储
	private void SaveSkuTotalAdd(Jedis jedis, String Promo_price)
	{
		try
		{
			// 进行秒杀订单的存储
			String TotalMoney = m_redisInterface.GetValueInRedis(jedis, m_strSkuid, "TotalMoney");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值
			String TotalNum = m_redisInterface.GetValueInRedis(jedis, m_strSkuid, "TotalNum");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值
			String TotalMoneyNew = String.valueOf(Double.parseDouble(TotalMoney) + Double.parseDouble(m_strMoneyBalance) + Double.parseDouble(m_stramountPayable));
			String TotalNumNew = String.valueOf(Double.parseDouble(TotalNum) + Double.parseDouble(m_strNum));
			// 开始保存
			m_redisInterface.SetKeyValue(jedis,m_strOrderid_Skuid_key, "hasUsed", "1", true);
			m_redisInterface.SetKeyValue(jedis,m_strSkuid, "TotalMoney", TotalMoneyNew, true);
			m_redisInterface.SetKeyValue(jedis,m_strSkuid, "TotalNum", TotalNumNew, true);
			/*jedis.hset(m_strOrderid_Skuid_key, "hasUsed", "1");// 订单已经使用
			jedis.hset(m_strSkuid, "TotalMoney", TotalMoneyNew);// 保存数据至Redis
			jedis.hset(m_strSkuid, "TotalNum", TotalNumNew);// 保存数据至Redis
*/			if (!jedis.hexists(m_strSkuid, "Promo_price"))// 不存在的时候再进行存储
				m_redisInterface.SetKeyValue(jedis,m_strSkuid, "Promo_price", Promo_price, true);
				//jedis.hset(m_strSkuid, "Promo_price", Promo_price);// 保存数据至Redis
		} catch (Exception e)
		{
			log.error("OrderamountConsume: SaveSkuTotal error" + e);
		}
	}

	// 以Skuid+Time,进行存储
	private void SaveSkuTimeToalAdd(Jedis jedis, String Promo_price)
	{
		try
		{
			Date date = new Date(m_strCreatetime);// 更新某个时间段里某个skuid的订单状况
			int year = date.getYear();
			int month = date.getMonth();
			int day = date.getDate();
			int hour = date.getHours();
			// 按照另外一个维度来进行保存
			String time_key = "" + year + month + day + hour;
			String TotalMoney = m_redisInterface.GetValueInRedis(jedis, m_strSkuid + "-" + time_key, "TotalMoney");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值
			String TotalNum = m_redisInterface.GetValueInRedis(jedis, m_strSkuid + "-" + time_key, "TotalNum");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值
			String TotalMoneyNew = String.valueOf(Double.parseDouble(TotalMoney) + Double.parseDouble(m_strMoneyBalance) + Double.parseDouble(m_stramountPayable));
			String TotalNumNew = String.valueOf(Double.parseDouble(TotalNum) + Double.parseDouble(m_strNum));
			// 开始保存
			m_redisInterface.SetKeyValue(jedis,m_strSkuid + "-" + time_key, "TotalMoney", TotalMoneyNew, true);
			m_redisInterface.SetKeyValue(jedis,m_strSkuid + "-" + time_key, "TotalNum", TotalNumNew, true);
			/*jedis.hset(m_strSkuid + "-" + time_key, "TotalMoney", TotalMoneyNew);// 保存数据至Redis
			jedis.hset(m_strSkuid + "-" + time_key, "TotalNum", TotalNumNew);// 保存数据至Redis
*/			if (!jedis.hexists(m_strSkuid + "-" + time_key, "Promo_price"))// 不存在的时候再进行存储
				m_redisInterface.SetKeyValue(jedis,m_strSkuid + "-" + time_key, "Promo_price", Promo_price, true);
				//jedis.hset(m_strSkuid + "-" + time_key, "Promo_price", Promo_price);// 保存数据至Redis
		} catch (Exception e)
		{
			log.error("OrderamountConsume: SaveSkuTimeToal error," + e);
		}
	}

	private void Consume() throws JDQException
	{
		log.info("OrderamountConsume开始执行");
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
	 * private void UpdateSkuOrder_invalid(Jedis jedis) { //
	 * 把一些字段保存起来，保存redis,只是进行保存，并不进行逻辑处理 jedis.hset(m_strOrderid_Skuid_key,
	 * "MoneyBalance", m_strMoneyBalance); jedis.hset(m_strOrderid_Skuid_key,
	 * "amountPayable", m_stramountPayable); jedis.hset(m_strOrderid_Skuid_key,
	 * "Createtime", m_strCreatetime); jedis.hset(m_strOrderid_Skuid_key, "Num",
	 * m_strNum); jedis.hset(m_strOrderid_Skuid_key, "Price", m_strPrice); //
	 * Status保留原来的状态0，Reslut也不会进行跟新，hasUsed维持原来的0未用过状态 }
	 */
	/*
	 * // 以Skuid+Time,进行存储 private void SaveSkuTimeToalMinus(Jedis jedis) { try
	 * { Date date = new Date(m_strCreatetime);// 更新某个时间段里某个skuid的订单状况 int year
	 * = date.getYear(); int month = date.getMonth(); int day = date.getDate();
	 * int hour = date.getHours(); // 按照另外一个维度来进行保存 String time_key = "" + year
	 * + month + day + hour; String TotalMoney =
	 * m_redisInterface.GetValueInRedis(jedis, m_strSkuid + "-" + time_key,
	 * "TotalMoney");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值 String TotalNum =
	 * m_redisInterface.GetValueInRedis(jedis, m_strSkuid + "-" + time_key,
	 * "TotalNum");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值 String TotalMoneyNew =
	 * String.valueOf(Double.parseDouble(TotalMoney) -
	 * Double.parseDouble(m_strMoneyBalance) -
	 * Double.parseDouble(m_stramountPayable)); String TotalNumNew =
	 * String.valueOf(Double.parseDouble(TotalNum) -
	 * Double.parseDouble(m_strNum)); // 开始保存 jedis.hset(m_strSkuid + "-" +
	 * time_key, "TotalMoney", TotalMoneyNew);// 保存数据至Redis
	 * jedis.hset(m_strSkuid + "-" + time_key, "TotalNum", TotalNumNew);//
	 * 保存数据至Redis } catch (Exception e) {
	 * log.error("OrderamountConsume: SaveSkuTimeToal error," + e); } }
	 */
	/*
	 * // 以Skuid为维度，进行存储 private void SaveSkuTotalMinus(Jedis jedis) { try { //
	 * 进行秒杀订单的存储 String TotalMoney = m_redisInterface.GetValueInRedis(jedis,
	 * m_strSkuid, "TotalMoney");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值 String
	 * TotalNum = m_redisInterface.GetValueInRedis(jedis, m_strSkuid,
	 * "TotalNum");// 有可能会返回为0，这种情况一般是key时候初始化而返回的数值 String TotalMoneyNew =
	 * String.valueOf(Double.parseDouble(TotalMoney) -
	 * Double.parseDouble(m_strMoneyBalance) -
	 * Double.parseDouble(m_stramountPayable)); String TotalNumNew =
	 * String.valueOf(Double.parseDouble(TotalNum) -
	 * Double.parseDouble(m_strNum)); // 开始保存 jedis.hset(m_strOrderid_Skuid_key,
	 * "Status", m_strStatusNow);// 订单使用,现在m_strStatusNow为"0",订单取消状态
	 * jedis.hset(m_strSkuid, "TotalMoney", TotalMoneyNew);// 保存数据至Redis
	 * jedis.hset(m_strSkuid, "TotalNum", TotalNumNew);// 保存数据至Redis } catch
	 * (Exception e) { log.error("OrderamountConsume: SaveSkuTotal error" + e);
	 * } }
	 */
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
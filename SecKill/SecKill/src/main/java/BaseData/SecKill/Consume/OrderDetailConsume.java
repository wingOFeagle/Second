package BaseData.SecKill.Consume;

/**
 * 
 */
/**
 * @author sunguangsheng
 *
 */

import java.io.UnsupportedEncodingException;

import org.apache.log4j.Logger;

import kafka.serializer.Decoder;
import BaseData.SecKill.BussinessInfo.BusinessInfo;
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

public class OrderDetailConsume extends Thread
{

	private static String m_strAppId = Tokens.getM_strOrderDetailAppId();
	private static String m_strToken = Tokens.getM_strOrderDetailToken();
	private static Logger log = Logger.getLogger(OrderDetailConsume.class);

	@Override
	public void run()
	{
		try
		{
			// JDQ_ENV.assignRunningEnv(ENV.OFFLINE);
			System.out.println("OrderDetailConsumer开始执行");
			Consume();
		} catch (JDQException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void HandleMsg(String key, JdwData data, long offset,int partition)
	{
		log.info("key:" + key 
				+ "|||data:" + data.toString()
				+ "|||offset:" + offset + "|||partition:" + partition
				+ "|||Thread:" + Thread.currentThread().getId());
		BusinessInfo.INFO.put(BusinessInfo.KEY_RECEIVE_ORDERDETAIL, KeyType.CUSTOM, 1,false);
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
			public void doMessageHandler(String key, JdwData data, long offset,
					int partition)
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
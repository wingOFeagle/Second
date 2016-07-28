package BaseData.SecKill;

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

import com.jd.bdp.jdq.auth.Authentication;
import com.jd.bdp.jdq.config.ENV;
import com.jd.bdp.jdq.config.OffsetReset;
import com.jd.bdp.jdq.consumer.JDQConsumerClient;
import com.jd.bdp.jdq.consumer.zk.MessageHandler;
import com.jd.bdp.jdq.control.JDQ_ENV;

import ex.JDQException;

public class OrderamountConsumer extends Thread
{

	private static String m_strAppId = Tokens.getM_strOrderamountAppId();
	private static String m_strToken = Tokens.getM_strOrderamountToken();
	private static Logger log = Logger.getLogger(OrderamountConsumer.class);

	@Override
	public void run()
	{
		try
		{
			// JDQ_ENV.assignRunningEnv(ENV.OFFLINE);
			Consume();
		} catch (JDQException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void Consume() throws JDQException
	{
		Authentication auth = new Authentication(m_strAppId, m_strToken);// 应用ID和Token需要申请
		JDQConsumerClient cousmer = new JDQConsumerClient(auth);

		// cousmer.setGroupId("LogInfoReport_TestGroup");
		cousmer.setAutoCommitTimeOfSecond(5);// 自动会帮助提交记录offset,每隔5秒钟提交一次。
		cousmer.setOffsetReset(OffsetReset.Before);
		// 消费的时候会在后台起线程进行消费（正常情况消费线程会一直消费，没有消息的时候会等待消息不退出），主线程不会退出，当所有的消费线程都退出（出现异常的时候退出）的时候主线程退出
		cousmer.consume(new MessageHandler<String, String>()
		{
			public void doMessageHandler(String key, String data, long offset,
					int partition)
			{
				log.info("key:" + key + " - " + 
									"|||data:" + data + " - " + 
									"|||offset:" + offset + 
									"|||partition:" + partition + 
									"|||Thread:" + Thread.currentThread().getId());
			}
		}, new StrDecoder(), new StrDecoder());
	}

	class StrDecoder implements Decoder<String>
	{

		public String fromBytes(byte[] bytes)
		{
			String value = null;
			try
			{
				value = new String(bytes, "utf-8");
			} catch (UnsupportedEncodingException e)
			{
				throw new RuntimeException(e);
			}
			return value;
		}
	}
}
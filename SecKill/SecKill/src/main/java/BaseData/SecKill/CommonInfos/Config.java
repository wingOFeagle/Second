package BaseData.SecKill.CommonInfos;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import org.apache.log4j.Logger;
import org.json.JSONObject;

public class Config 
{
	//声明LogReport所需要的变量
	private final static String m_strLogConfigFile = "/export/App/seckill.jd.local/resources/log4j.properties";//日志文件
	private final static String m_strConfigVarFileJson = "/export/App/seckill.jd.local/resources/config.properties";//配置文件的Json文件
	private static int m_nRecvOrderThreadNum;//并发接收order数据的线程数目
	private static int m_nRecvOrderDetailThreadNum;//并发接收orderdetal数据的线程数目
	private static int m_nRecvOrderamountThreadNum;//并发接收orderamount数据的线程数目
	private static int m_nRecvOrderamountexpandThreadNum;//并发接收orderamountexpand数据的线程数目
	private static int m_nRedisExpireTLL;//设置redis key的有效过期时间
	private static String m_strServerName;
	private static int m_nServerId;
	private static int m_nInterval;
	private static String m_strUdpInfoIp;
	private static int m_nUdpInfoPort;
	private static String m_strHostIp;
	private static Logger log = Logger.getLogger(Config.class);
	

	private static String getHostIp()
	{
		StringBuilder IFCONFIG = new StringBuilder();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() //不是回送地址 ：127.0.0.1
							&& !inetAddress.isLinkLocalAddress() 
							&& inetAddress.isSiteLocalAddress()) {
						IFCONFIG.append(inetAddress.getHostAddress().toString());
					}
				}
			}
		} catch (SocketException ex) {
		}
		return IFCONFIG.toString();
	}
	
	//获取参数
	public static void GetParas()
	{
		// 从json文件中读取变量
		log.info("Begin GetParas");
		BufferedReader reader = null;
		StringBuilder strbuilder = new StringBuilder();
		try
		{
			reader = new BufferedReader(new FileReader(getmStrconfigvarfilejson()));
			String str = null;
			// 读取json文件
			while ((str = reader.readLine()) != null)
				strbuilder.append(str);
			reader.close();
		} catch (IOException e)
		{
			e.printStackTrace();
			log.error(e.getMessage());
		} finally
		{

			if (reader != null)
			{
				try
				{
					reader.close();
				} catch (IOException e)
				{
					e.printStackTrace();
					log.error(e);
				}
			}
		}
		// 以上代码为读取变量部分,下面进行变量读取
		String strAll = strbuilder.toString();
		
		log.info("StrAll: " + strAll);
		org.json.JSONObject js = new org.json.JSONObject(strAll);
		m_nRecvOrderThreadNum = js.getInt("nRecvOrderThreadNum");//接收线程数
		m_nRecvOrderDetailThreadNum = js.getInt("nRecvOrderDetailThreadNum");//接收线程数
		m_nRecvOrderamountThreadNum = js.getInt("nRecvOrderamountThreadNum");//接收线程数
		m_nRecvOrderamountexpandThreadNum = js.getInt("nRecvOrderamountexpandThreadNum");//接收线程数
		m_nRedisExpireTLL = js.getInt("nRedisExpireTLL");
		m_strServerName = js.getString("ServerName");
		m_nServerId = js.getInt("ServerId");
		m_nInterval = js.getInt("Interval");
		m_strUdpInfoIp = js.getString("UdpInfoIp");//数目配置
		m_nUdpInfoPort = js.getInt("UdpInfoPort");
		//获取本机ip
		m_strHostIp = getHostIp();
		//打印获取的变量
		log.info("m_nRecvOrderThreadNum: " + m_nRecvOrderThreadNum);
		log.info("m_nRecvOrderDetailThreadNum: " + m_nRecvOrderDetailThreadNum);
		log.info("m_nRecvOrderamountThreadNum: " + m_nRecvOrderamountThreadNum);
		log.info("m_nRecvOrderamountexpandThreadNum: " + m_nRecvOrderamountexpandThreadNum);
		log.info("m_nRedisExpireTLL: " + m_nRedisExpireTLL);
		log.info("m_strServerName: " + m_strServerName);
		log.info("m_nServerId: " + m_nServerId);
		log.info("m_nInterval: " + m_nInterval);
		log.info("m_strUdpInfoIp: " + m_strUdpInfoIp);
		log.info("m_nUdpInfoPort: " + m_nUdpInfoPort);
		log.info("m_strHostIp: " + m_strHostIp);
	}
	
	public static String getmStrlogconfigfile()
	{
		return m_strLogConfigFile;
	}
	public static String getmStrconfigvarfilejson()
	{
		return m_strConfigVarFileJson;
	}
	public static int getM_nRecvOrderThreadNum()
	{
		return m_nRecvOrderThreadNum;
	}
	public static int getM_nRecvOrderDetailThreadNum()
	{
		return m_nRecvOrderDetailThreadNum;
	}
	public static int getM_nRecvOrderamountThreadNum()
	{
		return m_nRecvOrderamountThreadNum;
	}
	public static int getM_nRecvOrderamountexpandThreadNum()
	{
		return m_nRecvOrderamountexpandThreadNum;
	}
	public static int getM_nRedisExpireTLL()
	{
		return m_nRedisExpireTLL;
	}
	public static String getM_strServerName()
	{
		return m_strServerName;
	}
	public static int getM_nServerId()
	{
		return m_nServerId;
	}
	public static int getM_nInterval()
	{
		return m_nInterval;
	}
	public static String getM_strUdpInfoIp()
	{
		return m_strUdpInfoIp;
	}
	public static int getM_nUdpInfoPort()
	{
		return m_nUdpInfoPort;
	}
	public static String getM_strHostIp()
	{
		return m_strHostIp;
	}
		
}

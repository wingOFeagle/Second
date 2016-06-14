package test_udp;

/**
 * Hello world!
 *
 */


import com.jd.info.calc.InfoManager;
import com.jd.info.calc.UDP.UDPInfoManager;
import com.jd.info.calc.UDP.impl.UDPService;


public class App
{

	private final static int m_nCycleNum = 1000;
	
	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		InfoManager INFO = UDPInfoManager.Builder.newBuilder()
				.serverName("TestServer")
				.serverIp("10.8.135.71:23456")
				.serverId(10)
				.interval(300)
				.udpIp("10.191.191.172")
				.udpPort(23456)
				.udpService(new UDPService())
				.build();
		
		if (!INFO.init())
		{
			System.out.println("监控信息初始化失败");
		}
		for (int i = 0;i < m_nCycleNum;i++)
		{
			System.out.println("Begin start thread " + i);
			new SendThread(INFO).start();
		}
	}

}


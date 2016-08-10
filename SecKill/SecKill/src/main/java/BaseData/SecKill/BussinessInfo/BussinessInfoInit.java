package BaseData.SecKill.BussinessInfo;

import org.apache.log4j.Logger;

import BaseData.SecKill.CommonInfos.Config;

import com.jd.info.calc.UDP.UDPInfoManager;
import com.jd.info.calc.UDP.impl.UDPService;

/**
 * Hello world!
 * 
 */
public class BussinessInfoInit
{

	private static Logger log = Logger.getLogger(BussinessInfoInit.class);
	private static Config config;
	
	public static void ConfigBusinessInfo()
	{
		// 创建InfoManager
		try
		{
			if ((null == config.getM_strUdpInfoIp()) || (0 == config.getM_nUdpInfoPort()))
			{
				log.info("m_strUdpInfoIp:" + config.getM_strUdpInfoIp() + 
						"; m_nUdpInfoPort:" + Integer.toString(config.getM_nUdpInfoPort()));
				Thread.sleep(1000);// 睡眠等待参数配置完成，只有在启动的时候才会发生
			}
			log.info("ConfigBusinessInfo_ip: " + config.getM_strHostIp());
			log.info("ConfigBusinessInfo_m_strUdpInfoIp: " + config.getM_strUdpInfoIp());
			log.info("ConfigBusinessInfo_m_nInterval: " + Integer.toString(config.getM_nInterval()));
			log.info("ConfigBusinessInfo_m_nUdpInfoPort: " + Integer.toString(config.getM_nUdpInfoPort()));

			BusinessInfo.INFO = UDPInfoManager.Builder.newBuilder()
					.serverName(config.getM_strServerName())
					.serverIp(config.getM_strHostIp())
					.serverId(config.getM_nServerId())
					.interval(config.getM_nInterval())
					.udpIp(config.getM_strUdpInfoIp())
					.udpPort(config.getM_nUdpInfoPort())
					.udpService(new UDPService())
					.build();
			if (!BusinessInfo.INFO.init())
			{
				log.error("监控信息初始化失败");
			}
		} catch (Exception e)
		{
			log.error("监控信息初始化失败", e);
		}
	}
}

package BaseData.SecKill.Main;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import BaseData.SecKill.BussinessInfo.BussinessInfoInit;
import BaseData.SecKill.CommonInfos.Config;
import BaseData.SecKill.Consume.OrderConsume;
import BaseData.SecKill.Consume.OrderDetailConsume;
import BaseData.SecKill.Consume.OrderamountConsume;
import BaseData.SecKill.Consume.OrderamountexpandConsume;

import com.jd.bdp.jdq.config.ENV;
import com.jd.bdp.jdq.control.JDQ_ENV;

import ex.JDQException;

/**
 * Hello world!
 * 
 */
public class SeckillMain
{
	private static Logger log = Logger.getLogger(SeckillMain.class);

	public static void TaskHandle()
	{
		//多线程处理orderamount
		for(int i = 0;i < Config.getM_nRecvOrderamountThreadNum();i++)
			new OrderamountConsume().start();
		//多线程处理orderamount_expand
		for(int i = 0;i < Config.getM_nRecvOrderamountexpandThreadNum();i++)
			new OrderamountexpandConsume().start();
		//多线程处理order
		for(int i = 0;i < Config.getM_nRecvOrderThreadNum();i++)
			new OrderConsume().start();
		//多线程处理orderdetail
		for(int i = 0;i < Config.getM_nRecvOrderDetailThreadNum();i++)
			new OrderDetailConsume().start();
	}

	public static void main(String[] args)
	{
		/*
		 * //环境的设置 try { JDQ_ENV.assignRunningEnv(ENV.OFFLINE); } catch
		 * (JDQException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
		
		String strFile = "";

		//读取参数与设置日志配置
		try
		{
			//配置日志文件
			strFile = Config.getmStrlogconfigfile();
			System.out.println("strFile:" + strFile);
			assert (!strFile.isEmpty());
			PropertyConfigurator.configure(strFile);
			log.info("InitLog Finished in InitLog");
			//配置文件参数
			Config.GetParas();
			//配置InfoManager
			BussinessInfoInit.ConfigBusinessInfo();
			
		} catch (Exception e)// 打印错误日志
		{
			log.error(e);
		}
		
		//主业务处理
		TaskHandle();

	}
}

package BaseData.SecKill.Main;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import BaseData.SecKill.BussinessInfo.BussinessInfoInit;
import BaseData.SecKill.CommonInfos.Config;
import BaseData.SecKill.CommonInfos.MapCache;
import BaseData.SecKill.CommonInfos.RedisInterface;
import BaseData.SecKill.Consume.OrderConsume;
import BaseData.SecKill.Consume.OrderDetailConsume;
import BaseData.SecKill.Consume.OrderamountConsume;
import BaseData.SecKill.Consume.OrderamountexpandConsume;
import BaseData.SecKill.Sqlquery.Sql_brand_goods;
import BaseData.SecKill.Sqlquery.Sql_seckill_activity;
import BaseData.SecKill.Sqlquery.Sql_seckill_goods;

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
		/*//redis接口
		RedisInterface redisInterface = new RedisInterface();
		//多线程处理orderamount
		for(int i = 0;i < Config.getM_nRecvOrderamountThreadNum();i++)
			new OrderamountConsume(redisInterface).start();
		//多线程处理orderamount_expand
		for(int i = 0;i < Config.getM_nRecvOrderamountexpandThreadNum();i++)
			new OrderamountexpandConsume(redisInterface).start();
		//多线程处理order
		for(int i = 0;i < Config.getM_nRecvOrderThreadNum();i++)
			new OrderConsume().start();*/
		
		/*//多线程处理orderdetail
		for(int i = 0;i < Config.getM_nRecvOrderDetailThreadNum();i++)
			new OrderDetailConsume().start();*/
		
		//并发线程用来处理sql查询
		Sql_brand_goods brand_goods = new Sql_brand_goods();
		brand_goods.UpdateData();
		Sql_seckill_activity sk_activity = new Sql_seckill_activity();
		sk_activity.UpdateData();
		Sql_seckill_goods sk_goods = new Sql_seckill_goods();
		sk_goods.UpdateData();
		//定时做清理任务,重新更新数据
		MapCache.ClearData();
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

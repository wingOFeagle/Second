package BaseData.SecKill.Sqlquery;

import java.sql.Date;
import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

import BaseData.SecKill.CommonInfos.DateInterface;
import BaseData.SecKill.CommonInfos.MapCache;
import BaseData.SecKill.CommonInfos.SqlToken;

//拉取品牌表的数据
public class Sql_seckill_activity
{
	private Connection m_sqlconn = null;
	private Statement m_stmt;
	private static Logger log = Logger.getLogger(Sql_seckill_activity.class);

	public Sql_seckill_activity()
	{
		log.info("begin Sql_seckill_activity");
		// 初始化sql对象
		// MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
		// 避免中文乱码要指定useUnicode和characterEncoding
		// 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
		// 下面语句之前就要先创建javademo数据库
		String url = "jdbc:mysql://" + SqlToken.getM_strPromotion_miaosha_hostip() + ":"
				+ Integer.toString(SqlToken.getM_nPromotion_hostport()) + "/" + SqlToken.getM_strPromotion_miaosha_database() + "?" + "user="
				+ SqlToken.getM_strPromotion_miaosha_username() + "&" + "password=" + SqlToken.getM_strPromotion_miaosha_passwd() + "&"
				+ "useUnicode=true&characterEncoding=UTF8&autoReconnect = true";

		try
		{
			// 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
			// 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
			Class.forName("com.mysql.jdbc.Driver").newInstance();// 动态加载mysql驱动
			// or:
			// JDBCType.Driver driver = new JDBCType.Driver();
			// or：
			// new com.mysql.jdbc.Driver();

			log.error("Sql_seckill_activity 成功加载MySQL驱动程序");
			// 一个Connection代表一个数据库连接
			m_sqlconn = DriverManager.getConnection(url);
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			if (m_sqlconn == null)
			{
				log.error("Sql_seckill_activity m_sqlconn is null");
			}
			m_stmt = m_sqlconn.createStatement();
		} catch (SQLException e)
		{
			log.error("Sql_seckill_activity MySql操作错误");
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public Statement getStatement()
	{
		return m_stmt;
	}

	public void UpdateData()
	{
		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				try
				{
					log.info("Begin to query data in Sql_seckill_activity");
					String time_now = DateInterface.getNowDate();
					String sql = String.format("select sku_id,product_name,jd_price,promo_price,start_date,end_date from cms_seckillactivity where status=2 and start_date <= '%s' and end_date >= '%s';",time_now,time_now);
					log.info("Sql_seckill_activity sql: " + sql);
					// stmt.execute(sql);
					// 执行sql语句
					ResultSet result = m_stmt.executeQuery(sql);
					while (result.next())
					{
						int row = result.getRow();
						long sku_id = result.getLong(1);
						String sku_name = result.getString(2);
						double jd_price = result.getDouble(3);
						double promo_price = result.getDouble(4);
						String start_time = result.getString(5);
						String end_time = result.getString(6);
						log.info("Sql_seckill_activity|||" + sku_id + "|" + sku_name + "|" + jd_price + "|" + promo_price + "|" + start_time + "|" + end_time);
						//格式化数据进行存储
						String strKey = Long.toString(sku_id);
						//jd_price|promo_price|start_time|end_time|source_id,当对应sku_id有多个数值时，用逗号分隔如jd_price1,jd_price2|;
						//这里的source对应秒杀的种类，我们自定义为：品牌秒杀source_id为1，精品秒杀2,量贩秒杀3
						int source_id = 2;
						String strValue =  Double.toString(jd_price) + "|" + Double.toString(promo_price) + "|" + start_time + "|" +  end_time +  "|" + Integer.toString(source_id);
						MapCache.InsertData(strKey, strValue);
					}

				} catch (Exception e)
				{
					log.error(e);
				}
			}
		};
		// 调度线程
		Timer timer = new Timer();
		long period = 5 * 60 * 1000;
		// // 每天的date时刻执行task，每隔min重复执行
		timer.schedule(task, 0, period);
	}
}
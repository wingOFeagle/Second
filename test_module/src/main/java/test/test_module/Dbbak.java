package test.test_module;

/**
 * Hello world!
 *
 */
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;


public class Dbbak
{
	public void showTimer() throws Exception
	{
		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				try
				{
					if (Dbbak.isRun())
					{
						System.out.println("任务开始执行了");
					} else
					{
						System.out.println("时间未到");
					}
				} catch (Exception e)
				{
					e.printStackTrace();
				}

			}
		};

		String fileStr = Thread.currentThread().getContextClassLoader()
				.getResource("").toURI().getPath();
		System.out.println(fileStr);
		String fileStr1 = this.getClass().getClassLoader().getResource("").toURI().getPath();
		System.out.println(fileStr1);
		String str2 = this.getClass().getClassLoader().getResourceAsStream("").toString();
		System.out.println(str2);
		
		String file = fileStr + "dbbak.properties";
		InputStream in = new FileInputStream(new File(file));
		Properties prop = new Properties();
		prop.load(in);
		String time = prop.get("time").toString(); // 设置执行时间
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);// 每天
		// 定制每天的....执行，
		String t[] = time.split(":");
		calendar.set(year, month, day, Integer.parseInt(t[0]),
				Integer.parseInt(t[1]), 00);

		java.util.Date date = calendar.getTime();
		Timer timer = new Timer();

		int period = 2 * 1000;
		// 每天的date时刻执行task，每隔2秒重复执行
		timer.schedule(task, date, period);
	}

	public static void main(String[] args) throws Exception
	{
		new Dbbak().showTimer();
	}

	public static boolean isRun() throws Exception
	{
		String fileStr = Thread.currentThread().getContextClassLoader()
				.getResource("").toURI().getPath();
		System.out.println(fileStr);
		String file = fileStr + "dbbak.properties";
		InputStream in = new FileInputStream(new File(file));
		Properties prop = new Properties();
		prop.load(in);
		String time = prop.get("time").toString();
		System.out.println(time);

		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm-ss");
		String str = sdf.format(new java.util.Date());
	
		if (str.split("-")[0].equals(time))
		{
			return true;
		}
		return false;

	}
}

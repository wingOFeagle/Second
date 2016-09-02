package BaseData.SecKill.CommonInfos;
import java.text.*;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class DateInterface
{
	
	/**
	   * 获取现在时间
	   * 
	   * @return 返回时间类型 yyyy-MM-dd HH:mm:ss
	   */
	private static Logger log = Logger.getLogger("DateInterface");
	public static String getNowDate()
	{
	   Date currentTime = new Date();
	   SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	   String dateString = formatter.format(currentTime);
	   log.info("Time now is :" + dateString);
	   return dateString;
	}
/*	public static String getNowTime() throws InterruptedException
	{
		Date currentTime = new Date();
		Date planTime = new Date("2016-08-08 15:00:00");
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
		String timeString = formatter.format(currentTime);
		long timenow = currentTime.getTime();
		Thread.sleep(3000);
		Date time = new Date();
		long timeafter = new Date().getTime();
		System.out.println("time minus: " + (timeafter - timenow));
		return timeString;
	}
	public static void main(String[] args) throws InterruptedException
	{
		String str = getNowTime();
		System.out.println("time: " + str);
	}*/
}

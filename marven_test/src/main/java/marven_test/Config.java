package marven_test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.SimpleFormatter;

public class Config
{

	public static void main(String[] args)
	{
		// TODO Auto-generated method stub
		System.out.println(GetStartHour());

	}

	public static String GetDayNow()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = simpleFormatter.format(calendar.getTime());
		System.out.println(time);
		return time;
	}

	public static String GetLastDayTime()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = simpleFormatter.format(calendar.getTime());
		System.out.println(time);
		return time;
	}

	public static String GetStartHour()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = simpleFormatter.format(calendar.getTime());
		System.out.println(time);
		return time;
	}

	public static String GetEndHour()
	{
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);
		SimpleDateFormat simpleFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = simpleFormatter.format(calendar.getTime());
		System.out.println(time);
		return time;
	}

}

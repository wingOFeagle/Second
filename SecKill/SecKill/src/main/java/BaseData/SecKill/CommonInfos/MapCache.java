package BaseData.SecKill.CommonInfos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.log4j.Logger;

public class MapCache
{
	private static Map m_mapCache = new HashMap<String, String>();
	private static Logger log = Logger.getLogger(MapCache.class);

	// 如果有相同的key，那么进行value叠加;
	// 在调试阶段我们输出，以验证是否有冲突的情况
	public static Map GetMapCache()
	{
		return m_mapCache;
	}
	public static void InsertData(String key, String value)
	{
		try
		{
			if (!m_mapCache.containsKey(key))
			{
				m_mapCache.put(key, value);
			} 
			else
			// 已经有这个sku_id了
			{
				// 硬编码的好处是可以及时发现不规范的错误；但缺点就是项目如果有变更那么修改的地方比较多
				//原来的数据
				String value_old = m_mapCache.get(key).toString();
				StringTokenizer strToken_old = new StringTokenizer(value_old, "|");
				List<String> data_old = new ArrayList<String>();
				while (strToken_old.hasMoreElements())
				{
					data_old.add(strToken_old.nextToken());
				}
				String jd_price_old = data_old.get(0);
				String promo_price_old = data_old.get(1);
				String start_time_old = data_old.get(2);
				String end_time_old = data_old.get(3);
				String source_id_old = data_old.get(4);
				//要添加的字段
				String value_now = value;
				StringTokenizer strToken_now = new StringTokenizer(value_now, "|");
				List<String> data_now = new ArrayList<String>();
				while (strToken_now.hasMoreElements())
				{
					data_now.add(strToken_now.nextToken());
				}
				String jd_price_now = data_now.get(0);
				String promo_price_now = data_now.get(1);
				String start_time_now = data_now.get(2);
				String end_time_now = data_now.get(3);
				String source_id_now = data_now.get(4);
				//组成的新字段
				String jd_price_new = jd_price_old + "," + jd_price_now;
				String promo_price_new = promo_price_old + "," + promo_price_now;
				String start_time_new = start_time_old + "," + start_time_now;
				String end_time_new = end_time_old + "," + end_time_now;
				String source_id_new = source_id_old + "," + source_id_now;
				String value_new = jd_price_new + "|" +  promo_price_new + "|" + start_time_new + "|" + end_time_new + "|" + source_id_new;
				log.error("Repeated: " + value_new);
				m_mapCache.put(key, value_new);
			}
		} catch (Exception e)
		{
			log.error("MapCache: InsertData" + e);
		}
	}
	public static void ClearData()
	{
		TimerTask task = new TimerTask()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				log.error("MapCache ClearData");
				m_mapCache.clear();
			}
		};
		Timer timer = new Timer();
		long period = 24 * 60 * 60 * 1000;//24h
		timer.schedule(task,0,period);//每隔24h进行清理一次
	}
	
	/*
	 * public static void main(String[] args) { map1.put("sku_id", new
	 * HashMap<String, String>() { put("sku_name","sku_name"); } );
	 * map1.put("sku_id",new HashMap<String, String>() { { put("Name", "June");
	 * put("QQ", "4889983"); put("sex","femal"); } }); map1.put("sku_id",new
	 * HashMap<String, String>() { { put("Name", "June1"); put("QQ",
	 * "48899831"); put("sex","femal1"); } });
	 * Iterator<java.util.Map.Entry<String, Map<String, String>>> iter =
	 * map1.entrySet().iterator();
	 * 
	 * while(iter.hasNext()) { Map.Entry entry = iter.next(); final Object key =
	 * entry.getKey(); Object value = entry.getValue();
	 * System.out.println("key: "+ key.toString());
	 * System.out.println("value: "+ value.toString());
	 * map1.put(key.toString(),new HashMap<String,String>(){ {
	 * put("Name",map1.get(key.toString()).get("Name") + ",June1");
	 * put("QQ",map1.get(key.toString()).get("QQ") + ",++++");
	 * put("sex",map1.get(key.toString()).get("sex") + ",++++"); } });
	 * 
	 * }
	 * 
	 * iter = map1.entrySet().iterator(); while(iter.hasNext()) { Map.Entry
	 * entry = iter.next(); final Object key = entry.getKey(); Object value =
	 * entry.getValue(); System.out.println("key: "+ key.toString());
	 * System.out.println("value: "+ value.toString()); } }
	 */
}

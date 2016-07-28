package BaseData.SecKill;

import org.apache.log4j.Logger;

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

	public static void main( String[] args )
    {
    		//环境的设置
		try
		{
			JDQ_ENV.assignRunningEnv(ENV.OFFLINE);
		} catch (JDQException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        log.info( "Hello World!" );
        System.out.println("Hello Word!");
        
        System.out.println("Orderamount开始执行"); 
        new OrderamountConsumer().start();
        System.out.println("Orderamountexpand开始执行"); 
        new OrderamountexpandConsumer().start();
        
    }
}

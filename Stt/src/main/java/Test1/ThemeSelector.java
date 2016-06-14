/**
 * 
 */
/**
 * @author sunguangsheng
 *
 */
package Test1;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;;

public class ThemeSelector{
	private int i_NumThreads;
	private ExecutorService executorservice ;
	private Logger log = Logger.getLogger(ThemeSelector.class);
	
	public ThemeSelector(int NumThreads)
	{
		log.info("enter construction of ThemeSelector");
		i_NumThreads = NumThreads;
		executorservice = Executors.newFixedThreadPool(i_NumThreads);		
	}
	public void Execute(final byte[] bytes)
	{
		//输出接收到的字节
		executorservice.execute(new Runnable()
		{
			
			public void run()
			{
				// TODO Auto-generated method stub
				log.info("enter Execute of ThemeSelector");
				String str = new String(bytes);
				System.out.print(str);
			}
		});
	}
};

package WordCount;

import java.lang.Thread.UncaughtExceptionHandler;

public class Counter {
	 
    public volatile static int count = 0;
 
    public  synchronized static void inc() 
    {
        //这里延迟1毫秒，使得结果明显
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
        }
        System.out.println(Thread.currentThread().getName() + ":  "+ count);
        count++;
    }
 
    @SuppressWarnings("deprecation")
	public static void main(String[] args) 
    {
 
        //同时启动1000个线程，去进行i++计算，看看实际结果
        for (int i = 0; i < 1000; i++) 
        {
        	final int tt = i;
            Thread tg = new Thread(new Runnable() {
                @Override
                public void run() 
                {
                	try
					{
                		if( tt % 100 == 0)
                		{
                			Thread.sleep(100);
                			System.out.println("tt " + tt + " is sleeping");
                		}
					} catch (InterruptedException e)
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    Counter.inc();
                }
                });
            UncaughtExceptionHandler eh = new UncaughtExceptionHandler()
			{
				
				@Override
				public void uncaughtException(Thread t, Throwable e)
				{
					// TODO Auto-generated method stub
					System.out.println(t.currentThread().getName() + "get caught");
					e.printStackTrace();
				}
			};
            tg.setUncaughtExceptionHandler(eh);
            tg.start();
            try
			{
				tg.join();
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        //这里每次运行的值都有可能不同,可能为1000
       // runningThreadNum.await();
        System.out.println("运行结果:Counter.count=" + Counter.count);
    }
}

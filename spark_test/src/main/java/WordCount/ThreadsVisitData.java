package WordCount;

class ShareData
{
	private int x = 0;

	public synchronized void addx()
	{

		x++;
		System.out.println("x++ : " + x);
	}

	public synchronized void subx()
	{

		x--;
		System.out.println("x-- : " + x);
	}

	public void Getx()
	{
		System.out.println("数值 x:" + x);
	}
}

class MyRunnable1 implements Runnable
{
	private ShareData share1 = null;
	public MyRunnable1(ShareData share1)
	{
		this.share1 = share1;
	}

	public void run()
	{
		for (int i = 0; i < 10000; i++)
		{
			share1.addx();
		}
	}
}

class MyRunnable2 implements Runnable
{
	private ShareData share2 = null;

	public MyRunnable2(ShareData share2)
	{
		this.share2 = share2;
	}

	public void run()
	{
		for (int i = 0; i < 5000; i++)
		{
			share2.subx();
		}
	}
}

public class ThreadsVisitData
{

	public static void main(String[] args)
	{
		ShareData share = new ShareData();
		Thread t1 = new Thread(new MyRunnable1(share));
		t1.start();
		Thread t2 = new Thread(new MyRunnable2(share));
		t2.start();
		try
		{
			t1.join();
			t2.join();
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		share.Getx();

	}
}
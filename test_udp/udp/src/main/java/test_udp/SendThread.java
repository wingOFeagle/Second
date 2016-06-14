package test_udp;
/**
 * 
 */
/**
 * @author sunguangsheng
 *
 */
import com.jd.info.calc.InfoManager;
import com.jd.info.calc.KeyType;

class SendThread extends java.lang.Thread
{
	private final long m_lNum = 100000;
	private String key = "TestUdp";
	private InfoManager m_InfoManager;
	public SendThread(InfoManager info)
	{
		// TODO Auto-generated constructor stub
		m_InfoManager = info;
	}
	@java.lang.Override
	public void run()
	{
		java.lang.System.out.println("Begin start run");
		for (int i = 0;i < m_lNum;i++)
			m_InfoManager.put(key, KeyType.CUSTOM, 1,false);
	}
}

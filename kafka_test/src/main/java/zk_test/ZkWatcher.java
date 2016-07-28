package zk_test;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;


public class ZkWatcher extends Thread
{
    private ZkClient m_zkClient;
    private String m_strZkPath;
    private static Log log = LogFactory.getLog(ZkWatcher.class);
  
    /**
     * 初始化zookeeper
     */
    public ZkWatcher(String strZkHost,int nZkConnectionTimeout,String strZkPath)
    {
    	try
    	{
    		System.out.println("begin ZkWatcher");
    		m_strZkPath = strZkPath;
        	//初始化zclient对象
            m_zkClient = new ZkClient(strZkHost, nZkConnectionTimeout);
            //检测路径存在
            if(!m_zkClient.exists(m_strZkPath)) 
            {
            	log.warn("zkpath:" + m_strZkPath + "is going to be created!");
            	m_zkClient.create(strZkPath,new Long(System.currentTimeMillis()), CreateMode.EPHEMERAL);
            	log.warn("zkpath:" + m_strZkPath + "created!");
             }
            else
            	log.error("zkpath" + m_strZkPath + "has already exists");
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
        
    }
    public ZkClient GetzkClient()
    {
    	return m_zkClient;
    }
    public void watch()
    {
        //检测数据的变化并写数据
        m_zkClient.subscribeDataChanges(m_strZkPath, new IZkDataListener() 
        {
            public void handleDataDeleted(String dataPath) throws Exception {
                log.warn("the node 'dataPath'===>");    
            }
            
            public void handleDataChange(String dataPath, Object data) throws Exception {
                log.warn("the node 'dataPath'===>" + dataPath + ", data has changed.it's data is "+String.valueOf(data));
            }
        });
    }
}
    

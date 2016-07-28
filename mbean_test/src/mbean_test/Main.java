package mbean_test;

import   java.lang.management.ManagementFactory;
import   javax.management.MBeanServer;
import   javax.management.ObjectName;
public   class   Main 
{
     public   static   void   main(String[] args)  throws   Exception{
         MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
         ObjectName name =  new   ObjectName( "agent:name=test" );
         Test testMBean =  new   Test();
         mBeanServer.registerMBean(testMBean, name);
         String proName = ManagementFactory.getRuntimeMXBean().getName();
         System.out.println("Name: " + proName);
         String pid = proName.split("@")[0];
         System.out.println("pid: "+ pid);
         Thread.sleep( 5000000 );
     }
}
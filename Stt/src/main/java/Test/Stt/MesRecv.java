package Test.Stt;

import java.util.Enumeration;
import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Logger;

import Test1.ThemeSelector;


/**
 * Hello world!
 *
 */
public class MesRecv 
{
	private static final int RCV_BUFFER_SIZE = 128 * 1024 * 16;// 16KB
	private static final int MAX_PACKET_SIZE = 65507;
	private static final String CHARSET_NAME = "utf-8";
	private ThemeSelector selector;
	private static Logger log = Logger.getLogger(MesRecv.class);

	
	public MesRecv(int NumThreads)
	{
		selector = new ThemeSelector(NumThreads);
	}
	
	public static String getHostIp() {
		StringBuilder IFCONFIG = new StringBuilder();
		log.info("enter get HostIp");
		
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() //不是回送地址 ：127.0.0.1
							&& !inetAddress.isLinkLocalAddress() 
							&& inetAddress.isSiteLocalAddress()) {
						IFCONFIG.append(inetAddress.getHostAddress().toString());
					}
				}
			}
		} catch (SocketException ex) {
		}
		return IFCONFIG.toString();
	}
	
	private void HandleMsg(int port) throws IOException
	{
		log.info("enter HandleMsg");
		DatagramChannel channel = DatagramChannel.open();
		DatagramSocket socket = channel.socket();
		socket.setReceiveBufferSize(RCV_BUFFER_SIZE);
		socket.bind(new InetSocketAddress(port));
		
		ByteBuffer buffer = ByteBuffer.allocateDirect(MAX_PACKET_SIZE);
		
		try {
			while (true) {
				log.info("enter cycle in HandleMsg");
				channel.receive(buffer);
				buffer.flip();
				byte[] bytes = new byte[buffer.remaining()];
				buffer.get(bytes);
				selector.Execute(bytes);
				buffer.clear();
			}
		} catch (Exception e) {
			System.out.println("Recv mes Wrong!");
		}	
	}
	public void InitLog4j(String file)
	{
		try
		{
			PropertyConfigurator.configure(file);
		}
		catch(Exception e)
		{
			System.out.println("can not open" + file + ",Error infomation:" + e.getMessage());;
			
		}
	}
	
    public static void main( String[] args ) throws IOException
    {
    	int port = 5555;
    //	String file = "/home/stat/test/log4j.properties";
    	String file = "src/log4j.properties";
    	if (!new File(file).exists())
    	{
    		System.out.println(file + " not exist!");
    		System.exit(-1);
    	}
    	
    	if ( args.length >= 0 )
    	{
    		//port = Integer.parseInt(args[0]);
    		int NumThreads = 5;
    		MesRecv mesg = new MesRecv(NumThreads);
    		mesg.InitLog4j(file);
    		String str = mesg.getHostIp();
    		System.out.println("hostip:" + str);
    		System.out.println("port:" + Integer.toString(port));
    		mesg.HandleMsg(port);
    	}
    	else
    	{
    		throw new RuntimeException("please set port");
    		
    	}
    }
}

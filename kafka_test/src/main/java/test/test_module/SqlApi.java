package test.test_module;

import java.sql.DriverManager;
import java.sql.JDBCType;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.Statement;

import scala.sys.process.ProcessBuilderImpl.DaemonBuilder;


public class SqlApi
{
	private Connection m_sqlconn=null;
	private Statement m_stmt;
	
	public SqlApi(String SqlHost,int SqlPort,String Database,String User,String Passwd)
	{
		System.out.println("begin SqlApi");
		// 初始化sql对象
		// MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
		// 避免中文乱码要指定useUnicode和characterEncoding
		// 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
		// 下面语句之前就要先创建javademo数据库
		String url = "jdbc:mysql://" + SqlHost + ":"
				+ Integer.toString(SqlPort) + "/" + Database + "?" + "user="
				+ User + "&" + "password=" + Passwd + "&"
				+ "useUnicode=true&characterEncoding=UTF8&autoReconnect = true";

		try
		{
			// 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
			// 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
			Class.forName("com.mysql.jdbc.Driver").newInstance();// 动态加载mysql驱动
			// or:
			//JDBCType.Driver driver = new JDBCType.Driver();
			// or：
			// new com.mysql.jdbc.Driver();

			System.out.println("成功加载MySQL驱动程序");
			// 一个Connection代表一个数据库连接
			m_sqlconn = DriverManager.getConnection(url);
			// Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
			if(m_sqlconn == null)
			{
				System.out.println("m_sqlconn is null");
				System.out.println("m_sqlconn is null");
				System.out.println("m_sqlconn is null");
			}
			m_stmt = m_sqlconn.createStatement();
		} catch (SQLException e)
		{
			System.out.println("MySQL操作错误");
			e.printStackTrace();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	public Statement getStatement()
	{
		return m_stmt;
	}

}
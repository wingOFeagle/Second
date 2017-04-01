
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import marven_test.Config;

/**
 * Servlet implementation class UpdateQueryTime
 */
@WebServlet("/UpdateQueryTime")
public class UpdateQueryTime extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private String m_strHost = "localhost";
	private String m_strDatabase = "sgs";
	private String m_strUser = "root";
	private String m_strPass = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public UpdateQueryTime()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		try
		{
			PrintWriter printWriter = response.getWriter();

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			java.sql.ResultSet sqlRst;
			java.sql.Connection sqlConn = java.sql.DriverManager.getConnection("jdbc:mysql://" + m_strHost + "/" + m_strDatabase, m_strUser,
					m_strPass);
			java.sql.Statement sqlStmt = sqlConn.createStatement();
			StringBuilder sb = new StringBuilder("");
			String StartTime = Config.GetStartHour();
			String EndTime = Config.GetEndHour();
			String sqlQuery = String.format("select count(*) from times where type=1 and time>='%s' and time<='%s'", StartTime, EndTime);
			sb.append("sql: " + sqlQuery + "\r\n");
			sqlRst = sqlStmt.executeQuery(sqlQuery);
			String num = "";
			while (sqlRst.next())
				num = sqlRst.getString(1);
			if (num.equals("0"))// 没有当前小时段的数据那么进行插入
			{
				int type = 1;
				int num_1 = 1;
				String sql = String.format("insert into times(type,num) values('%d','%d')", type, num_1);
				sb.append("sql: " + sql + "\r\n");
				sqlStmt.execute(sql);
			}
			else// 更新数据库
			{
				int type = 1;
				String sql = String.format("update times set num=num+1 where type='%d' and time>='%s' and time<='%s'", type, StartTime, EndTime);
				sb.append("sql: " + sql + "\r\n");
				sqlStmt.execute(sql);

			}
			// 更新一下总数据统计的数据
			{
				int type = 0;
				String sql = String.format("update times set num=num+1 where type='%d'", type);
				sb.append("sql: " + sql);
				sqlStmt.execute(sql);
			}
			printWriter.println(sb.toString());
			;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}

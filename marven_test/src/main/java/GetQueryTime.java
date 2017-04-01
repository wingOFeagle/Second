
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import marven_test.Config;

/**
 * Servlet implementation class GetQueryTime
 */
@WebServlet("/GetQueryTime")
public class GetQueryTime extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private String m_strHost = "localhost";
	private String m_strDatabase = "sgs";
	private String m_strUser = "root";
	private String m_strPass = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GetQueryTime()
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
		// TODO Auto-generated method stub
		PrintWriter printWriter = response.getWriter();
		String starttime_v = request.getParameter("starttime");
		String endtime_v = request.getParameter("endtime");
		if ((starttime_v == null) || (starttime_v.equals("")))
			starttime_v = Config.GetLastDayTime();
		if ((endtime_v == null) || (endtime_v.equals("")))
			endtime_v = Config.GetDayNow();
		String host = "localhost";
		String database = "sgs";
		String user = "root";
		String pass = "";
		int type = 0;
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			java.sql.ResultSet sqlRst;
			java.sql.Connection sqlConn = java.sql.DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, user, pass);
			java.sql.Statement sqlStmt = sqlConn.createStatement();
			String sqlQuery = String.format("select num,time from times where time>='%s'and time<='%s' and type=1", starttime_v, endtime_v);
			sqlRst = sqlStmt.executeQuery(sqlQuery);
			StringBuilder sb = new StringBuilder("");
			int tt = 0;
			sb.append("[");
			while (sqlRst.next())
			{
				if (tt > 0)// 还没有添加过
					sb.append(",");
				int num = sqlRst.getInt(1);
				String time = sqlRst.getString(2);
				sb.append("{num:'" + num + "',time:'" + time + "'}");
				tt++;
			}
			sb.append("]");
			printWriter.println(sb.toString());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

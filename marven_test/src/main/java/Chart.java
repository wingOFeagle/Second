
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Chart
 */
@WebServlet("/Chart")
public class Chart extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private String m_strHost = "localhost";
	private String m_strDatabase = "sgs";
	private String m_strUser = "root";
	private String m_strPass = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Chart()
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
			String user_v = request.getParameter("txtUser");
			String Pwd_v = request.getParameter("txtPwd");

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			java.sql.ResultSet sqlRst;
			java.sql.Connection sqlConn = java.sql.DriverManager.getConnection("jdbc:mysql://" + m_strHost + "/" + m_strDatabase, m_strUser,
					m_strPass);
			java.sql.Statement sqlStmt = sqlConn.createStatement();
			String sqlQuery = "";
			StringBuilder sb = new StringBuilder("");
			String name = "0";
			int age = 0;
			sqlQuery = "select name,age from test";
			sqlRst = sqlStmt.executeQuery(sqlQuery);
			sb.append("[");
			int tt = 0;
			while (sqlRst.next())
			{
				if (tt > 0)// 还没有添加过
					sb.append(",");
				name = sqlRst.getString(1);
				age = sqlRst.getInt(2);
				sb.append("{name:'" + name + "',age:'" + age + "'}");
				tt++;
			}
			sb.append("]");
			printWriter.println(sb.toString());
			// response.sendRedirect("http://localhost:8080/marven_test/html/test2.html");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}

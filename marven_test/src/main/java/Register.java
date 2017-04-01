
import java.io.Console;
import java.io.IOException;
import java.io.PrintWriter;

import javax.naming.NamingEnumeration;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class RegisterAccount
 */
@WebServlet("/Register")
public class Register extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private String m_strHost = "localhost";
	private String m_strDatabase = "sgs";
	private String m_strUser = "root";
	private String m_strPass = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Register()
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
		try
		{
			String CCheckUser = "CheckUser";
			String CRegister = "Register";
			PrintWriter printWriter = response.getWriter();
			String type = request.getParameter("method");
			String user_v = request.getParameter("txtUser");

			Class.forName("com.mysql.jdbc.Driver").newInstance();
			java.sql.ResultSet sqlRst;
			java.sql.Connection sqlConn = java.sql.DriverManager.getConnection("jdbc:mysql://" + m_strHost + "/" + m_strDatabase, m_strUser,
					m_strPass);
			java.sql.Statement sqlStmt = sqlConn.createStatement();
			String sqlQuery = "";
			StringBuilder sb = new StringBuilder("");
			String num = "0";
			if (type.equals(CCheckUser))
			{
				sqlQuery = String.format("select count(*) from test where name='%s'", user_v);
				sqlRst = sqlStmt.executeQuery(sqlQuery);
				while (sqlRst.next())
					num = sqlRst.getString(1);
				if (num.equals("0"))// 说明还没有注册
					sb.append("0");
				else
					sb.append("1");
			}
			else if (type.equals(CRegister))
			{
				String Pwd_v = request.getParameter("txtPwd");
				String sql = String.format("insert into test(name,password)  values('%s','%s')", user_v, Pwd_v);
				sqlStmt.execute(sql);
				sqlQuery = String.format("select count(*) from test where name='%s'", user_v);
				sqlRst = sqlStmt.executeQuery(sqlQuery);
				while (sqlRst.next())
					num = sqlRst.getString(1);
				if (num.equals("0"))
					sb.append("0");// 没有成功注册
				else
					sb.append("1");// 注册成功
			}
			printWriter.println(sb.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

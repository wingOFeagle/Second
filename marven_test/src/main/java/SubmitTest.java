
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class SubmitTest
 */
@WebServlet("/Submit_Test")
public class SubmitTest extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public SubmitTest()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException
	{
		// TODO Auto-generated method stub
	}

	/**
	 * @see Servlet#getServletConfig()
	 */
	public ServletConfig getServletConfig()
	{
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		PrintWriter printWriter = response.getWriter();
		String fname_v = request.getParameter("fname");
		String lname_v = request.getParameter("lname");
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
			String sqlQuery = String.format("select count(*) from test where name='%s'", fname_v);
			sqlRst = sqlStmt.executeQuery(sqlQuery);
			StringBuilder sb = new StringBuilder("");
			sb.append("test\r\n");
			sb.append("fname: " + fname_v + "\r\n" + "lname: " + lname_v + "\r\n");
			while (sqlRst.next())
			{
				String num = sqlRst.getString(1);
				sb.append("num: " + num  + ";");
			}
			printWriter.println(sb.toString());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}

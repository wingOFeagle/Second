
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class LoadServlet
 */
@WebServlet("/LoadServlet")
public class LoadServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoadServlet()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException
	{
		// TODO Auto-generated method stubs
	}

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		// TODO Auto-generated method stub
		PrintWriter printWriter = response.getWriter();
		printWriter.println("This is a test");
		// RequestDispatcher RequetsDispatcherObj =
		// request.getRequestDispatcher("/mysql1.jsp");
		// RequetsDispatcherObj.forward(request, response);
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
			String sqlQuery = String.format("select * from test2 where type=%d", type);
			sqlRst = sqlStmt.executeQuery(sqlQuery);
			StringBuilder sb = new StringBuilder("");
			sb.append("<table border='1' width='50%' summary='Summary' bgcolor='blue' bordercolorlight='#CC99FF' cellpadding='2' bordercolordark='#FFFFFF' cellspacing='0'>");
			sb.append("<tr><th align='center'>Num</th>");
			sb.append("<th align='center'>Time</th>");
			sb.append("<th align='center'>Type</th></tr>");

			while (sqlRst.next())
			{
				sb.append("<tr>");
				String num = sqlRst.getString(1);
				String time = sqlRst.getString(2);
				String type1 = sqlRst.getString(3);
				sb.append("<td align='left'>" + num + "</td>");
				sb.append("<td align='center'>" + time + "</td>");
				sb.append("<td align='right'>" + type1 + "</td>");
				sb.append("</tr>");
			}
			sb.append("</table>");
			printWriter.println(sb.toString());
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}

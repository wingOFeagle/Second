
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import javax.swing.OverlayLayout;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private String m_strHost = "localhost";
	private String m_strDatabase = "sgs";
	private String m_strUser = "root";
	private String m_strPass = "";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Login()
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
			response.setContentType("text/html;charset=gb2312");
			
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
			String num = "0";
			sqlQuery = String.format("select count(*) from test where name='%s' and password", user_v, Pwd_v);
			boolean IsLogin = false;
			sqlRst = sqlStmt.executeQuery(sqlQuery);
			while (sqlRst.next())
				num = sqlRst.getString(1);
			
			//sqlStmt.close();//关闭，否则会有内存泄漏
			
			if (num.equals("0"))// 说明还没有注册
				sb.append("0");
			else// 已经注册过了
			{
				sb.append("1");
				IsLogin = true;
			}
			if (!IsLogin)
			{
				sb.append("没有此用户: " + user_v);
				printWriter.println(sb.toString());
			}
			else
			{
				HttpSession session = request.getSession();
				session.setMaxInactiveInterval(120);// 时间间隔120s
				String sessionName = (String) session.getAttribute("name");//如果没有，
				if (sessionName != null)
				{
					RequestDispatcher rd = request.getRequestDispatcher("/test1");
					rd.forward(request, response);
					return;
				}

				String paraName = user_v;

				session.setAttribute("name", paraName);
				RequestDispatcher rd = request.getRequestDispatcher("/test1");
				rd.forward(request, response);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

}

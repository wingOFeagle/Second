
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HelloServlet
 */
@WebServlet("/HelloServlet")
public class HelloServlet extends HttpServlet
{
	private static final long serialVersionUID = 1L;
	private String m_ServletName;
	private ServletContext m_ServletContext;
	private ServletConfig m_Config;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public HelloServlet()
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
		m_Config = config;
		m_ServletName = config.getServletName();
		m_ServletContext = config.getServletContext();
	}

	/**
	 * @see Servlet#getServletConfig()
	 */
	public ServletConfig getServletConfig()
	{
		// TODO Auto-generated method stub
		return m_Config;
	}

	/**
	 * @see Servlet#getServletInfo()
	 */
	public String getServletInfo()
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
		try
		{
			init();
			response.setContentType("text/html;charset=GB2312");
			// response.setCharacterEncoding("GB2312");
			// response.setStatus(200);
			// response.setHeader("Cache-Control", "no-cache");
			PrintWriter out = response.getWriter();
			out.println("<html>");
			out.println("<font size=5 color=red>www.it315.org</font><br>");
			out.println("<marquee>" + new Date() + "</marquee>");
			out.println(this.getClass().getClassLoader().getClass().getName() + "<br>");
			out.println("<font size=5 color=blue>ServletConfig</font><br>");
			out.println(m_ServletName + "<br>");
			out.println("<font size=5 color=blue>ServletContext</font><br>");
			out.println(m_ServletContext + "<br>");
			Enumeration enumeration = m_ServletContext.getInitParameterNames();
			while (enumeration.hasMoreElements())
			{
				String key = (String) enumeration.nextElement();
				String value = m_ServletContext.getInitParameter(key);
				out.println(key + "=" + value + "<br>");
			}
			out.println(m_ServletContext.getRealPath("/") + "<br>");
			out.println("<font size=5 color=blue>test Chinese</font><br>");
			out.println("这是一段中文测试.");
			out.println("<font size=5 color=blue>button</font><br>");
			out.println("<form action='LoadServlet1'" + "method='POST'>" + "第一个参数：<input type='text' name='key1'><br>"
					+ "第二个参数: <textarea name='key2'></textarea><br>" + "<input type='submit' value='提交'> " + "</form>");
			out.println("<font size=5 color=blue>LoadFile</font><br><br>");
			out.println("<a href='/marven_test/LoadServlet1'><input type='button' />Hello Load</a> <br>");
			out.println("<font size=4 color=blue>动态文件下载</font><br>");
			out.println("</html>");

			// out.close();
			// m_ServletContext.log("this is a test");
			// log("this is a test");
		}
		catch (Exception e)
		{
			log(e.toString());
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

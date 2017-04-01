
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

/**
 * Servlet implementation class CookieServer
 */
@WebServlet("/CookieServer")
public class CookieServer extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CookieServer()
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
		response.setContentType("text/html;charset=gb2312");
		PrintWriter out = response.getWriter();

		String name = request.getParameter("name");
		String nickname = request.getParameter("nickname");
		if ((null == name) || (null == nickname))
		{
			out.println("请传递参数！");
			return;
		}
		if ("".equals(name.trim()) || "".equals(nickname.trim()))
		{
			out.println("参数不能为空");
			return;
		}
		else
		{
			Cookie ckName = new Cookie("name", name);
			Cookie ckNickname = new Cookie("nickname", nickname);
			ckNickname.setMaxAge(60 * 60);
			Cookie ckEmail = new Cookie("email", "sun@jd.com");
			Cookie ckPhone = new Cookie("phone", "111111");
			response.addCookie(ckName);
			response.addCookie(ckNickname);
			response.addCookie(ckEmail);
			response.addCookie(ckPhone);
		}

		String lastNickname = null;
		Cookie[] cookies = request.getCookies();
		for (int i = 0; (cookies != null) && (i < cookies.length); i++)
		{
			if ("nickname".equals(cookies[i].getName()))
			{
				lastNickname = cookies[i].getValue();
				break;
			}
		}
		if (lastNickname != null)
			out.println("欢迎您,<b>" + lastNickname + "<b>!!<br>");
		else
			out.println("欢迎您，新客人!<br>");
		String CookieHeader = request.getHeader("Cookie");
		if (CookieHeader != null)
		{
			out.println("请求头cookie如下：<br>");
			out.println("Cookie: " + CookieHeader);
		}
		else
		{
			out.println("请求头中没有cookie！");
		}
	}

}

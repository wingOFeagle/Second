
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class home
 */
@WebServlet("/test")
public class test extends HttpServlet
{
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public test()
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
		PrintWriter printWriter = response.getWriter();

		HttpSession session = request.getSession();
		printWriter.println("time: " + session.getCreationTime());
		printWriter.println("id: " + session.getId());

		String name =  (String) session.getAttribute("name");
		printWriter.println("name: " + name);
		
		if ((null == name) || (name.trim().equals("")) || (name.trim().equals("null")))
		{
			response.sendRedirect("http://localhost:8080/marven_test/html/test1.html");
			return;
		}

		response.sendRedirect("http://localhost:8080/marven_test/html/test1.html?name=" + name);
	}
}

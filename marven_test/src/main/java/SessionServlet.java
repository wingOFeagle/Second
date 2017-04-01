

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SessionServlet
 */
@WebServlet("/SessionServlet")
public class SessionServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SessionServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/html;charset=gb2312");
		PrintWriter printWriter = response.getWriter();
		HttpSession sesson = request.getSession();
		Integer sessionCount = (Integer)sesson.getAttribute("ok");
		int count1 = 0;
		if(sessionCount != null)
		{
			count1 = sessionCount.intValue();
		}
		printWriter.println("当前会话发生了" + (++count1) + "次访问<br>");
		printWriter.println(sesson.getId());
		sesson.setAttribute("ok", new Integer(count1));
	}

}

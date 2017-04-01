

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Button1_Servlet
 */
@WebServlet("/Button1_Servlet")
public class Button1_Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Button1_Servlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter printWriter = response.getWriter();
		Enumeration<String> enumeration = request.getParameterNames();
		while (enumeration.hasMoreElements())
		{
			String key = (String) enumeration.nextElement();
			String value =(String) request.getParameter(key);
			printWriter.println(key + ":" + value);
		}
		//printWriter.close();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		//response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
//		PrintWriter printWriter = response.getWriter();
//		Enumeration<String> enumeration = request.getAttributeNames();
//		while (enumeration.hasMoreElements())
//		{
//			String key = (String) enumeration.nextElement();
//			String value =(String) request.getAttribute(key);
//			printWriter.println(key + ":" + value);
//		}
//		printWriter.println("how to pass paras");
		//doGet(request, response);
	}

}

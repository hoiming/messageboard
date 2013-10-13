package com.haiming.messageboard.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.haiming.messageboard.bean.DateTimeInfo;
import com.haiming.messageboard.bean.User;
import com.haiming.messageboard.logic.ConcreteLoginHandler;
import com.haiming.messageboard.logic.LoginHandler;
import com.haiming.messageboard.logic.TestDateTimeInfo;
import com.haiming.messageboard.logic.TimeInterface;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/TestServlet")
public class TestServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TestServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		LoginHandler login = new ConcreteLoginHandler();
		if (login.verify(username, password)) {
			// 成功验证，转到登录页面
			User u = new User(username, password);
			request.getSession().setAttribute("user", u);
			TimeInterface time = new TestDateTimeInfo();
			DateTimeInfo dtinfo = new DateTimeInfo(time.getDateTimeInfo());
			request.getSession().setAttribute("dateTimeInfo", dtinfo);
			response.sendRedirect("index.jsp");
		}
	}

}

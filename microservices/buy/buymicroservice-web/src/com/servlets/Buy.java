package com.servlets;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import com.beans.BuyService;

/**
 * Servlet implementation class Buy
 */
@WebServlet("/Buy")
public class Buy extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@EJB 
	BuyService bean;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Buy() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        setAccessControlHeaders(response);
		String body = IOUtils.toString(request.getReader());
		Cookie[] cookies = null;
		Cookie cookie = null;
		cookies = request.getCookies();
		
		String token = null;
		System.out.println("Cookies");
		for (int i = 0; i < cookies.length; i++) {
			System.out.println("Cookie");
            cookie = cookies[i];
            
            if( cookie.getName().contentEquals("auth_token")) {
            	token = cookie.getValue();
            }
         }
		
		if(token != null) {
			 bean.buy(body, token);
		}
	}
	
	@Override
	  protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
	          throws ServletException, IOException {
	      setAccessControlHeaders(resp);
	      resp.setStatus(HttpServletResponse.SC_OK);
	  }
	
	  private void setAccessControlHeaders(HttpServletResponse resp) {
		  System.out.println("Setting headers");
	      resp.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
	      resp.setHeader("Access-Control-Allow-Methods", "POST,GET,OPTIONS");
	      resp.setHeader("Access-Control-Allow-Credentials", "true");
	      resp.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
	  }


}

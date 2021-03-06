package com.servlets;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import org.json.HTTP;
import org.json.JSONException;

import com.beans.PaymentsService;

/**
 * Servlet implementation class Pay
 */
@WebServlet("/Pay")
public class Pay extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    @EJB
    PaymentsService bean;
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Pay() {
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
    	
        StringBuffer jb = new StringBuffer();
        String line = null;
        String body = IOUtils.toString(request.getReader());
        System.out.println(body);
        JSONObject json = new JSONObject(body);
        String token=json.get("token").toString();
        int amount=Integer.valueOf(json.get("amount").toString());
        String description=json.get("description").toString();
        System.out.println(token);
        if(bean.creditPayment(token,amount,description)) {
        	response.getWriter().append("success");
        }else {
        	response.setStatus(401);
        }

    }

}

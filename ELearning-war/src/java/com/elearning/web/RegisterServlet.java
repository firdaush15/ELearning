package com.elearning.web;

import com.elearning.entities.User;
import com.elearning.entities.ELearningException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "RegisterServlet", urlPatterns = {"/Register"})
public class RegisterServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        String name = request.getParameter("fullname");
        // Capture the role from the form
        String role = request.getParameter("role");
        
        ELearningDelegate delegate = new ELearningDelegate();
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>Registration Status</title></head><body>");
            try {
                // Pass the role to the constructor
                User newUser = new User(user, pass, name, role);
                delegate.getFacade().registerUser(newUser);
                
                out.println("<h2 style='color:green; text-align:center; margin-top:50px;'>Registration Successful!</h2>");
                out.println("<p style='text-align:center;'>Welcome, " + name + " (" + role + "). You can now <a href='login.html'>Login</a>.</p>");
                
            } catch (ELearningException ex) {
                out.println("<h2 style='color:red; text-align:center; margin-top:50px;'>Registration Failed</h2>");
                out.println("<p style='text-align:center;'>" + ex.getMessage() + "</p>");
                out.println("<p style='text-align:center;'><a href='register.html'>Try Again</a></p>");
            }
            out.println("</body></html>");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
package com.elearning.web;

import com.elearning.entities.User;
import com.elearning.entities.ELearningException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/Login"})
public class LoginServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String user = request.getParameter("username");
        String pass = request.getParameter("password");
        
        ELearningDelegate delegate = new ELearningDelegate();
        
        try {
            // 1. Validate credentials using the EJB
            User foundUser = delegate.getFacade().validateUser(user, pass);
            
            if (foundUser != null) {
                // 2. Login Success: Create a Session
                HttpSession session = request.getSession();
                session.setAttribute("loggedUser", foundUser);
                
                // 3. Redirect to Dashboard
                response.sendRedirect("Dashboard");
            } else {
                // 4. Login Failed: Redirect back with error
                response.sendRedirect("login.html?error=1");
            }
            
        } catch (ELearningException ex) {
            ex.printStackTrace();
            response.sendRedirect("login.html?error=1");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
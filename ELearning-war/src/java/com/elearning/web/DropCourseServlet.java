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

@WebServlet(name = "DropCourseServlet", urlPatterns = {"/DropCourse"})
public class DropCourseServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Security Check
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        
        if (currentUser == null) {
            response.sendRedirect("login.html");
            return;
        }

        // 2. Get Data
        String courseCode = request.getParameter("code");
        ELearningDelegate delegate = new ELearningDelegate();
        
        // 3. Call Logic
        try {
            delegate.getFacade().dropCourse(currentUser.getUsername(), courseCode);
        } catch (ELearningException ex) {
            ex.printStackTrace(); // Log error
        }
        
        // 4. Redirect back to list
        response.sendRedirect("MyEnrollments");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
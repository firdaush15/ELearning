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

@WebServlet(name = "EnrollCourseServlet", urlPatterns = {"/EnrollCourse"})
public class EnrollCourseServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        
        if (currentUser == null) {
            response.sendRedirect("login.html");
            return;
        }

        String courseCode = request.getParameter("code");
        ELearningDelegate delegate = new ELearningDelegate();
        
        try {
            // Call the EJB to enroll
            delegate.getFacade().enrollStudent(currentUser.getUsername(), courseCode);
            // Redirect to the "My Enrollments" page
            response.sendRedirect("MyEnrollments"); 
        } catch (ELearningException ex) {
            // If error (e.g., already enrolled), go back to list with error message
            response.sendRedirect("CourseList?error=" + ex.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
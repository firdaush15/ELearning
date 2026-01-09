package com.elearning.web;

import com.elearning.entities.ELearningException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DeleteCourseServlet", urlPatterns = {"/DeleteCourse"})
public class DeleteCourseServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String code = request.getParameter("code");
        ELearningDelegate delegate = new ELearningDelegate();
        
        try {
            if (code != null) {
                delegate.getFacade().deleteCourse(code);
            }
        } catch (ELearningException ex) {
            ex.printStackTrace(); // In real app, log this
        }
        // Redirect back to list
        response.sendRedirect("CourseList");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
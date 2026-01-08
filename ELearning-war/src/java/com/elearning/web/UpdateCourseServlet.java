package com.elearning.web;

import com.elearning.entities.Course;
import com.elearning.entities.ELearningException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "UpdateCourseServlet", urlPatterns = {"/UpdateCourse"})
public class UpdateCourseServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Get all parameters
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        int credits = Integer.parseInt(request.getParameter("credits"));
        String desc = request.getParameter("description");
        String url = request.getParameter("contentUrl");
        
        // 2. Create Course Object
        // Note: We pass 'null' for instructorId because the Facade Logic 
        // does NOT update the instructor (it keeps the original owner).
        Course c = new Course(code, name, credits, null, desc, url);
        
        ELearningDelegate delegate = new ELearningDelegate();
        
        try {
            // 3. Call Update
            delegate.getFacade().updateCourse(c);
        } catch (ELearningException ex) {
            ex.printStackTrace();
        }
        
        response.sendRedirect("CourseList");
    }
}
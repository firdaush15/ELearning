package com.elearning.web;

import com.elearning.entities.Course;
import com.elearning.entities.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UpdateDescriptionServlet", urlPatterns = {"/UpdateDescription"})
public class UpdateDescriptionServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Security Check
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        
        if (currentUser == null || "STUDENT".equals(currentUser.getRole())) {
            response.sendRedirect("login.html");
            return;
        }

        // 2. Get Params
        String courseCode = request.getParameter("code");
        String newDescription = request.getParameter("description");
        
        ELearningDelegate delegate = new ELearningDelegate();
        
        try {
            // 3. Fetch existing course
            Course course = delegate.getFacade().getCourse(courseCode);
            
            if (course != null) {
                // 4. Update ONLY the description
                course.setDescription(newDescription);
                
                // 5. Commit changes
                delegate.getFacade().updateCourse(course);
            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        // 6. Redirect back to the classroom page
        response.sendRedirect("ManageContent?code=" + courseCode);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
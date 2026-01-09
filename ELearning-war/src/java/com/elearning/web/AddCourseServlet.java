package com.elearning.web;

import com.elearning.entities.Course;
import com.elearning.entities.User;
import com.elearning.entities.ELearningException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "AddCourseServlet", urlPatterns = {"/AddCourseServlet"})
public class AddCourseServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        // 1. Get Params
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String creditsStr = request.getParameter("credits");
        String description = request.getParameter("description");
        
        // --- FIX: We no longer ask for this, so we default it to empty ---
        String contentUrl = ""; 
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        String instructorId = (currentUser != null) ? currentUser.getUsername() : "unknown";
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html><head><title>Processing...</title>");
            out.println("<link rel='stylesheet' type='text/css' href='css/style.css'>");
            // Redirect after 1.5 seconds
            out.println("<meta http-equiv='refresh' content='1.5;url=CourseList' />");
            out.println("</head><body style='display:flex; align-items:center; justify-content:center; height:100vh;'>");
            
            try {
                int credits = Integer.parseInt(creditsStr);
                
                // Create Course (Status defaults to PENDING via Constructor)
                Course newCourse = new Course(code, name, credits, instructorId, description, contentUrl, "PENDING");
                
                ELearningDelegate delegate = new ELearningDelegate();
                delegate.getFacade().addCourse(newCourse);
                
                // Success Message
                out.println("<div class='card' style='text-align:center; border-left:5px solid #48bb78;'>");
                out.println("<h2 style='color:#2f855a; margin-top:0;'>Success!</h2>");
                out.println("<p>Course <strong>" + code + "</strong> created.</p>");
                out.println("<p style='font-size:0.9rem; color:#718096;'>Redirecting to list...</p>");
                out.println("</div>");
                
            } catch (Exception e) {
                 // Error Message
                 out.println("<div class='card' style='text-align:center; border-left:5px solid #f56565;'>");
                 out.println("<h2 style='color:#c53030; margin-top:0;'>Error</h2>");
                 out.println("<p>" + e.getMessage() + "</p>");
                 out.println("<a href='add_course.html' class='btn btn-secondary'>Try Again</a>");
                 out.println("</div>");
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
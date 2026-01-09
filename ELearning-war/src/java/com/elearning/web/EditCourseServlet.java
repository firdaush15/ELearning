package com.elearning.web;

import com.elearning.entities.Course;
import com.elearning.entities.ELearningException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "EditCourseServlet", urlPatterns = {"/EditCourse"})
public class EditCourseServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String code = request.getParameter("code");
        ELearningDelegate delegate = new ELearningDelegate();
        
        try (PrintWriter out = response.getWriter()) {
            Course c = delegate.getFacade().getCourse(code);
            
            out.println("<!DOCTYPE html><html><head><title>Edit Course</title>");
            out.println("<link rel='stylesheet' type='text/css' href='css/style.css'>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            out.println("<div class='card' style='max-width:600px; margin:0 auto;'>");
            out.println("<h2>Edit Course: " + code + "</h2>");
            
            out.println("<form action='UpdateCourse' method='POST'>");
            
            // Hidden or Read-only Code
            out.println("<div class='form-group'><label>Code:</label>");
            out.println("<input type='text' name='code' value='" + c.getCourseCode() + "' readonly style='background:#eee;'></div>");
            
            out.println("<div class='form-group'><label>Name:</label>");
            out.println("<input type='text' name='name' value='" + c.getCourseName() + "' required></div>");
            
            out.println("<div class='form-group'><label>Credits:</label>");
            out.println("<input type='number' name='credits' value='" + c.getCredits() + "' required></div>");
            
            // --- NEW FIELDS ---
            String desc = (c.getDescription() != null) ? c.getDescription() : "";
            String url = (c.getContentUrl() != null) ? c.getContentUrl() : "";
            
            out.println("<div class='form-group'><label>Description:</label>");
            out.println("<input type='text' name='description' value='" + desc + "'></div>");
            
            out.println("<div class='form-group'><label>Content Link:</label>");
            out.println("<input type='text' name='contentUrl' value='" + url + "'></div>");
            // ------------------

            out.println("<div style='text-align:right; margin-top:20px;'>");
            out.println("<a href='CourseList' class='btn btn-secondary'>Cancel</a>");
            out.println("<button type='submit' class='btn btn-warning'>Update Course</button>");
            out.println("</div>");
            
            out.println("</form>");
            out.println("</div></div>");
            out.println("</body></html>");
            
        } catch (ELearningException ex) {
            response.sendRedirect("CourseList?error=CourseNotFound");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
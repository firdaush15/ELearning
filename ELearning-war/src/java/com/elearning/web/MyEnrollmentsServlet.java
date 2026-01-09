package com.elearning.web;

import com.elearning.entities.Course;
import com.elearning.entities.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MyEnrollmentsServlet", urlPatterns = {"/MyEnrollments"})
public class MyEnrollmentsServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        ELearningDelegate delegate = new ELearningDelegate();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>My Schedule</title>");
            out.println("<link rel='stylesheet' type='text/css' href='css/style.css'>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            
            // Header
            out.println("<div class='nav'>");
            out.println("<div style='display:flex; align-items:center; gap:10px;'><a href='Dashboard' class='btn btn-back'><i class='fas fa-arrow-left'></i></a> <span class='nav-brand'>My Learning</span></div>");
            out.println("</div>");

            out.println("<div class='course-grid'>"); 

            try {
                Course[] courses = delegate.getFacade().getEnrolledCourses(currentUser.getUsername());
                
                if(courses.length == 0) {
                     out.println("<div style='grid-column:1/-1; text-align:center; padding:50px;'>");
                     out.println("<i class='fas fa-box-open' style='font-size:3rem; color:#cbd5e1; margin-bottom:20px;'></i>");
                     out.println("<h3 style='color:var(--text-muted);'>No enrollments yet.</h3>");
                     out.println("<a href='CourseList' class='btn btn-primary' style='margin-top:10px;'>Browse Catalog</a>");
                     out.println("</div>");
                }

                for (Course c : courses) {
                    out.println("<div class='card course-card'>");
                    
                    // Thumb (Gradient Border effect)
                    out.println("<div class='course-thumb' style='height:120px; background:linear-gradient(to right, #fdfbfb, #ebedee); border-bottom:1px solid rgba(0,0,0,0.05);'>");
                    out.println("<i class='fas fa-book-reader'></i>");
                    out.println("</div>");

                    out.println("<div class='course-content'>");
                    // Enrolled Status Badge
                    out.println("<span class='badge' style='background:#e0f2fe; color:#0369a1; margin-bottom:10px; display:inline-block;'>Enrolled</span>");
                    
                    out.println("<h3 style='margin:0 0 5px 0;'>" + c.getCourseName() + "</h3>");
                    out.println("<p style='font-size:0.85rem; color:var(--text-muted);'>" + c.getCourseCode() + " • " + c.getCredits() + " Credits</p>");
                    
                    // REMOVED: Fake Progress Bar
                    
                    // Actions
                    out.println("<div style='margin-top:20px; display:flex; gap:10px;'>");
                    out.println("<a href='ManageContent?code=" + c.getCourseCode() + "' class='btn btn-primary' style='flex:1; justify-content:center;'>Open Class</a>");
                    out.println("<a href='DropCourse?code=" + c.getCourseCode() + "' class='btn btn-back' style='color:#ef4444; border:1px solid #fee2e2;' onclick=\"return confirm('Drop this course?');\">Drop</a>");
                    out.println("</div>");
                    
                    out.println("</div></div>");
                }
            } catch (Exception e) {}
            
            out.println("</div></div></body></html>");
        }
    }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { processRequest(req, resp); }
}
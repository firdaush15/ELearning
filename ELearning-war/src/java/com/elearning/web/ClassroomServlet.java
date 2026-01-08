package com.elearning.web;

import com.elearning.entities.Course;
import com.elearning.entities.Material;
import com.elearning.entities.Topic;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ClassroomServlet", urlPatterns = {"/Classroom"})
public class ClassroomServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("loggedUser") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String courseCode = request.getParameter("code");
        ELearningDelegate delegate = new ELearningDelegate();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>Classroom</title>");
            out.println("<link rel='stylesheet' type='text/css' href='css/style.css'>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            out.println("<div class='nav'>");
            out.println("<div class='nav-brand'><i class='fas fa-chalkboard'></i> Virtual Classroom</div>");
            out.println("<a href='MyEnrollments' class='btn btn-back'><i class='fas fa-arrow-left'></i> Back to Schedule</a>");
            out.println("</div>");

            try {
                Course c = delegate.getFacade().getCourse(courseCode);
                
                // --- Course Header Info ---
                out.println("<div class='card' style='border-left:5px solid var(--primary);'>");
                out.println("<h1>" + c.getCourseName() + " <small style='color:var(--text-muted); font-size:0.6em;'>(" + c.getCourseCode() + ")</small></h1>");
                out.println("<p style='color:var(--text-main); font-size:1.1rem;'>" + (c.getDescription() != null ? c.getDescription() : "Welcome to the course.") + "</p>");
                out.println("<div style='margin-top:10px; font-size:0.9rem; color:var(--text-muted);'><i class='fas fa-user-tie'></i> Instructor ID: " + c.getInstructorId() + "</div>");
                out.println("</div>");
                
                // --- Topics Loop ---
                List<Topic> topics = delegate.getFacade().getTopicsByCourse(courseCode);
                
                if (topics.isEmpty()) {
                    out.println("<div class='card' style='text-align:center; padding:50px;'>");
                    out.println("<i class='fas fa-box-open' style='font-size:3rem; color:#cbd5e1; margin-bottom:20px;'></i>");
                    out.println("<h3>Content Coming Soon</h3>");
                    out.println("<p style='color:var(--text-muted);'>The instructor hasn't uploaded any modules for this course yet.</p>");
                    out.println("</div>");
                }

                for (Topic t : topics) {
                    out.println("<div class='card'>");
                    out.println("<h3 style='color:var(--text-main); margin-bottom:20px;'><i class='fas fa-bookmark' style='color:var(--primary); margin-right:10px;'></i>" + t.getTopicName() + "</h3>");
                    
                    List<Material> materials = delegate.getFacade().getMaterialsByTopic(t.getTopicId());
                    
                    if(materials.isEmpty()){
                        out.println("<p style='color:var(--text-muted); font-style:italic; padding-left:15px;'>No materials available for this section.</p>");
                    } else {
                        out.println("<div style='display:grid; gap:12px;'>");
                        for (Material m : materials) {
                            
                            // LOGIC: File Download vs External Link
                            String link = "";
                            String target = "_blank";
                            String iconClass = "fa-link";
                            String actionText = "Click to access";

                            if (m.getFileData() != null) {
                                // It is a file in the DB
                                link = "DownloadMaterial?id=" + m.getMaterialId();
                                iconClass = "fa-file-download";
                                actionText = "Download File";
                                
                                if(m.getContentType().contains("pdf")) iconClass = "fa-file-pdf";
                                else if(m.getContentType().contains("presentation")) iconClass = "fa-file-powerpoint";
                                else if(m.getContentType().contains("word")) iconClass = "fa-file-word";
                                else if(m.getContentType().contains("image")) iconClass = "fa-file-image";
                                
                            } else {
                                // It is a URL link
                                link = m.getContentUrl();
                                if(link.toLowerCase().contains("youtube")) iconClass = "fa-play-circle";
                                else if(link.toLowerCase().contains("drive")) iconClass = "fa-folder-open";
                            }

                            // Render Content Card
                            out.println("<a href='" + link + "' target='" + target + "' style='text-decoration:none;'>");
                            out.println("<div style='background:#f8fafc; padding:15px; border-radius:10px; border:1px solid #e2e8f0; display:flex; align-items:center; transition:all 0.2s;' onmouseover=\"this.style.borderColor='var(--primary)'; this.style.transform='translateX(5px)';\" onmouseout=\"this.style.borderColor='#e2e8f0'; this.style.transform='translateX(0)';\">");
                            
                            out.println("<i class='fas " + iconClass + "' style='font-size:1.5rem; color:var(--primary); margin-right:20px;'></i>");
                            out.println("<div style='flex-grow:1;'>");
                            out.println("<div style='color:var(--text-main); font-weight:700;'>" + m.getMaterialName() + "</div>");
                            out.println("<div style='color:var(--text-muted); font-size:0.8rem;'>" + actionText + "</div>");
                            out.println("</div>");
                            out.println("<i class='fas fa-chevron-right' style='color:#cbd5e1;'></i>");
                            out.println("</div></a>");
                        }
                        out.println("</div>");
                    }
                    out.println("</div>");
                }
                
            } catch (Exception e) {
                out.println("<div class='card'><p style='color:red'>Error loading classroom: " + e.getMessage() + "</p></div>");
            }
            out.println("</div></body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
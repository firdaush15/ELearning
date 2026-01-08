package com.elearning.web;

import com.elearning.entities.Course;
import com.elearning.entities.Material;
import com.elearning.entities.Topic;
import com.elearning.entities.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ManageContentServlet", urlPatterns = {"/ManageContent"})
public class ManageContentServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        
        if (currentUser == null) { response.sendRedirect("login.html"); return; }
        
        String role = currentUser.getRole();
        boolean isInstructor = "INSTRUCTOR".equals(role);
        boolean isAdmin = "ADMIN".equals(role);
        boolean canEdit = isInstructor || isAdmin;

        String courseCode = request.getParameter("code");
        ELearningDelegate delegate = new ELearningDelegate();

        try (PrintWriter out = response.getWriter()) {
            
            // --- Fetch Course First for Security Check ---
            Course c = delegate.getFacade().getCourse(courseCode);
            String status = (c.getStatus() != null) ? c.getStatus() : "APPROVED";

            // --- SECURITY FIX: Block Instructor if PENDING ---
            if (isInstructor && "PENDING".equals(status)) {
                response.sendRedirect("CourseList?error=You cannot manage a course until it is approved by an Admin.");
                return;
            }
            // -------------------------------------------------

            out.println("<!DOCTYPE html><html><head><title>Classroom</title>");
            out.println("<link rel='stylesheet' type='text/css' href='css/style.css'>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            
            // Nav
            out.println("<div class='nav'>");
            String backLink = "STUDENT".equals(role) ? "MyEnrollments" : "CourseList";
            out.println("<div style='display:flex; align-items:center; gap:10px;'><a href='" + backLink + "' class='btn btn-back'><i class='fas fa-arrow-left'></i></a> <span class='nav-brand'>Classroom</span></div>");
            out.println("</div>");

            // --- HEADER CARD WITH EDITABLE DESCRIPTION ---
            out.println("<div class='card' style='background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color:white; border:none; position:relative;'>");
            out.println("<h1 style='margin:0;'>" + c.getCourseName() + "</h1>");
            
            // 1. View Mode (Visible by default)
            out.println("<div id='desc-view'>");
            out.println("<p style='opacity:0.9; margin-top:5px; line-height:1.6;'>" + c.getDescription() + "</p>");
            if (canEdit) {
                out.println("<button onclick=\"document.getElementById('desc-view').style.display='none';document.getElementById('desc-edit').style.display='block';\" class='btn' style='background:rgba(255,255,255,0.2); color:white; padding:5px 10px; font-size:0.8rem; margin-top:10px; border:1px solid rgba(255,255,255,0.3);'><i class='fas fa-edit'></i> Edit Description</button>");
            }
            out.println("</div>");

            // 2. Edit Mode (Hidden by default)
            if (canEdit) {
                out.println("<div id='desc-edit' style='display:none; margin-top:15px;'>");
                out.println("<form action='UpdateDescription' method='POST'>");
                out.println("<input type='hidden' name='code' value='" + courseCode + "'>");
                out.println("<textarea name='description' rows='4' style='width:100%; border-radius:8px; padding:12px; border:none; font-family:inherit; font-size:0.9rem; resize:vertical;' required>" + c.getDescription() + "</textarea>");
                out.println("<div style='margin-top:10px; display:flex; gap:10px;'>");
                out.println("<button type='submit' class='btn' style='background:white; color:#667eea; border:none; font-weight:bold;'>Save Changes</button>");
                out.println("<button type='button' onclick=\"document.getElementById('desc-view').style.display='block';document.getElementById('desc-edit').style.display='none';\" class='btn' style='background:transparent; border:1px solid rgba(255,255,255,0.5); color:white;'>Cancel</button>");
                out.println("</div>");
                out.println("</form>");
                out.println("</div>");
            }
            out.println("</div>");

            out.println("<div style='display:grid; grid-template-columns: 1fr; gap:30px;'>"); 
            
            // ADD TOPIC FORM
            if(canEdit) {
                out.println("<div class='card' style='border-left:5px solid #84fab0;'>");
                out.println("<h4><i class='fas fa-plus-circle'></i> New Chapter</h4>");
                out.println("<form action='AddTopic' method='POST' style='display:flex; gap:10px; margin-top:15px;'>");
                out.println("<input type='hidden' name='courseCode' value='" + courseCode + "'>");
                out.println("<input type='text' name='topicName' placeholder='Chapter Title...' required style='margin:0; flex-grow:1;'>");
                out.println("<button type='submit' class='btn btn-add'>Create</button>");
                out.println("</form>");
                out.println("</div>");
            }

            // TOPICS LIST
            List<Topic> topics = delegate.getFacade().getTopicsByCourse(courseCode);
            
            if(topics.isEmpty()){
                out.println("<div style='text-align:center; padding:40px; color:var(--text-muted);'><p>No content has been added to this course yet.</p></div>");
            }

            for (Topic t : topics) {
                out.println("<div class='card'>");
                out.println("<div style='display:flex; justify-content:space-between; align-items:center; border-bottom:1px solid rgba(0,0,0,0.05); padding-bottom:15px; margin-bottom:20px;'>");
                out.println("<h3 style='margin:0; color:var(--text-main);'><i class='fas fa-bookmark' style='color:#667eea; margin-right:10px;'></i>" + t.getTopicName() + "</h3>");
                if(canEdit) {
                    out.println("<a href='DeleteTopic?id=" + t.getTopicId() + "&code=" + courseCode + "' style='color:#ef4444; font-size:0.9rem;' onclick=\"return confirm('Delete?');\"><i class='fas fa-trash'></i></a>");
                }
                out.println("</div>");

                List<Material> materials = delegate.getFacade().getMaterialsByTopic(t.getTopicId());
                
                if(!materials.isEmpty()) {
                    out.println("<div style='display:flex; flex-direction:column; gap:10px;'>");
                    for(Material m : materials){
                        out.println("<div style='background:#f8fafc; padding:15px; border-radius:12px; display:flex; justify-content:space-between; align-items:center; transition:0.2s;' onmouseover=\"this.style.background='#edf2f7'\" onmouseout=\"this.style.background='#f8fafc'\">");
                        String icon = "fa-link";
                        if(m.getFileData() != null) {
                            icon = "fa-file-alt";
                            if(m.getContentType().contains("pdf")) icon = "fa-file-pdf";
                        } else if(m.getContentUrl().contains("youtube")) icon = "fa-play-circle";
                        
                        out.println("<div style='display:flex; align-items:center; gap:15px;'>");
                        out.println("<div style='width:35px; height:35px; background:#e2e8f0; border-radius:8px; display:flex; align-items:center; justify-content:center;'><i class='fas " + icon + "' style='color:#4a5568;'></i></div>");
                        out.println("<span style='font-weight:500;'>" + m.getMaterialName() + "</span>");
                        out.println("</div>");
                        
                        out.println("<div style='display:flex; gap:15px;'>");
                        if(m.getFileData() != null){
                            out.println("<a href='DownloadMaterial?id=" + m.getMaterialId() + "' target='_blank' style='color:#667eea;'><i class='fas fa-download'></i></a>");
                        } else {
                            out.println("<a href='" + m.getContentUrl() + "' target='_blank' style='color:#667eea;'><i class='fas fa-external-link-alt'></i></a>");
                        }
                        if(canEdit) {
                            out.println("<a href='DeleteMaterial?id=" + m.getMaterialId() + "&code=" + courseCode + "' style='color:#ef4444;' onclick=\"return confirm('Remove?');\"><i class='fas fa-times'></i></a>");
                        }
                        out.println("</div></div>");
                    }
                    out.println("</div>");
                } else {
                        out.println("<p style='font-style:italic; color:var(--text-muted); font-size:0.9rem;'>No materials yet.</p>");
                }

                if(canEdit) {
                    out.println("<div style='margin-top:20px; padding-top:15px; border-top:1px dashed #cbd5e1;'>");
                    out.println("<form action='AddMaterial' method='POST' enctype='multipart/form-data' style='display:flex; flex-direction:column; gap:10px;'>");
                    out.println("<input type='hidden' name='topicId' value='" + t.getTopicId() + "'>");
                    out.println("<input type='hidden' name='courseCode' value='" + courseCode + "'>");
                    out.println("<div style='display:flex; gap:10px;'>");
                    out.println("<input type='text' name='materialName' placeholder='Title' required style='margin:0; flex:2;'>");
                        String js = "if(this.value=='file'){document.getElementById('fileInput"+t.getTopicId()+"').style.display='block';document.getElementById('linkInput"+t.getTopicId()+"').style.display='none';}else{document.getElementById('fileInput"+t.getTopicId()+"').style.display='none';document.getElementById('linkInput"+t.getTopicId()+"').style.display='block';}";
                    out.println("<select name='type' onchange=\"" + js + "\" style='margin:0; flex:1;'><option value='link'>Link</option><option value='file'>File</option></select>");
                    out.println("<button type='submit' class='btn btn-primary' style='padding:0 20px;'><i class='fas fa-plus'></i></button>");
                    out.println("</div>");
                    out.println("<input type='text' id='linkInput"+t.getTopicId()+"' name='contentUrl' placeholder='URL...' style='margin:0;'>");
                    out.println("<input type='file' id='fileInput"+t.getTopicId()+"' name='fileUpload' style='display:none; margin:0; padding:10px; background:white;'>");
                    out.println("</form></div>");
                }
                out.println("</div>"); 
            }
            out.println("</div>");

        } catch (Exception e) { 
            // In case of error (e.g. course not found), redirect safely
            response.sendRedirect("CourseList?error=Course not found");
        }
    }
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { processRequest(req, resp); }
}
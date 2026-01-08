package com.elearning.web;

import com.elearning.entities.Course;
import com.elearning.entities.User;
import com.elearning.entities.ELearningException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "CourseListServlet", urlPatterns = {"/CourseList"})
public class CourseListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        String role = (currentUser != null) ? currentUser.getRole() : "GUEST";
        boolean isInstructor = "INSTRUCTOR".equals(role);
        boolean isAdmin = "ADMIN".equals(role);
        boolean isStudent = "STUDENT".equals(role);
        
        String error = request.getParameter("error");
        ELearningDelegate delegate = new ELearningDelegate();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>Course Catalog</title>");
            out.println("<link rel='stylesheet' type='text/css' href='css/style.css'>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            
            // --- Nav ---
            out.println("<div class='nav'>");
            out.println("<div style='display:flex; align-items:center; gap:10px;'><a href='Dashboard' class='btn btn-back'><i class='fas fa-arrow-left'></i></a> <span class='nav-brand'>Catalog</span></div>");
            if(isInstructor) out.println("<a href='add_course.html' class='btn btn-add'><i class='fas fa-plus'></i> New Course</a>");
            out.println("</div>");

            if(error != null) {
                out.println("<div style='background:#fee2e2; color:#b91c1c; padding:15px; border-radius:12px; margin-bottom:20px; border:1px solid #fca5a5;'>" + error + "</div>");
            }

            try {
                // 1. Fetch Courses
                Course[] courses = isInstructor ? delegate.getFacade().getCoursesByInstructor(currentUser.getUsername()) : delegate.getFacade().getAllCourses();
                
                // 2. Separate into Lists
                List<Course> pendingList = new ArrayList<>();
                List<Course> activeList = new ArrayList<>();

                for (Course c : courses) {
                    String status = (c.getStatus() != null) ? c.getStatus() : "APPROVED";
                    
                    // Students strictly see ONLY Approved courses
                    if (isStudent && !"APPROVED".equals(status)) continue;

                    if ("PENDING".equals(status)) {
                        pendingList.add(c);
                    } else {
                        activeList.add(c);
                    }
                }

                if (pendingList.isEmpty() && activeList.isEmpty()) {
                    out.println("<div style='text-align:center; padding:50px; color:var(--text-muted);'>");
                    out.println("<i class='fas fa-folder-open' style='font-size:3rem; margin-bottom:15px;'></i>");
                    out.println("<p>No courses available at the moment.</p>");
                    out.println("</div>");
                }

                // --- SECTION 1: PENDING COURSES (Admin & Instructor Only) ---
                if (!pendingList.isEmpty()) {
                    out.println("<h3 style='color:#d97706; margin-bottom:15px; display:flex; align-items:center; gap:10px;'><i class='fas fa-clock'></i> Pending Approval</h3>");
                    out.println("<div class='course-grid' style='margin-bottom:40px;'>");
                    for (Course c : pendingList) {
                        printCourseCard(out, c, role, "PENDING");
                    }
                    out.println("</div>");
                    
                    // Divider
                    if (!activeList.isEmpty()) {
                        out.println("<hr style='border:0; border-top:1px dashed #cbd5e1; margin-bottom:30px;'>");
                    }
                }

                // --- SECTION 2: ACTIVE COURSES ---
                if (!activeList.isEmpty()) {
                    if (!pendingList.isEmpty()) out.println("<h3 style='color:var(--text-main); margin-bottom:15px;'><i class='fas fa-check-circle' style='color:#16a34a;'></i> Active Courses</h3>");
                    
                    out.println("<div class='course-grid'>");
                    for (Course c : activeList) {
                        printCourseCard(out, c, role, "APPROVED");
                    }
                    out.println("</div>");
                }

            } catch (Exception ex) {
                out.println("<p style='color:red'>Error loading courses.</p>");
                ex.printStackTrace(out);
            }
            out.println("</div></body></html>");
        }
    }

    // --- Helper Method to Print Card ---
    private void printCourseCard(PrintWriter out, Course c, String role, String status) {
        boolean isInstructor = "INSTRUCTOR".equals(role);
        boolean isAdmin = "ADMIN".equals(role);
        boolean isStudent = "STUDENT".equals(role);

        out.println("<div class='card course-card'>");
        
        // Thumbnail
        int colorIndex = Math.abs(c.getCourseName().hashCode()) % 5;
        String[] icons = {"fa-code", "fa-database", "fa-network-wired", "fa-laptop-code", "fa-microchip"};
        String icon = icons[colorIndex];
        
        out.println("<div class='course-thumb'>");
        out.println("<i class='fas "+icon+"'></i>");
        
        // Overlay Badge
        if ("PENDING".equals(status)) {
            out.println("<span style='position:absolute; top:15px; right:15px; background:#fef08a; color:#854d0e; padding:4px 10px; border-radius:10px; font-size:0.7rem; font-weight:bold;'>PENDING</span>");
        }
        out.println("</div>");

        // Content
        out.println("<div class='course-content'>");
        out.println("<div style='font-size:0.75rem; font-weight:700; color:#667eea; letter-spacing:1px; margin-bottom:5px;'>" + c.getCourseCode() + "</div>");
        out.println("<h3 style='margin:0 0 10px 0; font-size:1.2rem;'>" + c.getCourseName() + "</h3>");
        
        String desc = (c.getDescription() != null && c.getDescription().length() > 60) ? c.getDescription().substring(0, 60) + "..." : c.getDescription();
        out.println("<p style='color:var(--text-muted); font-size:0.9rem; height:40px;'>" + desc + "</p>");
        
        // Footer Credits
        out.println("<div style='display:flex; justify-content:flex-end; align-items:center; margin-top:15px; padding-top:15px; border-top:1px solid rgba(0,0,0,0.05);'>");
        out.println("<span style='font-size:0.85rem; color:var(--text-muted); font-weight:600;'>" + c.getCredits() + " Credits</span>");
        out.println("</div>");

        // Actions
        out.println("<div style='margin-top:20px; display:flex; gap:10px;'>");
        
        if (isAdmin) {
            if ("PENDING".equals(status)) {
                out.println("<a href='ApproveCourse?code=" + c.getCourseCode() + "&action=approve' class='btn btn-add' style='flex:1; justify-content:center; background:#16a34a; color:white;'>Approve</a>");
            }
            out.println("<a href='DeleteCourse?code=" + c.getCourseCode() + "' class='btn btn-danger' style='padding:10px;'> <i class='fas fa-trash'></i> </a>");
        
        } else if (isInstructor) {
            if ("APPROVED".equals(status)) {
                out.println("<a href='ManageContent?code=" + c.getCourseCode() + "' class='btn btn-primary' style='flex:1; justify-content:center;'>Manage</a>");
            } else {
                out.println("<span class='btn' style='flex:1; justify-content:center; background:#e2e8f0; color:#94a3b8; cursor:not-allowed;'><i class='fas fa-lock'></i> Locked</span>");
            }
            out.println("<a href='DeleteCourse?code=" + c.getCourseCode() + "' class='btn btn-danger' onclick=\"return confirm('Delete?');\"><i class='fas fa-trash'></i></a>");
        
        } else if (isStudent) {
            out.println("<a href='EnrollCourse?code=" + c.getCourseCode() + "' class='btn btn-enroll' style='width:100%; justify-content:center;'>Enroll Now</a>");
        }
        
        out.println("</div>"); // End Actions
        out.println("</div>"); // End Content
        out.println("</div>"); // End Card
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
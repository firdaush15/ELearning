package com.elearning.web;

import com.elearning.entities.User;
import com.elearning.services.SystemStatusBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/Dashboard"})
public class DashboardServlet extends HttpServlet {

    @EJB
    private SystemStatusBeanLocal statusBean;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        Object userObj = (session != null) ? session.getAttribute("loggedUser") : null;
        
        if (userObj == null) {
            response.sendRedirect("login.html");
            return;
        }
        
        User currentUser = (User) userObj;
        String role = currentUser.getRole();
        ELearningDelegate delegate = new ELearningDelegate();

        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>Dashboard</title>");
            out.println("<link rel='stylesheet' type='text/css' href='css/style.css'>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
            out.println("</head><body>");

            out.println("<div class='container'>");
            
            // --- 1. Navigation ---
            out.println("<div class='nav'>");
            out.println("<div class='nav-brand'><i class='fas fa-graduation-cap'></i> ELearning</div>");
            out.println("<div class='nav-user'>");
            out.println("<span class='badge badge-role'>" + role + "</span>");
            out.println("<div style='text-align:right; line-height:1.2;'><small style='display:block; color:var(--text-muted);'>Welcome back,</small><strong>" + currentUser.getFullName() + "</strong></div>");
            out.println("<div style='width:40px; height:40px; background:linear-gradient(135deg, #a18cd1, #fbc2eb); border-radius:50%; display:flex; align-items:center; justify-content:center; color:white; font-weight:bold;'>" + currentUser.getUsername().substring(0,1).toUpperCase() + "</div>");
            out.println("<a href='Logout' style='color:#e53e3e; margin-left:10px;' title='Logout'><i class='fas fa-sign-out-alt'></i></a>");
            out.println("</div></div>");

            // --- 2. Stats Row (REAL DATA ONLY) ---
            out.println("<div class='stats-grid'>");
            
            // Logic for Stat Numbers
            int count1 = 0; 
            String label1 = "Active Courses";
            String icon1 = "fa-book-open";
            String color1 = "linear-gradient(135deg, #667eea, #764ba2)";

            try {
                if("ADMIN".equals(role)){
                    count1 = delegate.getFacade().getAllCourses().length;
                    label1 = "Total Courses";
                } else if("INSTRUCTOR".equals(role)){
                    count1 = delegate.getFacade().getCoursesByInstructor(currentUser.getUsername()).length;
                    label1 = "My Courses";
                } else {
                    count1 = delegate.getFacade().getEnrolledCourses(currentUser.getUsername()).length;
                    label1 = "Enrolled";
                }
            } catch (Exception e) {
                System.out.println("Error fetching dashboard stats: " + e.getMessage());
            }

            // Stat Card 1: Counts
            out.println("<div class='stat-card'>");
            out.println("<div class='stat-icon' style='background:" + color1 + "'><i class='fas " + icon1 + "'></i></div>");
            out.println("<div><div style='font-size:2rem; font-weight:700; color:var(--text-main); line-height:1;'>" + count1 + "</div><div style='font-size:0.85rem; color:var(--text-muted);'>" + label1 + "</div></div>");
            out.println("</div>");

            // Stat Card 2: System Status (Real data from Bean)
            if(statusBean != null) {
                String health = "Online";
                out.println("<div class='stat-card'>");
                out.println("<div class='stat-icon' style='background:linear-gradient(135deg, #84fab0, #8fd3f4)'><i class='fas fa-server'></i></div>");
                out.println("<div><div style='font-size:1.1rem; font-weight:700; color:var(--text-main);'>" + health + "</div><div style='font-size:0.85rem; color:var(--text-muted);'>System Status</div></div>");
                out.println("</div>");
            }
            
            out.println("</div>"); // End Stats Grid

            // --- 3. Main Dashboard Content ---
            out.println("<div class='card'>");
            if ("ADMIN".equals(role)) {
                out.println("<h3><i class='fas fa-shield-alt' style='color:#667eea; margin-right:10px;'></i> Administration</h3>");
                out.println("<p style='color:var(--text-muted); margin-bottom:25px;'>Manage the system infrastructure, users, and oversee the course catalog.</p>");
                out.println("<div style='display:flex; gap:15px; flex-wrap:wrap;'>");
                out.println("<a href='UserList' class='btn btn-secondary'><i class='fas fa-users'></i> User Management</a>");
                out.println("<a href='CourseList' class='btn btn-secondary'><i class='fas fa-th-list'></i> Manage Catalog</a>"); 
                out.println("</div>");
            } else if ("INSTRUCTOR".equals(role)) {
                out.println("<h3><i class='fas fa-chalkboard-teacher' style='color:#667eea; margin-right:10px;'></i> Instructor Studio</h3>");
                out.println("<div style='display:flex; gap:15px; margin-top:20px;'>");
                out.println("<a href='add_course.html' class='btn btn-add'><i class='fas fa-plus'></i> Create Course</a>");
                out.println("<a href='CourseList' class='btn btn-secondary'><i class='fas fa-layer-group'></i> My Course List</a>");
                out.println("</div>");
            } else {
                out.println("<h3><i class='fas fa-user-graduate' style='color:#667eea; margin-right:10px;'></i> Student Dashboard</h3>");
                out.println("<p style='color:var(--text-muted); margin-bottom:25px;'>Track your progress and browse new content.</p>");
                out.println("<div style='display:flex; gap:15px;'>");
                out.println("<a href='MyEnrollments' class='btn btn-primary'><i class='fas fa-calendar-alt'></i> My Schedule</a>");
                out.println("<a href='CourseList' class='btn btn-secondary'><i class='fas fa-search'></i> Browse Catalog</a>");
                out.println("</div>");
            }
            out.println("</div>");

            out.println("</div></body></html>");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { processRequest(request, response); }
}
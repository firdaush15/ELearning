package com.elearning.web;

import com.elearning.entities.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "UserListServlet", urlPatterns = {"/UserList"})
public class UserListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        
        // Security: Check if ADMIN
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect("Dashboard");
            return;
        }

        ELearningDelegate delegate = new ELearningDelegate();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html><html><head><title>User Management</title>");
            out.println("<link rel='stylesheet' type='text/css' href='css/style.css'>");
            out.println("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>");
            out.println("</head><body>");
            
            out.println("<div class='container'>");
            
            // Nav
            out.println("<div class='nav'>");
            out.println("<div class='nav-brand'>User Management</div>");
            out.println("<a href='Dashboard' class='btn btn-back'><i class='fas fa-arrow-left'></i> Back to Dashboard</a>");
            out.println("</div>");

            out.println("<h2><i class='fas fa-users-cog' style='color:var(--primary)'></i> System Users</h2>");
            
            // Table
            out.println("<div class='card'>");
            out.println("<table style='width:100%; border-collapse:collapse;'>");
            out.println("<tr style='background:#f8fafc; text-align:left;'>");
            out.println("<th style='padding:12px; border-bottom:2px solid #e2e8f0;'>Username</th>");
            out.println("<th style='padding:12px; border-bottom:2px solid #e2e8f0;'>Full Name</th>");
            out.println("<th style='padding:12px; border-bottom:2px solid #e2e8f0;'>Role</th>");
            out.println("<th style='padding:12px; border-bottom:2px solid #e2e8f0; text-align:right;'>Action</th>");
            out.println("</tr>");

            try {
                User[] users = delegate.getFacade().getAllUsers();
                for (User u : users) {
                    out.println("<tr style='border-bottom:1px solid #f1f5f9;'>");
                    out.println("<td style='padding:12px; font-weight:bold; color:#475569;'>" + u.getUsername() + "</td>");
                    out.println("<td style='padding:12px;'>" + u.getFullName() + "</td>");
                    
                    // Role Badge
                    String badgeColor = "STUDENT".equals(u.getRole()) ? "#e0f2fe; color:#0369a1" : 
                                      ("ADMIN".equals(u.getRole()) ? "#fce7f3; color:#be185d" : "#dcfce7; color:#15803d");
                    out.println("<td style='padding:12px;'><span style='background:"+badgeColor+"; padding:4px 8px; border-radius:4px; font-size:0.85rem; font-weight:600;'>" + u.getRole() + "</span></td>");
                    
                    out.println("<td style='padding:12px; text-align:right;'>");
                    if(!u.getUsername().equals(currentUser.getUsername())) {
                        out.println("<a href='DeleteUser?id=" + u.getUsername() + "' class='btn btn-danger' style='padding:5px 10px; font-size:0.8rem;' onclick=\"return confirm('Are you sure you want to delete user: " + u.getUsername() + "?');\"><i class='fas fa-trash'></i> Delete</a>");
                    } else {
                        out.println("<span style='color:#cbd5e1; font-style:italic;'>Current User</span>");
                    }
                    out.println("</td>");
                    out.println("</tr>");
                }
            } catch (Exception e) {
                out.println("<tr><td colspan='4' style='color:red; padding:20px;'>Error: " + e.getMessage() + "</td></tr>");
            }
            
            out.println("</table>");
            out.println("</div>"); // End card
            out.println("</div></body></html>");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
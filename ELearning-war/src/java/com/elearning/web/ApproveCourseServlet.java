package com.elearning.web;

import com.elearning.entities.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "ApproveCourseServlet", urlPatterns = {"/ApproveCourse"})
public class ApproveCourseServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Security Check
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect("login.html");
            return;
        }

        // 2. Get Params
        String code = request.getParameter("code");
        String action = request.getParameter("action"); // e.g., "approve"
        
        ELearningDelegate delegate = new ELearningDelegate();
        
        try {
            if(code != null && "approve".equals(action)) {
                delegate.getFacade().updateCourseStatus(code, "APPROVED");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        response.sendRedirect("CourseList");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
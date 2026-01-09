package com.elearning.web;

import com.elearning.entities.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "DeleteUserServlet", urlPatterns = {"/DeleteUser"})
public class DeleteUserServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        User currentUser = (User) ((session != null) ? session.getAttribute("loggedUser") : null);
        
        // Only Admin can delete
        if (currentUser == null || !"ADMIN".equals(currentUser.getRole())) {
            response.sendRedirect("login.html");
            return;
        }

        String id = request.getParameter("id");
        ELearningDelegate delegate = new ELearningDelegate();
        
        if (id != null && !id.equals(currentUser.getUsername())) {
            try {
                delegate.getFacade().deleteUser(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        response.sendRedirect("UserList");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
package com.elearning.web;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DeleteTopicServlet", urlPatterns = {"/DeleteTopic"})
public class DeleteTopicServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String code = request.getParameter("code");
            new ELearningDelegate().getFacade().deleteTopic(id);
            response.sendRedirect("ManageContent?code=" + code);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
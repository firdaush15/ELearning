package com.elearning.web;
import com.elearning.entities.Topic;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "AddTopicServlet", urlPatterns = {"/AddTopic"})
public class AddTopicServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("topicName");
            String code = request.getParameter("courseCode");
            ELearningDelegate delegate = new ELearningDelegate();
            delegate.getFacade().addTopic(new Topic(name, code));
            response.sendRedirect("ManageContent?code=" + code);
        } catch (Exception e) { e.printStackTrace(); }
    }
}
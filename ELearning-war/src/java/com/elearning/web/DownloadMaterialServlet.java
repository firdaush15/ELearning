package com.elearning.web;

import com.elearning.entities.Material;
import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "DownloadMaterialServlet", urlPatterns = {"/DownloadMaterial"})
public class DownloadMaterialServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            ELearningDelegate delegate = new ELearningDelegate();
            Material m = delegate.getFacade().getMaterial(id);
            
            if (m != null && m.getFileData() != null) {
                // Tells browser this is a file (PDF, PPT, etc.)
                response.setContentType(m.getContentType());
                // Forces browser to download or open
                response.setHeader("Content-Disposition", "inline; filename=\"" + m.getFileName() + "\"");
                response.setContentLength(m.getFileData().length);
                
                OutputStream out = response.getOutputStream();
                out.write(m.getFileData());
                out.flush();
            } else {
                response.getWriter().write("File content not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
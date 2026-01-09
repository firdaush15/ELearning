package com.elearning.web;

import com.elearning.entities.Material;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

@WebServlet(name = "AddMaterialServlet", urlPatterns = {"/AddMaterial"})
@MultipartConfig(maxFileSize = 16177215) // Approx 16MB limit
public class AddMaterialServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String name = request.getParameter("materialName");
            String type = request.getParameter("type"); // "link" or "file"
            int topicId = Integer.parseInt(request.getParameter("topicId"));
            String code = request.getParameter("courseCode");
            
            ELearningDelegate delegate = new ELearningDelegate();
            Material m = null;

            if ("file".equals(type)) {
                // Handle File Upload
                Part filePart = request.getPart("fileUpload");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = filePart.getSubmittedFileName();
                    String contentType = filePart.getContentType();
                    
                    InputStream is = filePart.getInputStream();
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = is.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }
                    byte[] fileData = buffer.toByteArray();
                    
                    m = new Material(name, topicId, fileData, fileName, contentType);
                }
            } else {
                // Handle Regular Link
                String url = request.getParameter("contentUrl");
                m = new Material(name, url, topicId);
            }

            if(m != null) {
                delegate.getFacade().addMaterial(m);
            }
            response.sendRedirect("ManageContent?code=" + code);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Dashboard");
        }
    }
}
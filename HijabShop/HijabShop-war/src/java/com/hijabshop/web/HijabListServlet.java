package com.hijabshop.web;

import com.hijabshop.entities.HijabProduct;
import com.hijabshop.entities.InventoryException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "HijabListServlet", urlPatterns = {"/HijabList"})
public class HijabListServlet extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // 1. Create the Delegate
        ShopDelegate delegate = new ShopDelegate();

        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Hijab Shop Collection</title>");

            // --- BOUTIQUE THEME CSS (SOFT PINK & WHITE) ---
            out.println("<style>");
            out.println("@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@600&family=Lato:wght@400;700&display=swap');");

            out.println("body { font-family: 'Lato', sans-serif; background-color: #fcf5f7; margin: 0; padding: 0; color: #555; }");

            // Navbar: White with a soft shadow
            out.println(".navbar { background-color: #ffffff; box-shadow: 0 2px 10px rgba(0,0,0,0.05); padding: 15px 40px; display: flex; justify-content: space-between; align-items: center; position: sticky; top: 0; z-index: 1000; }");
            out.println(".navbar h1 { margin: 0; font-family: 'Playfair Display', serif; font-size: 28px; color: #d63384; letter-spacing: 1px; }");
            out.println(".nav-links a { color: #777; text-decoration: none; margin-left: 30px; font-weight: 700; text-transform: uppercase; font-size: 13px; letter-spacing: 1px; transition: color 0.3s; }");
            out.println(".nav-links a:hover { color: #d63384; }");

            // Header: Soft Pink Gradient
            out.println(".header { text-align: center; padding: 60px 20px; background: linear-gradient(135deg, #ffeef2 0%, #fff0f3 100%); margin-bottom: 40px; border-bottom: 1px solid #ffe0e9; }");
            out.println(".header h2 { margin: 0; color: #d63384; font-family: 'Playfair Display', serif; font-size: 42px; }");
            out.println(".header p { margin-top: 10px; color: #888; font-size: 18px; font-style: italic; }");

            // Product Grid Container
            out.println(".container { max-width: 1200px; margin: 0 auto; display: flex; flex-wrap: wrap; gap: 30px; justify-content: center; padding: 20px; }");

            // Product Card: Clean White
            out.println(".card { background: white; width: 280px; border-radius: 12px; border: 1px solid #fceef2; box-shadow: 0 5px 15px rgba(214, 51, 132, 0.05); overflow: hidden; transition: all 0.3s ease; display: flex; flex-direction: column; }");
            out.println(".card:hover { transform: translateY(-7px); box-shadow: 0 15px 30px rgba(214, 51, 132, 0.15); }");

            // Image
            out.println(".card-img { width: 100%; height: 320px; object-fit: cover; border-bottom: 1px solid #f8f9fa; }");

            // Card Body
            out.println(".card-body { padding: 25px; text-align: center; }");
            out.println(".card-title { font-size: 16px; font-weight: 700; margin-bottom: 8px; color: #333; text-transform: uppercase; letter-spacing: 0.5px; }");
            out.println(".card-price { color: #d63384; font-size: 20px; font-family: 'Playfair Display', serif; font-weight: bold; margin: 10px 0; }");

            // Button: Rose Pink
            out.println(".btn { background-color: #ff8da1; color: white; padding: 12px 20px; border: none; border-radius: 30px; cursor: pointer; width: 100%; font-size: 14px; font-weight: bold; text-transform: uppercase; letter-spacing: 1px; margin-top: 15px; transition: background 0.3s; }");
            out.println(".btn:hover { background-color: #e06c85; }");

            out.println("</style>");
            out.println("</head>");

            out.println("<body>");

            // --- NAVIGATION BAR ---
            out.println("<div class='navbar'>");
            out.println("<h1>🌸 HijabShop</h1>");
            out.println("<div class='nav-links'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("<a href='HijabList'>Collections</a>");
            out.println("<a href='about.html'>About Us</a>");
            out.println("</div>");
            out.println("</div>");

            // --- HERO HEADER ---
            out.println("<div class='header'>");
            out.println("<h2>The Elegance Collection</h2>");
            out.println("<p>Modesty meets modern sophistication</p>");
            out.println("</div>");

            // --- MAIN GRID ---
            out.println("<div class='container'>");

            try {
                // 2. Fetch Data
                HijabProduct[] products = delegate.getFacade().getAllProducts();

                // 3. Loop through products and create CARDS
                for (HijabProduct h : products) {
                    out.println("<div class='card'>");

                    // Placeholder Image - using soft colors
                    out.println("<img src='https://placehold.co/280x320/ffe4e1/d63384?text=Hijab+Style' class='card-img' alt='Hijab'>");

                    out.println("<div class='card-body'>");
                    out.println("<div class='card-title'>" + h.getSku() + "</div>");
                    out.println("<div class='card-price'>RM " + String.format("%.2f", h.getPrice()) + "</div>");
                    out.println("<button class='btn' onclick='alert(\"Added " + h.getSku() + " to cart!\")'>Add to Cart</button>");
                    out.println("</div>"); // End card-body

                    out.println("</div>"); // End card
                }
            } catch (InventoryException ex) {
                out.println("<h3 style='color:#d63384; text-align:center;'>Unable to load collection at this time.</h3>");
            }

            out.println("</div>"); // End container
            out.println("</body>");
            out.println("</html>");
        }
    }

    // Standard Servlet methods
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}

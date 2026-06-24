package com.example;

import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.Context;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class VulnApp {
    
    public static void main(String[] args) throws Exception {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "8080"));
        
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        tomcat.getConnector();
        
        Context ctx = tomcat.addContext("", null);
        
        Tomcat.addServlet(ctx, "home", new HomeServlet());
        ctx.addServletMappingDecoded("/", "home");
        
        Tomcat.addServlet(ctx, "health", new HealthServlet());
        ctx.addServletMappingDecoded("/api/health", "health");
        
        tomcat.start();
        System.out.println("Java Vulnerable App running on port " + port);
        tomcat.getServer().await();
    }
    
    public static class HomeServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
                throws IOException {
            resp.setContentType("text/html");
            PrintWriter out = resp.getWriter();
            out.println("<html><head><title>Java Vulnerable App</title>");
            out.println("<style>body{font-family:Arial;margin:40px;background:#f5f5f5;}");
            out.println(".container{background:white;padding:30px;border-radius:8px;}");
            out.println("h1{color:#d32f2f;}.vuln{background:#fff3cd;padding:10px;margin:10px 0;border-left:4px solid #ffc107;}</style></head>");
            out.println("<body><div class='container'>");
            out.println("<h1>☕ Java Vulnerable Test Application</h1>");
            out.println("<div class='vuln'><strong>⚠️ Warning:</strong> Outdated Java dependencies</div>");
            out.println("<h2>Vulnerable Dependencies</h2><ul>");
            out.println("<li>Spring Framework 4.3.0 (CVE-2016-9878, CVE-2018-1270)</li>");
            out.println("<li>Jackson 2.8.0 (CVE-2017-7525, CVE-2017-15095)</li>");
            out.println("<li>Commons Collections 3.2.1 (CVE-2015-6420)</li>");
            out.println("<li>Log4j 2.14.0 (CVE-2021-44228 Log4Shell)</li>");
            out.println("<li>Commons FileUpload 1.3.1 (CVE-2016-1000031)</li>");
            out.println("<li>Hibernate 5.2.0 (SQL injection)</li>");
            out.println("<li>Tomcat 8.5.0 (multiple CVEs)</li>");
            out.println("</ul><h2>Endpoints</h2>");
            out.println("<p><strong>GET /api/health</strong> - Health check</p>");
            out.println("</div></body></html>");
        }
    }
    
    public static class HealthServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
                throws IOException {
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.println("{\"status\":\"running\",\"app\":\"Java Vulnerable App\"}");
        }
    }
}

// Made with Bob

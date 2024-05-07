package mg.itu.prom16;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    public void doGet (HttpServletRequest req , HttpServletResponse res) throws ServletException, IOException {
        processRequest(req , res);
    }

    public void doPost (HttpServletRequest req , HttpServletResponse res) throws ServletException, IOException {
        processRequest(req , res);
    }

    public void processRequest (HttpServletRequest req , HttpServletResponse res)throws ServletException, IOException{
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        String message = req.getRequestURL().toString();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>HELLO WORLD</TITLE></HEAD>");
        out.println("<BODY>");
        out.println("<BIG>"+ message +"</BIG>");
        out.println("</BODY></HTML>");
    }
}
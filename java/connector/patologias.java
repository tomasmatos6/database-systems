package connector;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/patologias")
public class patologias extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<String> patologias = getPatologiasFromDatabase(); 

        // Set the content type
        response.setContentType("text/html;charset=UTF-8");

        // Get the PrintWriter
        PrintWriter out = response.getWriter();

        // Write the HTML options directly to the response
        for (String patologia : patologias) {
            out.println("<option value=\"" + patologia + "\">" + patologia + "</option>");
        }
    }

    private List<String> getPatologiasFromDatabase() {
        List<String> patologias = null;

        JavaConnector jc = new JavaConnector();
        
        patologias = jc.getPatologias();
        
        jc.DBdisconnect();
        
        return patologias;
    }
}


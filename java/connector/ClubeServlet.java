package connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/ClubeServlet")
public class ClubeServlet extends HttpServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {	
        List<String> clubes = getClubesFromDatabase(); 
        // Set the content type
        response.setContentType("text/html;charset=UTF-8");

        // Get the PrintWriter
        PrintWriter out = response.getWriter();

        // Write the HTML options directly to the response
        for (String clube : clubes) {
            out.println("<option value=\"" + clube + "\">" + clube + "</option>");
        }
    }

    private List<String> getClubesFromDatabase() {
        List<String> clubes = null;

        JavaConnector jc = new JavaConnector();
        
        clubes = jc.getClubes();
        
        jc.DBdisconnect();
        
        return clubes;
    }
}

package connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/GetClubesPTServlet")
public class GetClubesPTServlet extends HttpServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {	
		HttpSession session = request.getSession(true);
     
    	Integer nifPT = (Integer) session.getAttribute("nifPT");
        
		JavaConnector jc = new JavaConnector();
        List<String> clubes = jc.getClubFromPT(nifPT);
        jc.DBdisconnect();
        // Set the content type
        response.setContentType("text/html;charset=UTF-8");

        // Get the PrintWriter
        PrintWriter out = response.getWriter();

        // Write the HTML options directly to the response
        for (String clube : clubes) {
            out.println("<option value=\"" + clube + "\">" + clube + "</option>");
        }
    }
}
package connector;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;



@WebServlet("/CreateSalaServlet")
public class CreateSalaServlet extends HttpServlet {


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

    	HttpSession session = request.getSession(true);
    	int nifClube = -1;
        if (session.getAttribute("nifClube") != null) {
            // Set a default value for 'nif'
        	nifClube = (int) session.getAttribute("nifClube");
        }
    	
        // Get the lotacao parameter from the request
        String lotacaoString = request.getParameter("lotacao");
        int lotacao = Integer.parseInt(lotacaoString);
        JavaConnector jc = new JavaConnector();
        jc.createSala(nifClube, lotacao);
        jc.DBdisconnect();
        
    }
}
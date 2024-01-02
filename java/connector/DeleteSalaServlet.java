package connector;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/DeleteSalaServlet")
public class DeleteSalaServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	String idSalaPar = request.getParameter("idSala");
    	int idSala = Integer.parseInt(idSalaPar);
    	
    	JavaConnector jc = new JavaConnector();
    	jc.deleteSala(idSala);
    	jc.DBdisconnect();
    	
    }
}
package connector;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DesinscricaoAtividadeServlet")
public class DesinscricaoAtividadeServlet extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve parameters from the URL
        int idAtividade = Integer.parseInt(request.getParameter("idAtividade"));
        int nifCliente = Integer.parseInt(request.getParameter("nifCliente"));
        JavaConnector jc = new JavaConnector(); 

        // Call the inscreverAtividade function
        jc.desinscreverAtividade(idAtividade, nifCliente);
        jc.DBdisconnect();
        
    }
}

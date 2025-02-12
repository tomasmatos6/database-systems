package connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/InscricaoAtividadeServlet")
public class InscricaoAtividadeServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Retrieve parameters from the URL
        int idAtividade = Integer.parseInt(request.getParameter("idAtividade"));
        int nifCliente = Integer.parseInt(request.getParameter("nifCliente"));
        JavaConnector jc = new JavaConnector(); 

        // Call the inscreverAtividade function
        try {
			jc.inscreverAtividade(idAtividade, nifCliente);
		} catch (SQLException e) {
			 // Set an error attribute in the request
            request.setAttribute("error_message", "An error occurred: Já está inscrito nesta atividade.");

            // Forward the request to the JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("atividades_cliente.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException | IOException e1) {
                e1.printStackTrace();
            }
		}
        jc.DBdisconnect();
        
    }
}
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
import jakarta.servlet.http.HttpSession;

@WebServlet("/horarios")
public class horarios extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
		JavaConnector jc = new JavaConnector();
		
		String diaSemana = request.getParameter("diaSemana");
        String abertura = request.getParameter("abertura");
        String fecho = request.getParameter("fecho");
        int id = Integer.parseInt(request.getParameter("id"));
        int idClubeHorario = Integer.parseInt(request.getParameter("idClubeHorario"));
        int nifClube = -1;
        
        if (session.getAttribute("nifClube") != null) {
            // Set a default value for 'nif'
        	nifClube = (int) session.getAttribute("nifClube");
        }
        
        try {
			jc.setHorario(abertura, fecho, diaSemana, id, nifClube, idClubeHorario);
		} catch (SQLException e) {
			// Set an error attribute in the request
            request.setAttribute("error_message", "An error occurred: " + e.getMessage());

            // Forward the request to the JSP
            RequestDispatcher dispatcher = request.getRequestDispatcher("manage_clube.jsp");
            try {
                dispatcher.forward(request, response);
            } catch (ServletException | IOException e1) {
                e1.printStackTrace();
            }
		} finally {
			jc.DBdisconnect();
		}   
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.write("Horário Atualizado!");
	}

	
}
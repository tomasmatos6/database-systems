package connector;

import java.io.IOException;
import java.io.PrintWriter;

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
        String id = request.getParameter("id");
        int idInt = Integer.parseInt(id);
        int nifClube = -1;

        if (session.getAttribute("nifClube") != null) {
            // Set a default value for 'nif'
        	nifClube = (int) session.getAttribute("nifClube");
        }
        
        jc.setHorario(abertura, fecho, diaSemana, idInt, nifClube);
        jc.DBdisconnect();
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.write("Horário Atualizado!");
	}

	
}
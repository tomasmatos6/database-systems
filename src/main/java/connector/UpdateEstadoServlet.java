package connector;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateEstadoServlet")
public class UpdateEstadoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(true);
    	int nifClube = -1;
        if (session.getAttribute("nifClube") != null) {
            // Set a default value for 'nif'
        	nifClube = (int) session.getAttribute("nifClube");
        }
    	
        int idEquipamento = Integer.parseInt(request.getParameter("idEquipamento"));
        String nomeEquipamento = request.getParameter("nomeEquipamento");
        String novoEstado = request.getParameter("newEstado");

        JavaConnector jc = new JavaConnector();
        jc.updateEstado(idEquipamento, nomeEquipamento, nifClube, novoEstado);
        jc.DBdisconnect();

        response.getWriter().write("Update successful");
    }
}
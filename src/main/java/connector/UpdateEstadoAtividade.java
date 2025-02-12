package connector;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateEstadoAtividade")
public class UpdateEstadoAtividade extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	HttpSession session = request.getSession(true);
    	int nifClube = -1;
        if (session.getAttribute("nifClube") != null) {
            // Set a default value for 'nif'
        	nifClube = (int) session.getAttribute("nifClube");
        }
        
        String idAtividadeStr = request.getParameter("idAtividade");
        int idAtividade = Integer.parseInt(idAtividadeStr);
        String novoEstado = request.getParameter("newEstado");
        

        JavaConnector jc = new JavaConnector();
        jc.updateEstadoAtividade(idAtividade, novoEstado);
        jc.DBdisconnect();

        response.getWriter().write("Update successful");
    }
}
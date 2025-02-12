package connector;

import java.io.IOException;
import java.util.Enumeration;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/updateRecomendacao")
public class updateRecomendacao extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JavaConnector jc = new JavaConnector();
		String nomeCliente = request.getParameter("nomeCliente");
		String idEquipamento = request.getParameter("idEquipamento");
		String dataFim = request.getParameter("dataFim");
		String descricao = request.getParameter("descricao");
		jc.updateRecomendacao(nomeCliente, idEquipamento, dataFim, descricao);
		jc.DBdisconnect();
		
		response.getWriter().write("Update successful");
	}
}

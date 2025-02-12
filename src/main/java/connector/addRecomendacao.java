package connector;

import java.io.IOException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/addRecomendacao")
public class addRecomendacao extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JavaConnector jc = new JavaConnector();
		HttpSession session = request.getSession(true);
		
		int nifPT = (int) session.getAttribute("nifPT");
		String nomeCliente = request.getParameter("nomeCliente");
		String equipamento = request.getParameter("equipamento");
		String dataInicio = request.getParameter("dataInicio");
		String dataFim = request.getParameter("dataFim");
		String descricao = request.getParameter("descricao");
		System.out.println("Cliente " + nomeCliente);
		System.out.println("Equipamento " + equipamento);
		System.out.println("Inicio " + dataInicio);
		System.out.println("Fim " + dataFim);
		System.out.println("descricao " + descricao);
		jc.addRecomendacao(nifPT, nomeCliente, equipamento, dataInicio, dataFim, descricao);
		jc.DBdisconnect();
		
		
		response.sendRedirect("manage_recomendacoes.jsp");
	}
}

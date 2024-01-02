package connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/addAtividade")
public class addAtividade extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws IOException {
		JavaConnector jc = new JavaConnector();
		HttpSession session = request.getSession(true);
		
		int nif = (int) session.getAttribute("nifPT");
		String nome = request.getParameter("atividade");
		String tipo = request.getParameter("tipo");
		int vagas = request.getParameter("vagas").equals("") ? 1 : Integer.valueOf(request.getParameter("vagas"));
		String data = request.getParameter("data");
		String horaInicio = request.getParameter("horaInicio");
		String horaFim = request.getParameter("horaFim");
		int idSala = Integer.valueOf(request.getParameter("sala"));
		String[] idsEquipamento = request.getParameter("selectedEquipamentos").split(",",0);

		int idHorario = jc.getHorario(horaInicio, horaFim);
		
		int idAtividade = jc.addAtividade(nome, nif, data, idHorario, tipo, vagas);
		for(String idEquipamento : idsEquipamento)
			jc.addASE(idAtividade, idSala, Integer.valueOf(idEquipamento));
		
		response.sendRedirect("atividades_pt.jsp");
	}
}

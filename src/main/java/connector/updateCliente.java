package connector;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@WebServlet("/updateCliente")
public class updateCliente extends HttpServlet {
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		HttpSession session = request.getSession(true);
		JavaConnector jc = new JavaConnector();
		String nif = request.getParameter("nif");
        String email = request.getParameter("email");
        String telemovel = request.getParameter("telemovel");
        String objetivo = request.getParameter("objetivo");
        String[] nomes =  request.getParameterValues("nome");
        String[] datasInicio = request.getParameterValues("dataInicio");
        String[] datasFim = request.getParameterValues("dataFim");
        List<Patologia> patologias = new ArrayList<>();
        for(int i=0;i<nomes.length;i++) {
        	patologias.add(new Patologia(nomes[i], datasInicio[i], datasFim[i]));
        }
       
		session.setAttribute("email_cliente", email);
        jc.updateCliente(nif,email,telemovel,objetivo,patologias);
        jc.DBdisconnect();
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        out.write("Cliente Atualizado!");
	}
}

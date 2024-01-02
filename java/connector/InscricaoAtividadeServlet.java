package connector;

import java.io.IOException;
import java.io.PrintWriter;

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
        System.out.println("ID ATIVIDADE SERVLET: " + idAtividade);
        System.out.println("NIFCLIENTE SERVLET: " + nifCliente);
        JavaConnector jc = new JavaConnector(); 

        // Call the inscreverAtividade function
        jc.inscreverAtividade(idAtividade, nifCliente);
        jc.DBdisconnect();
        
    }
}
package connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/processRegistration")
@MultipartConfig
public class processRegistration extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession(true);
        request.getRequestDispatcher("/add_cliente.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession(true);
    	String nif = request.getParameter("nif");
        String nome = request.getParameter("nome");
        String dataNascimento = request.getParameter("data_nascimento");
        String email = request.getParameter("email");
        String telemovel = request.getParameter("telemovel");
        String objetivos = request.getParameter("objetivos");
        
        int nifClube = -1;

        if (session.getAttribute("nifClube") != null) {
            // Set a default value for 'nif'
        	nifClube = (int) session.getAttribute("nifClube");
        }

        // Retrieve the file part from the request
        Part filePart = request.getPart("foto");

        // Check if a file was uploaded
        if (filePart != null && filePart.getSize() > 0) {
            // Get the input stream of the file
            InputStream fileContent = filePart.getInputStream();

            // Now you can use the input stream for further processing, e.g., save it to a file or store it in a database
        }
        
        String[] nomes = request.getParameterValues("nome");
        String[] datasInicio = request.getParameterValues("dataInicio");
        String[] datasFim = request.getParameterValues("dataFim");
        
        List<Patologia> patologias = new ArrayList<>();
        for(int i=0;i<nomes.length;i++) {
        	patologias.add(new Patologia(nomes[i], datasInicio[i], datasFim[i]));
        }
        
        JavaConnector jc = new JavaConnector();
        
        jc.addCliente(nif, nome, dataNascimento, email, telemovel, objetivos, patologias, nifClube);
        
        jc.DBdisconnect();
       
        response.sendRedirect("manage_clientes.jsp");
    }
}


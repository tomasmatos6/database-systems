package connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/ListaClientesDePTServlet")
public class ListaClientesDePTServlet extends HttpServlet{
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {	
		HttpSession session = request.getSession(true);
		String nifPTPar = request.getParameter("nifPT");
    	int nifPT = Integer.parseInt(nifPTPar);
		
		JavaConnector jc = new JavaConnector();
		
        List<Integer> listaClientes = jc.getClientNIFsForPT(nifPT);
        
        List<String> nomesClientes = new ArrayList<String>();
        
        for(Integer cliente : listaClientes) {

        	nomesClientes.add(jc.getNomeClienteByNif(cliente));
        }
		
        jc.DBdisconnect();
        
        // Set the content type to text/plain
        response.setContentType("text/plain;charset=UTF-8");

        // Get the PrintWriter to write the list as a whole to the response
        try (PrintWriter out = response.getWriter()) {
            // Convert the first list to a string
            String clientsString = listaClientes.stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining("\n"));

            // Convert the second list to a string
            String anotherListString = nomesClientes.stream()
                    .collect(Collectors.joining("\n"));

            // Write both lists to the response
            out.println("\n" + clientsString);
            out.println("\n" + anotherListString);
        }
    }
}
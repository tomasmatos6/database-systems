package connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/AutoSearchClientes")
public class AutoSearchClientes extends HttpServlet{

	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	    	
	    	HttpSession session = request.getSession(true);
	        String term = request.getParameter("term");
	        int nifClube = (int) session.getAttribute("nifClube");
	        int nifPT = (int) session.getAttribute("nifPT");	
	        System.out.println("NIFCLUBE: " + nifClube);
	        
	        PrintWriter out = response.getWriter();

	        JavaConnector jc = new JavaConnector();
	        List<String> results = jc.autoSearchCliente(term, nifClube, nifPT);

	        for (String result : results) {
	            out.println(result);
	        }
	        
	        
	        jc.DBdisconnect();
	    }
	}

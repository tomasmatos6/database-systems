package connector;

import java.io.IOException;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UpdateValueServlet")
public class UpdateValueServlet extends HttpServlet {

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String newValue = request.getParameter("newValue");
    	String nifClubePar = request.getParameter("nifClube");
    	int nifClube = Integer.parseInt(nifClubePar);
    	String tipo = request.getParameter("type");
		
    	JavaConnector jc = new JavaConnector();
    	
		switch(tipo) {
			case "telefone":
		        Pattern pattern = Pattern.compile("^\\d{9}$");
		        Matcher matcher = pattern.matcher(newValue);
		        //9 digitos
		        if (matcher.matches()) {
		            jc.updateTelefone(nifClube, newValue);
		            jc.DBdisconnect();
		        } else {
		            System.out.println("Número de telefone inválido.");
		        }
				
				break;
			case "email":
				jc.updateEmail(nifClube, newValue);
				jc.DBdisconnect();
				break;
		}
		
	}
}

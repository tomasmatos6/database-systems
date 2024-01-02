package connector;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/processPTs")
public class processPTs extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(true);
    	
    	String data = request.getParameter("data");
    	String action = request.getParameter("action");
    	
		JavaConnector jc = new JavaConnector();
		
        switch (action) {
        
        //exportar pt
        case "export":
        	Utilizador pt_info_XML = jc.carregarPT(data);
        	PersonalTrainerXML exportar = new PersonalTrainerXML(pt_info_XML);
        	
            response.setContentType("application/xml;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=cliente.xml");

            response.setBufferSize(0);
            response.resetBuffer();

            // Use the response output stream to write the XML content
            exportar.exportToXml(response.getOutputStream());
        	jc.DBconnect();
            break;
        //apagar pt
        case "delete":
        	int nif = Integer.parseInt(data);	
        	jc.deletePersonalTrainer(nif);
        	jc.DBdisconnect();
            break;
        default:
            // Handle unknown action
            response.getWriter().write("Unknown action");
            return;
        }
    }
}

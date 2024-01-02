package connector;

import java.io.IOException;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/processClients")
public class processClients extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
    	HttpSession session = request.getSession(true);
    	
    	String data = request.getParameter("data");
    	String action = request.getParameter("action");
    	
		JavaConnector jc = new JavaConnector();
		
        switch (action) {
        
        //editar perfil cliente
        case "edit":
            // Recebe objeto cliente
            Utilizador cliente_info = jc.carregarPerfil(data);
            // Guarda na sessao
            session.setAttribute("info_cliente", cliente_info);
            jc.DBdisconnect();
            break;
        //exportar pdf
        case "export":
            Utilizador cliente_info_XML = jc.carregarPerfil(data);
            ClienteXML exportar = new ClienteXML(cliente_info_XML);
            response.setContentType("application/xml;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=cliente.xml");

            response.setBufferSize(0);
            response.resetBuffer();

            // Use the response output stream to write the XML content
            if(response.getOutputStream()!=null) System.out.println("EXISTE OUTPUTSTREAM");
            exportar.exportToXml(response.getOutputStream());
            System.out.println("XML EXPORTADO");
            break;
            
        //apagar cliente
        case "delete":
        	int nif = Integer.parseInt(data);
        	jc.deleteClient(nif);
        	jc.DBdisconnect();
            break;
        default:
            // Handle unknown action
            response.getWriter().write("Unknown action");
            return;
        }
    }
}

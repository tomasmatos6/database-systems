package connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;


/**
 * Servlet implementation class updateEquipamento
 */
@WebServlet("/updateEquipamento")
@MultipartConfig
public class updateEquipamento extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		HttpSession session = request.getSession(true);
        int nifClube = -1;
        if (session.getAttribute("nifClube") != null) {
            // Set a default value for 'nif'
            nifClube = (int) session.getAttribute("nifClube");
        }
        Part fotoPart = request.getPart("foto");
        Part videoPart = request.getPart("video");
        String nomeEquipamento = request.getParameter("nome");
        int idEquipamento = Integer.parseInt(request.getParameter("id"));
        System.out.println("EQUIPAMENTO NOME: " + nomeEquipamento);
        System.out.println("EQUIPAMENTO: " + idEquipamento);
        
     
        
        try {
        	JavaConnector jc = new JavaConnector();
        	System.out.println(fotoPart);
        	System.out.println(videoPart);
    	   if (fotoPart != null && fotoPart.getSize() > 0) {
    		   Blob fotoBlob = convertToBlob(fotoPart.getInputStream());
    		   jc.insertFoto(nomeEquipamento, idEquipamento, fotoBlob);
           }
           if (videoPart != null && videoPart.getSize() > 0) {
        	   Blob videoBlob = convertToBlob(videoPart.getInputStream());
        	   jc.insertVideo(nomeEquipamento, idEquipamento, videoBlob);
           }
        	jc.DBdisconnect();

        } catch (SQLException e) {
            e.printStackTrace();
        }
	}
	
	// Helper method to convert InputStream to Blob
    private Blob convertToBlob(InputStream inputStream) throws SQLException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;

        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new SerialBlob(outputStream.toByteArray());
    }

}

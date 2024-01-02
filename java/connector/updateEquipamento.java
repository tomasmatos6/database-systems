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
        Part fotoPart = null;
        Part videoPart = null;
        String nomeEquipamento = request.getParameter("nome");
        String idEquipamento = request.getParameter("id");
        System.out.println("EQUIPAMENTO NOME: " + nomeEquipamento);
        System.out.println("EQUIPAMENTO: " + idEquipamento);
        
        // Retrieve form fields and file parts
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            switch (part.getName()) {
                case "foto":
                    fotoPart = part;
                    break;
                case "video":
                    videoPart = part;
                    break;
            }
        }
        
        try {
            // Convert the InputStreams of file parts into Blobs
            Blob fotoBlob = convertToBlob(fotoPart.getInputStream());
            Blob videoBlob = convertToBlob(videoPart.getInputStream());


            JavaConnector jc = new JavaConnector();
            jc.updateEquipamento(nomeEquipamento, idEquipamento, fotoBlob, videoBlob);

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

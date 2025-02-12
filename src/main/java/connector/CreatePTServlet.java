package connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

@WebServlet("/CreatePTServlet")
@MultipartConfig
public class CreatePTServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	JavaConnector jc = new JavaConnector();
    	// Retrieve selected clubes from the form
    	String selectedClubes = request.getParameter("selectedClubes");
    	String[] clubesArray = selectedClubes.split(",");
    	String[] clubesNif = new String[clubesArray.length];
        
        // Retrieve form values
        String nif = request.getParameter("nif");
        String nome = request.getParameter("nome");
        String email = request.getParameter("email");
        String telemovel = request.getParameter("telemovel");
        Part fotoPart = request.getPart("fotoPT");
        Blob fotoBlob = null;
        try {
			fotoBlob = convertToBlob(fotoPart.getInputStream());
		} catch (SQLException | IOException e) {
			e.printStackTrace();
		}
        // Print form values for debugging
        System.out.println("NIF: " + nif);
        System.out.println("Nome: " + nome);
        System.out.println("Email: " + email);
        System.out.println("Telemovel: " + telemovel);
        
        for (int i = 0; i < clubesArray.length; i++) {
            String clube = clubesArray[i];

            clubesNif[i] = jc.getNifFromNomeClube(clube);
            System.out.println("Clube: " + clubesNif[i]);
        }
        System.out.println("Foto: " + fotoPart);
        jc.addPersonalTrainer(nif, nome, email, telemovel, clubesNif, fotoBlob);
        jc.DBdisconnect();
        response.sendRedirect("manage_pts.jsp");

    }
    
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
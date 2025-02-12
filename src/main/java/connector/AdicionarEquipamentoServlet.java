package connector;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/AdicionarEquipamentoServlet")
@MultipartConfig
public class AdicionarEquipamentoServlet extends HttpServlet{

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	HttpSession session = request.getSession(true);
        request.getRequestDispatcher("/addEquipamento.jsp").forward(request, response);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        int nifClube = -1;
        if (session.getAttribute("nifClube") != null) {
            // Set a default value for 'nif'
            nifClube = (int) session.getAttribute("nifClube");
        }
        String nomeEquipamento = null;
        Part fotoPart = null;
        Part videoPart = null;
        
        
        // Retrieve form fields and file parts
        Collection<Part> parts = request.getParts();
        for (Part part : parts) {
            switch (part.getName()) {
                case "nomeEquipamento":
                    nomeEquipamento = extractTextFromPart(part);
                    break;
                case "fotoEquipamento":
                    fotoPart = part;
                    break;
                case "videoFile":
                    videoPart = part;
                    break;
            }
        }

        try {
            // Convert the InputStreams of file parts into Blobs
            Blob fotoBlob = convertToBlob(fotoPart.getInputStream());
            Blob videoBlob = convertToBlob(videoPart.getInputStream());


            JavaConnector jc = new JavaConnector();
            jc.addEquipamento(nomeEquipamento, "disponível", nifClube, fotoBlob, videoBlob);

        } catch (SQLException e) {
            e.printStackTrace();
        }

            

        response.sendRedirect("manage_equipamentos.jsp");
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

    // Helper method to extract text content from a Part
    private String extractTextFromPart(Part part) throws IOException {
        return new BufferedReader(new InputStreamReader(part.getInputStream()))
                .lines().collect(Collectors.joining("\n"));
    }
}
	


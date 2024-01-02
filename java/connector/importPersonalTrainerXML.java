package connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@WebServlet("/importPersonalTrainerXML")
public class importPersonalTrainerXML extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	HttpSession session = request.getSession(true);
    	int nifClube = -1;

    	if (session.getAttribute("nifClube") != null) {
    	    // Set a default value for 'nif'
    		nifClube = (int) session.getAttribute("nifClube");
    	}
    	
    	StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }

        String xmlData = sb.toString();
        if (xmlData != null && !xmlData.isEmpty()) {
            

        	// Extract values using substrings
            String nif = extrairElementos(xmlData, "nif");
            String nome = extrairElementos(xmlData, "nome");
            String email = extrairElementos(xmlData, "email");
            String telemovel = extrairElementos(xmlData, "telemovel");
            String[] clubes = extractClubes(xmlData);
            String fotoBase64 = extrairElementos(xmlData, "photo");
            Blob fotoBlob = convertBase64ToBlob(fotoBase64);

            JavaConnector jc = new JavaConnector();
            
            jc.addPersonalTrainer(nif, nome, email, telemovel, clubes, fotoBlob);
            
            jc.DBdisconnect();
           
            response.sendRedirect("manage_pts.jsp");
            
        } else {
        	System.out.println("NAO Há DATA");
        }
    }
    
  
    private static String extrairElementos(String xmlString, String nome) {
    	
        String inicio = "<" + nome + ">";
        String fim = "</" + nome + ">";
        int startIndex = xmlString.indexOf(inicio) + inicio.length();
        int endIndex = xmlString.indexOf(fim);
        if (startIndex != -1 && endIndex != -1) {
            return xmlString.substring(startIndex, endIndex);
        } else {
            return null; 
        }
    }
    
    private static String[] extractClubes(String xmlString) {
        List<String> clubesList = new ArrayList<>();
        String inicio = "<clube><nif>";
        String fim = "</nif>";
        int startIndex = xmlString.indexOf(inicio);
        while (startIndex != -1) {
            int endIndex = xmlString.indexOf(fim, startIndex);
            if (endIndex != -1) {
                String clube = xmlString.substring(startIndex + inicio.length(), endIndex);
                clubesList.add(clube);
                startIndex = xmlString.indexOf(inicio, endIndex);
            } else {
                break;
            }
        }
        return clubesList.toArray(new String[0]);
    }
    
    private static Blob convertBase64ToBlob(String base64Data) {
        try {
            // Decode Base64 to byte array
            byte[] imageData = Base64.getDecoder().decode(base64Data);

            // Get a Blob object from the byte array
            return new javax.sql.rowset.serial.SerialBlob(imageData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
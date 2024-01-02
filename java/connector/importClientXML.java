package connector;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@WebServlet("/importClientXML")
public class importClientXML extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    	HttpSession session = request.getSession(true);
    	int nifClube = -1;

    	if (session.getAttribute("nifClube") != null) {
    	    // Set a default value for 'nif'
    		nifClube = (int) session.getAttribute("nifClube");
    	}
    	
        String xmlData = request.getParameter("xmlData");

        if (xmlData != null && !xmlData.isEmpty()) {


        	// Extract values using substrings
            String nif = extrairElementos(xmlData, "nif");
            String nome = extrairElementos(xmlData, "nome");
            String dataNascimento = extrairElementos(xmlData, "dataNascimento");
            String email = extrairElementos(xmlData, "email");
            String telemovel = extrairElementos(xmlData, "telemovel");
            String objetivos = extrairElementos(xmlData, "objetivos");
            List<Patologia> patologias = extractPatologias(xmlData);
            
            // Log or use the extracted values as needed
            System.out.println("NIF: " + nif);
            System.out.println("Nome: " + nome);
            System.out.println("Data de Nascimento: " + dataNascimento);
            System.out.println("Email: " + email);
            System.out.println("Telemovel: " + telemovel);
            System.out.println("Objetivos: " + objetivos);
            //System.out.println("Patologias: " + String.join(", ", patologias));
        	
            
            JavaConnector jc = new JavaConnector();
            
            jc.addCliente(nif, nome, dataNascimento, email, telemovel, objetivos, patologias, nifClube);
            
            jc.DBdisconnect();
           
            response.sendRedirect("manage_clientes.jsp");
            
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
    
    private static List<Patologia> extractPatologias(String xmlString) {
        List<Patologia> patologias = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlString.getBytes()));

            NodeList patologiaNodes = document.getElementsByTagName("patologia");

            for (int i = 0; i < patologiaNodes.getLength(); i++) {
                Element patologiaElement = (Element) patologiaNodes.item(i);

                String nome = patologiaElement.getElementsByTagName("nome").item(0).getTextContent();
                String dataInicio = patologiaElement.getElementsByTagName("dataInicio").item(0).getTextContent();
                String dataFim = patologiaElement.getElementsByTagName("dataFim").item(0).getTextContent();

               patologias.add(new Patologia(nome,dataInicio,dataFim));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return patologias;
    }
}
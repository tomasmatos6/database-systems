package connector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;

public class PersonalTrainerXML {
    private Utilizador utilizador;

    public PersonalTrainerXML(Utilizador utilizador) {
        this.utilizador = utilizador;
    }

    public void exportToXml(OutputStream outputStream) {
        try {
            // Create a DocumentBuilder
            Document document = createXmlDocument();

            // Write the Document to the output stream
            writeXmlToOutputStream(document, outputStream);


        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    private String documentToString(Document document) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(writer));
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Document createXmlDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        // Create the root element for Cliente
        Element ptElement = document.createElement("personal_trainer");
        document.appendChild(ptElement);

        appendElement(document, ptElement, "nif", utilizador.getNif());
        appendElement(document, ptElement, "nome", utilizador.getNome());
        appendElement(document, ptElement, "email", utilizador.getEmail());
        appendElement(document, ptElement, "telemovel", utilizador.getTelemovel());
        
        // Add child elements for Patologias
        Element clubesElement = document.createElement("clubes");
        ptElement.appendChild(clubesElement);

        List<String> clubes = utilizador.getClubes();
        if (clubes != null) {
            for (String clube : clubes) {
                Element clubeElement = document.createElement("clube");
                appendElement(document, clubeElement, "nif", clube);
                clubesElement.appendChild(clubeElement);
            }
        }
        
        Element photoElement = convertBlobToXmlElement(utilizador.getFoto());
        System.out.println("PHOTOELEMENT " + photoElement);
        
        if (photoElement != null) {
        	System.out.println("EXISTE");
            ptElement.appendChild(document.importNode(photoElement, true));
        }

        return document;
    }
    
    

    private void appendElement(Document document, Element parentElement, String tagName, String textContent) {
        Element element = document.createElement(tagName);
        if (textContent != null) {
            element.appendChild(document.createTextNode(textContent));
        }
        parentElement.appendChild(element);
    }
    
    public static Element convertBlobToXmlElement(Blob blob) {
        try {
            // Step 1: Retrieve bytes from Blob
            byte[] imageData = blob.getBytes(1, (int) blob.length());

            // Step 2: Convert bytes to Base64
            String base64Image = Base64.getEncoder().encodeToString(imageData);

            // Step 3: Create Document and Element
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("photo");
            doc.appendChild(rootElement);

            // Add Base64-encoded content as text content
            rootElement.appendChild(doc.createTextNode(base64Image));

            return rootElement;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    
    private void writeXmlToOutputStream(Document document, OutputStream outputStream) {
        try {
            if (document == null) {
                throw new IllegalArgumentException("Document is null");
            }

            // Create a Transformer
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // Use a DOMSource and StreamResult to write the Document to the output stream
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(outputStream);

            // Transform the DOMSource into a StreamResult
            transformer.transform(source, result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
package connector;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.List;

public class ClienteXML {
    private Utilizador utilizador;

    public ClienteXML(Utilizador utilizador) {
        this.utilizador = utilizador;
    }

    public void exportToXml(OutputStream outputStream) {
        try {
            // Create a DocumentBuilder
            Document document = createXmlDocument();

            // Write the Document to the output stream
            writeXmlToOutputStream(document, outputStream);

            // For debugging, print the XML as a string
            System.out.println(documentToString(document));

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
        Element clienteElement = document.createElement("cliente");
        document.appendChild(clienteElement);

        appendElement(document, clienteElement, "nif", utilizador.getNif());
        appendElement(document, clienteElement, "nome", utilizador.getNome());
        appendElement(document, clienteElement, "dataNascimento", utilizador.getDataNascimento());
        appendElement(document, clienteElement, "email", utilizador.getEmail());
        appendElement(document, clienteElement, "telemovel", utilizador.getTelemovel());
        appendElement(document, clienteElement, "objetivos", utilizador.getObjetivos());

        // Add child elements for Patologias
        Element patologiasElement = document.createElement("patologias");
        clienteElement.appendChild(patologiasElement);

        List<Patologia> patologias = utilizador.getPatologias();
        if (patologias != null) {
            for (Patologia patologia : patologias) {
                Element patologiaElement = document.createElement("patologia");
                appendElement(document, patologiaElement, "nome", patologia.getNome());
                appendElement(document, patologiaElement, "dataInicio", patologia.getDataInicio());
                appendElement(document, patologiaElement, "dataFim", patologia.getDataFim());
                patologiasElement.appendChild(patologiaElement);
            }
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
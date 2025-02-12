package connector;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

//SetClubeSessionAttributeServlet.java
@WebServlet("/SetClubeSessionAttributeServlet")
public class SetClubeSessionAttributeServlet extends HttpServlet {

 protected void doPost(HttpServletRequest request, HttpServletResponse response)
         throws ServletException, IOException {
     // Get the selected club value from the request parameter
     String selectedClube = request.getParameter("selectedClube");
     JavaConnector jc = new JavaConnector();
     
     int nif = jc.getClubeNif(selectedClube);
     
     jc.DBdisconnect();
     
     // Set the selected club as a session attribute
     HttpSession session = request.getSession();
     session.setAttribute("nifClube", nif);
 }
}

package connector;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/updatePT")
public class updatePT extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        JavaConnector jc = new JavaConnector();
        
        String telemovel = request.getParameter("telemovel");
        String email = request.getParameter("email");
        String nif = request.getParameter("nif");
        String[] selectedClubes = request.getParameterValues("selectedClubes");
        jc.deletePTClube(nif);
        for(String clube : selectedClubes) {
        	System.out.println(clube);
        	jc.updatePTClube(nif, clube);
        }


        jc.updatePT(nif, telemovel, email);
        jc.DBdisconnect();
        out.write("Personal Trainer Atualizado!");
    }
}
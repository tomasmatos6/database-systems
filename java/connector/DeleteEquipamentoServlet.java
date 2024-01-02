package connector;

import java.io.IOException;
import java.io.PrintWriter;

import connector.JavaConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/DeleteEquipamentoServlet")
public class DeleteEquipamentoServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int idEquipamento = Integer.parseInt(request.getParameter("idEquipamento"));
            JavaConnector jc = new JavaConnector();
            jc.deleteEquipamento(idEquipamento);
            jc.DBdisconnect();
        } catch (NumberFormatException e) {
            out.println("Error: " + e.getMessage());
        } finally {
            out.close();
        }
    }
}
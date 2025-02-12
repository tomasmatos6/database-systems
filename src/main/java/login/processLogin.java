package login;

import connector.JavaConnector;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/processLogin")
public class processLogin extends HttpServlet{
	
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
	           throws ServletException, java.io.IOException {
				HttpSession session = request.getSession(true);
				response.setContentType("text/html;charset=UTF-8");
					try
					{	    
						JavaConnector jc = new JavaConnector();
						
						String email = request.getParameter("email");
						String password = request.getParameter("password");
						
						int tipoUtilizador = jc.fazerLogin(email, password);
						if (tipoUtilizador > 0)
						{
							switch (tipoUtilizador) {
							case 1:
								session.setAttribute("permissoes", tipoUtilizador);
								session.setAttribute("email_cliente", email);
								jc.DBdisconnect();
								response.sendRedirect("dashboard_cliente.jsp");
								break;
								
							case 2:
								session.setAttribute("permissoes", tipoUtilizador);
								session.setAttribute("email", email);
								jc.DBdisconnect();
								response.sendRedirect("dashboard_pt.jsp");
								break;
							
							case 3:
								session.setAttribute("permissoes", tipoUtilizador);
								session.setAttribute("email", email);
								jc.DBdisconnect();
								response.sendRedirect("dashboard_gestor.jsp");
								break;
							}
						}
						 
						else 
						{
							System.out.println("Erro ao fazer login...");
							response.sendRedirect("login.jsp"); 
						} 
					} 
						
					catch (Throwable theException) 	    
					{
						System.out.println(theException); 
						response.sendRedirect("login.jsp"); 
					}
	}
	
}

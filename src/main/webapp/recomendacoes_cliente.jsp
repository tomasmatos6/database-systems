<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<link rel="stylesheet" type="text/css" href="style.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        /* Add some basic styling to the icons */
        .icon {
            margin-right: 15px; /* Adjust spacing as needed */
        }
 		 img.logo {
            width: 180px;
            height: 60px;
        }
        .content {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
        }
        .sidebar {
        	background-color: #3e725a;
        	
        
        }
        .sidebar a{
        	background-color: #3e725a;
        	color:white;
        
        }
        
    </style>
<title>FitnessUP</title>
</head>
<body>

<%
int permissoes = (int) session.getAttribute("permissoes");
String email_cliente = (String) session.getAttribute("email_cliente");
System.out.println("EMAIL: " + email_cliente);
int nifClube = -1;

if (session.getAttribute("nifClube") != null) {
    // Set a default value for 'nif'
	nifClube = (int) session.getAttribute("nifClube");
}

JavaConnector jc = new JavaConnector();
    		
Utilizador cliente = jc.carregarPerfil(email_cliente);

session.setAttribute("info_cliente", cliente);
%>


<div class="content">


	<div class="sidebar">
			<a><img src="https://cdn.discordapp.com/attachments/697907812524884021/1185417478512324639/Untitled-removebg-preview.png?ex=658f890e&is=657d140e&hm=44a7f107dc445a250f059e98ed6935ddf42658c6993e7b29ad047e93dcface26&" alt="Logo" class="logo"></a>
			<a href="dashboard_cliente.jsp" class="icon"><i class="fa-solid fa-house"></i>  Página Inicial</a>
	        <a href="perfil_cliente_cliente.jsp" class="icon"><i class="fas fa-address-book"></i>  Perfil</a>
	        <a href="atividades_cliente.jsp" class="icon"><i class="fa-regular fa-calendar-days"></i>  Atividades</a>
	        <a href="atividades_semanais_cliente.jsp" class="icon"><i class="fa-solid fa-door-closed"></i>  Calendário Semanal</a>
	        <a href="recomendacoes_cliente.jsp" class="icon"><i class="fa-solid fa-dumbbell"></i>  Recomendações</a>
	        <a href="LogoutServlet" class="icon"><i class="fa-solid fa-right-from-bracket"></i>  Terminar Sessão</a>
	</div>

</div>

<h1>Recomendações do Cliente <%= cliente.getNome() %></h1>

<table border="1">
	<tr>
		<th>Equipamento</th>
		<th>Descrição</th>
		<th>Data de Início</th>
		<th>Data de Fim</th>
	</tr>
	
	<%
		ResultSet resultSet = jc.getRecomendacoesCliente(cliente.getNif());
		try {
			while(resultSet.next()) {
				%>
			<tr>
				<td><%= resultSet.getString("equipamentoNome") %></td>
				<td><%= resultSet.getString("descricao") %></td>
				<td><%= resultSet.getString("dataInicio") %></td>
				<td><%= resultSet.getString("dataFim") %></td>
			</tr>
				<%
			}
		} catch (SQLException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (resultSet != null) resultSet.close();
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	%>
	
</table>
</body>
</html>
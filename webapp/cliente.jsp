<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<%@ page import="java.sql.Blob" %>
<%@ page import="java.util.Base64" %>
<%@ page import="java.io.InputStream" %>
<%@ page import="java.util.List" %>
<%@ page import="connector.Patologia" %>
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
        #clubeDropdown {
		    padding: 5px;
		    font-size: 14px;
		    border-radius: 3px;
			background-color: #111;
			color:white;
			border: none;
			font-size: 14px;
			font-weight: bold;
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
        .sidebar a {
            padding: 8px 8px 8px 32px;
            text-decoration: none;
            font-size: 18px;
            color: white;
            display: block;
        }

        .sidebar a:hover {
            color: grey;
        }
        
    </style>
<title>FitnessUP</title>
</head>
<body>

<%

int permissoes = (int) session.getAttribute("permissoes");
String email = (String) session.getAttribute("email");
int nifPT = -1;
int nifClube = -1;
if(session.getAttribute("nifClube")!=null){
	nifClube = (int)session.getAttribute("nifClube");	
}
JavaConnector jc = new JavaConnector();
Utilizador personalTrainer = jc.carregarPersonalTrainer(email);
session.setAttribute("nifPT", Integer.valueOf(personalTrainer.getNif()));

%>

<div class="content">
<h1>Personal Trainer</h1>
<h2><%= "Nif do clube atual: " + session.getAttribute("nifClube")%></h2>
	<div class="sidebar" style="background-color: #0070dd;">
			<a><img src="https://cdn.discordapp.com/attachments/697907812524884021/1185417478512324639/Untitled-removebg-preview.png?ex=658f890e&is=657d140e&hm=44a7f107dc445a250f059e98ed6935ddf42658c6993e7b29ad047e93dcface26&" alt="Logo" class="logo"></a>
			<a class="icon"><i class="fa-solid fa-location-dot"></i><select id="clubeDropdown" style="background-color: #0070dd;">
            <option value="">Select Clube</option>
        	</select></a> 
			<a href="" class="icon"><i class="fa-solid fa-house"></i>  Página Inicial</a>
	        <a href="atividades_pt.jsp" class="icon"><i class="fas fa-address-book"></i>  Atividades</a>
	        <a href="autosearch_clientes.jsp" class="icon"><i class="fa-regular fa-calendar-days"></i>  Clientes</a>
	        <a href="" class="icon"><i class="fa-solid fa-door-closed"></i>  Recomendações</a>
	        <a href="LogoutServlet" class="icon"><i class="fa-solid fa-right-from-bracket"></i>  Terminar Sessão</a>
	</div>

</div>


<% 

String nomeCliente = request.getParameter("nomeCliente");
System.out.println("Nome do cliente: " + nomeCliente);
String email_cliente = jc.getNomeByEmail(nomeCliente);
Utilizador cliente = jc.carregarPerfil(email_cliente);
int nifCliente = Integer.parseInt(cliente.getNif());
%> 

<h1>Perfil do cliente <%= cliente.getNome() %></h1>

<table border="1">
    <tr>
        <td>Email:</td>
        <td><%= cliente.getEmail() %></td>
    </tr>
    <tr>
        <td>Data de Nascimento:</td>
        <td><%= cliente.getDataNascimento() %></td>
    </tr>
    <tr>
        <td>Telemóvel:</td>
        <td><%= cliente.getTelemovel() %></td>
    </tr>
    <tr>
        <td>Objetivo:</td>
        <td><%= cliente.getObjetivos() %></td>
    </tr>
    <!-- Additional fields for the client information can be added here -->

    <!-- Patologias subtable -->
    <tr>
        <td colspan="2">
            <h2>Patologias</h2>
            <table id="patologiasTable" border="1">
                <tr>
                    <th>Nome</th>
                    <th>Data de Início</th>
                    <th>Data de Fim</th>
                </tr>
                <% 
                    // Assuming getPatologias() returns a List<Patologia> for each client
                    List<Patologia> patologias = cliente.getPatologias();
                    for (Patologia patologia : patologias) {
                %>
                <tr class="data-row">
                    <td><%= patologia.getNome() %></td>
                    <td><%= patologia.getDataInicio() %></td>
                    <td><%= patologia.getDataFim() %></td>
                </tr>
                <% } %>
                <tr>
                </tr>
            </table>
        </td>
    </tr>
</table>




<script>
$(document).ready(function () {
    // Fetch and populate the club data into the dropdown
    fetchClubData();

    // Attach a change event handler to the dropdown
    $("#clubeDropdown").change(function () {
        var selectedClube = $(this).val();

        // Exclude the default option
        if (selectedClube !== "") {
            // Optionally, perform actions based on the selected club
            $.ajax({
                type: "POST",
                url: "SetClubeSessionAttributeServlet", 
                data: { selectedClube: selectedClube },
                success: function () {
                    // Reload the page after setting the session attribute
                    location.reload();
                },
                error: function () {
                    alert("Error setting session attribute");
                }
            });
        }
    });

    // Function to fetch club data from the server and populate the dropdown
    function fetchClubData() {
        $.ajax({
            type: "GET",
            url: "GetClubesPTServlet", // Adjust the URL accordingly
            success: function (data) {
                var dropdown = $("#clubeDropdown");
                dropdown.empty(); // Clear existing options
                dropdown.append('<option value="">Selecionar Clube</option>')
                dropdown.append(data); // Add default option
                dropdown.val("").change();
            },
            error: function () {
                alert("Error fetching club data");
            }
        });
    }
});


</script>

</body>
</html>
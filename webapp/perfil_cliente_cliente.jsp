<%@ page language="java" contentType="text/html; UTF-8" pageEncoding="UTF-8"%>
<%@ page import="connector.Utilizador" %>
<%@ page import="connector.Patologia" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
        <link rel="stylesheet" type="text/css" href="style.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
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
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>

    <title>FitnessUP</title>
</head>
<body>

<%
session = request.getSession();
Utilizador cliente_info = (Utilizador) session.getAttribute("info_cliente");
%>

<div class="content">


	<div class="sidebar">
			<a><img src="https://cdn.discordapp.com/attachments/697907812524884021/1185417478512324639/Untitled-removebg-preview.png?ex=658f890e&is=657d140e&hm=44a7f107dc445a250f059e98ed6935ddf42658c6993e7b29ad047e93dcface26&" alt="Logo" class="logo"></a>
			<a href="dashboard_cliente.jsp" class="icon"><i class="fa-solid fa-house"></i>  Página Inicial</a>
	        <a href="perfil_cliente_cliente.jsp" class="icon"><i class="fas fa-address-book"></i>  Perfil</a>
	        <a href="atividades_cliente.jsp" class="icon"><i class="fa-regular fa-calendar-days"></i>  Atividades</a>
	        <a href="atividades_semanais_cliente.jsp" class="icon"><i class="fa-solid fa-door-closed"></i>  Calendário Semanal</a>
	        <a href="" class="icon"><i class="fa-solid fa-dumbbell"></i>  Recomendações</a>
	        <a href="LogoutServlet" class="icon"><i class="fa-solid fa-right-from-bracket"></i>  Terminar Sessão</a>
	</div>

</div>

<h1>Perfil do cliente <%= cliente_info.getNome() %></h1>

<form method="post" onsubmit="return updateClient()" id="updateClientForm">
    <table border="1">
    	<tr>
            <td>NIF:</td>
            <td><input type="text" name="nif" value="<%= cliente_info.getNif() %>" readonly></td>
        </tr>
        <tr>
            <td>Email:</td>
            <td><input type="text" name="email" value="<%= cliente_info.getEmail() %>"></td>
        </tr>
        <tr>
            <td>Data de Nascimento:</td>
            <td><input type="text" name="dataNascimento" value="<%= cliente_info.getDataNascimento() %>"></td>
        </tr>
        <tr>
            <td>Telemóvel:</td>
            <td><input type="text" name="telemovel" value="<%= cliente_info.getTelemovel() %>"></td>
        </tr>
        <tr>
        	<td>Objetivo:</td>
            <td><input type="text" name="objetivo" value="<%= cliente_info.getObjetivos() %>"></td>
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
                        List<Patologia> patologias = cliente_info.getPatologias();
                        List<List<String>> aux = new ArrayList<>();
                        for (Patologia patologia : patologias) {
                        	aux.add(Arrays.asList(patologia.getNome(), patologia.getDataInicio(), patologia.getDataFim()));
                    %>
                    <tr class="data-row">
                        <td><%= patologia.getNome() %></td>
                        <td><%= patologia.getDataInicio() %></td>
                    	<td>
                    		<input type="hidden" name="nome" value="<%= patologia.getNome() %>">
                    		<input type="hidden" name="dataInicio" value="<%= patologia.getDataInicio() %>">
                            <input type="text" name="dataFim" value="<%= patologia.getDataFim() %>">
                        </td>
                    </tr>
                    <% } %>
                    <tr>
					</tr>
                </table>
                <button type="button" onclick="addPatologiaRow()">Add Patologia</button>
            </td>
        </tr>

        <tr>
            <td colspan="2"><input type="submit" value="Save Changes"></td>
        </tr>
    </table>
</form>

<script>

function fetchPatologias(row) {
    $.ajax({
        type: "GET",
        url: "patologias",
        success: function (data) {
            console.log("Fetched data:", data); // Log the fetched data
            populateDropdown(row, data); // Call a function to populate the dropdown
        },
        error: function (error) {
            console.error("Error fetching patologias:", error); // Log the error
            alert("Error fetching patologias");
        }
    });
}

function populateDropdown(row, options) {
	var dropdown = $(row).find('.patologiasDropdown');
    dropdown.empty(); // Clear existing options

    // Add a default option if needed
    // dropdown.append('<option value="" selected>Select Patologia</option>');

    // Add options from the fetched data
    dropdown.html(options);
}

function addPatologiaRow() {
    var table = document.getElementById("patologiasTable");
    var newRow = table.insertRow(-1);

    var cell1 = newRow.insertCell(0);
    var cell2 = newRow.insertCell(1);
    var cell3 = newRow.insertCell(2);

    // Use the populated dropdown HTML in cell1
    cell1.innerHTML = '<select class="patologiasDropdown" name="nome">' +
                        '</select>';

    cell2.innerHTML = '<input type="text" name="dataInicio">';
    cell3.innerHTML = '<input type="text" name="dataFim">';

    // Fetch and populate the dropdown options
    fetchPatologias(newRow);
}
function updateClient() {
	// Gather form data, including existing and new rows
    var formData = $("#updateClientForm").serializeArray();

    console.log(formData);
    // Send AJAX request to your server
    $.ajax({
        type: "POST",
        url: "updateCliente", // Replace with your servlet URL
        data: formData,
        success: function (response) {
            // Handle success response
            window.location.href = 'dashboard_cliente.jsp';
            alert("Cliente atualizado!")
            console.log(response);
        },
        error: function () {
            // Handle error
            console.log("Error updating client");
        }
    });
    
    return false;
}

</script>

</body>
</html>

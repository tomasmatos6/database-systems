<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<%@ page import="java.util.List" %>
<%@ page import="java.sql.ResultSet" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Personal Trainer Page</title>
    <style>
        body {
            display: flex;
            align-items: flex-start; /* Align items to the top */
            justify-content: space-between;
            margin: 20px;
        }

        #profileInfo {
            max-width: 65%; /* Adjust the max-width accordingly */
        }

        #profilePhoto {
            width: 30%;
            position: sticky; /* Make the div sticky */
            top: 0; /* Stick to the top */
        }

        img {
            width: 100%;
            height: auto;
            max-width: 400px;
            max-height: 400px;
        }

        h1 {
            margin-bottom: 10px;
        }
    </style>
    <script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
</head>
<body>

<%
    // Get the email parameter from the request
    String email = request.getParameter("email");
    String nifPar = request.getParameter("nif");
    int nif = Integer.parseInt(nifPar);
    JavaConnector jc = new JavaConnector();
    Utilizador pt_info = jc.carregarPT(email);
%>
<div id="profileInfo">
        <h1>Personal Trainer <%= pt_info.getNome() %><input type="hidden" id="nif" value="<%= nif%>"></h1>

        <h3>Telemovel: <input type="text" id="telemovel" class="editable" value="<%= pt_info.getTelemovel() %>" disabled></h3>
        <h3>Email: <input type="text" id="email" class="editable" value="<%= pt_info.getEmail() %>" disabled></h3>

<h3>Clubes em que trabalha: </h3>
 <% 
        List<String> clubes = jc.getNifClubesForNifPt(nifPar);
    %>
    <div id="selectedClubes">
        <%
            for (String clube : clubes) {
                out.println("<p>" + jc.NifToNome(clube) + "</p>");
            } 
    %>
    </div>
        <select id="clubeDropdown" class="editable" multiple disabled style='display:none'>   
        </select>


 		<button id="editBtn">Edit</button>
        <button id="saveBtn" style="display: none;">Save</button>

<h2>Atividades</h2>
<table border="1">
    <thead>
        <tr>
            <th>Nome do Clube</th>
            <th>Nome da Atividade</th>
            <th>Dia Da Atividade</th>
            <th>Hora de Inicio</th>
            <th>Hora de Fim</th>
            <th>Tipo De Atividade</th>
            <th>Inscricoes</th>
            <th>Vagas</th>
            <th>Lotacao da Sala</th>
            <th>Estado</th>
        </tr>
    </thead>
    <tbody>
        <%
            ResultSet resultSet = jc.getAtividadesByNomePT(pt_info.getNome());
            while (resultSet.next()) { 
        %>
            <tr>
                <td><%= resultSet.getString("NomeClube") %></td>
                <td><%= resultSet.getString("NomeAtividade") %></td>
                <td><%= resultSet.getString("DiaDaAtividade") %></td>
                <td><%= resultSet.getString("HoraInicio") %></td>
                <td><%= resultSet.getString("HoraFim") %></td>
                <td><%= resultSet.getString("TipoDeAtividade") %></td>
                <td><%= resultSet.getString("Inscricoes") %></td>
                <td><%= resultSet.getString("Vagas") %></td>
                <td><%= resultSet.getString("LotacaoSala") %></td>
                <td><%= resultSet.getString("Estado") %></td>
            </tr>
        <% } %>
    </tbody>
</table>

<h2>Clientes</h2>
<table border="1">
    <thead>
        <tr>
            <th>Nome</th>
            <th>Data de nascimento</th>
            <th>Email</th>
            <th>Telemovel</th>
            <th>Escalão Etário</th>
            <th>Objetivo</th>
        </tr>
    </thead>
    <tbody>
        <%
            // Use a different ResultSet for clients
            ResultSet resultSet_cli = jc.getClientesDePT(pt_info.getNome());
            while (resultSet_cli.next()) { 
        %>
            <tr>
                <td><%= resultSet_cli.getString("nome") %></td>
                <td><%= resultSet_cli.getString("dataNascimento") %></td>
                <td><%= resultSet_cli.getString("email") %></td>
                <td><%= resultSet_cli.getString("telemovel") %></td>
                <td><%= resultSet_cli.getString("escalaodescricao") %></td>
                <td><%= resultSet_cli.getString("objetivos") %></td>
            </tr>
        <% } %>
    </tbody>
</table>
</div>

<div id="profilePhoto">
	<img src="<%= jc.getProfileFoto(nif) %>" alt="Profile Photo" class="resizable-image">
</div>
<script>
	var selectedClubes = [];

$(document).ready(function () {
	var clubes = $("#selectedClubes p");
	clubes.each(function () {
        // Add the text content of each <p> to the array
        selectedClubes.push($(this).text());
    });
	
	
    $("#editBtn").click(function () {
    	fetchClubData();
    	$("#clubeDropdown").toggle();
        $(".editable").prop("disabled", false);
        $("#clubeDropdown").prop("disabled", false); // Enable the club dropdown
        $("#saveBtn").show();
        $(this).hide();
    });

    $("#saveBtn").click(function () {
    	if(selectedClubes.length === 0) {
    		alert("Select at least 1 club");
    	}
    	else {
    		var telemovel = $("#telemovel").val();
            var email = $("#email").val();
            console.log(selectedClubes)
            var nif = document.getElementById("nif").value;
            $("#clubeDropdown").toggle();
            $.ajax({
                type: "POST",
                url: "updatePT",
                data: {
                    telemovel: telemovel,
                    email: email,
                    selectedClubes: selectedClubes,
                    nif: nif
                },
                traditional: true, 
                success: function () {
                    $("#saveBtn").hide();
                    $("#editBtn").show();
                    $(".editable").prop("disabled", true);
                    $("#clubeDropdown").prop("disabled", true); // Disable the club dropdown after saving
                },
                error: function () {
                    alert("Error updating profile. Please try again.");
                }
            });
    	}
    });
});

//Function to handle click on options
function handleClick(optionValue) {
 	// Check if the optionValue is already in the array
	 var index = selectedClubes.indexOf(optionValue);
	
	 if (index === -1) {
	     // If not in the array, add the optionValue
	     selectedClubes.push(optionValue);
	 } else {
	     // If already in the array, remove the optionValue
	     $('.dateField').hide();
	     selectedClubes.splice(index, 1);
	 }
	
	 // Optionally, update your UI or perform other actions here
	 updateSelectedClubesDisplay();
}

function updateSelectedClubesDisplay() {
    var displayDiv = $("#selectedClubes");
    displayDiv.text(selectedClubes.join(", "));
}

//Function to fetch club data from the server and populate the dropdown
function fetchClubData() {
    $.ajax({
        type: "GET",
        url: "ClubeServlet", // Adjust the URL accordingly
        success: function (data) {
            var dropdown = $("#clubeDropdown");
            dropdown.empty(); // Clear existing options
            dropdown.append('<option value="">Selecionar Clube</option>')
            dropdown.append(data); 
            dropdown.val("").change();
            
         	// Attach a click event handler
            dropdown.on('click', 'option', function () {
                handleClick($(this).val());
            });
        },
        error: function () {
            alert("Error fetching club data");
        }
    });
}
    </script>
</body>
</html>
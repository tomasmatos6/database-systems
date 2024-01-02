<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<%@ page import="java.sql.ResultSet" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
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
			color:#818181;
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
    </style>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>

<title>FitnessUP</title>
</head>
<body>

<%
int permissoes = (int) session.getAttribute("permissoes");
String email = (String) session.getAttribute("email");


int numClientes = 0;
int numPTs = 0;
JavaConnector jc = new JavaConnector();
String nifClubeStr = jc.carregarGestor(email);
int nifClube = Integer.parseInt(nifClubeStr);
session.setAttribute("nifClube", nifClube);

if (session.getAttribute("nifClube") != null) {
	nifClube = (int) session.getAttribute("nifClube");
	numClientes = jc.numeroClientes(nifClube);
	numPTs = jc.numeroPTS(nifClube);
}

Utilizador cliente = jc.carregarPerfil(email);

%>



<h1>Gestor</h1>
<h2><%= "Nif do clube atual: " + nifClube%></h2>
	<div class="sidebar">
			<a><img src="https://cdn.discordapp.com/attachments/697907812524884021/1185417478512324639/Untitled-removebg-preview.png?ex=658f890e&is=657d140e&hm=44a7f107dc445a250f059e98ed6935ddf42658c6993e7b29ad047e93dcface26&" alt="Logo" class="logo"></a>
			<a class="icon" style="font-size: 16px;"><i class="fa-solid fa-location-dot"></i> <%= jc.getNomeClubeFromNif(nifClube) %></a> 
			<a href="dashboard_gestor.jsp" class="icon"><i class="fa-solid fa-house"></i>  Página Inicial</a>
	        <a href="manage_contacto.jsp" class="icon"><i class="fas fa-address-book"></i>  Contactos</a>
	        <a href="manage_clube.jsp" class="icon"><i class="fa-regular fa-calendar-days"></i>  Horários</a>
	        <a href="manage_salas.jsp" class="icon"><i class="fa-solid fa-door-closed"></i>  Salas</a>
	        <a href="manage_equipamentos.jsp" class="icon"><i class="fa-solid fa-dumbbell"></i>  Equipamentos</a>
	        <a href="manage_clientes.jsp" class="icon"><i class="fa-solid fa-users"></i>  Clientes</a>
	        <a href="manage_pts.jsp" class="icon"><i class="fa-solid fa-person"></i>  Personal Trainers</a>
	        <a href="LogoutServlet" class="icon"><i class="fa-solid fa-right-from-bracket"></i>  Terminar Sessão</a>
	</div>

</div>

<div id="telefoneSection">
	<a>Telefone:</a>
    <span id="telefoneValue"><%= jc.getTelefone(nifClube) %></span>
    <button id="telefoneButton" onclick="toggleEditMode('telefoneValue', 'telefone')">Editar</button>
</div>

<div id="emailSection">
    <a>Email:</a>
    <span id="emailValue"><%= jc.getEmail(nifClube) %></span>
    <button id="emailButton" onclick="toggleEditMode('emailValue', 'email')">Editar</button>
</div>




<script>

function toggleEditMode(valueElementId, type) {
    var valueElement = $("#" + valueElementId);
    var editButton = $("#" + valueElementId.replace("Value", "Button"));

    if (valueElement.attr("contentEditable") === "true") {
       
        var newValue = valueElement.text();
        var nifClube = <%= nifClube %>; 
        console.log("NEW VALUE: ", newValue)
        console.log("TYPE: ", type)
        updateValue(valueElementId, newValue, nifClube, type);
        valueElement.attr("contentEditable", "false");
        editButton.text("Editar");
    } else {
        // If content is non-editable, make it editable
        valueElement.attr("contentEditable", "true");
        valueElement.focus();
        editButton.text("Salvar");
    }
}

function updateValue(elementId, newValue, nifClube, type) {
    $.ajax({
        type: "POST",
        url: "UpdateValueServlet", 
        data: { elementId: elementId, newValue: newValue, nifClube: nifClube, type: type },
        success: function () {
            // Refresh the page after successful update
            location.reload();
        },
        error: function () {
            alert("Error updating value");
        }
    });
}


$(document).ready(function () {
    // Fetch and populate the club data into the dropdown
    fetchClubData();

    // Attach a change event handler to the dropdown
    $("#clubeDropdown").change(function () {
        var selectedClube = $(this).val();

        console.log("Selected Clube: ", selectedClube);
        // Exclude the default option
        if (selectedClube !== "") {
            // Optionally, perform actions based on the selected club
            console.log("Selected Clube: ", selectedClube);
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
            url: "ClubeServlet", // Adjust the URL accordingly
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
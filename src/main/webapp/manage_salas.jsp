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

	<div class="table-container">
	<table class="clientes" border="1">
	<caption>Mapa Semanal de ocupação de Salas</caption>
        <tr>
            <th>Lotação da Sala</th>
            <th>Data</th>
            <th>Horas</th>
            <th>Nome da Atividade</th>
            <!-- Add more headers based on your table structure -->
        </tr>

        <%  
            ResultSet resultSet = jc.getSalasSemanal(nifClube);
            while (resultSet.next()) {
        %>
                <tr>
                    <td><%= resultSet.getString("lotacao") %></td>
                    <td><%= resultSet.getString("data") %></td>
                    <td><%= resultSet.getString("horas") %></td>
                    <td><%= resultSet.getString("nome") %></td>
                </tr>
        <%
            }
            // Close the ResultSet properly
            resultSet.close();
            
        %>

    </table>

</div>

<div class="table-container">
	<table class="salas" border="1">
	<caption>Salas do Clube</caption>
        <tr>
            <th>Número da Sala</th>
            <th>Lotação</th>
            <th>idSala</th>
            <th>Apagar</th>
        </tr>

        <%  
        	int ctr = 1;
            ResultSet resultSet_salas = jc.getSalas(nifClube);
            while (resultSet_salas.next()) {
        %>
                <tr>
                    <td><%= ctr %></td>
                    <td><%= resultSet_salas.getString("lotacao") %></td>
                     <td><%= resultSet_salas.getString("idSala") %></td>
                     <td><button onclick="deleteSala('<%= resultSet_salas.getString("idSala") %>')">Apagar</button></td>
                </tr>
        <%
        ++ctr;
            }
            // Close the ResultSet properly
            resultSet_salas.close();
            
        %>
    </table>
	<div class="center-button">
	 	<button id="addRowBtn" class="addRowBtn" onclick="addNewRow()">Criar Sala</button>
	</div>
</div>
	

<script>

function deleteSala(idSala) {
    // Make an AJAX request to DeleteSalaServlet with the idSala
    var servletURL = 'DeleteSalaServlet';
    var xhr = new XMLHttpRequest();
    xhr.open('POST', servletURL, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            if (xhr.status === 200) {
                console.log('Servlet response:', xhr.responseText);
                // Refresh the page after the request is completed
                location.reload();
            } else {
                // Handle the case where the server returns an error
                alert('Error: ' + xhr.statusText);
            }
        }
    };
    xhr.send('idSala=' + encodeURIComponent(idSala));
}

let ctr = <%= ctr %>; // Initialize the counter with the JSP value

function addNewRow() {
    var table = document.querySelector('.salas');

    // Create a new row
    var newRow = table.insertRow(-1);

    // Define the cells for the new row
    var cellNumSala = newRow.insertCell(0);
    var cellLotacao = newRow.insertCell(1);
    var cellidSala = newRow.insertCell(2);

    // Set the content and attributes for each cell
    cellNumSala.innerText = ctr++; // Increment and use the value
    cellLotacao.innerText = "";
    cellidSala.innerText = "";

    // Make cells editable
    cellLotacao.contentEditable = true;
    
 	// Add event listener to cells for Enter key
    cellLotacao.addEventListener('keydown', function (event) {
        if (event.key === 'Enter') {
            handleEnterKey(event, cellLotacao.innerText);
            console.log(cellLotacao.innerText);
        }
    });
}
function handleEnterKey(event, value) {
    // Check if the Enter key is pressed for the first time
    if (!event.repeat) {
        // Prevent the default behavior of the Enter key (newline character)
        event.preventDefault();

        // Validate using regex for numbers
        if (/^\d+$/.test(value)) {
            var servletURL = 'CreateSalaServlet';
            var xhr = new XMLHttpRequest();
            xhr.open('POST', servletURL, true);
            xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4 && xhr.status === 200) {
                    console.log('Servlet response:', xhr.responseText);
                    location.reload();
                }
            };
            xhr.send('lotacao=' + encodeURIComponent(value));
        } else {
            alert('Please enter a valid number.');
        }
    }
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
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map.Entry" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Set" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
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
        #chartContainer {
        float: left;
        
        margin-left: 300px; /* Adjust the margin as needed */
        margin-top: 20px; /* Adjust the margin as needed */
        background-color: white; /* Set the background color to white */
        padding: 20px; /* Add padding for better appearance */
        box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1); /* Add box shadow effect */
	    }
	
	    .content {
	        display: flex;
	        flex-direction: row; /* Change to row to align horizontally */
	        align-items: flex-start;
	        justify-content: flex-start; /* Adjust to your layout needs */
	    }
    </style>
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

<div class="content">
<% 

if (session.getAttribute("nifClube") != null) {
	nifClube = (int) session.getAttribute("nifClube");
    numClientes = jc.numeroClientes(nifClube);
    numPTs = jc.numeroPTS(nifClube);
%>
    <h1>Numero clientes: <%= numClientes %></h1>
    <h1>Numero PTS: <%= numPTs %></h1>

<%
    String nomeClube = jc.getNomeClubeFromNif(nifClube);
    Map<String, Integer> counts = jc.countEscalaoByClube(nomeClube);


    StringBuilder labels = new StringBuilder("[");
    StringBuilder data = new StringBuilder("[");
    Set<Entry<String, Integer>> entrySet = counts.entrySet();
    Iterator<Entry<String, Integer>> iterator = entrySet.iterator();
    while (iterator.hasNext()) {
        Entry<String, Integer> entry = iterator.next();
        labels.append("'").append(entry.getKey()).append("'");
        data.append(entry.getValue());
        if (iterator.hasNext()) {
            labels.append(",");
            data.append(",");
        }
    }
    labels.append("]");
    data.append("]");

%>
</div>
    <h2>Escalões Etários dos Clientes</h2>
    
    <div id="chartContainer">
        <canvas id="myPieChart" width="400" height="400"></canvas>
    </div>

    <!-- JavaScript code to draw the pie chart using Chart.js -->
    <script>
        var ctx = document.getElementById('myPieChart').getContext('2d');
        var myPieChart = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: <%= labels %>,
                datasets: [{
                    data: <%= data %>,
                    backgroundColor: [
                        'rgba(255, 87, 34, 0.8)',    // Vivid Orange
                        'rgba(255, 193, 7, 0.8)',    // Vibrant Yellow
                        'rgba(76, 175, 80, 0.8)',    // Lush Green
                        'rgba(33, 150, 243, 0.8)',   // Sky Blue
                        'rgba(156, 39, 176, 0.8)',   // Rich Purple
                        'rgba(255, 64, 129, 0.8)'    // Hot Pink
                    ],
                    borderColor: [
                        'rgba(255, 87, 34, 1)',
                        'rgba(255, 193, 7, 1)',
                        'rgba(76, 175, 80, 1)',
                        'rgba(33, 150, 243, 1)',
                        'rgba(156, 39, 176, 1)',
                        'rgba(255, 64, 129, 1)'
                    ],
                    borderWidth: 1
                }]
            },
            options: {
                legend: {
                    display: true,
                    position: 'bottom'
                },
                responsive: false, // Set responsive to false to use fixed width and height
                maintainAspectRatio: false,
            }
        });
    </script>

<%
}
jc.DBdisconnect();
%>

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
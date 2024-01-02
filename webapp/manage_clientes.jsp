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
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<link rel="stylesheet" type="text/css" href="style.css">
<style>
body {
  font-family: 'lato', sans-serif;
}
.container {
  max-width: 1500px;
  margin-left: 275px;
  margin-right: auto;
  padding-left: 10px;
  padding-right: 10px;
}

h2 {
  font-size: 26px;
  margin: 20px 0;
  text-align: center;
  small {
    font-size: 0.5em;
  }
}

.responsive-table {
  li {
    border-radius: 3px;
    padding: 25px 30px;
    display: flex;
    justify-content: space-between;
    margin-bottom: 25px;
  }
  .table-header {
    background-color: #95A5A6;
    font-size: 14px;
    text-transform: uppercase;
    letter-spacing: 0.03em;
    padding: 25px 30px;
    color:white;
  }
  .table-row {
    background-color: #ffffff;
    box-shadow: 0px 0px 9px 0px rgba(0,0,0,0.1);
  }
  .col-1 {
    flex-basis: 12%;
  }
  .col-2 {
    flex-basis: 12%;
  }
  .col-3 {
    flex-basis: 12%;
  }
  .col-4 {
    flex-basis: 22%;
  }
  .col-5 {
    flex-basis: 12%;
  }
  .col-6 {
    flex-basis: 8%;
  }
  .col-7 {
    flex-basis: 15%;
  }
  .col-8 {
    flex-basis: 18%;
  }
  .col-9 {
    flex-basis: 7%;
  }
  .col-10 {
    flex-basis: 8%;
  }
  .col-11 {
    flex-basis: 6%;
  }
  
  @media all and (max-width: 767px) {
    .table-header {
      display: none;
    }
    .table-row{
      
    }
    li {
      display: block;
    }
    .col {
      flex-basis: 100%;
      
    }
    .col {
      display: flex;
      }
    }
  }

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

<!--  -->
<div class="container">
<ul class="responsive-table">
<li class="table-header">
      <div class="col col-1">NIF</div>
      <div class="col col-2">Nome</div>
      <div class="col col-3">Data de Nascimento</div>
      <div class="col col-4">Email</div>
      <div class="col col-5">Telemóvel</div>
      <div class="col col-6">Escalão Etário</div>
      <div class="col col-7">Objetivos</div>
      <div class="col col-8">Personal Trainer</div>
      <div class="col col-9">Editar</div>
      <div class="col col-10">Exportar</div>
      <div class="col col-11">Apagar</div>
</li>
    <%  
            ResultSet resultSet = jc.getClientes(nifClube);
            while (resultSet.next()) {
    %>
		    <li class="table-row">
		      <div class="col col-1" data-label="NIF"><%= resultSet.getString("NIF") %></div>
		      <div class="col col-2" data-label="nome"><%= resultSet.getString("nome") %></div>
		      <div class="col col-3" data-label="dataNascimento"><%= resultSet.getString("dataNascimento") %></div>
		      <div class="col col-4" data-label="email"><%= resultSet.getString("email") %></div>
		      <div class="col col-5" data-label="telemovel"><%= resultSet.getString("telemovel") %></div>
		      <div class="col col-6" data-label="EscalaoDescricao"><%= resultSet.getString("EscalaoDescricao") %></div>
		      <div class="col col-7" data-label="objetivos"><%= resultSet.getString("objetivos") %></div>
		      <div class="col col-8" data-label="NomePT"><%= resultSet.getString("NomePT") %></div>
		      <div class="col col-9" data-label="Editar"><button onclick="callProcessClientsServlet('<%= resultSet.getString("email") %>', 'edit')">Editar</button></div>
		      <div class="col col-10" data-label="Exportar"><button onclick="callProcessClientsServlet('<%= resultSet.getString("email") %>', 'export')">Exportar</button></div>
		      <div class="col col-11" data-label="Apagar"><button onclick="callProcessClientsServlet('<%= resultSet.getString("NIF") %>', 'delete')">Apagar</button></div>
		    </li>
    
    <%
            }
            // Close the ResultSet properly
            resultSet.close();
            
   %>
</ul>
</div>
<!--  -->


    
    <script>
    function callProcessClientsServlet(data, action) {
       
        $.ajax({
            type: 'POST',
            url: 'processClients',
            data: { data: data, action: action }, //data pode ser email ou nif
            success: function(response) {
            	
            	switch (action) {
                case 'edit':
                	window.location.href = 'perfil_cliente.jsp';
                    break;
                case 'export':
                	console.log("Resposta: ", response);
                	var xmlDoc = response; // Assuming response is the XML document object

                    // Convert the XML document to a string
                    var xmlString = new XMLSerializer().serializeToString(xmlDoc);

                    // Initiate download
                    var blob = new Blob([xmlString], { type: 'application/xml' });
                    var link = document.createElement('a');
                    link.href = window.URL.createObjectURL(blob);
                    link.download = 'cliente.xml';
                    document.body.appendChild(link);
                    link.click();
                    document.body.removeChild(link);
                    break;
                case 'delete':
                	window.location.href = 'manage_clientes.jsp';
                    break;
                    
                default:
                	window.location.href = 'manage_clientes.jsp';
                    break;
            }
            	
                
            },
            error: function() {
                alert('Error calling processClients servlet');
            }
        });
    }
    
    function redirectToAddCliente() {
        window.location.assign('add_cliente.jsp');
    }
    
    function loadXmlAndCreateCliente() {
        var fileInput = document.getElementById("xmlFileInput");

        // Check if a file is selected
        if (fileInput.files.length > 0) {
            var file = fileInput.files[0];

            // Read the content of the selected file as text
            var reader = new FileReader();
            reader.onload = function (event) {
                var xmlData = event.target.result;

                // Make an AJAX request to the server
                $.ajax({
                    type: 'POST',
                    url: 'importClientXML',  // Replace with the actual endpoint URL
                    data: { xmlData: xmlData },
                    success: function(response) {
                        // Log the response (created Cliente object) to the console
                        //console.log("Data: " , xmlData);
                        window.location.href = 'manage_clientes.jsp';
                        // You can now use the "response" object as needed
                    },
                    error: function() {
                        alert('Error processing the XML on the server.');
                    }
                });
            };

            // Read the file as text
            reader.readAsText(file);
            
        } else {
            alert("Please select an XML file.");
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
    
    <div id="addRowContainer" class="addRowContainer">
	    <button id="addRowBtn" class="addRowBtn" onclick="redirectToAddCliente()">Adicionar cliente</button>
	    
	    <label for="xmlFileInput" class="custom-file-upload">
	        Importar cliente
	    </label>
	    <input type="file" id="xmlFileInput" accept=".xml" style="display:none;" onchange="loadXmlAndCreateCliente()">
	</div>
	
	<button id="processXmlBtn" class="addRowBtn" onclick="loadXmlAndCreateCliente()">Processar XML</button>
</div>
	

    
</body>
</html>
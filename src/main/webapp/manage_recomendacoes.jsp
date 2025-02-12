<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.text.SimpleDateFormat" %>
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
}

h2 small {
  font-size: 0.5em;
}

.responsive-table li {
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
  color: white;
}

.table-row {
  background-color: #ffffff;
  box-shadow: 0px 0px 9px 0px rgba(0, 0, 0, 0.1);
}

.col-1 {
  flex-basis: 12%;
}

.col-2 {
  flex-basis: 15%;
}

.col-3 {
  flex-basis: 15%;
}

.col-4 {
  flex-basis: 15%;
}

.col-5 {
  flex-basis: 30%;
}

.col-6 {
  flex-basis: 5%;
}

@media all and (max-width: 767px) {
  .table-header {
    display: none;
  }

  .table-row {
    /* Add your responsive styles for table rows in smaller screens here */
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
  
  .header {
  	cursor: pointer;
  }
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
Utilizador cliente = jc.carregarPersonalTrainer(email);
session.setAttribute("nifPT", Integer.valueOf(cliente.getNif()));

ResultSet resultSet_eq = jc.getEquipamentos(nifClube);
HashMap<Integer, String> equipamentos = new HashMap<>();
while(resultSet_eq.next()) {
	if(resultSet_eq.getString("estado").equals("disponível"))
		equipamentos.put(resultSet_eq.getInt("id"), resultSet_eq.getString("nome"));
}

%>

<div class="content">
<h1>Personal Trainer</h1>
<h2><%= "Nif do clube atual: " + session.getAttribute("nifClube")%></h2>
	<div class="sidebar" style="background-color: #0070dd;">
			<a><img src="https://cdn.discordapp.com/attachments/697907812524884021/1185417478512324639/Untitled-removebg-preview.png?ex=658f890e&is=657d140e&hm=44a7f107dc445a250f059e98ed6935ddf42658c6993e7b29ad047e93dcface26&" alt="Logo" class="logo"></a>
			<a class="icon"><i class="fa-solid fa-location-dot"></i><select id="clubeDropdown" style="background-color: #0070dd;">
            <option value="">Select Clube</option>
        	</select></a> 
			<a href="dashboard_pt.jsp" class="icon"><i class="fa-solid fa-house"></i>  Página Inicial</a>
	        <a href="atividades_pt.jsp" class="icon"><i class="fas fa-address-book"></i>  Atividades</a>
	        <a href="autosearch_clientes.jsp" class="icon"><i class="fa-regular fa-calendar-days"></i>  Clientes</a>
	        <a href="manage_recomendacoes.jsp" class="icon"><i class="fa-solid fa-door-closed"></i>  Recomendações</a>
	        <a href="LogoutServlet" class="icon"><i class="fa-solid fa-right-from-bracket"></i>  Terminar Sessão</a>
	</div>
</div>
<h2>Recomendações</h2>
<div class="container">
<ul class="responsive-table">
<li class="table-header">
      <div class="col col-1">Cliente</div>
      <div class="col col-2">Equipamento</div>
      <div class="col col-3">Data de Inicio</div>
      <div class="col col-4">Data de Fim</div>
      <div class="col col-5">Descricao</div>
      <div class="header col col-6">Editar</div>
</li>
    <%  
    ResultSet resultSet = jc.getRecomendacaoPT(String.valueOf(nifClube), cliente.getNif());
    while (resultSet.next()) {
    %>
		    <li class="table-row">
		      <div class="col col-1 nomeCliente" data-label="nomeCliente"><%= resultSet.getString("NomeCliente") %></div>
		      <div class="col col-2 nomeEquipamento" data-label="nomeEquipamento"><%= resultSet.getString("NomeEquipamento") %></div>
		      <div class="col col-3 dataInicio" data-label="dataInicio"><%= resultSet.getString("DataDeInicio") %></div>
		      <div class="col col-4 dataFim" data-label="dataFim" contenteditable="false">
				<%= resultSet.getString("DataDeFim") %>
			  </div>
			  <div class="col col-5 descricao" data-label="Descricao" contenteditable="false">
				<%= resultSet.getString("Descricao") %>
			  </div>
			  <div class="col col-6 editBtn" data-label="editar">
				<button onclick="callUpdateRecomendacao(this)">Editar</button>
			  </div>
			  <div><input class="idEquipamento" value="<%= resultSet.getString("idEquipamento") %>" style="display:none;"/></div>
		    </li>
    		
    <%
            }
            // Close the ResultSet properly
            resultSet.close();
            
   %>
</ul>
</div>
<div id="addRowContainer" class="addRowContainer">
	    <button id="addRowBtn" class="addRowBtn" onclick="redirectToAddRecomendacao()">Adicionar Recomendação</button>
</div>
<script>
function callUpdateRecomendacao(button) {
	var row = $(button).closest("li");

    var dataFimElement = row.find(".dataFim");
    var descricaoElement = row.find(".descricao");
    var editBtn = row.find(".editBtn");

    // Check if currently in edit mode
    if (editBtn.text().trim().toLowerCase() === "editar") {
        // Enter edit mode
        dataFimElement.attr("contenteditable", true);
        descricaoElement.attr("contenteditable", true);
        editBtn.html('<button onclick="callUpdateRecomendacao(this)">Guardar</button>');
    } else {
        // Save changes
        var nomeCliente = row.find(".nomeCliente").text();
        var dataFim = row.find(".dataFim").text().trim();
        var dataInicio = row.find(".dataInicio").text().trim();
        var descricao = row.find(".descricao").text().trim();
		var idEquipamento = row.find(".idEquipamento").val();
        
		var dataInicioAux = new Date(row.find(".dataInicio").text().trim());
		var dataFimAux = new Date(row.find(".dataFim").text().trim());
		
		if (dataInicio > dataFim) {
		    alert("A data de inicio não pode ser inferior à data de fim");
		    location.reload();
		} else {
			$.ajax({
	        	type: "POST",
	            url: "updateRecomendacao",
	            data: {
	            	nomeCliente: nomeCliente,
	                idEquipamento: idEquipamento,
	                dataFim: dataFim,
	                descricao: descricao
	            },
	            success: function () {
	                // Exit edit mode
	                dataFimElement.attr("contenteditable", false);
	                descricaoElement.attr("contenteditable", false);
	                editBtn.html('<button onclick="callUpdateRecomendacao(this)">Editar</button>');
	                location.reload();
	            },
	            error: function (xhr, status, error) {
	                console.error("Error updating recomendação. Status:", status, "Error:", error);
	            }
	        });
		}
    }
}

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

function redirectToAddRecomendacao() {
    window.location.assign('add_recomendacao.jsp');
}
</script>
</body>
</html>
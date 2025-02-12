<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<%@ page import="java.sql.ResultSet" %>
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
  flex-basis: 5%;
}

.col-2 {
  flex-basis: 7.5%;
}

.col-3 {
  flex-basis: 7.5%;
}

.col-4 {
  flex-basis: 7.5%;
}

.col-5 {
  flex-basis: 7.5%;
}

.col-6 {
  flex-basis: 7.5%;
}

.col-7 {
  flex-basis: 7.5%;
}

.col-8 {
  flex-basis: 7.5%;
}

.col-9 {
  flex-basis: 7.5%;
}

.col-10 {
  flex-basis: 7.5%;
}

.col-11 {
  flex-basis: 7.5%;
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
<script>
function showAlert(errorMessage) {
    if (errorMessage && errorMessage.trim() !== "") {
        alert(errorMessage);
    }
}
</script>
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
<h2>Atividades</h2>
<%
    // Retrieve the error message from the request
    String errorMessage = (String) request.getAttribute("error_message");

    // Display the error message if it exists
    if (errorMessage != null && !errorMessage.isEmpty()) {
%>
    <script>
        // Call the JavaScript function to display an alert
        alert('<%= errorMessage %>');
    </script>
<%
    }
%>
<table border="1">
    <tr>
        <th>IdAtividade</th>
        <th>Atividade</th>
        <th>Dia Da Atividade</th>
        <th>Hora de Inicio</th>
        <th>Hora de Fim</th>
        <th>Tipo De Atividade</th>
        <th>Inscriçôes</th>
        <th>Vagas</th>
        <th>Sala</th>
        <th>LotacaoSala</th>
        <th>Estado</th>
    </tr>

    <% 
        ResultSet resultSet = jc.getAtividadePT(nifClube, Integer.valueOf(cliente.getNif()));
        try {
            while (resultSet.next()) { 
                String estado = resultSet.getString("estado");
                int idAtividade = resultSet.getInt("idAtividade");
    %>
                <tr>
                    <td class="clickable-cell" onclick="openNewWindow('<%= idAtividade %>')" data-id="<%= idAtividade %>" style="display: none;">
                        <%= idAtividade %>
                    </td>
                    <td><%= resultSet.getString("idAtividade") %></td>
                    <td class="clickable-cell" onclick="openNewWindow('<%= idAtividade %>')"><%= resultSet.getString("NomeAtividade") %></td>
                    <td><%= new SimpleDateFormat("yyyy-MM-dd").format(resultSet.getDate("DiaDaAtividade")) %></td>
                    <td><%= resultSet.getTime("HoraInicio") %></td>
                    <td><%= resultSet.getTime("HoraFim") %></td>
                    <td><%= resultSet.getString("TipoDeAtividade") %></td>
                    <td><%= resultSet.getInt("Inscricoes") %></td>
                    <td><%= resultSet.getInt("Vagas") %></td>
                    <td><%= resultSet.getInt("Sala") %></td>
                    <td><%= resultSet.getInt("LotacaoSala") %></td>
                    <td>
                        <select class="atividade-select" data-id="<%= estado %>">
                            <option value="confirmada" <%= estado.equals("confirmada") ? "selected" : "" %>>Confirmada</option>
                            <option value="não confirmada" <%= estado.equals("não confirmada") ? "selected" : "" %>>Não confirmada</option>
                            <option value="cancelada" <%= estado.equals("cancelada") ? "selected" : "" %>>Cancelada</option>
                        </select>
                    </td>
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
        jc.DBdisconnect();
    %>
</table>
<div id="addRowContainer" class="addRowContainer">
    <button id="addRowBtn" class="addRowBtn" onclick="redirectToAddAtividade()">Adicionar Atividade</button>
</div>


<script>

function openNewWindow(idAtividade) {
	
    var url = 'atividade.jsp?idAtividade=' + encodeURIComponent(idAtividade);

    window.open(url, '_blank');
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

var selects = document.querySelectorAll('.atividade-select');

selects.forEach(function (select) {
    select.addEventListener('change', function () {
    	var closestTr = this.closest('tr');
        var clickableCell = closestTr.querySelector('.clickable-cell');  
        var idAtividade = clickableCell.getAttribute('data-id');
        var newEstado = this.value;
        var nifClube = <%= nifClube%> ;
        	
        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'UpdateEstadoAtividade', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        
        var data = 'idAtividade=' + encodeURIComponent(idAtividade) +
                       '&newEstado=' + encodeURIComponent(newEstado);
        
        console.log(data);
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
                location.reload();
            }
        };
        
        xhr.send(data);
    });
});

function redirectToAddAtividade() {
    window.location.assign('add_atividade.jsp');
}
</script>

</body>
</html>
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
<link rel="stylesheet" type="text/css" href="style.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
<style>
        .table-container {
        	position: relative;
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
        <tr>
            <th>Nome do equipamento</th>
            <th>Estado</th>
            <th>Apagar</th>
        </tr>

        <%  
            ResultSet resultSet = jc.getEquipamentos(nifClube);
            try {
                while (resultSet.next()) {
                    String nomeEquipamento = resultSet.getString("nome");
                    String estado = resultSet.getString("estado");
                    int idEquipamento = resultSet.getInt("id");
        %>
                    <tr>
                        <td class="clickable-cell" onclick="openNewWindow('<%= nomeEquipamento %>', '<%= idEquipamento %>')" data-id="<%= nomeEquipamento %>">
                            <%= nomeEquipamento %>
                        </td>
                        <td>
                            <select class="estado-select" data-id="<%= idEquipamento %>">
                                <option value="disponível" <%= estado.equals("disponível") ? "selected" : "" %>>Disponível</option>
                                <option value="manutenção" <%= estado.equals("manutenção") ? "selected" : "" %>>Manutenção</option>
                            </select>
                        </td>
                        <td>
                        <button onclick="deleteEquipamento('<%= idEquipamento %>')">Delete</button>
                    </td>
                   
                    </tr>
        <% 
                }
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close(); // Close the first ResultSet
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        %>
        
    </table>
       <div class="center-button">
        	<button class="button" onclick="redirectToAddEquipamentoForm()">Adicionar Equipamento</button>
       </div>

</div>

<div class="table-container">
	<h2 >Equipamentos menos usados no último trimestre</h2>
    <table class="clientes" border="1">
        <tr>
            <th>Nome do equipamento</th>
            <th>Usos</th>
            <th>id</th>
        </tr>

        <%  
        	int idEquipamento_usos = -1;
            ResultSet resultSet_usos = jc.getLessUsedEquipamentos(nifClube);
            try {
                while (resultSet_usos.next()) {
                    String nomeEquipamento_usos = resultSet_usos.getString("nome");
                    String usos = resultSet_usos.getString("occurrences");
                    idEquipamento_usos = resultSet_usos.getInt("idEquipamento");
        %>
                    <tr>
                        <td class="clickable-cell" onclick="openNewWindow('<%= nomeEquipamento_usos %>')" data-id="<%= nomeEquipamento_usos %>">
                            <%= nomeEquipamento_usos %>
                        </td>
                        <td><%= usos %></td>
                        <td><%= idEquipamento_usos %></td>
                        
                    </tr>
                    
        <% 
                }
            } finally {
                try {
                    if (resultSet_usos != null) {
                        resultSet_usos.close(); // Close the second ResultSet
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        %>
    </table>
</div>
<script>

function redirectToAddEquipamentoForm() {
    window.location.href = 'addEquipamento.jsp';
}

function deleteEquipamento(idEquipamento) {
    var result = confirm("Tem a certeza que deseja apagar este equipamento?");
    if (result) {
        $.ajax({
            type: "POST",
            url: "DeleteEquipamentoServlet", 
            data: { idEquipamento: idEquipamento },
            success: function (response) {
                console.log(response);
                location.reload();
            },
            error: function () {
                alert("Error deleting equipamento");
            }
        });
    }
}


function openNewWindow(equipamentoNome, idEquipamento) {
	console.log('equipamentoNome:', equipamentoNome);
    console.log('idEquipamento:', idEquipamento);
    var nifClube = '<%= nifClube %>';
    var url = 'equipamento.jsp?equipamentoNome=' + encodeURIComponent(equipamentoNome) + '&nifClube=' + encodeURIComponent(nifClube) + '&idEquipamento=' + encodeURIComponent(idEquipamento);

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

var selects = document.querySelectorAll('.estado-select');

selects.forEach(function (select) {
    select.addEventListener('change', function () {
        var idEquipamento = this.getAttribute('data-id');
        var newEstado = this.value;
        
        var closestTr = this.closest('tr');
        var clickableCell = closestTr.querySelector('.clickable-cell');  
        var nomeEquipamento = clickableCell.getAttribute('data-id');

        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'UpdateEstadoServlet', true);
        xhr.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
        
        var data = 'idEquipamento=' + encodeURIComponent(idEquipamento) +
                       '&newEstado=' + encodeURIComponent(newEstado) +
                       '&nomeEquipamento=' + encodeURIComponent(nomeEquipamento);
        
        xhr.onreadystatechange = function () {
            if (xhr.readyState == 4 && xhr.status == 200) {
                // Handle the response if needed
                console.log(xhr.responseText);
            }
        };
        
        xhr.send(data);
    });
});

</script>
    
</body>
</html>
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

<div class="table-container">
	<table border="1">
        <tr>
            <th>Dia da Semana</th>
            <th>Abertura</th>
            <th>Fecho</th>
            <th>Editar</th>
            <!-- Add more headers based on your table structure -->
        </tr>

        <%  
            ResultSet resultSet = jc.getHorarios(nifClube);
            while (resultSet.next()) {
        %>
            <tr>
                <td class="editable" data-column="Dia_da_Semana" data-id="<%= resultSet.getString("Dia_da_Semana") %>" contenteditable="false">
                    <%= resultSet.getString("Dia_da_Semana") %>
                </td>
                <td class="editable" data-column="Abertura" data-id="<%= resultSet.getString("Abertura") %>" contenteditable="false">
                    <%= resultSet.getString("Abertura") %>
                </td>
                <td class="editable" data-column="Fecho" data-id="<%= resultSet.getString("Fecho") %>" contenteditable="false">
                    <%= resultSet.getString("Fecho") %>
                </td>
                <td>
                    <button class="editRowBtn" onclick="editRow(this)" data-id="<%= resultSet.getString("id") %>">Editar</button>
                </td>
                
                <!-- Adicione mais células para outras colunas com base na estrutura da sua tabela -->
            </tr>
        <%
            }
            resultSet.close();
        %>
    </table>  
    <div id="addRowContainer" class="addRowContainer">
		<button id="addRowBtn" class="addRowBtn" onclick="addRow()">Adicionar horário</button>
	</div>
	
	<script>
    // Array de dias da semana válidos
    var diasDaSemanaValidos = ["2ª Feira", "3ª Feira", "4ª Feira", "5ª Feira", "6ª Feira", "Sábado", "Domingo"];

    function editRow(button) {
        var row = button.parentNode.parentNode;
        var cells = row.getElementsByTagName("td");
        var id = button.getAttribute('data-id');
		console.log(id);
        // Torna as células editáveis
        for (var i = 0; i < cells.length - 1; i++) {
            if (i === 0) {
                // Se for a primeira coluna (Dia da Semana), cria um dropdown
                createDropdown(cells[i]);
            } else {
                // Se não for a primeira coluna, torna a célula editável
                cells[i].contentEditable = true;
            }
        }

        // Altera o texto do botão para "Salvar" e atribui a função "saveRow"
        button.innerHTML = "Salvar";
        button.onclick = function () {
            saveRow(this, id);
        };
    }

    function createDropdown(cell) {
        // Cria um elemento <select>
        var select = document.createElement("select");

        // Adiciona as opções de dias da semana ao dropdown
        for (var i = 0; i < diasDaSemanaValidos.length; i++) {
            var option = document.createElement("option");
            option.value = diasDaSemanaValidos[i];
            option.text = diasDaSemanaValidos[i];
            select.appendChild(option);
        }

        // Define o valor inicial do dropdown com base no texto atual da célula
        select.value = cell.innerText.trim();

        // Substitui o conteúdo da célula pelo dropdown
        cell.innerHTML = "";
        cell.appendChild(select);
    }

    function addRow() {
    	var table = document.querySelector('.table-container table');
        
        // Create a new row
        var newRow = table.insertRow(-1);

        // Define the cells for the new row
        var cellDiaSemana = newRow.insertCell(0);
        var cellAbertura = newRow.insertCell(1);
        var cellFecho = newRow.insertCell(2);
        var cellEditar = newRow.insertCell(3);

        // Set the default content and attributes for each cell
        cellDiaSemana.innerText = ""; // You can change this default value
        cellAbertura.innerText = "";
        cellFecho.innerText = "";
        cellEditar.innerHTML = '<button class="editRowBtn" onclick="saveRow(this)" data-id="0">Salvar</button>';
        var cells = newRow.getElementsByTagName("td");

        for (var i = 0; i < cells.length - 1; i++) {
        	console.log("here")
            if (i === 0) {
                // Se for a primeira coluna (Dia da Semana), cria um dropdown
                createDropdown(cells[i]);
            } else {
                // Se não for a primeira coluna, torna a célula editável
                cells[i].contentEditable = true;
            }
        }

        // Set a new function for the "Editar" button
        var saveButton = cellEditar.querySelector('.editRowBtn');
        saveButton.onclick = function() {
            saveRow(this);
        };
    }
    
    function saveRow(button) {
    	var row = button.parentNode.parentNode;
        var cells = row.getElementsByTagName("td");
        
        // Torna as células não editáveis
        for (var i = 0; i < cells.length - 1; i++) {
            if (i === 0) {
                // Se for a primeira coluna (Dia da Semana), obtém o valor do dropdown
                cells[i].innerText = cells[i].getElementsByTagName("select")[0].value;
            } else {
                // Se não for a primeira coluna, torna a célula não editável
                cells[i].contentEditable = false;
            }
        }

        // Altera o texto do botão de volta para "Editar" e atribui a função "editRow"
        button.innerHTML = "Editar";
        button.onclick = function () {
            editRow(this);
        };
    	
    	var newDiaSemana = cells[0].innerText.trim();
        var newAbertura = cells[1].innerText.trim();
        var newFecho = cells[2].innerText.trim();
        var id = button.getAttribute('data-id');
        
       	callProcessHorariosServlet(newDiaSemana, newAbertura, newFecho, id);
    }
    
    function callProcessHorariosServlet(diaSemana, abertura, fecho, id) {
        $.ajax({
            type: 'POST',
            url: 'horarios', 
            data: {
                diaSemana: diaSemana,
                abertura: abertura,
                fecho: fecho,
                id: id,
                action: 'update'
            },
            success: function(response) {
                //window.location.href = 'manage_clube.jsp';
            },
            error: function() {
                alert('Erro ao chamar o servlet de processamento de horários');
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
</div>

    
</body>
</html>
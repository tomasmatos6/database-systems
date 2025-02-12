<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<link rel="stylesheet" type="text/css" href="style.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">

    <style>
        
        .icon {
            margin-right: 15px;
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

String idAtividadePar = request.getParameter("idAtividade");
int idAtividade = Integer.parseInt(idAtividadePar);

ResultSet resultSet  = jc.getAtividadeById(idAtividade);
while (resultSet.next()) {
%>

<div class="content">
<h1>Personal Trainer</h1>
<h2><%= "Nif do clube atual: " + session.getAttribute("nifClube") + "\n"%> id atividade: <%= idAtividade%></h2>
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

<h2>Atividade: <%= resultSet.getString("NomeAtividade")%></h2>
<h2>Dia da Atividade: <%= resultSet.getString("DiaDaAtividade")%> | Hora de Inicio:  <%= resultSet.getString("HoraInicio")%>| Hora de Fim: <%= resultSet.getString("HoraFim")%></h2>
<h2>Tipo de atividade: <%= resultSet.getString("TipoDeAtividade")%> | Inscrições: <%= resultSet.getString("Inscricoes")%> | Espaços Livres: <%= (Integer.parseInt(resultSet.getString("Vagas")) - Integer.parseInt(resultSet.getString("Inscricoes")))%></h2>
<h2>Sala: <%= resultSet.getString("Sala")%> | Lotação da Sala: <%= resultSet.getString("LotacaoSala")%> | Estado: <%= resultSet.getString("Estado")%></h2>

<% }
ResultSet resultSet_equipamentos = jc.getEquipamentosDeAtividade(idAtividade);
%>
<h2>Equipamentos da Atividade</h2>
<table>
    <thead>
        <tr>
            <th>ID</th>
            <th>Nome</th>
        </tr>
    </thead>
    <tbody>
        <%
            while (resultSet_equipamentos.next()) {
                int equipamentoID = resultSet_equipamentos.getInt("EquipamentoID");
                String equipamentoNome = resultSet_equipamentos.getString("EquipamentoNome");

                %>
                <tr>
                    <td><%= equipamentoID %></td>
                    <td><%= equipamentoNome %></td>
                    <!-- Add more columns as needed -->
                </tr>
                <%
            }
        %>
    </tbody>
</table>
<% 

List<Integer> nifClientes = jc.getNifClientesForAtividade(idAtividade);
List<Integer> clientesNaAtividade = new ArrayList<Integer>();
List<String> nomeClientes = new ArrayList<String>();
%>

<h2>Clientes da Atividade</h2>
    <table>
        <thead>
            <tr>
                <th>Nome</th>
                <th>Email</th>
                <th>Telemovel</th>
                <th>EscalaoDescricao</th>
            </tr>
        </thead>
        <tbody>
            <%
                for (Integer nif : nifClientes) {
                	clientesNaAtividade.add(nif);
                    ResultSet clientResultSet = jc.getClienteByNif(nif);
                    try {
                        while (clientResultSet.next()) {
                            String nome = clientResultSet.getString("nome");
                            String emailCliente = clientResultSet.getString("email");
                            String telemovel = clientResultSet.getString("telemovel");
                            String escalaoDescricao = clientResultSet.getString("EscalaoDescricao");

                            %>
                            <tr>
                                <td><%= nome %></td>
                                <td><%= emailCliente %></td>
                                <td><%= telemovel %></td>
                                <td><%= escalaoDescricao %></td>
                            </tr>
                            <%
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            %>
        </tbody>
        
    </table>
	<div class="button-and-dropdown" style="margin-left:700px">
    <button id="addClientButton" style="font-size: 18px; padding: 12px 20px;">Adicionar Cliente</button>
    <select id="clientDropdown" style="font-size: 18px; padding: 12px 20px; margin-left: 10px;">
        <!-- Options will be dynamically added here -->
    </select>
</div>
    <%
	System.out.println("INSCRITOS: " + clientesNaAtividade);
   	//jc.DBdisconnect();
    %>


<script>

$(document).ready(function () {
	
	var clientesNaAtividade = [<%= clientesNaAtividade.stream().map(Object::toString).collect(java.util.stream.Collectors.joining(",")) %>];

	
	$("#clientDropdown").hide();
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
    
    $("#addClientButton").click(function () {
        // Make an AJAX request to the ListaClientesDePTServlet
        $.ajax({
            type: "GET",
            url: "ListaClientesDePTServlet",
            data: { nifPT: <%= cliente.getNif() %> },  
            success: function (data) {
                var lists = data.split('\n\n');
                var clientsList = lists[0].replace('\n', '').split('\n');
                var nomesList = lists[1].replace(/\n/g, ',').split(',');
                
                
                var nomes = nomesList.map(function(element) {
                    return element.replace(/\r/g, '');
                });
                
                var clientes = clientsList.map(function(element) {
                    return element.replace(/\r/g, '');
                });
           
                updateClientDropdown(clientes, nomes);
                $("#clientDropdown").toggle();
            },
            error: function () {
                alert("Error getting client list");
            }
        });
    });

    // Function to update the client dropdown based on the provided data
	    function updateClientDropdown(clientes, nomes) {
	    var dropdown = $("#clientDropdown");
	    
	    dropdown.append('<option value="">Selecionar Cliente</option>');
		console.log("CLIENTES NA ATIVIDADE: " + clientesNaAtividade);
		console.log("Nomes", nomes);
        console.log("Clientes", clientes);
        
	    for (var i = 0; i < clientes.length; i++) {
	        var nifCliente = parseInt(clientes[i]);
	        if (!clientesNaAtividade.includes(nifCliente)) {

	            dropdown.append('<option value="' + clientes[i] + '">' + nomes[i] + '</option>');
	        }
    	}
	    
		}
	    
	    $("#clientDropdown").change(function () {
	        var selectedClient = $(this).val();
	        var idAtividade = <%= idAtividade %>;
	        console.log("Selected Client: " + selectedClient);
	        console.log("Id da atividade " + idAtividade);
	        $.ajax({
	            type: "POST", 
	            url: "InscricaoAtividadeServlet", 
	            data: { 
	            		idAtividade: idAtividade,
	                	nifCliente: selectedClient 
	                  },
	            success: function (response) {
	            	alert("Cliente inscrito com sucesso!");
	                location.reload();
	            },
	            error: function () {
	                console.error("Error calling servlet");
	            }
	        });
	    });
});

</script>

</body>
</html>
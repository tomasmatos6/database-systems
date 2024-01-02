<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%@ page import="connector.JavaConnector" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.Map" %>
<%@ page import="connector.Utilizador" %>
<html>
<head>
<style>
	td {
		block-size: auto;
		writing-mode: horizontal-tb;
	}
	table {
		border-collapse: collapse;
	}
</style>
<meta charset="UTF-8">
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>

<title>FitnessUP</title>
</head>
<body>
<h1>Ficha de Novo Cliente</h1>

<%

int permissoes = (int) session.getAttribute("permissoes");
String email = (String) session.getAttribute("email");
System.out.println("PERMISSOES: " + permissoes);
JavaConnector jc = new JavaConnector();
Utilizador pt_info = jc.carregarPT(email);

session.setAttribute("nifPT", Integer.valueOf(pt_info.getNif()));

ResultSet resultSet_cli = jc.getClientesDePT(pt_info.getNome());
List<String> nomesCliente = new ArrayList<>();
while(resultSet_cli.next()) {
	nomesCliente.add(resultSet_cli.getString("nome"));
}

int nifClube = -1;

if (session.getAttribute("nifClube") != null) {
    // Set a default value for 'nif'
	nifClube = (int) session.getAttribute("nifClube");
}
ResultSet resultSet_eq = jc.getEquipamentos(nifClube);
HashMap<Integer, String> equipamentos = new HashMap<>();
while(resultSet_eq.next()) {
	if(resultSet_eq.getString("estado").equals("disponível"))
		equipamentos.put(resultSet_eq.getInt("id"), resultSet_eq.getString("nome"));
}
%>

<form action="addRecomendacao" method="post" onsubmit="return validateForm()">
	 <table style="width: 500px">
        <tr>
            <td>Cliente</td>
            <td><select name ="nomeCliente" id="nomeCliente" onchange="setNome(this)">
    		<% for (String nomeCliente : nomesCliente) { %>
        	<option value="<%= nomeCliente %>"><%= nomeCliente %></option>
    		<% } %>
		</select></td>
        </tr>
        <tr>
            <td>Equipamento</td>
            <td><select name="equipamento" id="equipamentoDropdown">
            <% for (Map.Entry<Integer, String> entry : equipamentos.entrySet()) { 
            		int equipamentoId = entry.getKey();
        			String equipamentoNome = entry.getValue();%>
                <option value="<%= equipamentoId %>"><%= equipamentoNome %></option>
            <% } %>
        </select></td>
        </tr>
        <tr>
            <td>Data de Inicio</td>
            <td><input type="date" name="dataInicio" id="dataInicio"/></td>
        </tr>
        <tr>
            <td>Data de Fim</td>
            <td><input type="date" name="dataFim" id="dataFim"/></td>
        </tr>
        <tr>
            <td>Descricao</td>
            <td><input type="text" name="descricao" /></td>
        </tr>
    </table>
    
    <input type="submit" value="Adicionar" />
</form>
<script>
	var selectedCliente;
	var selectedEquipamento;
	
	function setNome() {
		var dropdown = document.getElementById("clienteDropdown");
        selectedCliente = dropdown.value;
        console.log("Selected Cliente: " + selectedCliente);
	}
	
	function setEquipamento() {
		var dropdown = document.getElementById("equipamentoDropdown");
		selectedEquipamento = dropdown.value;
		console.log("Selected Equipamento: " + selectedEquipamento);
	}
	
	function validateForm() {
	    var dataInicio = new Date(document.getElementById("dataInicio").value);
	    var dataFim = new Date(document.getElementById("dataFim").value);
		var dataNow = new Date();
	    // Compare dates
	    if (dataInicio >= dataFim) {
	        alert("A data de início deve ser anterior à data de fim.");
	        return false; // Prevent form submission
	    }
	    if(dataFim < dataNow) {
	    	alert("A data de fim não pode ser anterior à data atual.");
	    	return false;
	    }

	    // If the dates are valid, allow form submission
	    return true;
	}
</script>
</body>
</html>
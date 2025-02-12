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
<h1>Nova Atividade</h1>

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

%>

<form action="addAtividade" method="post" onsubmit="return validateForm()">
	 <table style="width: 500px">
        <tr>
            <td>Atividade</td>
            <td><input type="text" name="atividade" id="atividade" /></td>
        </tr>
        <tr>
        	<td>Tipo</td>
            <td>
                <select name="tipo" id="tipoDropdown" onchange="toggleVagasInput()">
                    <option value="individual">Individual</option>
                    <option value="grupo">Grupo</option>
                </select>
            </td>
        </tr>
        <tr id="vagasRow" style="display:none">
            <td>Vagas</td>
            <td><input type="text" name="vagas" id="vagas" /></td>
        </tr>
        <tr>
            <td>Data</td>
            <td><input type="date" name="data" id="data" onchange="showDropdown()"/></td>
        </tr>
        <tr>
            <td>Hora de Inicio</td>
            <td><input type="text" name="horaInicio" id="horaInicio" onchange="showDropdown()"/></td>
        </tr>
        <tr>
            <td>Hora de Fim</td>
            <td><input type="text" name="horaFim" id="horaFim" onchange="showDropdown()"/></td>
        </tr>
        <tr id="salaRow" style="display:none">
        	<td>Sala</td>
        	<td><select name="sala" id="salaDropdown" onchange="setSala(this)">

        </select></td>
        </tr>
        <tr id="equipamentoRow" style="display:none">
            <td>Equipamento</td>
            <td><select name="equipamento" id="equipamentoDropdown" multiple>
        </select></td>
        </tr>
    </table>
    
    <input type="submit" value="Adicionar" />
</form>

<div id="selectedEquipamentosDisplay"></div>

<script>
	var selectedSalas = [];
	var selectedEquipamentos = [];
	var selectedEquipamentosNomes = [];
	
	function handleClick(optionValue, optionName) {
		var index = selectedEquipamentos.indexOf(optionValue);
		
		if(index === -1) {
			selectedEquipamentos.push(optionValue);
			selectedEquipamentosNomes.push(optionName);
		} else {
			selectedEquipamentos.splice(index,1);
			selectedEquipamentosNomes.splice(index, 1);
		}
		updateSelectedEquipamentosDisplay();
	}
	
	function updateSelectedEquipamentosDisplay() {
        var displayDiv = $("#selectedEquipamentosDisplay");
        displayDiv.text("Equipamentos Selecionadas: " + selectedEquipamentosNomes.join(", "));
}
	
	function setSala() {
		var dropdown = document.getElementById("salaDropdown");
        selectedSala = dropdown.value;
        console.log("Selected Sala: " + selectedSala);
	}
	
	function setEquipamento() {
		var dropdown = document.getElementById("equipamentoDropdown");
        selectedEquipamentos = Array.from(dropdown.selectedOptions).map(option => option.value);
        console.log("Selected Equipamentos: " + selectedEquipamentos);
	}
	
	function showDropdown() {
		var data = document.getElementById("data").value;
        var horaInicio = document.getElementById("horaInicio").value;
        var horaFim = document.getElementById("horaFim").value;
		if (data && horaInicio && horaFim) {
            // If all three fields have values, show the hidden rows
            document.getElementById("salaRow").style.display = "table-row";
            document.getElementById("equipamentoRow").style.display = "table-row";
            
         	// Fetch and populate dropdown options
            fetchDropdownOptions("salaDropdown", "sala", data, horaInicio, horaFim);
            fetchDropdownOptions("equipamentoDropdown", "equipamento", data, horaInicio, horaFim);
        }
		else {
			// If any of the fields is empty, hide the rows
            salaRow.style.display = "none";
            equipamentoRow.style.display = "none";
		}
	}
	
	function fetchDropdownOptions(dropdownId, optionType, data, horaInicio, horaFim) {
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                // Parse JSON response
                console.log(xhr.responseText);
                var data = JSON.parse(xhr.responseText);
				
                // Populate dropdown options
                var dropdown = document.getElementById(dropdownId);
                dropdown.innerHTML = ""; // Clear existing options

                data[optionType].forEach(function (option) {
                    var optionElement = document.createElement("option");
                    optionElement.value = option.id; // Use the ID as the value
                    optionElement.text = option.name; // Use the name as the displayed text
                    optionElement.classList.add("dateField"); // Add class "dateField"
                    optionElement.addEventListener('click', function () {
                        handleClick(this.value, this.text);
                    });
                    
                    dropdown.appendChild(optionElement);
                });
            }
        };

        xhr.open("POST", "getEquipamentoSala", true);
        xhr.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        xhr.send("optionType=" + optionType + "&data=" + data + "&horaInicio=" + horaInicio + "&horaFim=" + horaFim + "&nif=" + <%= nifClube%>);
    }
	
	function validateForm() {
	    var data = new Date(document.getElementById("data").value);
		var dataNow = new Date();
	    if(data < dataNow) {
	    	alert("A data não pode ser anterior à data atual.");
	    	return false;
	    }

	    // If the dates are valid, allow form submission
	    return true;
	}
	
	$(document).ready(function () {
		toggleVagasInput();
	    // Add a submit event handler for the form
	    $("form").submit(function (event) {
	        // Get the selected values from the array
	        var selectedValues = selectedEquipamentos.join(",");

	        // Create a hidden input field and add it to the form
	        $(this).append('<input type="hidden" name="selectedEquipamentos" value="' + selectedValues + '">');
	        console.log($(this).serializeArray());
	    });
	});
	
	function toggleVagasInput() {
        var tipoDropdown = document.getElementById("tipoDropdown");
        var vagasRow = document.getElementById("vagasRow");

        // If the selected value is "grupo", show the Vagas input; otherwise, hide it
        vagasRow.style.display = tipoDropdown.value === "grupo" ? "table-row" : "none";
    }
</script>
</body>
</html>
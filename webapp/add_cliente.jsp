<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
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

%>

<form action="processRegistration" method="post" enctype="multipart/form-data">
	 <table style="width: 1000px">
        <tr>
            <td>NIF</td>
            <td><input type="text" name="nif" /></td>
        </tr>
        <tr>
            <td>Nome</td>
            <td><input type="text" name="nome" /></td>
        </tr>
        <tr>
            <td style="width: 280px;">Data de Nascimento (YYYY-MM-DD)</td>
            <td><input type="date" name="data_nascimento" /></td>
        </tr>
        <tr>
            <td>Email</td>
            <td><input type="text" name="email" /></td>
        </tr>
        <tr>
            <td>Telemóvel</td>
            <td><input type="text" name="telemovel" /></td>
        </tr>
        <tr>
            <td>Objetivos</td>
            <td><textarea name="objetivos"></textarea></td>
        </tr>
      	<tr>
		    <td>Patologias</td>
		    <td>
		        <!-- Use a <select> element for the dropdown with multiple selection -->
		        <select name="patologias[]" multiple id="patologiasDropdown" onchange="updatePatologiaFields(this)"></select>
		    </td>
		    <td class="dateField" style="width: 180px; display: none;">Data Início
		    <input type="text" name="dataInicio" class="dateField" style="display: none;"/></td>
		    <td class="dateField" style="width: 180px; display: none;">Data Fim
		    <input type="text" name="dataFim" class="dateField" style="display: none;"/></td>
		    <td class="dateField" style="display: none;"><button onclick="addPatologia()">Add</button></td>
		</tr>
        <tr>
        <td>Foto</td>
        <td><input type="file" name="foto" accept="image/*" /></td>
    </tr>
    </table>
    
    <input type="submit" value="Registrar" />
</form>

<div id="selectedPatologiasDisplay"></div>

<script>
	var selectedPatologias = [];
	var selectedPatologia;
	//Function to handle click on options
	function handleClick(optionValue) {
	 	// Check if the optionValue is already in the array
		 var index = selectedPatologias.indexOf(optionValue);
		
		 if (index === -1) {
		     // If not in the array, add the optionValue
		     selectedPatologias.push(optionValue);
		 } else {
		     // If already in the array, remove the optionValue
		     $('.dateField').hide();
		     selectedPatologias.splice(index, 1);
		 }
		
		 // Optionally, update your UI or perform other actions here
		 updateSelectedPatologiasDisplay();
	}
	
	function updatePatologiaFields(selectElement) {
	    // Get the selected values from the dropdown
	    var selectedValues = $(selectElement).val();

	    // Show/hide the date fields based on the number of selected values
	    console.log(selectedValues)
	    if (selectedValues && selectedValues.length >= 1) {
	        $('.dateField').show();
	    }

	    // Update dataInicio and dataFim fields based on the selected values
	    if (selectedValues && selectedValues.length === 1) {
	        selectedPatologia = selectedValues[0];
	        
	    } else {
	        $('input[name="dataInicio"]').val("");
	        $('input[name="dataFim"]').val("");
	    }
	}
	
	function addPatologia() {
		event.preventDefault();
		$('.dateField').hide();
		$("form").append('<input type="hidden" name="nome" value="' + selectedPatologia + '">');
		$("form").append('<input type="hidden" name="dataInicio" value="' + $('input[name="dataInicio"]').val() + '">');
		$("form").append('<input type="hidden" name="dataFim" value="' + $('input[name="dataFim"]').val() + '">');
	}
	
	function updateSelectedPatologiasDisplay() {
	        var displayDiv = $("#selectedPatologiasDisplay");
	        displayDiv.text("Patologias Selecionadas: " + selectedPatologias.join(", "));
	}
	
	function fetchPatologias() {
	    $.ajax({
	        type: "GET",
	        url: "patologias",
	        success: function (data) {
	            // Assume that the server returns the options directly
	            var dropdown = $("#patologiasDropdown");
	            dropdown.html(data); // Use .html() to directly set the HTML content

	            // Attach a click event handler
	            dropdown.on('click', 'option', function () {
	                handleClick($(this).val());
	            });
	        },
	        error: function () {
	            alert("Error fetching patologias");
	        }
	    });
	}

	$(document).ready(function () {
	    fetchPatologias();

	    // Add a submit event handler for the form
	    $("form").submit(function (event) {
	        // Get the selected values from the array
	        var selectedValues = selectedPatologias.join(",");

	        // Create a hidden input field and add it to the form
	        $(this).append('<input type="hidden" name="selectedPatologias" value="' + selectedValues + '">');
	        console.log($(this).serializeArray());
	    });
	});
</script>

</body>
</html>
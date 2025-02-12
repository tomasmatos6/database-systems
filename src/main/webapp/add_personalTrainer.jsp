<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="java.util.List" %>
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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.13/js/select2.min.js"></script>

<title>FitnessUP</title>
</head>
<body>
<h1>Ficha de Novo PersonalTrainer</h1>

<%

int permissoes = (int) session.getAttribute("permissoes");
String email = (String) session.getAttribute("email");
System.out.println("PERMISSOES: " + permissoes);
JavaConnector jc = new JavaConnector();
%>

<form action="CreatePTServlet" method="post" enctype="multipart/form-data">
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
                <td>Email</td>
                <td><input type="text" name="email" /></td>
            </tr>
            <tr>
                <td>Telemóvel</td>
                <td><input type="text" name="telemovel" /></td>
            </tr>
            <tr>
            	<td>Foto</td>
            	<td><input type="file" name="fotoPT" accept="image/*"></td>
            </tr>
            <tr>
           <tr>
                <td>Clubes</td>
                <td>
                    <!-- Use a <select> element for the dropdown with multiple selection -->
                    <select name="clubes[]" multiple id="clubesDropdown">
                        <%  
                            List<String> clubesList = jc.getClubes();
                            for (String clube : clubesList) {
                        %>
                                <option value="<%= clube %>"><%= clube %></option>
                        <% } %>
                    </select>
                </td>
            </tr>
        </table>
        <input type="submit" value="Registrar" />
    </form>

    <div id="selectedClubesDisplay"></div>
    
<script>

$(document).ready(function () {
    var selectedClubes = [];

    $("#clubesDropdown").on('click', 'option', function () {
        var selectedOption = $(this).val();

        if (selectedOption !== null) {
            var index = selectedClubes.indexOf(selectedOption);

            if (index === -1) {
                selectedClubes.push(selectedOption);
            } else {
                selectedClubes.splice(index, 1);
            }

            console.log("Selected Clubes Array:", selectedClubes);
            updateSelectedClubesDisplay();
        }
    });

    function updateSelectedClubesDisplay() {
        var displayDiv = $("#selectedClubesDisplay");
        displayDiv.text("Clubes Selecionados: " + selectedClubes.join(", "));
    }

    $("form").submit(function (event) {
        event.preventDefault();

        var formData = new FormData($(this)[0]);

        // Append the selectedClubes array to the FormData object
        formData.append("selectedClubes", selectedClubes);

        $.ajax({
            type: "POST",
            url: "CreatePTServlet",
            processData: false,  
            contentType: false,  
            data: formData,
            success: function () {
                console.log("Form data sent successfully");
                window.location.href = "manage_pts.jsp";
            },
            error: function () {
                alert("Error sending form data to server");
            }
        });
    });
});
</script>

</body>
</html>
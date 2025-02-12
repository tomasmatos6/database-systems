<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="stylesheet" type="text/css" href="style.css">
	<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
    <title>FitnessUP</title>
</head>
<body>

    <h1>Página Equipamento</h1>

    <% String equipamentoNome = request.getParameter("equipamentoNome"); %>
    <% String idEquipamento = request.getParameter("idEquipamento");%>
    <% String nifClubePar = request.getParameter("nifClube"); 
    	
    	int nifClube = Integer.parseInt(nifClubePar);%>

    <p>Equipamento Nome: <%= equipamentoNome %></p>
    <p>NIF Clube: <%= nifClube %></p>
	
	<%
    JavaConnector jc = new JavaConnector();
    List<String> fotos = jc.getFotos(nifClube, equipamentoNome);
    List<String> videos = jc.getVideos(nifClube, equipamentoNome);
    jc.DBdisconnect();
	%>
	<div class="options">
		<input id="nome" value="<%= equipamentoNome%>" style="display:none;">
		<button id="editBtn">Edit</button>
        
        <label class="option" for="fotoEquipamento" style="display:none;">Inserir Foto:</label>
	    <input class="option" id="foto" type="file" name="fotoEquipamento" accept="image/*" style="display:none;">
	
	    <label class="option" for="videoFile" style="display:none;">Inserir Vídeo:</label>
	    <input class="option" id="video" type="file"  name="videoFile" accept="video/*" style="display:none;">
	    <button id="saveBtn" style="display: none;">Save</button>
	</div>

<% for (int i = 0; i < fotos.size(); i++) { %>
    <div class="image-container">
        <p>Equipamento: <%= equipamentoNome %></p>
        <img src="<%= fotos.get(i) %>" alt="Image" class="resizable-image">
    </div>
<% } %>

<% for (int i = 0; i < videos.size(); i++) { %>
    <div class="video-container">
        <video controls class="resizable-video">
            <source src="<%= videos.get(i) %>" type="video/mp4">
        </video>
    </div>
<% } %>
<script>
$(document).ready(function () {
    $("#editBtn").click(function () {
        $("#editBtn").toggle();
        $(".option").toggle();
        $("#saveBtn").show();
        $(this).hide();
    });
    
    $("#saveBtn").click(function () {
    	var id = <%= idEquipamento %>;
    	var nome = $("#nome").val();
    	var formData = new FormData();
    	formData.append('id', id);
    	formData.append('nome', nome);
    	// Check if foto is selected
        if ($('#foto')[0].files.length > 0) {
            formData.append('foto', $('#foto')[0].files[0]);
        }

        // Check if video is selected
        if ($('#video')[0].files.length > 0) {
            formData.append('video', $('#video')[0].files[0]);
        }
        
        $.ajax({
            type: "POST",
            url: "updateEquipamento",
            processData: false,  
            contentType: false,  
            data: formData,
            success: function () {
                $("#saveBtn").hide();
                $(".option").toggle();
                $("#editBtn").show();
                location.reload();
            },
            error: function () {
                alert("Error updating equipamento. Please try again.");
            }
        });
    });
});


</script>

</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<link rel="stylesheet" type="text/css" href="style.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <style>
        body {
            display: flex;
            align-items: flex-start; /* Align items to the top */
            justify-content: space-between;
            margin: 20px;
        }
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
        #profileInfo {
		    margin-left: 400px;
		}
        #profilePhoto {
            width: 30%;
            position: sticky; /* Make the div sticky */
            top: 0; /* Stick to the top */
        }

        img {
            width: 100%;
            height: auto;
            max-width: 400px;
            max-height: 400px;
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
String nomeClube = null;
JavaConnector jc = new JavaConnector();
if(session.getAttribute("nifClube")!=null){
	nifClube = (int)session.getAttribute("nifClube");
	nomeClube = jc.getNomeClubeFromNif(nifClube);
}

Utilizador pt_info = jc.carregarPersonalTrainer(email);
session.setAttribute("nifPT", Integer.valueOf(pt_info.getNif()));



%>



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



<div id="profileInfo">
		<h2><%= "Clube atual: " + nomeClube%></h2>
        <h1>Página Pessoal <%= pt_info.getNome() %><input type="hidden" id="nif" value="<%= nifPT%>"></h1>

        <h3>Telemovel: <input type="text" id="telemovel" class="editable" value="<%= pt_info.getTelemovel() %>" disabled></h3>
        <h3>Email: <input type="text" id="email" class="editable" value="<%= pt_info.getEmail() %>" disabled></h3>
		<div id="profilePhoto">
	<img src="<%= jc.getProfileFoto(Integer.parseInt(pt_info.getNif())) %>" alt="Profile Photo" class="resizable-image">
</div>
</div>

<% jc.DBdisconnect(); %>
<script>
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


</script>

</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="connector.JavaConnector" %>
<%@ page import="connector.Utilizador" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
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
        
      	form{
      	margin-top:100px;
      		margin-left: 800px;
      	}
      
	  	.search-container {
            text-align: center;
            max-width: 400px; /* Set a maximum width for the container */
            width: 100%;
            padding: 20px; /* Add padding for better spacing */
            
        }

        .search-bar {
            width: 100%;
            padding: 10px;
            font-size: 16px;
            border: 1px solid #ccc;
            border-radius: 5px;
            box-sizing: border-box;
            margin-bottom: 10px; /* Add some space between input and button */
        }

        .search-button {
            width: 100%;
            padding: 10px;
            font-size: 16px;
            background-color: #4CAF50;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }
    </style>
<title>FitnessUP</title>
   	<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
    <link rel="stylesheet" href="//code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css">
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


	<h2>Clientes</h2>
    <form id="searchForm" method="get" onsubmit="return redirectToResults()">
        <div class="search-container">
        <input type="text" class="search-bar" id="searchInput" placeholder="Procurar Cliente...">
        <button class="search-button">Procurar</button>
   		</div>
        
    </form>

   <script>
    $(function() {
        $(".search-bar").autocomplete({
            source: function(request, response) {
                $.ajax({
                    url: "AutoSearchClientes",
                    method: "GET",
                    data: {
                        term: request.term
                    },
                    success: function(data) {
                        var resultsArray = data.split('\n');
                        response(resultsArray);
                    }
                });
            }
        });
    });
    
    
    function redirectToResults() {
        var searchTerm = document.getElementById("searchInput").value;
        var redirectUrl = "cliente.jsp?nomeCliente=" + encodeURIComponent(searchTerm);
        window.open(redirectUrl, '_blank');
        return false; 
    }
    
    $(document).ready(function () {
        
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
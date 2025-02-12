<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<meta charset="UTF-8">
<title>FitnessUP</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }

        form {
            max-width: 600px;
            margin: 20px auto;
        }

        label {
            display: block;
            margin-bottom: 8px;
        }

        input, textarea {
            width: 100%;
            padding: 8px;
            margin-bottom: 16px;
            box-sizing: border-box;
        }

        button {
            background-color: #4CAF50;
            color: white;
            padding: 10px 15px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>

<form action="AdicionarEquipamentoServlet" method="post" enctype="multipart/form-data">
    <label for="nomeEquipamento">Nome do Equipamento:</label>
    <input type="text" name="nomeEquipamento" required>

    <label for="fotoEquipamento">Inserir Foto:</label>
    <input type="file" name="fotoEquipamento" accept="image/*">

    <label for="videoFile">Inserir Vídeo:</label>
    <input type="file"  name="videoFile" accept="video/*">

    <input type="submit" value="Submeter" />
</form>


<script>

</script>
</body>
</html>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="stylesheet" type="text/css" href="style_login.css">
<title>FitnessUP</title>
</head>
<body>
<h1>FitnessUP</h1>
<form action="processLogin" method="post">
			<table style="with: 50%">
				<tr>
					<td>Email</td>
					<td><input type="text" name="email" /></td>
				</tr>
					<tr>
					<td>Password</td>
					<td><input type="password" name="password" /></td>
				</tr>
			</table>
			
			<input type="submit" value="Entrar" />
</form>
</body>
</html>
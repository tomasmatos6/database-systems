package connector;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

public class JavaConnector {
	
	private String jdbcUrl = "jdbc:mysql://localhost:3306/SBD";
    private String username = "root";
    private String password = "Root16!!"; 
	public Connection connection;
	
	public JavaConnector() {
		DBconnect();
	}
	
	public void DBconnect() {		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			connection = DriverManager.getConnection(jdbcUrl, username, password);
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
	}
	
	public void DBdisconnect() {
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("FAILED TO CLOSE CONNECTION...");
			e.printStackTrace();
		}
	}
	
	// Method to begin a transaction
    public void beginTransaction() {
        try {
            if (connection != null) {
                connection.setAutoCommit(false);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    // Method to commit a transaction
    public void commitTransaction() {
        try {
            if (connection != null) {
                connection.commit();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    // Method to rollback a transaction
    public void rollbackTransaction() {
        try {
            if (connection != null) {
                connection.rollback();
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }
	
	public int fazerLogin(String email, String password) {
		
		int permissoes = -1;
		
		try {
			//create statement object
			Statement stm = connection.createStatement();
			//execute SQL
			ResultSet resultSet = stm.executeQuery("SELECT tipo FROM utilizadores where email = '" + email + "' and password = '" + password + "';");
			//process result
			while(resultSet.next()) {
				//caso exista
				permissoes = resultSet.getInt("tipo");		
			}
		} catch (SQLException e) {	
			e.printStackTrace();
		}
		return permissoes;
	}
	
	//retorna nifClube onde gestor trabalha
	public String carregarGestor(String email) {
		String nifClube = null;
		String query = "SELECT nifclube FROM gerente where email = '" + email + "'";		
		try (Statement statement = connection.createStatement();
	             ResultSet resultSet = statement.executeQuery(query)) {

	            while (resultSet.next()) {
	                nifClube = resultSet.getString("nifclube");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
		
		return nifClube;
	}
	
	public Utilizador carregarPersonalTrainer(String email) {
		Utilizador utilizador = null;
		
		try {
			Statement stm = connection.createStatement();
			ResultSet resultSet = stm.executeQuery("SELECT * FROM personaltrainer where email = '" + email + "';");
		
			while(resultSet.next()) {
				
				//criar objeto temporario
				String nif = resultSet.getString("nif");
				String nome = resultSet.getString("nome");
				String telemovel = resultSet.getString("telemovel");
				List<String> clubes = getNifClubesForNifPt(nif);
				Blob foto = resultSet.getBlob("foto");
				utilizador = new Utilizador(nif, nome, email, telemovel, clubes, foto);
			}
		}	catch (SQLException e) {
				e.printStackTrace();
		}
			
		return utilizador;
	}
	
	
	
	//criar lista de patologias dentro
	public Utilizador carregarPerfil(String email) {
		
		Utilizador utilizador = null;
		List<Patologia> patologiasList = new ArrayList<>();
		
		try {
			Statement stm = connection.createStatement();
			
			ResultSet resultSet = stm.executeQuery("SELECT * FROM cliente where email = '" + email + "';");
			while(resultSet.next()) {
				
				//criar objeto temporario
				String nif = resultSet.getString("nif");
				String nome = resultSet.getString("nome");
				String dataNascimento = resultSet.getString("dataNascimento");
				String telemovel = resultSet.getString("telemovel");
				String objetivos = resultSet.getString("objetivos");
				utilizador = new Utilizador(nif, nome, dataNascimento, email, telemovel, objetivos, null);
			}
			
			
			if(utilizador!=null) {
				
				try {
				// Query for patologias using the view
		        ResultSet patologiasResultSet = stm.executeQuery("SELECT * FROM vClientePatologias WHERE NomeCliente = '" + utilizador.getNome() + "';");
	        	
		        while (patologiasResultSet.next()) {
		        		
			        String nomePatologia = patologiasResultSet.getString("NomePatologia");
			        String dataInicio = patologiasResultSet.getString("DataDeInicio");
			        String dataFim = patologiasResultSet.getString("DataDeFim");
			        Patologia patologia = new Patologia(nomePatologia, dataInicio, dataFim);
			        patologiasList.add(patologia);
		        }
		        
		        	// Set the patologias list in the Cliente object
			    utilizador.setPatologias(patologiasList);
		        
		       
				
				} catch (SQLException e) {
					e.printStackTrace();
				}
				
				
			}else {
				
				utilizador = new Utilizador(null, "gestor", null, email, null, null, null);
			}
			
		}catch (SQLException e) {
			e.printStackTrace();
		}
		
		return utilizador;
	
	}
	
	public List<Integer> getNifClientesForAtividade(int idAtividade) {
        List<Integer> nifClientes = new ArrayList<>();

        String sql = "SELECT nifCliente FROM atividade_cliente WHERE idAtividade = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, idAtividade);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int nifCliente = resultSet.getInt("nifCliente");
                    nifClientes.add(nifCliente);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception based on your application's needs
        }
        return nifClientes;
    }
	
	public String getNomeClienteByNif(int nifCliente) {
		String nomeCliente = null;
		String sql = "SELECT nome FROM cliente WHERE nif = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, nifCliente);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                	nomeCliente = resultSet.getString("nome");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception based on your application's needs
        }
        return nomeCliente;
	}
	
	public ResultSet getClienteByNif(int nifCliente) {
		
		Statement statement;
		ResultSet resultSet = null;	
		try {
			statement = connection.createStatement();
			String sql = "SELECT nome, email, telemovel, EscalaoDescricao FROM vcliente where nif = " + nifCliente + " ;";
		    resultSet = statement.executeQuery(sql);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}		
		return resultSet;	
	}
	
	public String getNomeByEmail(String nome) {
		String email = null;
			
            String sql = "SELECT email FROM cliente WHERE nome = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                // Set the parameter in the prepared statement
                statement.setString(1, nome);

                // Execute the query
                try (ResultSet resultSet = statement.executeQuery()) {
                    // Check if there is a result
                    if (resultSet.next()) {
                        // Retrieve the email from the result set
                        email = resultSet.getString("email");
                    }
                }
          
            	} catch (SQLException e) {
            e.printStackTrace();
        }
		
		return email;
	}
	
	public int numeroPTS(int nifClube) {
		int pts = 0;

        try {
            String query = "SELECT COUNT(*) AS pts FROM pt_clube WHERE NIFclube = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, nifClube);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                    	pts = resultSet.getInt("pts");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }

        return pts;
	}
	
	public int numeroClientes(int nifClube) {

        int clientes = 0;

        try {
            String query = "SELECT COUNT(*) AS rowCount FROM cliente WHERE NIFclube = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, nifClube);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        clientes = resultSet.getInt("rowCount");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }

        return clientes;
	}
	
	public Map<String, Integer> countEscalaoByClube(String nomeClube) {
        Map<String, Integer> escalaoCounts = new HashMap<>();

        try {
            String query = "SELECT EscalaoDescricao FROM vCliente WHERE NomeClube = ?";
            
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, nomeClube);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        String escalaoDescricao = resultSet.getString("EscalaoDescricao");

                        // Update the count in the HashMap
                        escalaoCounts.put(escalaoDescricao, escalaoCounts.getOrDefault(escalaoDescricao, 0) + 1);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }

        return escalaoCounts;
    }
	
	public int getLastId(String nif) {
		ResultSet resultSet = null;
		int totalHorarios = -1;
		String sqlQuery = "SELECT COUNT(*) AS totalHorarios\r\n"
				+ "FROM Horario_Clube hc\r\n"
				+ "JOIN Horarios h ON hc.idHorario = h.id\r\n"
				+ "WHERE hc.NIFclube = ?;";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setString(1, nif);
            resultSet = preparedStatement.executeQuery(sqlQuery);
            if (resultSet.next()) {
                totalHorarios = resultSet.getInt("totalHorarios");
            }
        } catch (SQLException e) {
        	e.printStackTrace();
        }
		return totalHorarios;
	}
	
	public ResultSet getHorarios(int nifClube) {
		
		Statement statement;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
		    String sqlQuery = "SELECT 	horario_clube.diaSemana as Dia_da_Semana,\r\n"
		    		+ "		horarios.horaAbertura as Abertura,\r\n"
		    		+ "		horarios.horaFecho as Fecho,\r\n"
		    		+ "     horarios.id as id\r\n"
		    		+ "FROM horarios\r\n"
		    		+ "INNER JOIN horario_clube ON horarios.id = horario_clube.idHorario\r\n"
		    		+ "WHERE horario_clube.NIFclube = '" + nifClube +"';";
		    resultSet = statement.executeQuery(sqlQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}

		return resultSet;	
	}
	
	public void createHorarioClube(int id, int nif, String diaSemana) throws SQLException {
		String sqlQuery = "INSERT INTO Horario_Clube (idHorario, NIFclube, diaSemana)"
						+ "VALUES (?, ?, ?);";
		PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
		preparedStatement.setInt(1, id);
		preparedStatement.setInt(2, nif);
		preparedStatement.setString(3, diaSemana);
		preparedStatement.executeUpdate();
}
	// NIFclube, idHorario, diaSemana, idHorario2
	public void updateHorarioClube(int idClubeHorario, int idHorario, String diaSemana) {
		try (PreparedStatement statement = connection.prepareStatement(
                "UPDATE horario_clube SET idHorario = ?, diaSemana = ? WHERE id = ?")) {
            statement.setInt(1, idHorario);
            statement.setString(2, diaSemana);
            statement.setInt(3, idClubeHorario);

            // Execute the update statement
            statement.executeUpdate();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//java e para gordossssssss~
	
	
	public void setHorario(String abertura, String fecho, String diaSemana, int idHorario, int nifClube, int idClubeHorario) throws SQLException {
		int id = getHorario(abertura, fecho);
		if(idHorario == 0) {
			createHorarioClube(id, nifClube, diaSemana);
		} else {
			updateHorarioClube(idClubeHorario, id, diaSemana);
		}
	}
	
	//puddi chilli is gordo
	
	public int createHorario(String abertura, String fecho) {
		String sqlQuery = "INSERT INTO horarios(horaAbertura, horaFecho) VALUES (?, ?)";
		int idHorario = 0;
		try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
			preparedStatement.setString(1, abertura);
			preparedStatement.setString(2, fecho);
			
			int rowsAffected = preparedStatement.executeUpdate();

		    // Check if insertion was successful
		    if (rowsAffected == 1) {
		        // Retrieve the generated id
		        ResultSet generatedId = preparedStatement.getGeneratedKeys();
		        if (generatedId.next()) 
		            idHorario = generatedId.getInt(1);
		    } 
		} catch (SQLException e) {
        	e.printStackTrace();
        }
		return idHorario;
	}
	
	public int getIdClubeHorario(int idHorario, int nifClube, String diaSemana) {
		int id = -1;  // Default value if not found

        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT id FROM horario_clube WHERE idHorario = ? AND NIFclube = ? AND diaSemana = ?")) {
            statement.setInt(1, idHorario);
            statement.setInt(2, nifClube);
            statement.setString(3, diaSemana);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    // Retrieve the id from the result set
                    id = resultSet.getInt("id");
                }
            }
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        return id;
	}
	
	public void updateClientePatologias(String nif, List<Patologia> patologias) {
		ResultSet resultSet = null;
		String sql = "SELECT P.nome AS Patologia\r\n"
				+ "FROM Cliente_Patologias CP\r\n"
				+ "JOIN Patologias P ON CP.idPatologia = P.id\r\n"
				+ "WHERE P.nome = ?\r\n"
				+ "AND CP.dataInicio = ?\r\n";
		for(Patologia patologia : patologias) {
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, patologia.getNome());
				preparedStatement.setString(2, patologia.getDataInicio());
				resultSet = preparedStatement.executeQuery();
				boolean next = resultSet.next();
				if(next) {
					System.out.println("CHAMAR UPDATE");
					updatePatologia(nif, patologia.getNome(), patologia.getDataInicio(), patologia.getDataFim());
				}
				else {
					System.out.println("CHAMAR CREATE");
					createPatologia(nif, patologia.getNome(), patologia.getDataInicio(), patologia.getDataFim());
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void createPatologia(String nif, String nome, String dataInicio, String dataFim) {
		String sql;
		System.out.println(dataFim);
		if(!dataFim.isEmpty()) {
			System.out.println("nao é null");
			sql = "INSERT INTO Cliente_Patologias (NIFcliente, idPatologia, dataInicio, dataFim) VALUES (?, (SELECT id FROM Patologias WHERE nome = ?), ?,?);";
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, nif);
				preparedStatement.setString(2, nome);
				preparedStatement.setString(3, dataInicio);
				preparedStatement.setString(4, dataFim);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			sql = "INSERT INTO Cliente_Patologias (NIFcliente, idPatologia, dataInicio) VALUES (?, (SELECT id FROM Patologias WHERE nome = ?), ?);";
			try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
				preparedStatement.setString(1, nif);
				preparedStatement.setString(2, nome);
				preparedStatement.setString(3, dataInicio);
				preparedStatement.executeUpdate();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public void updatePatologia(String nif, String nome, String dataInicio, String dataFim) {
		String sql = "UPDATE Cliente_Patologias\r\n"
				+ "SET dataFim = ?\r\n"
				+ "WHERE NIFcliente = ?\r\n"
				+ "  AND idPatologia = (SELECT id FROM Patologias WHERE nome = ?)\r\n"
				+ "  AND dataInicio = ?;";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			if(dataFim.equals("null") || dataFim == null || dataFim.equals("")) {
				preparedStatement.setNull(1, java.sql.Types.DATE);
			}
			else {
				preparedStatement.setString(1, dataFim);
			}
			preparedStatement.setString(2, nif);
			preparedStatement.setString(3, nome);
			preparedStatement.setString(4, dataInicio);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateCliente(String nif, String email, String telemovel, String objetivos, List<Patologia> patologias) {
	    String selectClienteSql = "SELECT email FROM Cliente WHERE NIF = ?;";
	    String updateClienteSql = "UPDATE Cliente "
	                            + "SET "
	                            + "  email = ?, "
	                            + "  telemovel = ?, "
	                            + "  objetivos = ? "
	                            + "WHERE NIF = ?;";

	    String selectUtilizadoresSql = "SELECT id FROM utilizadores WHERE email = ?;";
	    String updateUtilizadoresSql = "UPDATE utilizadores SET email = ? WHERE id = ?;";

	    String existingEmail = null;
	    int utilizadorId = -1;

	    try {
	        // Perform SELECT to get the existing email
	        try (PreparedStatement selectClienteStatement = connection.prepareStatement(selectClienteSql)) {
	            selectClienteStatement.setString(1, nif);
	            ResultSet resultSet = selectClienteStatement.executeQuery();
	            if (resultSet.next()) {
	                existingEmail = resultSet.getString("email");
	            }
	        }

	        // Perform UPDATE on Cliente table
	        try (PreparedStatement updateClienteStatement = connection.prepareStatement(updateClienteSql)) {
	            updateClienteStatement.setString(1, email);
	            updateClienteStatement.setString(2, telemovel);
	            updateClienteStatement.setString(3, objetivos);
	            updateClienteStatement.setString(4, nif);
	            updateClienteStatement.executeUpdate();
	        }

	        // Perform SELECT on utilizadores table to get the id
	        try (PreparedStatement selectUtilizadoresStatement = connection.prepareStatement(selectUtilizadoresSql)) {
	            selectUtilizadoresStatement.setString(1, existingEmail);
	            ResultSet resultSet = selectUtilizadoresStatement.executeQuery();
	            if (resultSet.next()) {
	                utilizadorId = resultSet.getInt("id");
	            }
	        }

	        // Perform UPDATE on utilizadores table
	        try (PreparedStatement updateUtilizadoresStatement = connection.prepareStatement(updateUtilizadoresSql)) {
	            updateUtilizadoresStatement.setString(1, email);
	            updateUtilizadoresStatement.setInt(2, utilizadorId);
	            updateUtilizadoresStatement.executeUpdate();
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    updateClientePatologias(nif, patologias);
	}
	
	public ResultSet getEquipamentos(int nif) {
		Statement statement;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
		    String sqlQuery = "SELECT * FROM equipamento WHERE NIFClube = " + nif + "";
		    resultSet = statement.executeQuery(sqlQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return resultSet;	
	}
	
	
	public ResultSet getLessUsedEquipamentos(int nifClube) {
		
		Statement statement;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			String sqlQuery = "SELECT e.Nome, ase.idEquipamento, COUNT(*) as occurrences " +
                    "FROM Atividade_Sala_Equipamento ase " +
                    "JOIN Atividade a ON ase.idAtividade = a.id " +
                    "JOIN Equipamento e ON ase.idEquipamento = e.id " +
                    "WHERE a.Data >= DATE_SUB(NOW(), INTERVAL 3 MONTH) " +
                    "  AND e.NIFclube = " + nifClube + " " +
                    "GROUP BY ase.idEquipamento " +
                    "ORDER BY occurrences " +
                    "LIMIT 5";
		    resultSet = statement.executeQuery(sqlQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return resultSet;	
        
	}
	
	public void insertFoto(String nome, int id, Blob foto) {
		System.out.println(nome + id+ foto);
		String insertFoto = "INSERT INTO foto (idEquipamento, ficheiro, descricao)"
				+ " VALUES (?,?,?)";
		try (PreparedStatement preparedStatementFoto = connection.prepareStatement(insertFoto)) {
			preparedStatementFoto.setInt(1, id);
			preparedStatementFoto.setBlob(2, foto);
			preparedStatementFoto.setString(3, nome);
			preparedStatementFoto.executeUpdate(); 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
	}
	
	public void insertVideo(String nome, int id, Blob video) {
		String insertVideo = "INSERT INTO video (idEquipamento, ficheiro, descricao)"
				+ " VALUES (?,?,?)";
		try (PreparedStatement preparedStatementVideo = connection.prepareStatement(insertVideo)) {
        	preparedStatementVideo.setInt(1, id);
        	preparedStatementVideo.setBlob(2, video);
        	preparedStatementVideo.setString(3, nome);
        	preparedStatementVideo.executeUpdate(); 
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void updateEstado(int id, String nomeEquipamento, int nifClube, String novoEstado) {
	    String sqlUpdate = "UPDATE equipamento SET estado = ? WHERE id = ? AND nome = ? AND NIFclube = ?";

	    try (PreparedStatement statement = connection.prepareStatement(sqlUpdate)) {
	        statement.setString(1, novoEstado);
	        statement.setInt(2, id);
	        statement.setString(3, nomeEquipamento);
	        statement.setInt(4, nifClube);

	        int rowsUpdated = statement.executeUpdate();

	        if (rowsUpdated > 0) {
	            System.out.println("Estado mudado com sucesso");
	        } else {
	            System.out.println("No rows updated");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public ResultSet getSalasSemanal(int nif) {
		Statement statement;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			//DISTINCT para nao repetir mesmas atividades que usem varios equipamentos diferentes ao mesmo tempo
			String sqlQuery = "SELECT DISTINCT Sala.lotacao, Atividade.data, Horarios.horas, Atividade.nome "
	                + "FROM Atividade_Sala_Equipamento "
	                + "JOIN Atividade ON Atividade_Sala_Equipamento.idAtividade = Atividade.id "
	                + "JOIN Sala ON Atividade_Sala_Equipamento.idSala = Sala.id "
	                + "JOIN Horarios ON Atividade.idHorario = Horarios.id "
	                + "WHERE Sala.NIFclube = " + nif + " "
	                + "AND YEARWEEK(Atividade.data) = YEARWEEK(NOW())";
		    resultSet = statement.executeQuery(sqlQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return resultSet;	
	}
	
	public ResultSet getSalas(int nifClube) {
		
		Statement statement;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			String sqlQuery = "SELECT id as idSala, lotacao FROM sala where nifclube=" + nifClube +" ";
		    resultSet = statement.executeQuery(sqlQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return resultSet;
		
	}
	
	public void createSala(int nifClube, int lotacao) {
		
		String sqlQuery = "INSERT INTO sala(lotacao, nifclube, estado) VALUES (?, ? , ?)";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {	
			preparedStatement.setInt(1, lotacao);
			preparedStatement.setInt(2, nifClube);
			preparedStatement.setString(3, "livre");
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
        	
        	e.printStackTrace();
        }
		
	}
	
	public void deleteSala(int idSala) {
		
		String deleteSalaAtividade = "delete from atividade_sala_equipamento where idSala = ? ";
		String deleteSala = "delete from sala where id = ? ";
		
		try {
			connection.setAutoCommit(false);
			
	        // Delete from recomendacao
	        try (PreparedStatement preparedStatementRecomendacao = connection.prepareStatement(deleteSalaAtividade)) {
	            preparedStatementRecomendacao.setInt(1, idSala);
	            preparedStatementRecomendacao.addBatch();
	            preparedStatementRecomendacao.executeBatch();
	        }

	        // Delete from cliente_patologias
	        try (PreparedStatement preparedStatementClientePatologias = connection.prepareStatement(deleteSala)) {
	            preparedStatementClientePatologias.setInt(1, idSala);
	            preparedStatementClientePatologias.addBatch();
	            preparedStatementClientePatologias.executeBatch();
	        }

	        connection.commit(); 
	    } catch (SQLException e) {
	        try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
	        e.printStackTrace();
			}
		
	    }
	}
	
	public List<Integer> getAtividadesByData(int nifClube, String data, String horaInicio, String horaFim) {
		Statement statement;
		ResultSet resultSet = null;
		List<Integer> ids = new ArrayList<>();
		try {
			statement = connection.createStatement();
			String sqlQuery = "SELECT a.*\r\n"
					+ "FROM atividade a\r\n"
					+ "JOIN horarios h ON a.idHorario = h.id\r\n"
					+ "JOIN personaltrainer pt ON a.NIFpt = pt.NIF\r\n"
					+ "JOIN pt_clube pc ON pt.NIF = pc.NIFpt\r\n"
					+ "WHERE a.data = '" + data + "'"
					+ "  AND h.horaAbertura = '" + horaInicio + "'"
					+ "  AND h.horaFecho = '" + horaFim + "'"
					+ "  AND pc.NIFclube = " + nifClube + ";";
			resultSet = statement.executeQuery(sqlQuery);
			
			while(resultSet.next()) {
				ids.add(resultSet.getInt("id"));
			}
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return ids;
	}
	
	public ResultSet getEquipamento_Sala(int id) {
		Statement statement;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			String sqlQuery = "SELECT\r\n"
					+ "    ase.idSala,\r\n"
					+ "    ase.idEquipamento,\r\n"
					+ "    e.nome AS nomeEquipamento\r\n"
					+ "FROM\r\n"
					+ "    atividade_sala_equipamento ase\r\n"
					+ "JOIN\r\n"
					+ "    equipamento e ON ase.idEquipamento = e.id\r\n"
					+ "WHERE\r\n"
					+ "    ase.idAtividade = " + id + ";";
			resultSet = statement.executeQuery(sqlQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}
	
	public int getHorario(String horaInicio, String horaFim) {
		int id = 0;
		String sqlQuery = "SELECT id\r\n"
				+ "FROM horarios\r\n"
				+ "WHERE horaAbertura = ? AND horaFecho = ?;";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {	
			preparedStatement.setString(1, horaInicio);
			preparedStatement.setString(2, horaFim);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    id = resultSet.getInt("id");
                } else {
                	System.out.println("CRIAR HORARIO");
                	id = createHorario(horaInicio, horaFim);
                }
            }
		} catch (SQLException e) {
        	e.printStackTrace();
        }
		
		return id;
	}	
	
	public int addAtividade(String nome, int nif, String data, int idHorario, String tipo, int vagas) {
		String sqlQuery = "INSERT INTO atividade (nome, NIFpt, data, idHorario, tipo, numeroInscricoes, vagas, estado)"
				+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
		int idAtividade = 0;
		try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {	
			preparedStatement.setString(1, nome);
			preparedStatement.setInt(2, nif);
			preparedStatement.setString(3, data);
			preparedStatement.setInt(4, idHorario);
			preparedStatement.setString(5, tipo);
			preparedStatement.setInt(6, 0);
			preparedStatement.setInt(7, vagas);
			preparedStatement.setString(8, "não confirmada");
			// Execute query
		    int rowsAffected = preparedStatement.executeUpdate();

		    // Check if insertion was successful
		    if (rowsAffected == 1) {
		        // Retrieve the generated id
		        ResultSet generatedId = preparedStatement.getGeneratedKeys();
		        if (generatedId.next()) 
		            idAtividade = generatedId.getInt(1);
		    } 
		} catch (SQLException e) {
        	e.printStackTrace();
        }
		return idAtividade;
	}
	
	public void addASE(int idAtividade, int idSala, int idEquipamento) throws SQLException {
		String sqlQuery = "INSERT INTO Atividade_Sala_Equipamento (idAtividade, idSala, idEquipamento) "
				+ "VALUES (?, ?, ?)";
		PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
		preparedStatement.setInt(1, idAtividade);
		preparedStatement.setInt(2, idSala);
		preparedStatement.setInt(3, idEquipamento);
		preparedStatement.executeUpdate();
	}
	
	public void addASE(int idAtividade, int idSala) throws SQLException {
		String sqlQuery = "INSERT INTO Atividade_Sala_Equipamento (idAtividade, idSala) "
				+ "VALUES (?, ?)";
		PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
		preparedStatement.setInt(1, idAtividade);
		preparedStatement.setInt(2, idSala);
		preparedStatement.executeUpdate();
	}
	
	public void deleteUtilizadorByNIF(String nif) {
	    String sql = "DELETE u " +
	                 "FROM utilizadores u " +
	                 "JOIN cliente c ON u.email = c.email " +
	                 "WHERE c.NIF = ?";

	    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	        preparedStatement.setString(1, nif);

	        // Execute the update
	        int rowsAffected = preparedStatement.executeUpdate();

	        if (rowsAffected > 0) {
	            System.out.println("Row deleted successfully");
	        } else {
	            System.out.println("No rows deleted. Check if the NIF exists.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.err.println("Error deleting row: " + e.getMessage());
	    }
	}
	
	//funcao que retorna o nif do clube do cliente
	public int getClubFromClient(int clientNIF) {
        int clubNIF = -1; // Assuming -1 as a default value for no club found

        String sqlQuery = "SELECT NIFclube FROM Cliente WHERE NIF = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, clientNIF);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    clubNIF = resultSet.getInt("NIFclube");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clubNIF;
    }
	
	//funcao para achar clientes de um pt
	public List<Integer> getClientNIFsForPT(int ptNIF) {
        List<Integer> clientNIFs = new ArrayList<>();

        // SQL query to retrieve client NIFs for a specific PT
        String sqlQuery = "SELECT NIF FROM Cliente WHERE NIFpt = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, ptNIF);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    int nif = resultSet.getInt("NIF");
                    // Convert the NIF to a String and add it to the list
                    clientNIFs.add(nif);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientNIFs;
    }
	
	//funcao para achar nifs dos pts que trabalham num clube
	public List<Integer> getDistinctNIFs(int nifClube) {
        List<Integer> distinctNIFs = new ArrayList<>();

        String sqlQuery = "SELECT DISTINCT pt.nif " +
                "FROM personaltrainer pt " +
                "JOIN pt_clube pc ON pt.nif = pc.nifPt " +
                "WHERE pc.nifClube = " + nifClube + ";";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {

            while (resultSet.next()) {
                int nif = resultSet.getInt("nif");
                distinctNIFs.add(nif);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return distinctNIFs;
    }
	
	//funcao para dar update ao PT de um cliente
	public void updateNIFpt(int nif, int newNIFpt) {
        // SQL query to update the NIFpt column for a given NIF
        String updateQuery = "UPDATE Cliente SET NIFpt = ? WHERE NIF = ?";

        // Try-with-resources to automatically close the PreparedStatement
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            // Set parameters for the PreparedStatement
            preparedStatement.setInt(1, newNIFpt);
            preparedStatement.setInt(2, nif);

            // Execute the update query
            int rowsUpdated = preparedStatement.executeUpdate();

            // Check the number of rows updated
            if (rowsUpdated > 0) {
                System.out.println("NIFpt updated successfully for NIF: " + nif);
            } else {
                System.out.println("No records found for NIF: " + nif);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

	
	public void deletePersonalTrainer(int nif) {
		Random random = new Random();
		//achar todos os clientes desse PT
		List<Integer> clientes = getClientNIFsForPT(nif);
		//para cada cliente, achar pts do clube que esse cliente frequenta
		int nifClube;
		for (Integer cliente: clientes) {
			//nif do clube de cada cliente (caso PT trabalhe em varios clubes)
			nifClube = getClubFromClient(cliente);
			//obter PTS desse clube
			List<Integer> tempPTS = getDistinctNIFs(nifClube);
			//remover o PT atual dessa lista
			tempPTS.remove(Integer.valueOf(nif));
			//ir buscar um PT random
            int randomIndex = random.nextInt(tempPTS.size());
            int novoPT = tempPTS.get(randomIndex);
            //dar update do PT 
            updateNIFpt(cliente, novoPT);
		}

		//apagar pt atual
	    String deleteUtilizador = "DELETE FROM utilizadores WHERE email IN (SELECT email FROM personaltrainer WHERE NIF = ?);";

	    String deletePtClube = "DELETE FROM pt_clube WHERE NIFpt = ?;";
	    
	    String deleteAtividadeCliente = "DELETE FROM atividade_cliente "
	    		+ "WHERE idAtividade IN (SELECT id FROM atividade WHERE NIFpt = ?)";
	    
	    String deleteSalaAtividadeEquipamento = "DELETE ase " +
	            "FROM atividade_sala_equipamento ase " +
	            "JOIN atividade a ON ase.idAtividade = a.id " +
	            "WHERE a.nifpt = ?;";
	    
	    String deleteAtividadesPT = "DELETE FROM atividade WHERE NIFpt = ?;";
	    String deletePT = "DELETE FROM personaltrainer WHERE NIF = ?;";

	    try {
	        connection.setAutoCommit(false);

	        // Create a batch for each statement
	        try (PreparedStatement preparedStatementAtividadeCliente = connection.prepareStatement(deleteAtividadeCliente);
	        	 PreparedStatement preparedStatementPTClube = connection.prepareStatement(deletePtClube);
		         PreparedStatement preparedStatementUtilizador = connection.prepareStatement(deleteUtilizador);
	             PreparedStatement preparedStatementSalaAtividadeEquipamento = connection.prepareStatement(deleteSalaAtividadeEquipamento);
	             PreparedStatement preparedStatementAtividadesPT = connection.prepareStatement(deleteAtividadesPT);
	             PreparedStatement preparedStatementPT = connection.prepareStatement(deletePT)) {

	            // Set parameters for each statement
	        	preparedStatementPTClube.setInt(1, nif);
	        	preparedStatementAtividadeCliente.setInt(1,  nif);
	            preparedStatementUtilizador.setInt(1, nif);
	            preparedStatementSalaAtividadeEquipamento.setInt(1, nif);
	            preparedStatementAtividadesPT.setInt(1, nif);
	            preparedStatementPT.setInt(1, nif);

	            // Add each statement to its respective batch
	            preparedStatementPTClube.addBatch();
	            preparedStatementAtividadeCliente.addBatch();
	            preparedStatementUtilizador.addBatch();
	            preparedStatementSalaAtividadeEquipamento.addBatch();
	            preparedStatementAtividadesPT.addBatch();
	            preparedStatementPT.addBatch();

	            // Execute each batch
	            preparedStatementPTClube.executeBatch();
	            preparedStatementAtividadeCliente.executeBatch();
	            preparedStatementUtilizador.executeBatch();
	            preparedStatementSalaAtividadeEquipamento.executeBatch();
	            preparedStatementAtividadesPT.executeBatch();
	            preparedStatementPT.executeBatch();

	            connection.commit();
	        }
	    } catch (SQLException e) {
	        try {
	            connection.rollback();
	        } catch (SQLException e1) {
	            e1.printStackTrace();
	        }
	        e.printStackTrace();
	    } finally {
	        try {
	            connection.setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	public ResultSet getRecomendacoesCliente(String nif) {
		Statement statement;
		ResultSet resultSet = null;
		System.out.println(nif);
		   try {
			   statement = connection.createStatement();
			   String sql = "SELECT e.nome AS equipamentoNome, r.dataInicio, r.dataFim, r.descricao \r\n"
						+ "	FROM recomendacao r JOIN equipamento e ON r.idEquipamento = e.id \r\n"
						+ "    WHERE r.NIFcliente = " + nif + "\r\n"
						+ "    AND (r.dataFim > NOW() OR r.dataFim IS NULL);";
			   resultSet = statement.executeQuery(sql);
		   } catch (SQLException e) {
				
				e.printStackTrace();
			}
		   return resultSet;
	}
	
	public void deleteClient(int nif) {	
		String deleteRecomendacao = "DELETE FROM recomendacao WHERE NIFcliente = ?";
		String deleteClientePatologias = "DELETE FROM cliente_patologias WHERE NIFcliente = ?";
		String deleteCliente = "DELETE FROM cliente WHERE NIF = ?";
		
		try {
			connection.setAutoCommit(false);
			
			deleteUtilizadorByNIF(String.valueOf(nif));
			
	        // Delete from recomendacao
	        try (PreparedStatement preparedStatementRecomendacao = connection.prepareStatement(deleteRecomendacao)) {
	            preparedStatementRecomendacao.setInt(1, nif);
	            preparedStatementRecomendacao.addBatch();
	            preparedStatementRecomendacao.executeBatch();
	        }

	        // Delete from cliente_patologias
	        try (PreparedStatement preparedStatementClientePatologias = connection.prepareStatement(deleteClientePatologias)) {
	            preparedStatementClientePatologias.setInt(1, nif);
	            preparedStatementClientePatologias.addBatch();
	            preparedStatementClientePatologias.executeBatch();
	        }

	        // Delete from cliente
	        try (PreparedStatement preparedStatementCliente = connection.prepareStatement(deleteCliente)) {
	            preparedStatementCliente.setInt(1, nif);
	            preparedStatementCliente.addBatch();
	            preparedStatementCliente.executeBatch();
	        }

	        connection.commit(); 
	    } catch (SQLException e) {
	        try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
	        e.printStackTrace();
			}
		
	    }
	}
	
	public static int calculateAge(String dateOfBirth) {
		// Parse the date of birth string to LocalDate
        LocalDate dob = LocalDate.parse(dateOfBirth);
        
        // Get the current date
        LocalDate currentDate = LocalDate.now();
        
        // Calculate the period between the date of birth and the current date
        Period period = Period.between(dob, currentDate);
        
        // Get the years from the period
        int age = period.getYears();
        
        return age;
    }
	
	public int getEscalao(String dateOfBirth) {
		
		int idade = calculateAge(dateOfBirth);
		int id = -1;
		
		try  {
            String sql = "SELECT id FROM EscalaoEtario WHERE ? BETWEEN idadeMinima AND idadeMaxima";
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setInt(1, idade);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                    	id = resultSet.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately in your application
        }		
		//se -1 nao pode 
		return id;
	}
	
	public void addPersonalTrainer(String nif, String nome, String email, String telemovel, String[] clubes, Blob foto) {
		
		String sqlPT = "insert into personaltrainer(nif, nome, email, telemovel, foto) values ( ? , ? , ? , ? , ? );";
		
		String sqlUtilizadores = "INSERT INTO utilizadores (email, password, tipo) VALUES (?, ?, ?)";
		
		try {
			try (PreparedStatement ptStatement = connection.prepareStatement(sqlPT)) {
					ptStatement.setString(1, nif);
					ptStatement.setString(2, nome);
		            ptStatement.setString(3, email);
		            ptStatement.setString(4, telemovel);
		            ptStatement.setBlob(5, foto);
	
		            ptStatement.executeUpdate();
				} 
	            
			String sqlPTCLUB = "INSERT INTO pt_clube(nifclube, nifpt) VALUES (?, ?);";
			
	        try (PreparedStatement ptClubStatement = connection.prepareStatement(sqlPTCLUB)) {
	        	
	            for (String clube : clubes) {
	                ptClubStatement.setString(1, clube);
	                ptClubStatement.setString(2, nif);
	
	                ptClubStatement.executeUpdate();
	            }
	        }
	        
	        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUtilizadores)) {
	            // Set parameters based on your data
	            preparedStatement.setString(1, email); // Assuming email is the username
	            preparedStatement.setString(2, "123"); // Set a default or generate a password
	            preparedStatement.setString(3, "2"); // Set the user type

	            // Execute the second query
	            preparedStatement.executeUpdate();
	        }
	        
		}catch (SQLException e) {
			e.printStackTrace();
		}  		
	}
	
	public void addEquipamento(String nomeEquipamento, String estadoEquipamento, int nifClube, Blob foto, Blob video) {
		
		String insertEquipamentoSql = "INSERT INTO Equipamento (nome, estado, NIFclube) VALUES (?, ?, ?)";
		String insertFotoSql = "INSERT INTO Foto (idEquipamento, ficheiro, descricao) VALUES (?, ?, ?)";
		String insertVideoSql = "INSERT INTO Video (idEquipamento, ficheiro, descricao) VALUES (?, ?, ?)";
		    
		try (PreparedStatement insertEquipamentoStatement = connection.prepareStatement(insertEquipamentoSql, Statement.RETURN_GENERATED_KEYS)) {
		    // Set parameters for Equipamento
		    insertEquipamentoStatement.setString(1, nomeEquipamento);
		    insertEquipamentoStatement.setString(2, estadoEquipamento);
		    insertEquipamentoStatement.setInt(3, nifClube);

		    // Execute Equipamento query
		    int rowsAffected = insertEquipamentoStatement.executeUpdate();

		    // Check if Equipamento insertion was successful
		    if (rowsAffected == 1) {
		        // Retrieve the generated id
		        ResultSet generatedId = insertEquipamentoStatement.getGeneratedKeys();
		        if (generatedId.next()) {
		            int equipamentoId = generatedId.getInt(1);

		            // Check and insert into Foto if applicable
		            if (foto != null && foto.length() > 0) {
		                try (PreparedStatement insertFotoStatement = connection.prepareStatement(insertFotoSql)) {
		                    // Set parameters for Foto
		                    insertFotoStatement.setInt(1, equipamentoId);
		                    insertFotoStatement.setBlob(2, foto);
		                    insertFotoStatement.setString(3, nomeEquipamento);

		                    // Execute Foto query
		                    int fotoRowsAffected = insertFotoStatement.executeUpdate();

		                    // Check if Foto insertion was successful
		                    if (fotoRowsAffected == 1) {
		                        System.out.println("FOTO: Insert successful!");
		                    } else {
		                        System.out.println("FOTO: Failed to insert into Foto.");
		                    }
		                }
		            } else {
		                System.out.println("FOTO: Blob is null or empty");
		            }

		            // Check and insert into Video if applicable
		            if (video != null && video.length() > 0) {
		                try (PreparedStatement insertVideoStatement = connection.prepareStatement(insertVideoSql)) {
		                    // Set parameters for Video
		                    insertVideoStatement.setInt(1, equipamentoId);
		                    insertVideoStatement.setBlob(2, video);
		                    insertVideoStatement.setString(3, nomeEquipamento);

		                    // Execute Video query
		                    int videoRowsAffected = insertVideoStatement.executeUpdate();

		                    // Check if Video insertion was successful
		                    if (videoRowsAffected == 1) {
		                        System.out.println("VIDEO: Insert successful!");
		                    } else {
		                        System.out.println("VIDEO: Failed to insert into Video.");
		                    }
		                }
		            } else {
		                System.out.println("VIDEO: Blob is null or empty");
		            }
		        } else {
		            System.out.println("Failed to get the generated ID for Equipamento.");
		        }
		    } else {
		        System.out.println("Failed to insert into Equipamento.");
		    }
		} catch (SQLException e) {
		    e.printStackTrace();
		}
	}
	
	public void deleteEquipamento(int id) {
		
		String deleteVideo = "delete from video where idEquipamento = ? ";
		String deleteFoto = "delete from foto where idEquipamento = ? ";
		String deleteEquipamento = "delete from equipamento where id = ? ";
		
		try {
			connection.setAutoCommit(false);
			
	        try (PreparedStatement preparedStatementVideo = connection.prepareStatement(deleteVideo)) {
	        	preparedStatementVideo.setInt(1, id);
	        	preparedStatementVideo.addBatch();
	        	preparedStatementVideo.executeBatch();
	        }

	        try (PreparedStatement preparedStatementFoto = connection.prepareStatement(deleteFoto)) {
	            preparedStatementFoto.setInt(1, id);
	            preparedStatementFoto.addBatch();
	            preparedStatementFoto.executeBatch();
	        }
	        
	        try (PreparedStatement preparedStatementEquipamento = connection.prepareStatement(deleteEquipamento)) {
	        	preparedStatementEquipamento.setInt(1, id);
	        	preparedStatementEquipamento.addBatch();
	        	preparedStatementEquipamento.executeBatch();
	        }


	        connection.commit(); 
	    } catch (SQLException e) {
	        try {
				connection.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
	        e.printStackTrace();
			}
		
	    }
	}
	
	public ResultSet getRecomendacaoPT(String nifClube, String nifPT) {
		Statement statement;
		ResultSet resultSet = null;
		
		   try {
			   statement = connection.createStatement();
			   String sql = "SELECT\r\n"
						+ "  v.NomeCliente,\r\n"
						+ "  v.NomeEquipamento,\r\n"
						+ "  v.DataDeInicio,\r\n"
						+ "  v.DataDeFim,\r\n"
						+ "  v.Descricao,\r\n"
						+ "	 v.idEquipamento\r\n"
						+ "FROM\r\n"
						+ "  vRecomendacoes v\r\n"
						+ "JOIN\r\n"
						+ "  Cliente c ON v.NomeCliente = c.nome\r\n"
						+ "JOIN\r\n"
						+ "  PersonalTrainer pt ON v.NomePT = pt.nome\r\n"
						+ "WHERE\r\n"
						+ "  pt.NIF = " + nifPT + ""
						+ "  AND c.NIFclube = " + nifClube + ";";
			   resultSet = statement.executeQuery(sql);
		   } catch (SQLException e) {
				
				e.printStackTrace();
			}
		   return resultSet;
	}
	
	public void addRecomendacao(int nif, String nome, String equipamento, String dataInicio, String dataFim, String descricao) {
		String sql = "INSERT INTO recomendacao (NIFpt, NIFcliente, idEquipamento, dataInicio, dataFim, descricao)\r\n"
				+ "VALUES (?,\r\n"
				+ "    (SELECT NIF FROM cliente WHERE nome = ?),\r\n"
				+ "    ?,?,?,?);";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set parameters based on your data
            preparedStatement.setInt(1, nif);
            preparedStatement.setString(2, nome);
            preparedStatement.setString(3, equipamento);
            preparedStatement.setString(4, dataInicio);
            if(dataFim.equals("null") || dataFim == null || dataFim.equals("")) {
				preparedStatement.setNull(5, java.sql.Types.DATE);
			}
			else {
				preparedStatement.setString(5, dataFim);
			}
            preparedStatement.setString(6, descricao);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateRecomendacao(String nome, String equipamento, String dataFim, String descricao) {
		System.out.println(nome);
		System.out.println(equipamento);
		System.out.println(dataFim);
		System.out.println(descricao);
		String sql = "UPDATE recomendacao\r\n"
				+ "SET dataFim = ?, descricao = ?\r\n"
				+ "WHERE idEquipamento = ?"
				+ "        AND NIFcliente = (\r\n"
				+ "            SELECT NIF\r\n"
				+ "            FROM cliente\r\n"
				+ "            WHERE nome = ?\r\n"
				+ "        );";
		
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // Set parameters based on your data
            preparedStatement.setString(1, dataFim);
            preparedStatement.setString(2, descricao);
            preparedStatement.setString(3, equipamento);
            preparedStatement.setString(4, nome);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addCliente(String nif, String nome, String dataNascimento, String email, String telemovel, String objetivos, List<Patologia> patologias, int nifClube) {

		int nifPT = -1;
		
		if (!getDistinctNIFs(nifClube).isEmpty()) {
			Random random = new Random();
	        int randomIndex = random.nextInt(getDistinctNIFs(nifClube).size());
	        nifPT = getDistinctNIFs(nifClube).get(randomIndex);
	    }
		
	    String sql = "INSERT INTO Cliente (NIF, nome, dataNascimento, email, telemovel, objetivos, idEscalao, NIFclube, NIFpt) " +
	            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    String sqlUtilizadores = "INSERT INTO utilizadores (email, password, tipo) VALUES (?, ?, ?)";

	    try {
	        // Disable auto-commit to start the transaction
	        connection.setAutoCommit(false);

	        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	            // Set parameters based on your data
	            preparedStatement.setString(1, nif);
	            preparedStatement.setString(2, nome);
	            preparedStatement.setString(3, dataNascimento);
	            preparedStatement.setString(4, email);
	            preparedStatement.setString(5, telemovel);
	            preparedStatement.setString(6, objetivos);
	            preparedStatement.setInt(7, getEscalao(dataNascimento));
	            preparedStatement.setInt(8, nifClube);
	            preparedStatement.setInt(9, nifPT);

	            // Execute the first query
	            preparedStatement.executeUpdate();
	        }

	        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlUtilizadores)) {
	            // Set parameters based on your data
	            preparedStatement.setString(1, email); // Assuming email is the username
	            preparedStatement.setString(2, "123"); // Set a default or generate a password
	            preparedStatement.setString(3, "1"); // Set the user type

	            // Execute the second query
	            preparedStatement.executeUpdate();
	        }

	        // Commit the transaction if everything is successful
	        connection.commit();

	        // Add patologias after committing the transaction
	        addPatologias(nif, patologias);

	        System.out.println("Records inserted successfully");

	    } catch (SQLException e) {
	        e.printStackTrace();

	        try {
	            // Roll back the transaction in case of an exception
	            connection.rollback();

	        } catch (SQLException rollbackException) {
	            rollbackException.printStackTrace();
	        }
	    } finally {
	        try {
	            // Enable auto-commit after completing the transaction
	            connection.setAutoCommit(true);
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	}

	public void addPatologias(String nif, List<Patologia> patologias) {
	    // Add patologias for the client
	    if (patologias != null) {
	        for (Patologia patologia : patologias) {
	        	System.out.println(patologia.getNome());
	            addPatologia(nif, patologia.getDataInicio(), patologia.getDataFim(), patologia.getNome());
	        }
	    }
	}

	public void addPatologia(String nif, String dataInicio, String dataFim, String patologia) {
		
	    String sql = "INSERT INTO Cliente_Patologias (NIFcliente, idPatologia, dataInicio, dataFim) " +
	            "VALUES (?, (SELECT id FROM Patologias WHERE nome = ?), ?, ?)";

	    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
	        preparedStatement.setString(1, nif);
	        preparedStatement.setString(2, patologia);
	        preparedStatement.setString(3, dataInicio);
	        if(dataFim.equals("null") || dataFim == null || dataFim.equals("")) {
				preparedStatement.setNull(4, java.sql.Types.DATE);
			}
			else {
				preparedStatement.setString(4, dataFim);
			}
	        // Execute the update
	        preparedStatement.executeUpdate();

	        System.out.println("Patologia adicionada com sucesso.");
	    } catch (SQLException e) {
	        System.err.println("Erro ao adicionar patologias: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	
	public String getNifFromNomeClube(String nomeClube) {
		String nifClube = null;
		String sqlQuery = "SELECT nif FROM clube where designacaoComercial = '" + nomeClube + "';";
		
		try (Statement statement = connection.createStatement();
		         ResultSet resultSet = statement.executeQuery(sqlQuery)) {

		        while (resultSet.next()) {
		            nifClube = resultSet.getString("nif");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		
		return nifClube;
	}
	public List<String> getNifClubesForNifPt(String nifPt) {
	    List<String> nifClubes = new ArrayList<>();

	    // Your SQL query to retrieve nifclube values based on nifpt
	    String sqlQuery = "SELECT nifclube FROM pt_clube WHERE nifpt = '" + nifPt + "';";

	    try (Statement statement = connection.createStatement();
	         ResultSet resultSet = statement.executeQuery(sqlQuery)) {

	        while (resultSet.next()) {
	            String nifClube = resultSet.getString("nifclube");
	            nifClubes.add(nifClube);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return nifClubes;
	}
	
	public String NifToNome(String nif) {
		String nome = null;
		String query = "select designacaoComercial from clube where nif = '" + nif + "'";
		
		try (Statement statement = connection.createStatement();
		         ResultSet resultSet = statement.executeQuery(query)) {

		        while (resultSet.next()) {
		            nome = resultSet.getString("designacaoComercial");
		        }
		    } catch (SQLException e) {
		        e.printStackTrace();
		    }
		return nome;
	}
	
	public Utilizador carregarPT(String email) {
		ResultSet result = getPersonalTrainer(email);
		
		String nif = null;
		String nome = null;
		String resultEmail = null;
		String telemovel = null;
		List<String> nifClubes = null;
		Blob foto = null;
		Utilizador pt = null;
		
			try {
				while(result.next()) {
				 nif = result.getString("NIF");
				 nome = result.getString("Nome");
				 resultEmail = result.getString("Email");
				 telemovel = result.getString("telemovel");
				 nifClubes = getNifClubesForNifPt(nif);		
				 foto = result.getBlob("foto");
				 System.out.println(foto);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		pt = new Utilizador(nif, nome, resultEmail, telemovel, nifClubes, foto);
		return pt;
	}
	
	public int getPTNifFromCliente(int nifCliente) {
		int nifPT = -1;
		
		String sql = "SELECT NIFpt FROM cliente WHERE NIF = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, nifCliente);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                	nifPT = resultSet.getInt("NIFpt");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
		return nifPT;
	}
	
	public ResultSet getPersonalTrainer(String email) {
	    Statement statement;
	    ResultSet resultSet = null;
	    
	    String sqlQuery = "SELECT pt.nif as NIF, pt.nome as Nome, pt.email as Email, pt.telemovel, pt.foto "
	            + "FROM personaltrainer pt "
	            + "JOIN pt_clube pc ON pt.nif = pc.nifPt "
	            + "WHERE pt.email = '" + email + "'; ";
	   
	    try {
	        statement = connection.createStatement();
	        resultSet = statement.executeQuery(sqlQuery);
	        
	        }
	     catch (SQLException e) {
	        e.printStackTrace();
	    } 
	    return resultSet;
	}
	
	public ResultSet getPersonalTrainers(int nifClube) {
		Statement statement;
		ResultSet resultSet = null;
		
		String sqlQuery = "SELECT pt.nif as NIF, pt.nome as Nome, pt.email as Email, pt.telemovel\r\n"
				+ "FROM personaltrainer pt\r\n"
				+ "JOIN pt_clube pc ON pt.nif = pc.nifPt\r\n"
				+ "WHERE pc.nifClube = " + nifClube + ";";
		
		try {
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlQuery);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return resultSet;
	}
	
	public ResultSet getClientes(int nif) {
		
		Statement statement;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
		    String sqlQuery = "SELECT\r\n"
		    		+ "  C.NIF,\r\n"
		    		+ "  C.nome,\r\n"
		    		+ "  C.dataNascimento,\r\n"
		    		+ "  C.email,\r\n"
		    		+ "  C.telemovel,\r\n"
		    		+ "  EE.descricao AS EscalaoDescricao,\r\n"
		    		+ "  C.objetivos,\r\n"
		    		+ "  PT.nome AS NomePT\r\n"
		    		+ "FROM\r\n"
		    		+ "  Cliente AS C\r\n"
		    		+ "  INNER JOIN EscalaoEtario AS EE ON C.idEscalao = EE.id\r\n"
		    		+ "  INNER JOIN Clube as CB ON C.NIFclube = CB.NIF\r\n"
		    		+ "  INNER JOIN PersonalTrainer as PT ON C.NIFpt = PT.NIF\r\n"
		    		+ "  \r\n"
		    		+ "  where c.nifClube = '" + nif + "';";
		    resultSet = statement.executeQuery(sqlQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return resultSet;	
	}
	
	
	
	
	public Connection getConnection() {
		return this.connection;
	}

	public List<String> getPatologias() {
		
		List<String> patologias = new ArrayList<>();

		String sqlQuery = "SELECT nome FROM Patologias";
		
		try (PreparedStatement statement = connection.prepareStatement(sqlQuery);
				ResultSet resultSet = statement.executeQuery()) {
               	while (resultSet.next()) {
                   patologias.add(resultSet.getString("nome"));
               	}
        } catch (SQLException e) {
        	   
			e.printStackTrace();
		}
		
		return patologias;
	}
	
	public List<String> getNIFClubes() {
        List<String> clubes = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT nif FROM clube");


            while (resultSet.next()) {
                String nifClube = resultSet.getString("nif");
                clubes.add(nifClube);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clubes;
    }
	
	public List<String> getClubes() {
		
		List<String> clubes = new ArrayList<>();

		String sqlQuery = "SELECT designacaoComercial FROM clube";
		
		try (PreparedStatement statement = connection.prepareStatement(sqlQuery);
				ResultSet resultSet = statement.executeQuery()) {
               	while (resultSet.next()) {
                   clubes.add(resultSet.getString("designacaoComercial"));
               	}
        } catch (SQLException e) {
        	   
			e.printStackTrace();
		}
		
		return clubes;
	}
	
	public List<String> getClubFromPT(int ptNIF) {
		
		List<String> clubes = new ArrayList<>();

		String sqlQuery = "SELECT c.designacaoComercial as nome\r\n"
				+ "FROM pt_clube pc\r\n"
				+ "JOIN clube c ON pc.nifClube = c.nif\r\n"
				+ "WHERE pc.nifpt = " + ptNIF + ";";
		
		try (PreparedStatement statement = connection.prepareStatement(sqlQuery);
				ResultSet resultSet = statement.executeQuery()) {
               	while (resultSet.next()) {
                   clubes.add(resultSet.getString("nome"));
               	}
        } catch (SQLException e) {
        	   
			e.printStackTrace();
		}
		
		return clubes;
	}

	public int getClubeNif(String nome) {
        int nif = -1;

        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT nif FROM clube WHERE designacaoComercial = ?")) {
            
            preparedStatement.setString(1, nome);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    nif = resultSet.getInt("nif");
                }
            }
        } catch (SQLException e) {
            
            e.printStackTrace(); 
        }

        
        return nif;
    }
	
	public String getTelefone(int nifClube) {
		String telefone = null;
		  try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT telefone FROM clube WHERE nif = ?")) {	            
	            preparedStatement.setInt(1, nifClube);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                	telefone = resultSet.getString("telefone");
	                }
	            }
	        } catch (SQLException e) {	            
	            e.printStackTrace(); 
	        }
		  return telefone;
	}
	
	public void updateTelefone(int nifClube, String novoTelefone) {
		try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clube SET telefone = ? WHERE nif = ?")) {	            
            preparedStatement.setString(1, novoTelefone);
            preparedStatement.setInt(2, nifClube);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Update successful. Rows affected: " + rowsAffected);
            } else {
                System.out.println("No rows updated.");
            }
        } catch (SQLException e) {	            
            e.printStackTrace(); 
        }
	}
	
	public String getEmail(int nifClube) {
		String email = null;
		  try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT email FROM clube WHERE nif = ?")) {	            
	            preparedStatement.setInt(1, nifClube);
	            try (ResultSet resultSet = preparedStatement.executeQuery()) {
	                if (resultSet.next()) {
	                	email = resultSet.getString("email");
	                }
	            }
	        } catch (SQLException e) {	            
	            e.printStackTrace(); 
	        }
		  return email;
	}
	
	public void updateEmail(int nifClube, String novoEmail){
		try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE clube SET email = ? WHERE nif = ?")) {	            
            preparedStatement.setString(1, novoEmail);
            preparedStatement.setInt(2, nifClube);
            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Update successful. Rows affected: " + rowsAffected);
            } else {
                System.out.println("No rows updated.");
            }
        } catch (SQLException e) {	            
            e.printStackTrace(); 
        }
	}
	
	public String getProfileFoto(int nifPT) {
	    String foto = null;

	    String sqlQuery = "SELECT foto FROM personaltrainer WHERE nif = ?";

	    try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
	        statement.setInt(1, nifPT);

	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Blob blob = resultSet.getBlob("foto");

	                if (blob != null) {
	                    byte[] imageData = blob.getBytes(1, (int) blob.length());
	                    String base64Image = Base64.getEncoder().encodeToString(imageData);
	                    //String base64Image = processImage(imageData);
	                    foto = "data:image;base64," + base64Image;

	                } else {
	                    System.out.println("Blob is null");
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return foto;
	}

	//base64 encoded string to generate image + data URI for the image
	public List<String> getFotos(int nifClube, String nomeEquipamento) {
	    List<String> fotos = new ArrayList<>();

	    String sqlQuery = "SELECT ft.ficheiro " +
	            "FROM equipamento e " +
	            "JOIN foto ft ON e.id = ft.idEquipamento " +
	            "WHERE e.NIFclube = ? AND e.nome = ?";

	    try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
	        statement.setInt(1, nifClube);
	        statement.setString(2, nomeEquipamento);

	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Blob blob = resultSet.getBlob("ficheiro");

	                if (blob != null) {
	                    byte[] imageData = blob.getBytes(1, (int) blob.length());
	                    String base64Image = new String(java.util.Base64.getEncoder().encode(imageData));
	                    String foto = "data:image/png;base64," + base64Image;
	                    fotos.add(foto);
	                } else {
	                    System.out.println("Blob is null");
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return fotos;
	}

	
	public List<String> getVideos(int nifClube, String nomeEquipamento) {
	    List<String> videos = new ArrayList<>();

	    String sqlQuery = "SELECT v.ficheiro " +
	            "FROM equipamento e " +
	            "JOIN video v ON e.id = v.idEquipamento " +
	            "WHERE e.NIFclube = ? AND e.nome = ? ";

	    try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
	        statement.setInt(1, nifClube);
	        statement.setString(2, nomeEquipamento);

	        try (ResultSet resultSet = statement.executeQuery()) {
	            while (resultSet.next()) {
	                Blob blob = resultSet.getBlob("ficheiro");

	                if (blob != null) {
	                    byte[] videoData = blob.getBytes(1, (int) blob.length());
	                    String base64Video = new String(java.util.Base64.getEncoder().encode(videoData));
	                    String video = "data:video/mp4;base64," + base64Video;
	                    videos.add(video);
	                } else {
	                    System.out.println("Blob is null");
	                }
	            }
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return videos;
	}
	

	public ResultSet getClientesDePT(String nomePT) {
		Statement statement;
		ResultSet resultSet = null;	
		try {
			statement = connection.createStatement();
			String sqlQuery = "SELECT nome, dataNascimento, email, telemovel, escalaodescricao, objetivos FROM vcliente where nomept = '" + nomePT + "';";
		    resultSet = statement.executeQuery(sqlQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return resultSet;	
	}
	
	public String getNomeClubeFromNif(int nifClube) {		
		Statement statement;
		ResultSet resultSet = null;
		String designacaoComercial = null;
		
        try  {
        	statement = connection.createStatement();
            String sqlQuery = "SELECT designacaoComercial FROM clube WHERE NIF = "+nifClube+";";
            resultSet = statement.executeQuery(sqlQuery);
            if(resultSet.next()) {
            	designacaoComercial = resultSet.getString("designacaoComercial");
            }
        }catch (SQLException e) {
    			
    			e.printStackTrace();
    		}
        System.out.println("NOME CLUBE: " + designacaoComercial);
        return designacaoComercial;
    }
	
	public String getNomeClubeFromCliente(String nifCliente) {
		return getNomeClubeFromNif(getClubFromClient(Integer.parseInt(nifCliente)));
	}
	
	public String getNomePTFromNif(int nifPT) {
		Statement statement;
		ResultSet resultSet = null;
		String nomePT = null;
		
        try  {
        	statement = connection.createStatement();
            String sqlQuery = "SELECT nome FROM personaltrainer WHERE NIF = " + nifPT+ " ;";
            resultSet = statement.executeQuery(sqlQuery);
            if(resultSet.next()) {
            	nomePT = resultSet.getString("nome");
            }
        }catch (SQLException e) {
    			
    			e.printStackTrace();
    		}
        System.out.println("NOME PT: " + nomePT);
        return nomePT;
	}
	
		//retorna atividade de certo ID
		public ResultSet getAtividadeById(int idAtividade) {
		 
		 	Statement statement;
			ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				String selectQuery = "SELECT * FROM vAtividades " +
	                    "WHERE IdAtividade = " + idAtividade + " ;";
			    resultSet = statement.executeQuery(selectQuery);
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}		
			return resultSet;	
		
		}
	
	//retorna atividades INDIVIDUAIS de um certo clube e certo pt
	public ResultSet getAtividadesPTIndividuais(int nifClube, int nifPT) {
		
		String nomeClube = getNomeClubeFromNif(nifClube); 
	 	String nomePT = getNomePTFromNif(nifPT);
	 
	 	Statement statement;
		ResultSet resultSet = null;
		try {
			statement = connection.createStatement();
			String selectQuery = "SELECT * FROM vAtividades " +
                    "WHERE NomeClube = '" + nomeClube + "' " +
                    "AND NomePT = '" + nomePT + "' " +
                    "AND TipoDeAtividade = 'individual' " +
                    "AND Inscricoes = 0 " +
                    "AND DiaDaAtividade > CURDATE();";
		    resultSet = statement.executeQuery(selectQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return resultSet;	
	
	}
	
	public ResultSet getEquipamentosDeAtividade(int idAtividade) {
		
		Statement statement;
		ResultSet resultSet = null;
		
		try {
			statement = connection.createStatement();
			String sql = "SELECT e.id AS EquipamentoID, e.nome AS EquipamentoNome, e.estado AS EquipamentoEstado " +
                    "FROM atividade_sala_equipamento ase " +
                    "JOIN equipamento e ON ase.idEquipamento = e.id " +
                    "WHERE ase.idAtividade = '"+idAtividade+"' ;";
		    resultSet = statement.executeQuery(sql);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		
		return resultSet;	
	}
	
	
	public void inscreverAtividade(int idAtividade, int nifCliente) throws SQLException {
		
		System.out.println("ID ATIVIDADE JC: " + idAtividade);
        System.out.println("NIFCLIENTE JC: " + nifCliente);
		
		String sql = "INSERT INTO atividade_cliente (idAtividade, NIFcliente) VALUES (?, ?)";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, idAtividade);
        preparedStatement.setInt(2, nifCliente);
        preparedStatement.executeUpdate();
		
	}
	
	public boolean inscritoAtividade(int idAtividade, String nifCliente) {
        boolean inscrito = false;
        int nifClienteInt = Integer.parseInt(nifCliente);
        
        String sqlQuery = "SELECT *\r\n"
                + "FROM atividade_cliente\r\n"
                + "WHERE idAtividade = ? AND nifcliente = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {    
            preparedStatement.setInt(1, idAtividade);
            preparedStatement.setInt(2, nifClienteInt);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    inscrito=true;
                } 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }        
        System.out.println("INSCRITO = " + inscrito);
        return inscrito;
    }
	
	//retorna todas as atividades de um PT de um certo clube --> utilizador personal trainer
	 public ResultSet getAtividadePT(int nifClube, int nifPT) {
		 	String nomeClube = getNomeClubeFromNif(nifClube); 
		 	String nomePT = getNomePTFromNif(nifPT);
		 
		 	Statement statement;
			ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				String selectQuery = "SELECT * FROM vAtividades WHERE NomeClube = '" + nomeClube + "' AND NomePT = '" +nomePT + "' ;";
			    resultSet = statement.executeQuery(selectQuery);
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			return resultSet;	
	    }
	 
	 public ResultSet getWeeklyAtividadesByClube(String nomeClube) {
		 Statement statement;
		 ResultSet resultSet = null;
		 try {
		     statement = connection.createStatement();
		     String selectQuery = "SELECT * FROM vAtividades " +
                     "WHERE NomeClube = '" + nomeClube + "' " +
                     "AND DiaDaAtividade >= CURDATE() " +  // Activities from today onwards
                     "AND WEEK(DiaDaAtividade) = WEEK(CURDATE()) " +
                     "AND YEAR(DiaDaAtividade) = YEAR(CURDATE());";
		     resultSet = statement.executeQuery(selectQuery);
		 } catch (SQLException e) {
		     e.printStackTrace();
		 }

		 return resultSet;
	 }
	 
	
	//retorna todas as atividades de um clube --> utilizador cliente
	 public ResultSet getAtividadesByClube(String nomeClube) {
		 
		 	Statement statement;
			ResultSet resultSet = null;
			try {
				statement = connection.createStatement();
				String selectQuery = "SELECT * FROM vAtividades WHERE NomeClube = '" + nomeClube + "' AND DiaDaAtividade > CURDATE();";
			    resultSet = statement.executeQuery(selectQuery);
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			}
			
			return resultSet;
	 }
	 
	public ResultSet getAtividadesByNomePT(String nomePT) {
		Statement statement;
        ResultSet resultSet = null;
        try {
			statement = connection.createStatement();
			String sqlQuery = "SELECT " +
	                "  c.designacaoComercial AS NomeClube, " +
	                "  a.nome AS NomeAtividade, " +
	                "  a.data AS DiaDaAtividade, " +
	                "  h.horaAbertura AS HoraInicio, " +
	                "  h.horaFecho AS HoraFim, " +
	                "  a.tipo AS TipoDeAtividade, " +
	                "  a.numeroInscricoes AS Inscricoes, " +
	                "  a.vagas as Vagas, " +
	                "  s.lotacao as LotacaoSala, " +
	                "  a.estado AS Estado " +
	                "FROM " +
	                "  Atividade a " +
	                "JOIN " +
	                "  PT_clube pc ON a.NIFpt = pc.NIFpt " +
	                "JOIN " +
	                "  PersonalTrainer pt ON pc.NIFpt = pt.NIF " +
	                "JOIN " +
	                "  Horarios h ON a.idHorario = h.id " +
	                "JOIN " +
	                "  Clube c ON pc.NIFclube = c.NIF " +
	                "JOIN " +
	                "  Atividade_Sala_equipamento ase ON a.id = ase.idAtividade " +
	                "JOIN " +
	                "  Sala s on ase.idSala = s.id " +
	                "WHERE pt.nome = '" + nomePT +  "'; "; 
		    resultSet = statement.executeQuery(sqlQuery);
			
		} catch (SQLException e) {
			
			e.printStackTrace();
		}
		return resultSet;	
   }
	
	public ResultSet getAtividadesByCliente(int nifCliente) {
		Statement statement;
		ResultSet resultSet = null;
		System.out.println(nifCliente);
		try {
			String sqlQuery = "SELECT A.* \r\n"
					+ "FROM vatividades A \r\n"
					+ "JOIN atividade_cliente AC ON A.idAtividade = AC.idAtividade \r\n"
					+ "WHERE AC.NIFcliente = " + nifCliente + ";";
			statement = connection.createStatement();
			resultSet = statement.executeQuery(sqlQuery);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultSet;
	}
	
	public void desinscreverAtividade(int idAtividade, int nifCliente) {
		String query = "DELETE FROM atividade_cliente WHERE idAtividade = ? AND NIFcliente = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, idAtividade);
            preparedStatement.setInt(2, nifCliente);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Row deleted successfully.");
            } else {
                System.out.println("No matching row found for deletion.");
            }
        } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public List<String> autoSearchCliente(String term, int nifClube, int nifPT){
		List<String> clientes = new ArrayList<>();
		
		try {
            String sql = "SELECT nome, dataNascimento, email FROM cliente WHERE nome LIKE ? AND NIFclube = ? AND NIFpt = ? ";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, term + "%");
                stmt.setInt(2, nifClube);
                stmt.setInt(3, nifPT);

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String result = rs.getString("nome");
                        clientes.add(result);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return clientes;
    }
	
	
	public void updateEstadoAtividade(int idAtividade, String novoEstado) {	
		String sql = "UPDATE atividade SET estado = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, novoEstado); 
            stmt.setInt(2, idAtividade); 
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Rows affected: " + rowsAffected);
            
            connection.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	public void updatePT(String nif, String telemovel, String email) {
		String sql = "UPDATE personaltrainer SET telemovel = ?, email = ? WHERE NIF = ?";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, telemovel);
			preparedStatement.setString(2, email);
			preparedStatement.setString(3, nif);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updatePTClube(String nif, String nome) {
		String sql = "INSERT INTO pt_clube (NIFclube, NIFpt)\r\n"
				+ "VALUES (\r\n"
				+ "  (SELECT NIF FROM clube WHERE designacaoComercial = ?),?);";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, nome);
			preparedStatement.setString(2, nif);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deletePTClube(String nif) {
		String sql = "DELETE FROM pt_clube WHERE NIFpt = ?;";
		try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
			preparedStatement.setString(1, nif);
			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
	
	


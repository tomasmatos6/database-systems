package connector;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/getEquipamentoSala")
public class getEquipamentoSala extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {
    	
    	String nif = request.getParameter("nif");
		String data = request.getParameter("data");
		String horaInicio = request.getParameter("horaInicio");
		String horaFim = request.getParameter("horaFim");
		
		JavaConnector jc = new JavaConnector();
		List<Integer> idAtividade = jc.getAtividadesByData(Integer.valueOf(nif), data, horaInicio, horaFim);
		ResultSet resultSet_salas = jc.getSalas(Integer.valueOf(nif));
		ResultSet resultSet_equip = jc.getEquipamentos(Integer.valueOf(nif));
		List<Map<String, String>> equipamentos = new ArrayList<>();
		List<Map<String, String>> salas = new ArrayList<>();
		try {
			while(resultSet_salas.next()) {
				salas.add(createSala(resultSet_salas.getString("idSala")));
			}
			resultSet_salas.close();
			while(resultSet_equip.next()) {
				if(resultSet_equip.getString("estado").equals("disponível"))
					equipamentos.add(
							createEquipamento(resultSet_equip.getString("nome"), 
									resultSet_equip.getString("id")));
			}
			resultSet_equip.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Integer id : idAtividade) {
			ResultSet rs = jc.getEquipamento_Sala(id);
			try {
				if(rs.next()) {
			        String idEquipamento = rs.getString("idEquipamento");
			        String sala = rs.getString("idSala");
			        equipamentos.removeIf(e -> e.get("id").equals(idEquipamento));
			        salas.removeIf(s -> s.get("id").equals(sala));
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
		jc.DBdisconnect();
		
    	Map<String, List<Map<String, String>>> dropdownOptions = new HashMap<>();

        dropdownOptions.put("equipamento", equipamentos);
        dropdownOptions.put("sala", salas);

        String jsonResponse = convertToJson(dropdownOptions);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(jsonResponse);
    }
    
    private String convertToJson(Map<String, List<Map<String, String>>> data) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{");

        for (Map.Entry<String, List<Map<String, String>>> entry : data.entrySet()) {
            jsonBuilder.append("\"").append(entry.getKey()).append("\":").append("[");

            List<Map<String, String>> listData = entry.getValue();
            for (int i = 0; i < listData.size(); i++) {
                Map<String, String> item = listData.get(i);
                jsonBuilder.append("{");

                int j = 0;
                for (Map.Entry<String, String> subEntry : item.entrySet()) {
                    jsonBuilder
                        .append("\"").append(subEntry.getKey()).append("\":\"").append(subEntry.getValue()).append("\"");
                    if (j < item.size() - 1) {
                        jsonBuilder.append(",");
                    }
                    j++;
                }

                jsonBuilder.append("}");
                if (i < listData.size() - 1) {
                    jsonBuilder.append(",");
                }
            }

            jsonBuilder.append("],");
        }

        // Remove the trailing comma if it exists
        if (jsonBuilder.charAt(jsonBuilder.length() - 1) == ',') {
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }

        jsonBuilder.append("}");
        return jsonBuilder.toString();
    }


    private Map<String, String> createEquipamento(String name, String id) {
        Map<String, String> equipamento = new HashMap<>();
        equipamento.put("name", name);
        equipamento.put("id", id);
        return equipamento;
    }

    private Map<String, String> createSala(String name) {
        Map<String, String> sala = new HashMap<>();
        sala.put("name", name);
        sala.put("id", name); // Using the same value for name and ID
        return sala;
    }
    
}


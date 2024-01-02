package connector;

public class Patologia {

	private String nome;
	
	private String dataInicio;
	private String dataFim;
	
	public Patologia(String nome, String dataInicio, String dataFim) {
		
		this.nome = nome;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
	}

	public String getNome() {
		return nome;
	}

	public String getDataInicio() {
		return dataInicio;
	}

	public String getDataFim() {
		return dataFim;
	}
}

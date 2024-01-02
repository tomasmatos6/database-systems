package connector;

import java.io.StringReader;
import java.sql.Blob;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Utilizador {

	private String nif;
	private String nome;
	private String dataNascimento;
	private String email;
	private String telemovel;
	private String objetivos;
	private List<Patologia> patologias;
	private List<String> clubes;
	private Blob foto;
	
	//Cliente
	public Utilizador(String nif, String nome, String dataNascimento, String email, String telemovel, String objetivos, List<Patologia> patologias) {
		this.nif = nif;
		this.nome = nome;
		this.dataNascimento = dataNascimento;
		this.email = email;
		this.telemovel = telemovel;
		this.objetivos = objetivos;
		this.patologias = patologias;
	}
	
	//PersonalTrainer
	public Utilizador(String nif, String nome, String email, String telemovel, List<String> clubes, Blob foto) {
		this.nif = nif;
		this.nome = nome;
		this.email = email;
		this.telemovel = telemovel;
		this.clubes = clubes;
		this.foto = foto;
		
	}
	
	public Blob getFoto() {
		return foto;
	}
	
	public void setFoto(Blob foto) {
		this.foto = foto;
	}
	
	public String getNif() {
		return nif;
	}
	
	public List<String> getClubes(){
		return clubes;
	}
	
	public void setClubes(List<String> clubes) {
		this.clubes = clubes;
	}
	
	public String getNome() {
		return nome;
	}

	public String getDataNascimento() {
		return dataNascimento;
	}

	public String getEmail() {
		return email;
	}

	public String getTelemovel() {
		return telemovel;
	}

	public String getObjetivos() {
		return objetivos;
	}

	public List<Patologia> getPatologias(){
		return patologias;
	}
	
	public void setPatologias(List<Patologia> patologias) {
		this.patologias = patologias;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public void setDataNascimento(String dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setTelemovel(String telemovel) {
		this.telemovel = telemovel;
	}

	public void setObjetivos(String objetivos) {
		this.objetivos = objetivos;
	}
	
	
}

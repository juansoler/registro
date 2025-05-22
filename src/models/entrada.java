package models;

import java.sql.Date;

public class entrada {

	public entrada(String asunto, String fecha,  String area, boolean confidencial, boolean urgente) {
		super();
		this.asunto = asunto;
		this.fecha = fecha;
		this.area = area;
		this.confidencial = confidencial;
		this.urgente = urgente;
		
		this.observaciones = "";
		//this.soloCoronel = false;
		this.tramitado = false;
		this.canalEntrada = "";
		
	}
	
	int id;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getFile() {
		return file;
	}
	
	public String getArea() {
		return area;
	}
	
	public void setArea(String area) {
		this.area = area;
	}
	public void setFile(String file) {
		this.file = file;
	}

	
	
	public boolean isConfidencial() {
		return confidencial;
	}
	public void setConfidencial(boolean confidencial) {
		this.confidencial = confidencial;
	}
	public boolean isUrgente() {
		return urgente;
	}
	public void setUrgente(boolean urgente) {
		this.urgente = urgente;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
//	public boolean isSoloCoronel() {
//		return soloCoronel;
//	}
//	public void setSoloCoronel(boolean soloCoronel) {
//		this.soloCoronel = soloCoronel;
//	}
	public boolean isTramitado() {
		return tramitado;
	}
	public void setTramitado(boolean tramitado) {
		this.tramitado = tramitado;
	}
	
	String asunto;
	String fecha;
	String file;
	String area;
	String observaciones;
	String canalEntrada;
	String numEntrada;
	String tramitadoPor;

	public String getTramitadoPor() {
		return tramitadoPor;
	}
	public void setTramitadoPor(String tramitadoPor) {
		this.tramitadoPor = tramitadoPor;
	}
	public comentario getComentario() {
		return comentario;
	}
	public void setComentario(comentario comentario) {
		this.comentario = comentario;
	}
	public String getNumEntrada() {
		return numEntrada;
	}
	public void setNumEntrada(String numEntrada) {
		this.numEntrada = numEntrada;
	}

	boolean urgente;
	boolean confidencial;
	//boolean soloCoronel;
	boolean tramitado;
	public comentario comentario;

	

	
	
	public String getCanalEntrada() {
		return canalEntrada;
	}
	public void setCanalEntrada(String canalEntrada) {
		this.canalEntrada = canalEntrada;
	}
	
	
}

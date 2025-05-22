package models;

import java.sql.Date;

public class entradaProd {

	public entradaProd(String asunto, String fecha,  String area, boolean confidencial, boolean urgente) {
		super();
		this.asunto = asunto;
		this.fecha = fecha;
		this.area = area;
		this.confidencial = confidencial;
		this.urgente = urgente;
		this.jefe1 = 0;
		this.jefe2 = 0;
		this.jefe3 = 0;
		this.jefe4 = 0;
		this.jefe5 = 0;
		this.observaciones = "";
		this.soloCoronel = false;
		this.tramitado = false;
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

	
	public int getJefe1() {
		return jefe1;
	}
	public void setJefe1(int jefe1) {
		this.jefe1 = jefe1;
	}
	public int getJefe2() {
		return jefe2;
	}
	public void setJefe2(int jefe2) {
		this.jefe2 = jefe2;
	}
	public int getJefe3() {
		return jefe3;
	}
	public void setJefe3(int jefe3) {
		this.jefe3 = jefe3;
	}
	public int getJefe4() {
		return jefe4;
	}
	public void setJefe4(int jefe4) {
		this.jefe4 = jefe4;
	}
	public int getJefe5() {
		return jefe5;
	}
	public void setJefe5(int jefe5) {
		this.jefe5 = jefe5;
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
	
	public boolean isSoloCoronel() {
		return soloCoronel;
	}
	public void setSoloCoronel(boolean soloCoronel) {
		this.soloCoronel = soloCoronel;
	}
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
	
	

	boolean urgente;
	boolean confidencial;
	boolean soloCoronel;
	boolean tramitado;

	

	int jefe1;
	int jefe2;
	int jefe3;
	int jefe4;
	int jefe5;

	
	public boolean isJefe1() {
		// TODO Auto-generated method stub
		if (jefe1 == 1 || jefe1 == 2) {
			return true;
		}else return false;
	}
	public boolean isJefe2() {
		// TODO Auto-generated method stub
		if (jefe2 == 1 || jefe2 == 2) {
			return true;
		}else return false;
	}
	public boolean isJefe3() {
		// TODO Auto-generated method stub
		if (jefe3 == 1 || jefe3 == 2) {
			return true;
		}else return false;
	}
	public boolean isJefe4() {
		// TODO Auto-generated method stub
		if (jefe4 == 1 || jefe4 == 2) {
			return true;
		}else return false;
	}
	public boolean isJefe5() {
		// TODO Auto-generated method stub
		if (jefe5 == 1 || jefe5 == 2) {
			return true;
		}else return false;
	}
	
}

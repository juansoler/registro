package models;

import java.sql.Date;
import java.sql.Time;
import java.util.Calendar;

import service.dbProd;

public class comentarioProd {
	
	public comentarioProd(int entrada_id) {
		super();
		this.entrada_id = entrada_id;
		this.jefe1 = "";
		this.jefe2 = "";
		this.jefe3 = "";
		this.jefe4 = "";
		this.jefe5 = "";
	}
	
	public String getJefe1() {
		return jefe1;
	}

	public void setJefe1(String jefe1) {
		this.jefe1 = jefe1;
	}

	public String getJefe1fecha() {
		return jefe1fecha;
	}

	public void setJefe1fecha(String jefe1fecha) {
		this.jefe1fecha = jefe1fecha;
	}

	public String getJefe1hora() {
		return jefe1hora;
	}

	public void setJefe1hora(String jefe1hora) {
		this.jefe1hora = jefe1hora;
	}

	public String getJefe2() {
		return jefe2;
	}

	public void setJefe2(String jefe2) {
		this.jefe2 = jefe2;
	}

	public String getJefe2fecha() {
		return jefe2fecha;
	}

	public void setJefe2fecha(String jefe2fecha) {
		this.jefe2fecha = jefe2fecha;
	}

	public String getJefe2hora() {
		return jefe2hora;
	}

	public void setJefe2hora(String jefe2hora) {
		this.jefe2hora = jefe2hora;
	}

	public String getJefe3() {
		return jefe3;
	}

	public void setJefe3(String jefe3) {
		this.jefe3 = jefe3;
	}

	public String getJefe3fecha() {
		return jefe3fecha;
	}

	public void setJefe3fecha(String jefe3fecha) {
		this.jefe3fecha = jefe3fecha;
	}

	public String getJefe3hora() {
		return jefe3hora;
	}

	public void setJefe3hora(String jefe3hora) {
		this.jefe3hora = jefe3hora;
	}

	public String getJefe4() {
		return jefe4;
	}

	public void setJefe4(String jefe4) {
		this.jefe4 = jefe4;
	}

	public String getJefe4fecha() {
		return jefe4fecha;
	}

	public void setJefe4fecha(String jefe4fecha) {
		this.jefe4fecha = jefe4fecha;
	}

	public String getJefe4hora() {
		return jefe4hora;
	}

	public void setJefe4hora(String jefe4hora) {
		this.jefe4hora = jefe4hora;
	}

	public String getJefe5() {
		return jefe5;
	}

	public void setJefe5(String jefe5) {
		this.jefe5 = jefe5;
	}

	public String getJefe5fecha() {
		return jefe5fecha;
	}

	public void setJefe5fecha(String jefe5fecha) {
		this.jefe5fecha = jefe5fecha;
	}

	public String getJefe5hora() {
		return jefe5hora;
	}

	public void setJefe5hora(String jefe5hora) {
		this.jefe5hora = jefe5hora;
	}

	public int getEntrada_id() {
		return entrada_id;
	}

	public void setEntrada_id(int entrada_id) {
		this.entrada_id = entrada_id;
	}
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getTramitadoPor() {
		return tramitadoPor;
	}

	public void setTramitadoPor(String tramitadoPor) {
		this.tramitadoPor = tramitadoPor;
	}
	
	int id;
	String jefe1;
	String jefe1fecha = "";
	String jefe1hora;
	String jefe2;
	String jefe2fecha;
	String jefe2hora;
	String jefe3;
	String jefe3fecha;
	String jefe3hora;
	String jefe4;
	String jefe4fecha;
	String jefe4hora;
	String jefe5;
	String jefe5fecha;
	String jefe5hora;
	String tramitadoPor;
	

	int entrada_id;
	
	 public void save(){
	        dbProd con = new dbProd();
	        con.connect();
	        con.saveComentario(this);
	        con.close();
	    }
}


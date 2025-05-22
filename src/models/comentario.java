package models;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import gui.Login;
import service.db;

public class comentario {
	
    db con = new db();

	
	int id;
	
	
	public ArrayList<comentarioJefe> comentarios;
	

	int entrada_id;
	
	public comentario(int entrada_id) {
		super();
		this.entrada_id = entrada_id;
		this.comentarios = new ArrayList<comentarioJefe>();
	}
	
	
	public void addComentarioJefe(comentarioJefe comentarioJefe) {
		this.comentarios.add(comentarioJefe);
	}
	
	public void removeComentarioJefe(comentarioJefe comentarioJefe) {
		this.comentarios.remove(comentarioJefe);
	}
	
	public void updateComentarioJefe(String nombre, String comentario, String fecha, String hora) {
		for (comentarioJefe comentarioJefe : comentarios) {
			if (comentarioJefe.nombreJefe.equals(nombre)) {
				comentarioJefe.fecha = fecha;
				comentarioJefe.hora = hora;
				comentarioJefe.comentario = comentario;
			}
		}
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
	
	
	
	
	 public void save(){
		 	for (comentarioJefe comentarioJefe : comentarios) {
		 		 con.connect();
			     con.saveComentario(comentarioJefe);
			     con.close();
			}
	       
	    }
	 
	 public void update(){
		 	for (comentarioJefe comentarioJefe : comentarios) {
//		 		System.out.println("usuari_id comentarioJefe " + comentarioJefe.usuario_id +  "= " + Login.usuarioActivo.usuario_id);
		 		
//		 		if (comentarioJefe.usuario_id == Login.usuarioActivo.jefe_id) {
		 		 if (comentarioJefe == null) {
		 			 continue;
		 		 }
		 			System.out.println("dentro de if ");
		 			System.out.println("comentario " + comentarioJefe.getComentario());
		 			 con.connect();
				     con.actualizarComentario(comentarioJefe);
				     con.close();
				     
//		 		}
		 		
			}
	       
	    }
}


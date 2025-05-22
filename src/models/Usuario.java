package models;

import service.db;

public class Usuario {
	
	public db db;
	public int usuario_id;
	public int jefe_id;
	public int getJefe_id() {
		return jefe_id;
	}
	public void setJefe_id(int jefe_id) {
		this.jefe_id = jefe_id;
	}
	public boolean permiso;
	public boolean isPermiso() {
		return permiso;
	}
	public void setPermiso(boolean permiso) {
		this.permiso = permiso;
	}
	public String username;
	public String role;
	public int role_id;
	public int getRole_id() {
		return role_id;
	}
	public void setRole_id(int role_id) {
		this.role_id = role_id;
	}
	public String nombre_usuario;
	public int posicion;
	public int getUsuario_id() {
		return usuario_id;
	}
	public void setUsuario_id(int usuario_id) {
		this.usuario_id = usuario_id;
	}
	public String getNombre_negociado() {
		return username;
	}
	public void setNombre_negociado(String nombre_negociado) {
		this.username = nombre_negociado;
	}
	
	public String getNombre_usuario() {
		return nombre_usuario;
	}
	public void setNombre_usuario(String nombre_usuario) {
		this.nombre_usuario = nombre_usuario;
	}
	public Usuario(int usuario_id, String role, String nombre_usuario, int posicion, boolean permiso, boolean isJefe) {
		super();
		this.usuario_id = usuario_id;
		this.role = role;
		this.nombre_usuario = nombre_usuario;
		this.posicion = posicion;
		this.jefe_id = usuario_id;
		this.permiso = permiso;
		this.isJefe = isJefe;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	public boolean isJefe = false;
	
	public boolean isJefe() {
		return isJefe;
	}
	public void setJefe(boolean isJefe) {
		this.isJefe = isJefe;
	}
	
	
	
	
	
	
}

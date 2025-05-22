package models;

public class comentarioJefe {
	

	 String usuarioNombre = "";

	public String getUsuarioNombre() {
		return usuarioNombre;
	}
	public void setUsuarioNombre(String usuarioNombre) {
		this.usuarioNombre = usuarioNombre;
	}
	public comentarioJefe(int entrada_id, int usuario_id, String role, String usuario, int posicion, String fecha, String hora, String comentario, int visto) {
		// TODO Auto-generated constructor stub
		this.nombreJefe = role;
		this.usuarioNombre  = usuario;
		this.fecha = fecha;
		this.hora = hora;
		this.comentario = comentario;
		this.visto = visto;
		this.usuario_id = usuario_id;
		this.entrada_id = entrada_id;
		this.posicion = posicion;
	}
	int usuario_id;
	int entrada_id;

	public int getEntrada_id() {
		return entrada_id;
	}
	public void setEntrada_id(int entrada_id) {
		this.entrada_id = entrada_id;
	}
	public int getUsuario_id() {
		return usuario_id;
	}
	public void setUsuario_id(int usuario_id) {
		this.usuario_id = usuario_id;
	}
	String nombreJefe = "";
	String fecha = "";
	String hora = "";
	String comentario = "";
	int posicion = -1;
	int visto = 0;
	public String getNombreJefe() {
		return nombreJefe;
	}
	public void setNombreJefe(String nombreJefe) {
		this.nombreJefe = nombreJefe;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getHora() {
		return hora;
	}
	public void setHora(String hora) {
		this.hora = hora;
	}
	public String getComentario() {
		return comentario;
	}
	public void setComentario(String comentario) {
		this.comentario = comentario;
	}
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}
	public int getVisto() {
		return visto;
	}
	public void setVisto(int visto) {
		this.visto = visto;
	}

}

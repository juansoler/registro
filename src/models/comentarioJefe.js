class ComentarioJefe {
    constructor(usuarioNombre, usuario_id, entrada_id, nombreJefe, fecha, hora, comentario, posicion, visto) {
        this.usuarioNombre = usuarioNombre;
        this.usuario_id = usuario_id;
        this.entrada_id = entrada_id;
        this.nombreJefe = nombreJefe;
        this.fecha = fecha;
        this.hora = hora;
        this.comentario = comentario;
        this.posicion = posicion;
        this.visto = visto;
    }

    // Getters
    getUsuarioNombre() { return this.usuarioNombre; }
    getUsuarioId() { return this.usuario_id; }
    getEntradaId() { return this.entrada_id; }
    getNombreJefe() { return this.nombreJefe; }
    getFecha() { return this.fecha; }
    getHora() { return this.hora; }
    getComentario() { return this.comentario; }
    getPosicion() { return this.posicion; }
    isVisto() { return this.visto; } // In Java, boolean getters often start with "is"

    // Setters
    setUsuarioNombre(usuarioNombre) { this.usuarioNombre = usuarioNombre; }
    setUsuarioId(usuario_id) { this.usuario_id = usuario_id; }
    setEntradaId(entrada_id) { this.entrada_id = entrada_id; }
    setNombreJefe(nombreJefe) { this.nombreJefe = nombreJefe; }
    setFecha(fecha) { this.fecha = fecha; }
    setHora(hora) { this.hora = hora; }
    setComentario(comentario) { this.comentario = comentario; }
    setPosicion(posicion) { this.posicion = posicion; }
    setVisto(visto) { this.visto = visto; }
}

module.exports = ComentarioJefe;

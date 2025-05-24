class Comentario {
    constructor(entrada_id) {
        this.id = null; // Or generate a unique ID if needed
        this.entrada_id = entrada_id;
        this.comentarios = []; // Array of ComentarioJefe objects
    }

    // Getters
    getId() { return this.id; }
    getEntradaId() { return this.entrada_id; }
    getComentarios() { return this.comentarios; }

    // Setters
    setId(id) { this.id = id; }
    setEntradaId(entrada_id) { this.entrada_id = entrada_id; }
    setComentarios(comentarios) { this.comentarios = comentarios; } // Typically you'd add/remove rather than replace all

    // Methods
    addComentarioJefe(comentarioJefe) {
        this.comentarios.push(comentarioJefe);
    }

    removeComentarioJefe(comentarioJefe) {
        // Remove by object reference. Alternatively, use a unique ID on ComentarioJefe.
        this.comentarios = this.comentarios.filter(cj => cj !== comentarioJefe);
    }

    updateComentarioJefe(nombreJefe, comentario, fecha, hora) {
        const comentarioToUpdate = this.comentarios.find(cj => cj.getNombreJefe() === nombreJefe);
        if (comentarioToUpdate) {
            comentarioToUpdate.setComentario(comentario);
            comentarioToUpdate.setFecha(fecha);
            comentarioToUpdate.setHora(hora);
        } else {
            console.warn(`ComentarioJefe with nombreJefe "${nombreJefe}" not found.`);
        }
    }
}

module.exports = Comentario;

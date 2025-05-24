const Comentario = require('./comentario'); // Assuming Comentario class is in the same directory

class Entrada {
    constructor(asunto, fecha, area, confidencial, urgente) {
        this.id = null; // Or generate a unique ID
        this.asunto = asunto;
        this.fecha = fecha;
        this.file = null; // Initialize as null or appropriate default
        this.area = area;
        this.observaciones = "";
        this.canalEntrada = "";
        this.numEntrada = "";
        this.tramitadoPor = "";
        this.urgente = urgente;
        this.confidencial = confidencial;
        this.tramitado = false;
        this.comentario = new Comentario(this.id); // Create a Comentario object, pass entrada's id
    }

    // Getters
    getId() { return this.id; }
    getAsunto() { return this.asunto; }
    getFecha() { return this.fecha; }
    getFile() { return this.file; }
    getArea() { return this.area; }
    getObservaciones() { return this.observaciones; }
    getCanalEntrada() { return this.canalEntrada; }
    getNumEntrada() { return this.numEntrada; }
    getTramitadoPor() { return this.tramitadoPor; }
    isUrgente() { return this.urgente; }
    isConfidencial() { return this.confidencial; }
    isTramitado() { return this.tramitado; }
    getComentario() { return this.comentario; }

    // Setters
    setId(id) {
        this.id = id;
        if (this.comentario) {
            this.comentario.setEntradaId(id); // Keep Comentario's entrada_id in sync
        }
    }
    setAsunto(asunto) { this.asunto = asunto; }
    setFecha(fecha) { this.fecha = fecha; }
    setFile(file) { this.file = file; }
    setArea(area) { this.area = area; }
    setObservaciones(observaciones) { this.observaciones = observaciones; }
    setCanalEntrada(canalEntrada) { this.canalEntrada = canalEntrada; }
    setNumEntrada(numEntrada) { this.numEntrada = numEntrada; }
    setTramitadoPor(tramitadoPor) { this.tramitadoPor = tramitadoPor; }
    setUrgente(urgente) { this.urgente = urgente; }
    setConfidencial(confidencial) { this.confidencial = confidencial; }
    setTramitado(tramitado) { this.tramitado = tramitado; }
    setComentario(comentario) { this.comentario = comentario; }
}

module.exports = Entrada;

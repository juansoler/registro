class EntradaProd {
    constructor(asunto, fecha, area, confidencial, urgente) {
        this.id = null; // Or generate a unique ID
        this.asunto = asunto;
        this.fecha = fecha;
        this.file = null; // Initialize as null or appropriate default
        this.area = area;
        this.observaciones = "";
        this.urgente = urgente;
        this.confidencial = confidencial;
        this.soloCoronel = false;
        this.tramitado = false;
        this.jefe1 = 0;
        this.jefe2 = 0;
        this.jefe3 = 0;
        this.jefe4 = 0;
        this.jefe5 = 0;
    }

    // Getters
    getId() { return this.id; }
    getAsunto() { return this.asunto; }
    getFecha() { return this.fecha; }
    getFile() { return this.file; }
    getArea() { return this.area; }
    getObservaciones() { return this.observaciones; }
    isUrgente() { return this.urgente; }
    isConfidencial() { return this.confidencial; }
    isSoloCoronel() { return this.soloCoronel; }
    isTramitado() { return this.tramitado; }
    getJefe1() { return this.jefe1; }
    getJefe2() { return this.jefe2; }
    getJefe3() { return this.jefe3; }
    getJefe4() { return this.jefe4; }
    getJefe5() { return this.jefe5; }

    // Setters
    setId(id) { this.id = id; }
    setAsunto(asunto) { this.asunto = asunto; }
    setFecha(fecha) { this.fecha = fecha; }
    setFile(file) { this.file = file; }
    setArea(area) { this.area = area; }
    setObservaciones(observaciones) { this.observaciones = observaciones; }
    setUrgente(urgente) { this.urgente = urgente; }
    setConfidencial(confidencial) { this.confidencial = confidencial; }
    setSoloCoronel(soloCoronel) { this.soloCoronel = soloCoronel; }
    setTramitado(tramitado) { this.tramitado = tramitado; }
    setJefe1(jefe1) { this.jefe1 = jefe1; }
    setJefe2(jefe2) { this.jefe2 = jefe2; }
    setJefe3(jefe3) { this.jefe3 = jefe3; }
    setJefe4(jefe4) { this.jefe4 = jefe4; }
    setJefe5(jefe5) { this.jefe5 = jefe5; }

    // Methods like isJefeX()
    isJefe1() { return this.jefe1 === 1 || this.jefe1 === 2; }
    isJefe2() { return this.jefe2 === 1 || this.jefe2 === 2; }
    isJefe3() { return this.jefe3 === 1 || this.jefe3 === 2; }
    isJefe4() { return this.jefe4 === 1 || this.jefe4 === 2; }
    isJefe5() { return this.jefe5 === 1 || this.jefe5 === 2; }
}

module.exports = EntradaProd;

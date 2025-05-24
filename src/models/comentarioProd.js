class ComentarioProd {
    constructor(entrada_id) {
        this.id = null; // Or generate a unique ID
        this.entrada_id = entrada_id;
        this.jefe1 = "";
        this.jefe1fecha = "";
        this.jefe1hora = "";
        this.jefe2 = "";
        this.jefe2fecha = "";
        this.jefe2hora = "";
        this.jefe3 = "";
        this.jefe3fecha = "";
        this.jefe3hora = "";
        this.jefe4 = "";
        this.jefe4fecha = "";
        this.jefe4hora = "";
        this.jefe5 = "";
        this.jefe5fecha = "";
        this.jefe5hora = "";
        this.tramitadoPor = "";
    }

    // Getters
    getId() { return this.id; }
    getEntradaId() { return this.entrada_id; }
    getJefe1() { return this.jefe1; }
    getJefe1Fecha() { return this.jefe1fecha; }
    getJefe1Hora() { return this.jefe1hora; }
    getJefe2() { return this.jefe2; }
    getJefe2Fecha() { return this.jefe2fecha; }
    getJefe2Hora() { return this.jefe2hora; }
    getJefe3() { return this.jefe3; }
    getJefe3Fecha() { return this.jefe3fecha; }
    getJefe3Hora() { return this.jefe3hora; }
    getJefe4() { return this.jefe4; }
    getJefe4Fecha() { return this.jefe4fecha; }
    getJefe4Hora() { return this.jefe4hora; }
    getJefe5() { return this.jefe5; }
    getJefe5Fecha() { return this.jefe5fecha; }
    getJefe5Hora() { return this.jefe5hora; }
    getTramitadoPor() { return this.tramitadoPor; }

    // Setters
    setId(id) { this.id = id; }
    setEntradaId(entrada_id) { this.entrada_id = entrada_id; }
    setJefe1(jefe1) { this.jefe1 = jefe1; }
    setJefe1Fecha(jefe1fecha) { this.jefe1fecha = jefe1fecha; }
    setJefe1Hora(jefe1hora) { this.jefe1hora = jefe1hora; }
    setJefe2(jefe2) { this.jefe2 = jefe2; }
    setJefe2Fecha(jefe2fecha) { this.jefe2fecha = jefe2fecha; }
    setJefe2Hora(jefe2hora) { this.jefe2hora = jefe2hora; }
    setJefe3(jefe3) { this.jefe3 = jefe3; }
    setJefe3Fecha(jefe3fecha) { this.jefe3fecha = jefe3fecha; }
    setJefe3Hora(jefe3hora) { this.jefe3hora = jefe3hora; }
    setJefe4(jefe4) { this.jefe4 = jefe4; }
    setJefe4Fecha(jefe4fecha) { this.jefe4fecha = jefe4fecha; }
    setJefe4Hora(jefe4hora) { this.jefe4hora = jefe4hora; }
    setJefe5(jefe5) { this.jefe5 = jefe5; }
    setJefe5Fecha(jefe5fecha) { this.jefe5fecha = jefe5fecha; }
    setJefe5Hora(jefe5hora) { this.jefe5hora = jefe5hora; }
    setTramitadoPor(tramitadoPor) { this.tramitadoPor = tramitadoPor; }
}

module.exports = ComentarioProd;

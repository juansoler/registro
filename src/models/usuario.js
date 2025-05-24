class Usuario {
    constructor(usuario_id, jefe_id, permiso, username, role, role_id, nombre_usuario, posicion, isJefe) {
        this.usuario_id = usuario_id;
        this.jefe_id = jefe_id;
        this.permiso = permiso;
        this.username = username;
        this.role = role;
        this.role_id = role_id;
        this.nombre_usuario = nombre_usuario;
        this.posicion = posicion;
        this.isJefe = isJefe;
    }

    // Getters
    getUsuarioId() { return this.usuario_id; }
    getJefeId() { return this.jefe_id; }
    getPermiso() { return this.permiso; }
    getUsername() { return this.username; }
    getRole() { return this.role; }
    getRoleId() { return this.role_id; }
    getNombreUsuario() { return this.nombre_usuario; }
    getPosicion() { return this.posicion; }
    isIsJefe() { return this.isJefe; } // In Java, boolean getters often start with "is"

    // Setters
    setUsuarioId(usuario_id) { this.usuario_id = usuario_id; }
    setJefeId(jefe_id) { this.jefe_id = jefe_id; }
    setPermiso(permiso) { this.permiso = permiso; }
    setUsername(username) { this.username = username; }
    setRole(role) { this.role = role; }
    setRoleId(role_id) { this.role_id = role_id; }
    setNombreUsuario(nombre_usuario) { this.nombre_usuario = nombre_usuario; }
    setPosicion(posicion) { this.posicion = posicion; }
    setIsJefe(isJefe) { this.isJefe = isJefe; }
}

module.exports = Usuario;

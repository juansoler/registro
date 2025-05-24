// src/services/databaseService.js
const Database = require('better-sqlite3');
let db;

// Placeholder for database path - this should be made configurable later
// For example, using app.getPath('userData') in Electron's main process
// and passing it down or using IPC. For now, local file for dev.
const dbPath = './db.sqlite';

function connectDb() {
    if (db) {
        return db;
    }
    try {
        db = new Database(dbPath, { verbose: console.log }); // verbose for logging SQL queries during development
        console.log('Connected to SQLite database.');
        
        // Basic schema setup (can be expanded)
        // USER TABLE (simplified, expand as needed)
        db.exec(`
            CREATE TABLE IF NOT EXISTS user (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user TEXT UNIQUE,
                password TEXT,
                role TEXT -- This was in the Java user table, might be redundant if using usuario_role
            );
        `);

        // USUARIO_ROLE TABLE
        db.exec(`
            CREATE TABLE IF NOT EXISTS usuario_role (
                user_id INTEGER,
                role TEXT, -- This is the 'negociado' or 'cargo' name
                permiso INTEGER, -- 0 or 1
                isJefe INTEGER, -- 0 or 1
                FOREIGN KEY (user_id) REFERENCES user (id) ON DELETE CASCADE,
                PRIMARY KEY (user_id, role)
            );
        `);
        
        // ROLE TABLE (for defining roles/negociados and their properties like 'posicion')
        db.exec(`
            CREATE TABLE IF NOT EXISTS role (
                id INTEGER PRIMARY KEY AUTOINCREMENT, -- Added for easier referencing if needed
                nombre_role TEXT UNIQUE,
                posicion INTEGER -- Used to distinguish 'cargos' (if not null) from 'negociados' (if null) and for ordering
            );
        `);

        // Add more table creations here as you identify them...
        // For example, 'entrada', 'comentario', etc.
        db.exec("CREATE TABLE IF NOT EXISTS CATEGORIA (NOMBRE TEXT UNIQUE);"); // Added
         db.exec(`
            CREATE TABLE IF NOT EXISTS entrada (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                asunto TEXT,
                fecha TEXT,
                area TEXT,
                confidencial INTEGER,
                urgente INTEGER,
                observaciones TEXT,
                numeroEntrada TEXT UNIQUE, -- Assuming this should be unique
                tramitado INTEGER DEFAULT 0,
                tramitadoPor TEXT,
                soloJefe1 INTEGER DEFAULT 0 -- From dbProd.java, might be relevant
            );
        `);
        
        db.exec(`
            CREATE TABLE IF NOT EXISTS comentario (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                entrada_id INTEGER,
                usuario_id INTEGER,
                role TEXT, -- Role of the user who made the comment (e.g., their 'cargo')
                usuario_posicion INTEGER, -- Original ordering logic
                fecha TEXT,
                hora TEXT,
                comentario TEXT,
                visto INTEGER DEFAULT 0,
                FOREIGN KEY (entrada_id) REFERENCES entrada (id) ON DELETE CASCADE,
                FOREIGN KEY (usuario_id) REFERENCES user (id) -- Assuming user_id refers to 'user' table
            );
        `);


        return db;
    } catch (err) {
        console.error('Failed to connect to SQLite database:', err);
        throw err;
    }
}

function closeDb() {
    if (db) {
        db.close();
        db = null;
        console.log('Disconnected from SQLite database.');
    }
}

function getDb() {
    if (!db) {
        //return connectDb(); // or throw error if explicit connect is preferred
         throw new Error('Database not connected. Call connectDb first.');
    }
    return db;
}

// Ported from db.java's getUsuario method
function getUsuario(usuario_id) {
    const stmt = getDb().prepare(`
        SELECT 
            u.id, 
            u.user as username, -- Changed 'user' to 'username' to avoid conflict with JS keyword
            ur.permiso, 
            ur.role as role_name, -- aliasing role to role_name
            ur.isJefe, 
            r.posicion 
        FROM user u
        LEFT JOIN usuario_role ur ON u.id = ur.user_id
        LEFT JOIN role r ON r.nombre_role = ur.role
        WHERE u.id = ?
    `);
    const row = stmt.get(usuario_id);
    if (row) {
        // Reconstruct the Usuario object similar to the Java version
        // Note: The JavaScript Usuario model should be imported and used here
        // For now, returning a plain object
        return {
            usuario_id: row.id,
            username: row.username, // Corresponds to 'nombre_usuario' or 'user' in Java's Usuario
            role: row.role_name,    // Corresponds to 'role' in Java's Usuario
            permiso: row.permiso === 1,
            isJefe: row.isJefe === 1,
            posicion: row.posicion,
            jefe_id: row.isJefe === 1 ? row.id : null // Simplified assumption for jefe_id
            // role_id might need another lookup or be stored differently
        };
    }
    return null;
}

function getUserByUsername(username) {
    // This is a simplified version. The Java code has complex login logic.
    const stmt = getDb().prepare('SELECT * FROM user WHERE user = ?');
    return stmt.get(username);
}

function getNegociados() {
    const stmt = getDb().prepare("SELECT nombre_role FROM role WHERE posicion IS NULL AND nombre_role != 'admin' ORDER BY nombre_role");
    // Assuming 'negociados' are roles where 'posicion' is NULL, excluding 'admin'
    // This matches the logic in db.java getNegociados fairly closely.
    const rows = stmt.all();
    return rows.map(row => row.nombre_role);
}

function getCategorias() {
    const stmt = getDb().prepare("SELECT NOMBRE FROM CATEGORIA ORDER BY NOMBRE");
    // Ensure CATEGORIA table exists from previous schema steps or add:
    // db.exec("CREATE TABLE IF NOT EXISTS CATEGORIA (NOMBRE TEXT UNIQUE);");
    const rows = stmt.all();
    return rows.map(row => row.NOMBRE);
}

module.exports = {
    connectDb,
    closeDb,
    getDb,
    getUsuario,
    getUserByUsername,
    getNegociados,
    getCategorias
    // Add other ported DB functions here
};

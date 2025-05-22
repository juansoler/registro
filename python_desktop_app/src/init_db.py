import sqlite3
from pathlib import Path

def init_database():
    db_path = Path(__file__).parent.parent / "db.sqlite"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # Tablas principales
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS user (
        id INTEGER PRIMARY KEY,
        username TEXT UNIQUE NOT NULL,
        password TEXT NOT NULL,
        permiso BOOLEAN DEFAULT 1
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS JEFES (
        id INTEGER PRIMARY KEY,
        nombre TEXT NOT NULL,
        posicion TEXT UNIQUE NOT NULL,
        usuario_id INTEGER REFERENCES user(id)
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS negociados (
        id INTEGER PRIMARY KEY,
        nombre TEXT NOT NULL,
        codigo TEXT UNIQUE NOT NULL
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS canalesEntrada (
        nombre TEXT PRIMARY KEY
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS entrada (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        Asunto TEXT NOT NULL,
        Fecha TEXT NOT NULL,
        Area TEXT,
        observaciones TEXT,
        urgente BOOLEAN DEFAULT 0,
        Confidencial BOOLEAN DEFAULT 0,
        soloJefe1 BOOLEAN DEFAULT 0,
        Tramitado BOOLEAN DEFAULT 0,
        canalEntrada TEXT REFERENCES canalesEntrada(nombre),
        numeroEntrada TEXT UNIQUE,
        tramitadoPor INTEGER REFERENCES user(id)
    )
    """)

    # Otras tablas
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS CATEGORIA (
        nombre TEXT PRIMARY KEY
    )
    """)

    cursor.execute("""
    CREATE TABLE IF NOT EXISTS CATEGORIA_ENTRADA (
        CATEGORIA TEXT REFERENCES CATEGORIA(nombre),
        ENTRADA_ID INTEGER REFERENCES entrada(id),
        PRIMARY KEY (CATEGORIA, ENTRADA_ID)
    )
    """)

      # Tabla de destinatario 
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS destinatario (
        entrada_id INTEGER REFERENCES entrada(id),
        negociado_id INTEGER REFERENCES negociados(id),
        PRIMARY KEY (entrada_id, negociado_id)
    )
    """)

    # Tabla de destinatarios jefes
    cursor.execute("""
    CREATE TABLE IF NOT EXISTS destinatarioJefe (
        entrada_id INTEGER REFERENCES entrada(id),
        jefe_id INTEGER REFERENCES JEFES(id),
        PRIMARY KEY (entrada_id, jefe_id)
    )
    """)

    conn.commit()
    conn.close()
    print(f"Base de datos inicializada en {db_path}")

if __name__ == "__main__":
    init_database()
import sqlite3
from pathlib import Path

def insert_initial_data():
    db_path = Path(__file__).parent.parent / "db.sqlite"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    print("Iniciando inserción de datos iniciales...")

    # Datos iniciales para canalesEntrada
    cursor.executemany(
        "INSERT OR IGNORE INTO canalesEntrada (nombre) VALUES (?)",
        [("Correo Electrónico",), ("Oficio",), ("Personal",)]
    )
    print("Canales de entrada verificados")

    # Datos iniciales para JEFES
    cursor.executemany(
        "INSERT OR IGNORE INTO JEFES (nombre, posicion) VALUES (?, ?)",
        [
            ("Juan Pérez", "Director General"),
            ("María García", "Jefa de Departamento")
        ]
    )
    print("Jefes verificados")

    # Datos iniciales para negociados
    cursor.executemany(
        "INSERT OR IGNORE INTO negociados (nombre, codigo) VALUES (?, ?)",
        [
            ("Registro", "REG"),
            ("Administración", "ADM"),
            ("Finanzas", "FIN")
        ]
    )
    print("Negociados verificados")

    # Datos iniciales para CATEGORIA
    cursor.executemany(
        "INSERT OR IGNORE INTO CATEGORIA (nombre) VALUES (?)",
        [("Administrativo",), ("Financiero",), ("Legal",)]
    )
    print("Categorías verificadas")

    # Crear/actualizar entrada de prueba para Registro
    cursor.execute(
        """INSERT OR REPLACE INTO entrada
        (id, Asunto, Fecha, Area, numeroEntrada, canalEntrada)
        VALUES (999, ?, ?, ?, ?, ?)""",
        ("Entrada de prueba", "2025-05-23", "Registro", "REG-001", "Correo Electrónico")
    )
    cursor.execute(
        "INSERT OR REPLACE INTO destinatario (entrada_id, negociado_id) VALUES (?, ?)",
        (999, 1)  # 1 = ID de Registro
    )
    print("Entrada de prueba para Registro creada/actualizada")

    conn.commit()
    conn.close()
    print(f"Datos iniciales verificados/actualizados en {db_path}")

if __name__ == "__main__":
    insert_initial_data()
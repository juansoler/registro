import sqlite3
import os
from . import config_manager # Assuming config_manager is in the same directory (src)
from . import models # Assuming models is in the same directory (src)
from typing import List, Dict, Optional, Any # For type hinting
import datetime # For date conversion

# --- Database Connection ---

_db_connection = None

def get_db_connection() -> sqlite3.Connection:
    """
    Establishes and returns an SQLite database connection.
    Loads configuration if necessary to determine database path.
    Sets row_factory to sqlite3.Row.
    """
    global _db_connection
    
    if _db_connection:
        try:
            _db_connection.execute("SELECT 1").fetchone() # Check if alive
            return _db_connection
        except sqlite3.Error:
            _db_connection = None 

    if not config_manager.get_base_dir(): # Ensure config is loaded if needed
        config_manager.load_config() 

    base_dir = config_manager.get_base_dir()
    if not base_dir:
        raise ValueError("BASE_DIR is not set, cannot connect to the database.")

    db_path = os.path.join(base_dir, "db.sqlite")

    try:
        conn = sqlite3.connect(db_path)
        conn.row_factory = sqlite3.Row 
        _db_connection = conn
        return conn
    except sqlite3.Error as e:
        print(f"Error connecting to database at {db_path}: {e}")
        raise

def close_db_connection():
    global _db_connection
    if _db_connection:
        _db_connection.close()
        _db_connection = None

# --- User and Authentication Functions ---

def comprobar_usuario(username: str) -> bool:
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT 1 FROM user WHERE user = ?", (username,))
        return cursor.fetchone() is not None
    except sqlite3.Error as e:
        print(f"Database error in comprobar_usuario: {e}")
        return False

def login_user(username: str, password_plaintext: str) -> Optional[models.Usuario | int]:
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, user, password, reset FROM user WHERE user = ?", (username,))
        user_row = cursor.fetchone()

        if not user_row: return None
        user_id = user_row["id"]; stored_password_hash = user_row["password"]
        if user_row["reset"] == 1 or stored_password_hash == "reset": return 2

        if password_plaintext == "testpassword": # Placeholder for actual password hashing
            print(f"Warning: Using placeholder password check for user {username}")
            roles_query = "SELECT r.id, r.nombre_role, r.posicion, r.isJefe FROM role r JOIN usuario_role ur ON r.id = ur.role_id WHERE ur.user_id = ?"
            cursor.execute(roles_query, (user_id,))
            user_roles = [models.Role(id=r["id"], nombre_role=r["nombre_role"], posicion=r["posicion"], is_jefe=bool(r["isJefe"])) for r in cursor.fetchall()]
            return models.Usuario(id=user_id, username=user_row["user"], password_hash=stored_password_hash, roles=user_roles)
        else:
            return None       
    except sqlite3.Error as e:
        print(f"Database error in login_user: {e}")
        return None

def get_usuario_details(user_id: int) -> Optional[models.Usuario]:
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, user, password FROM user WHERE id = ?", (user_id,))
        user_row = cursor.fetchone()
        if not user_row: return None
        roles_query = "SELECT r.id, r.nombre_role, r.posicion, r.isJefe FROM role r JOIN usuario_role ur ON r.id = ur.role_id WHERE ur.user_id = ?"
        cursor.execute(roles_query, (user_id,))
        user_roles = [models.Role(id=r["id"], nombre_role=r["nombre_role"], posicion=r["posicion"], is_jefe=bool(r["isJefe"])) for r in cursor.fetchall()]
        return models.Usuario(id=user_row["id"], username=user_row["user"], password_hash=user_row["password"], roles=user_roles)
    except sqlite3.Error as e:
        print(f"Database error in get_usuario_details: {e}")
        return None

# --- Initial Configuration Data Loading Functions ---
def get_negociados() -> List[models.Role]:
    conn = None; roles_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        cursor.execute("SELECT id, nombre_role, isJefe FROM role WHERE posicion IS NULL AND nombre_role != 'admin'")
        for row in cursor.fetchall(): roles_list.append(models.Role(id=row["id"], nombre_role=row["nombre_role"], is_jefe=bool(row["isJefe"]), posicion=None))
    except sqlite3.Error as e: print(f"Database error in get_negociados: {e}")
    return roles_list

def get_cargos() -> List[models.Role]:
    conn = None; roles_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        cursor.execute("SELECT id, nombre_role, posicion, isJefe FROM role WHERE posicion IS NOT NULL ORDER BY posicion")
        for row in cursor.fetchall(): roles_list.append(models.Role(id=row["id"], nombre_role=row["nombre_role"], posicion=row["posicion"], is_jefe=bool(row["isJefe"])))
    except sqlite3.Error as e: print(f"Database error in get_cargos: {e}")
    return roles_list

def get_canales() -> List[models.CanalEntrada]:
    conn = None; canales_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        cursor.execute("SELECT id, nombre FROM canalesEntrada") 
        for row in cursor.fetchall(): canales_list.append(models.CanalEntrada(id=row["id"], nombre=row["nombre"]))
    except sqlite3.Error as e:
        if "no such table: canalesEntrada" in str(e).lower(): # Fallback for different table name
            try: cursor.execute("SELECT id, nombre FROM canal_entrada"); # try alternative name
            except sqlite3.Error as e2: print(f"Database error in get_canales (fallback): {e2}")
        else: print(f"Database error in get_canales: {e}")
    return canales_list

def get_categorias() -> List[models.Categoria]:
    conn = None; categorias_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        cursor.execute("SELECT nombre FROM CATEGORIA") 
        for row in cursor.fetchall(): categorias_list.append(models.Categoria(nombre=row["nombre"]))
    except sqlite3.Error as e: print(f"Database error in get_categorias: {e}")
    return categorias_list

def get_posiciones_for_jefes() -> Dict[int, int]:
    conn = None; posiciones_map = {}
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        query = "SELECT ur.user_id, r.posicion FROM usuario_role ur JOIN role r ON ur.role_id = r.id JOIN user u ON ur.user_id = u.id WHERE r.isJefe = 1 AND r.posicion IS NOT NULL"
        cursor.execute(query)
        for row in cursor.fetchall():
            try: posiciones_map[row["user_id"]] = int(row["posicion"])
            except (ValueError, TypeError): print(f"Warning: Skipping user_id {row['user_id']} due to invalid posicion value: {row['posicion']}")
    except sqlite3.Error as e: print(f"Database error in get_posiciones_for_jefes: {e}")
    return posiciones_map

# --- Entrada Data Functions ---
def get_entradas_for_user(user: models.Usuario, date_str: Optional[str] = None, area_filter: Optional[str] = None, category_filter: Optional[str] = None, search_term: Optional[str] = None, search_field: Optional[str] = None) -> List[models.Entrada]:
    if not user or not user.roles: return []
    conn = None; entradas_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        sql = """SELECT DISTINCT e.id, e.Asunto, e.Fecha, e.Area AS AreaName, e.Confidencial, e.Urgente, e.numeroEntrada, e.Tramitado, e.Observaciones, e.Tramitado_por_id, uc.user AS Tramitado_por_username, ec.canalEntrada_nombre AS CanalNombre, ec.canalEntrada_id AS CanalId FROM entrada e LEFT JOIN destinatario d ON e.id = d.entrada_id LEFT JOIN CATEGORIA_ENTRADA ce ON e.id = ce.entrada_id LEFT JOIN entrada_canales ec ON e.id = ec.entrada_id LEFT JOIN user uc ON e.Tramitado_por_id = uc.id"""
        params = []; where_clauses = []
        user_role_names = [role.nombre_role for role in user.roles if hasattr(role, 'nombre_role')]
        if not user_role_names: return []
        where_clauses.append(f"d.role_name IN ({', '.join(['?'] * len(user_role_names))})"); params.extend(user_role_names)
        if date_str:
            try: day, month, year = date_str.split('/'); where_clauses.append("e.Fecha = ?"); params.append(f"{year}-{month}-{day}")
            except ValueError: print(f"Warning: Invalid date format '{date_str}'. Date filter ignored.")
        if area_filter and area_filter.lower() != "todos": where_clauses.append("e.Area = ?"); params.append(area_filter)
        if category_filter and category_filter.lower() != "todas": where_clauses.append("ce.CATEGORIA = ?"); params.append(category_filter)
        if search_term and search_field in ["Asunto", "numeroEntrada", "Observaciones"]: where_clauses.append(f"e.{search_field} LIKE ?"); params.append(f"%{search_term}%")
        if where_clauses: sql += " WHERE " + " AND ".join(where_clauses)
        sql += " ORDER BY e.Fecha DESC, e.id DESC"
        cursor.execute(sql, tuple(params))
        for row in cursor.fetchall():
            fecha_obj = None; 
            if row["Fecha"]: 
                try: fecha_obj = datetime.datetime.strptime(row["Fecha"], "%Y-%m-%d").date()
                except ValueError: print(f"Warning: Could not parse date '{row['Fecha']}' for entrada ID {row['id']}")
            entradas_list.append(models.Entrada(id=row["id"], asunto=row["Asunto"], fecha=fecha_obj, area=models.Role(nombre_role=row["AreaName"]) if row["AreaName"] else None, confidencial=bool(row["Confidencial"]), urgente=bool(row["Urgente"]), numero_entrada=row["numeroEntrada"], tramitado=bool(row["Tramitado"]), canal_entrada=models.CanalEntrada(id=row["CanalId"], nombre=row["CanalNombre"]) if row["CanalId"] and row["CanalNombre"] else None, observaciones=row["Observaciones"], tramitado_por=models.Usuario(id=row["Tramitado_por_id"], username=row["Tramitado_por_username"], password_hash="", roles=[]) if row["Tramitado_por_id"] and row["Tramitado_por_username"] else None, comentarios=[], archivos=[], antecedentes=[], salidas=[], categorias=[], destinatarios=[]))
    except sqlite3.Error as e: print(f"Database error in get_entradas_for_user: {e}")
    return entradas_list

# --- Update/Edit Entrada Functions ---
def update_entrada_details(entrada_obj: models.Entrada, destinatario_areas_nombres: List[str], destinatario_jefes_nombres: List[str], categoria_nombres: List[str], canal_entrada_nombre: str, updated_by_user: models.Usuario) -> bool:
    if not entrada_obj or not entrada_obj.id: print("Error: Invalid entrada_obj or entrada_id for update."); return False
    conn = None
    try:
        conn = get_db_connection(); cursor = conn.cursor(); conn.execute("BEGIN TRANSACTION")
        sql_update_entrada = "UPDATE entrada SET Asunto = ?, Fecha = ?, Area = ?, Confidencial = ?, Urgente = ?, numeroEntrada = ?, Tramitado = ?, Observaciones = ?, Tramitado_por_id = ? WHERE id = ?"
        primary_area_name = entrada_obj.area.nombre_role if entrada_obj.area else None
        fecha_str = entrada_obj.fecha.strftime("%Y-%m-%d") if isinstance(entrada_obj.fecha, datetime.date) else entrada_obj.fecha
        tramitado_por_id_to_set = updated_by_user.id if entrada_obj.tramitado else None # Simplified logic
        params_update_entrada = (entrada_obj.asunto, fecha_str, primary_area_name, entrada_obj.confidencial, entrada_obj.urgente, entrada_obj.numero_entrada, entrada_obj.tramitado, entrada_obj.observaciones, tramitado_por_id_to_set, entrada_obj.id)
        cursor.execute(sql_update_entrada, params_update_entrada)
        cursor.execute("DELETE FROM destinatario WHERE entrada_id = ?", (entrada_obj.id,))
        for area_nombre in destinatario_areas_nombres: cursor.execute("INSERT INTO destinatario (entrada_id, role_name) VALUES (?, ?)", (entrada_obj.id, area_nombre))
        for jefe_nombre_full in destinatario_jefes_nombres: cursor.execute("INSERT INTO destinatario (entrada_id, role_name) VALUES (?, ?)", (entrada_obj.id, jefe_nombre_full.split('(')[0].strip()))
        cursor.execute("DELETE FROM CATEGORIA_ENTRADA WHERE entrada_id = ?", (entrada_obj.id,))
        for cat_nombre in categoria_nombres: cursor.execute("INSERT INTO CATEGORIA_ENTRADA (entrada_id, CATEGORIA) VALUES (?, ?)", (entrada_obj.id, cat_nombre))
        cursor.execute("DELETE FROM entrada_canales WHERE entrada_id = ?", (entrada_obj.id,))
        cursor.execute("SELECT id FROM canalesEntrada WHERE nombre = ?", (canal_entrada_nombre,)); canal_row = cursor.fetchone()
        if canal_row: cursor.execute("INSERT INTO entrada_canales (entrada_id, canalEntrada_id, canalEntrada_nombre) VALUES (?, ?, ?)", (entrada_obj.id, canal_row["id"], canal_entrada_nombre))
        else: print(f"Warning: Canal de entrada '{canal_entrada_nombre}' no encontrado. Se omitirá en la actualización.")
        conn.commit(); return True
    except (sqlite3.Error, ValueError) as e: print(f"Error in update_entrada_details: {e}"); conn.rollback(); return False

# --- File Metadata Update/Delete/Add Functions ---
def update_archivo_metadata(archivo: models.Archivo | models.ArchivoSalida, tipo_archivo: str) -> bool:
    if not archivo or not archivo.id: print("Error: Invalid archivo object or ID for update."); return False
    conn = None; sql = ""; params = ()
    fecha_str = archivo.fecha.strftime("%Y-%m-%d") if isinstance(archivo.fecha, datetime.date) else archivo.fecha
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        if tipo_archivo == "entrada": sql = "UPDATE files SET ruta_archivo=?, fecha=?, asunto=?, origen=?, observaciones=? WHERE id=?"; params = (archivo.ruta_archivo, fecha_str, archivo.asunto, archivo.origen_destino, archivo.observaciones, archivo.id)
        elif tipo_archivo == "antecedente": sql = "UPDATE antecedentesFiles SET ruta_archivo=?, fecha=?, asunto=?, destino=?, observaciones=?, tipo=? WHERE id=?"; params = (archivo.ruta_archivo, fecha_str, archivo.asunto, archivo.origen_destino, archivo.observaciones, archivo.tipo, archivo.id)
        elif tipo_archivo == "salida": sql = "UPDATE salidaFiles SET ruta_archivo=?, fecha=?, asunto=?, destino=?, visto_bueno_general=? WHERE id=?"; params = (archivo.ruta_archivo, fecha_str, archivo.asunto, archivo.destino, archivo.visto_bueno_general, archivo.id)
        else: print(f"Error: Tipo de archivo desconocido '{tipo_archivo}' for update."); return False
        cursor.execute(sql, params); conn.commit(); return cursor.rowcount > 0
    except sqlite3.Error as e: print(f"Database error in update_archivo_metadata (tipo: {tipo_archivo}, id: {archivo.id}): {e}"); conn.rollback(); return False

def delete_archivo_record(archivo_id: int, tipo_archivo: str) -> bool:
    if not archivo_id: return False
    conn = None; sql = ""
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        if tipo_archivo == "entrada": sql = "DELETE FROM files WHERE id=?"
        elif tipo_archivo == "antecedente": sql = "DELETE FROM antecedentesFiles WHERE id=?"
        elif tipo_archivo == "salida": cursor.execute("DELETE FROM vistoBuenoJefes WHERE salida_file_id=?", (archivo_id,)); sql = "DELETE FROM salidaFiles WHERE id=?"
        else: print(f"Error: Tipo de archivo desconocido '{tipo_archivo}' for delete."); return False
        cursor.execute(sql, (archivo_id,)); conn.commit(); return cursor.rowcount > 0
    except sqlite3.Error as e: print(f"Database error in delete_archivo_record (tipo: {tipo_archivo}, id: {archivo_id}): {e}"); conn.rollback(); return False

def add_new_archivo_record(entrada_id: int, archivo: models.Archivo | models.ArchivoSalida, tipo_archivo: str) -> Optional[int]:
    if not entrada_id or not archivo: return None
    conn = None; new_file_id = None; sql = ""; params = ()
    fecha_str = archivo.fecha.strftime("%Y-%m-%d") if isinstance(archivo.fecha, datetime.date) else archivo.fecha
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        if tipo_archivo == "entrada": sql = "INSERT INTO files (entrada_id, ruta_archivo, fecha, asunto, origen, observaciones) VALUES (?, ?, ?, ?, ?, ?)"; params = (entrada_id, archivo.ruta_archivo, fecha_str, archivo.asunto, archivo.origen_destino, archivo.observaciones)
        elif tipo_archivo == "antecedente": sql = "INSERT INTO antecedentesFiles (entrada_id, ruta_archivo, fecha, asunto, destino, observaciones, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)"; params = (entrada_id, archivo.ruta_archivo, fecha_str, archivo.asunto, archivo.origen_destino, archivo.observaciones, archivo.tipo)
        elif tipo_archivo == "salida": sql = "INSERT INTO salidaFiles (entrada_id, ruta_archivo, fecha, asunto, destino, visto_bueno_general) VALUES (?, ?, ?, ?, ?, ?)"; params = (entrada_id, archivo.ruta_archivo, fecha_str, archivo.asunto, archivo.destino, archivo.visto_bueno_general)
        else: print(f"Error: Tipo de archivo desconocido '{tipo_archivo}' for add."); return None
        cursor.execute(sql, params); new_file_id = cursor.lastrowid; conn.commit(); return new_file_id
    except sqlite3.Error as e: print(f"Database error in add_new_archivo_record (tipo: {tipo_archivo}): {e}"); conn.rollback(); return None

# --- Comment Update/Add Functions ---
def update_comentario_jefe(comentario: models.Comentario) -> bool:
    if not comentario or not comentario.id: print("Error: Invalid comentario object or ID for update."); return False
    conn = None
    sql = "UPDATE comentario SET TextoComentario=?, Fecha=?, Hora=?, visto=? WHERE id=?"
    fecha_str = comentario.fecha.strftime("%Y-%m-%d") if isinstance(comentario.fecha, datetime.date) else comentario.fecha
    hora_str = comentario.hora.strftime("%H:%M:%S") if isinstance(comentario.hora, datetime.time) else str(comentario.hora)
    params = (comentario.texto_comentario, fecha_str, hora_str, comentario.visto, comentario.id)
    try:
        conn = get_db_connection(); cursor = conn.cursor(); cursor.execute(sql, params); conn.commit(); return cursor.rowcount > 0
    except sqlite3.Error as e: print(f"Database error in update_comentario_jefe (id: {comentario.id}): {e}"); conn.rollback(); return False

def add_comentario_jefe(entrada_id: int, comentario: models.Comentario) -> Optional[int]:
    if not entrada_id or not comentario or not comentario.usuario or not comentario.usuario.id: print("Error: Invalid entrada_id or comentario data for add."); return None
    conn = None; new_comment_id = None
    sql = "INSERT INTO comentario (entrada_id, Fecha, Hora, Usuario, TextoComentario, visto, posicionUsuario, user_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)"
    username_to_store = comentario.usuario.username; user_id_fk = comentario.usuario.id
    fecha_str = comentario.fecha.strftime("%Y-%m-%d") if isinstance(comentario.fecha, datetime.date) else comentario.fecha
    hora_str = comentario.hora.strftime("%H:%M:%S") if isinstance(comentario.hora, datetime.time) else str(comentario.hora)
    params = (entrada_id, fecha_str, hora_str, username_to_store, comentario.texto_comentario, comentario.visto, comentario.posicion_usuario, user_id_fk)
    try:
        conn = get_db_connection(); cursor = conn.cursor(); cursor.execute(sql, params); new_comment_id = cursor.lastrowid; conn.commit(); return new_comment_id
    except sqlite3.Error as e: print(f"Database error in add_comentario_jefe: {e}"); conn.rollback(); return None

# --- Save New Entrada Function ---
def save_new_entrada(entrada_data: models.Entrada, current_user: models.Usuario, destinatario_areas_nombres: List[str], destinatario_jefes_nombres: List[str], categoria_nombres: List[str], canal_entrada_nombre: str) -> Optional[int]:
    conn = None; new_entrada_id = None
    try:
        conn = get_db_connection(); cursor = conn.cursor(); conn.execute("BEGIN TRANSACTION")
        sql_entrada = "INSERT INTO entrada (Asunto, Fecha, Area, Confidencial, Urgente, numeroEntrada, Tramitado, Observaciones, Tramitado_por_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
        primary_area_name = entrada_data.area.nombre_role if entrada_data.area else None
        fecha_str = entrada_data.fecha.strftime("%Y-%m-%d") if isinstance(entrada_data.fecha, datetime.date) else entrada_data.fecha
        params_entrada = (entrada_data.asunto, fecha_str, primary_area_name, entrada_data.confidencial, entrada_data.urgente, entrada_data.numero_entrada, False, entrada_data.observaciones, current_user.id)
        cursor.execute(sql_entrada, params_entrada); new_entrada_id = cursor.lastrowid
        if not new_entrada_id: raise sqlite3.Error("Failed to get new_entrada_id.")
        for area_nombre in destinatario_areas_nombres: cursor.execute("INSERT INTO destinatario (entrada_id, role_name) VALUES (?, ?)", (new_entrada_id, area_nombre))
        for jefe_nombre_full in destinatario_jefes_nombres: cursor.execute("INSERT INTO destinatario (entrada_id, role_name) VALUES (?, ?)", (new_entrada_id, jefe_nombre_full.split('(')[0].strip()))
        for cat_nombre in categoria_nombres: cursor.execute("INSERT INTO CATEGORIA_ENTRADA (entrada_id, CATEGORIA) VALUES (?, ?)", (new_entrada_id, cat_nombre))
        cursor.execute("SELECT id FROM canalesEntrada WHERE nombre = ?", (canal_entrada_nombre,)); canal_row = cursor.fetchone()
        if canal_row: cursor.execute("INSERT INTO entrada_canales (entrada_id, canalEntrada_id, canalEntrada_nombre) VALUES (?, ?, ?)", (new_entrada_id, canal_row["id"], canal_entrada_nombre))
        else: print(f"Warning: Canal de entrada '{canal_entrada_nombre}' no encontrado. Se omitirá.")
        for archivo in entrada_data.archivos: fecha_archivo_str = archivo.fecha.strftime("%Y-%m-%d") if isinstance(archivo.fecha, datetime.date) else archivo.fecha; cursor.execute("INSERT INTO files (ruta_archivo, entrada_id, fecha, asunto, origen, observaciones) VALUES (?, ?, ?, ?, ?, ?)", (archivo.ruta_archivo, new_entrada_id, fecha_archivo_str, archivo.asunto, archivo.origen_destino, archivo.observaciones))
        for archivo in entrada_data.antecedentes: fecha_archivo_str = archivo.fecha.strftime("%Y-%m-%d") if isinstance(archivo.fecha, datetime.date) else archivo.fecha; cursor.execute("INSERT INTO antecedentesFiles (ruta_archivo, entrada_id, fecha, asunto, destino, observaciones, tipo) VALUES (?, ?, ?, ?, ?, ?, ?)", (archivo.ruta_archivo, new_entrada_id, fecha_archivo_str, archivo.asunto, archivo.origen_destino, archivo.observaciones, archivo.tipo))
        for archivo_salida in entrada_data.salidas: fecha_archivo_str = archivo_salida.fecha.strftime("%Y-%m-%d") if isinstance(archivo_salida.fecha, datetime.date) else archivo_salida.fecha; cursor.execute("INSERT INTO salidaFiles (ruta_archivo, entrada_id, fecha, asunto, destino, visto_bueno_general) VALUES (?, ?, ?, ?, ?, ?)", (archivo_salida.ruta_archivo, new_entrada_id, fecha_archivo_str, archivo_salida.asunto, archivo_salida.destino, archivo_salida.visto_bueno_general))
        conn.commit(); return new_entrada_id
    except (sqlite3.Error, ValueError) as e: print(f"Error in save_new_entrada: {e}"); conn.rollback(); return None

# --- File/Attachment Data Functions --- (Original _get_visto_bueno_for_salida_file and get_archivos_for_entrada)
def _get_visto_bueno_for_salida_file(cursor: sqlite3.Cursor, salida_file_id: int) -> List[models.VistoBuenoJefe]:
    visto_bueno_list = []
    sql = "SELECT vbj.id, vbj.salida_file_id, vbj.user_id, vbj.visto_bueno_status, u.user as user_username FROM vistoBuenoJefes vbj JOIN user u ON vbj.user_id = u.id WHERE vbj.salida_file_id = ?"
    try:
        cursor.execute(sql, (salida_file_id,))
        for row in cursor.fetchall(): user_obj = models.Usuario(id=row["user_id"], username=row["user_username"], password_hash="", roles=[]); visto_bueno_list.append(models.VistoBuenoJefe(id=row["id"], salida_file_id=row["salida_file_id"], usuario=user_obj, visto_bueno_status=bool(row["visto_bueno_status"])))
    except sqlite3.Error as e: print(f"Error fetching visto bueno for salida_file_id {salida_file_id}: {e}")
    return visto_bueno_list

def get_archivos_for_entrada(entrada_id: int, tipo_archivo: str) -> List[models.Archivo | models.ArchivoSalida]:
    conn = None; archivos_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor(); sql = ""; params = (entrada_id,)
        if tipo_archivo == "entrada": sql = "SELECT id, ruta_archivo, fecha, asunto, origen, observaciones FROM files WHERE entrada_id = ?"
        elif tipo_archivo == "antecedente": sql = "SELECT id, ruta_archivo, fecha, asunto, destino, observaciones, tipo FROM antecedentesFiles WHERE entrada_id = ?"
        elif tipo_archivo == "salida": sql = "SELECT id, ruta_archivo, fecha, asunto, destino, visto_bueno_general FROM salidaFiles WHERE entrada_id = ?"
        else: print(f"Error: Tipo de archivo desconocido '{tipo_archivo}'"); return []
        cursor.execute(sql, params)
        for row in cursor.fetchall():
            fecha_obj = None
            if row["fecha"]: 
                try: fecha_obj = datetime.datetime.strptime(row["fecha"], "%Y-%m-%d").date()
                except ValueError: print(f"Warning: Could not parse date '{row['fecha']}' for file ID {row['id']}")
            if tipo_archivo == "salida": visto_buenos = _get_visto_bueno_for_salida_file(cursor, row["id"]); archivo = models.ArchivoSalida(id=row["id"], ruta_archivo=row["ruta_archivo"], entrada_id=entrada_id, fecha=fecha_obj, asunto=row["asunto"], destino=row["destino"], visto_bueno_general=bool(row["visto_bueno_general"]), visto_bueno_jefes=visto_buenos)
            else: origen_destino_val = row["origen"] if tipo_archivo == "entrada" else row["destino"]; tipo_val = row["tipo"] if tipo_archivo == "antecedente" else None; archivo = models.Archivo(id=row["id"], ruta_archivo=row["ruta_archivo"], entrada_id=entrada_id, fecha=fecha_obj, asunto=row["asunto"], origen_destino=origen_destino_val, observaciones=row["observaciones"], tipo=tipo_val)
            archivos_list.append(archivo)
    except sqlite3.Error as e: print(f"Database error in get_archivos_for_entrada (tipo: {tipo_archivo}): {e}")
    return archivos_list

def get_entrada_details_by_id(entrada_id: int) -> Optional[models.Entrada]:
    conn = None
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        sql_main = "SELECT e.id, e.Asunto, e.Fecha, e.Area AS AreaName, e.Confidencial, e.Urgente, e.numeroEntrada, e.Tramitado, e.Observaciones, e.Tramitado_por_id, u.user AS Tramitado_por_username, ec.canalEntrada_id AS CanalId, ec.canalEntrada_nombre AS CanalNombre FROM entrada e LEFT JOIN entrada_canales ec ON e.id = ec.entrada_id LEFT JOIN user u ON e.Tramitado_por_id = u.id WHERE e.id = ?"
        cursor.execute(sql_main, (entrada_id,)); row = cursor.fetchone()
        if not row: return None
        dest_sql = "SELECT role_name FROM destinatario WHERE entrada_id = ?"; cursor.execute(dest_sql, (entrada_id,)); destinatario_roles = [models.Role(nombre_role=r_row["role_name"]) for r_row in cursor.fetchall() if r_row["role_name"]]
        cat_sql = "SELECT CATEGORIA FROM CATEGORIA_ENTRADA WHERE entrada_id = ?"; cursor.execute(cat_sql, (entrada_id,)); categorias_list = [models.Categoria(nombre=cat_row["CATEGORIA"]) for cat_row in cursor.fetchall() if cat_row["CATEGORIA"]]
        fecha_obj = None
        if row["Fecha"]: 
            try: fecha_obj = datetime.datetime.strptime(row["Fecha"], "%Y-%m-%d").date()
            except ValueError: print(f"Warning: Could not parse date '{row['Fecha']}' for entrada ID {row['id']}")
        
        # Fetch comments for the entrada
        comments_sql = "SELECT c.id, c.TextoComentario, c.Fecha, c.Hora, c.visto, c.posicionUsuario, u.id as user_id, u.user as user_username FROM comentario c JOIN user u ON c.user_id = u.id WHERE c.entrada_id = ? ORDER BY c.Fecha, c.Hora"
        cursor.execute(comments_sql, (entrada_id,))
        comentarios_list = []
        for c_row in cursor.fetchall():
            comment_user = models.Usuario(id=c_row["user_id"], username=c_row["user_username"], password_hash="", roles=[]) # Minimal user
            comment_fecha = datetime.datetime.strptime(c_row["Fecha"], "%Y-%m-%d").date() if c_row["Fecha"] else None
            comment_hora = datetime.datetime.strptime(c_row["Hora"], "%H:%M:%S").time() if c_row["Hora"] else None
            comentarios_list.append(models.Comentario(id=c_row["id"], entrada_id=entrada_id, fecha=comment_fecha, hora=comment_hora, visto=bool(c_row["visto"]), usuario=comment_user, texto_comentario=c_row["TextoComentario"], posicion_usuario=c_row["posicionUsuario"]))

        return models.Entrada(id=row["id"], asunto=row["Asunto"], fecha=fecha_obj, area=models.Role(nombre_role=row["AreaName"]) if row["AreaName"] else None, confidencial=bool(row["Confidencial"]), urgente=bool(row["Urgente"]), numero_entrada=row["numeroEntrada"], tramitado=bool(row["Tramitado"]), canal_entrada=models.CanalEntrada(id=row["CanalId"], nombre=row["CanalNombre"]) if row["CanalId"] and row["CanalNombre"] else None, observaciones=row["Observaciones"], tramitado_por=models.Usuario(id=row["Tramitado_por_id"], username=row["Tramitado_por_username"], password_hash="", roles=[]) if row["Tramitado_por_id"] and row["Tramitado_por_username"] else None, destinatarios=destinatario_roles, categorias=categorias_list, archivos=get_archivos_for_entrada(entrada_id, "entrada"), antecedentes=get_archivos_for_entrada(entrada_id, "antecedente"), salidas=get_archivos_for_entrada(entrada_id, "salida"), comentarios=comentarios_list)
    except sqlite3.Error as e: print(f"Database error in get_entrada_details_by_id for entrada_id {entrada_id}: {e}"); return None

# Example of how to use the functions (optional, for testing)
if __name__ == '__main__':
    print("Running database_manager.py tests...")
    if not os.path.exists("CONFIG.CFG"):
        with open("CONFIG.CFG", "w") as f: f.write(f"BASE_DIR = {os.getcwd()}\nLOCAL_DIR = {os.getcwd()}\n")
    try:
        conn = get_db_connection(); print(f"DB Connection: {conn}"); cursor = conn.cursor()
        # DDL statements (user, role, usuario_role, canalesEntrada, CATEGORIA, entrada, destinatario, CATEGORIA_ENTRADA, entrada_canales, files, antecedentesFiles, salidaFiles, vistoBuenoJefes, comentario)
        cursor.execute("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY, user TEXT UNIQUE, password TEXT, reset INTEGER DEFAULT 0, isJefe INTEGER DEFAULT 0, posicionJefe INTEGER)")
        cursor.execute("CREATE TABLE IF NOT EXISTS role (id INTEGER PRIMARY KEY, nombre_role TEXT UNIQUE, posicion TEXT, isJefe INTEGER DEFAULT 0)")
        cursor.execute("CREATE TABLE IF NOT EXISTS usuario_role (user_id INTEGER, role_id INTEGER, FOREIGN KEY(user_id) REFERENCES user(id), FOREIGN KEY(role_id) REFERENCES role(id), PRIMARY KEY(user_id, role_id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS canalesEntrada (id INTEGER PRIMARY KEY, nombre TEXT UNIQUE)")
        cursor.execute("CREATE TABLE IF NOT EXISTS CATEGORIA (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT UNIQUE)")
        cursor.execute("CREATE TABLE IF NOT EXISTS entrada (id INTEGER PRIMARY KEY AUTOINCREMENT, Asunto TEXT, Fecha TEXT, Area TEXT, Confidencial INTEGER DEFAULT 0, Urgente INTEGER DEFAULT 0, numeroEntrada TEXT UNIQUE, Tramitado INTEGER DEFAULT 0, Observaciones TEXT, Tramitado_por_id INTEGER, FOREIGN KEY(Tramitado_por_id) REFERENCES user(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS destinatario (id INTEGER PRIMARY KEY AUTOINCREMENT, entrada_id INTEGER, role_id INTEGER, role_name TEXT, FOREIGN KEY(entrada_id) REFERENCES entrada(id), FOREIGN KEY(role_id) REFERENCES role(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS CATEGORIA_ENTRADA (entrada_id INTEGER, CATEGORIA TEXT, FOREIGN KEY(entrada_id) REFERENCES entrada(id), PRIMARY KEY(entrada_id, CATEGORIA))")
        cursor.execute("CREATE TABLE IF NOT EXISTS entrada_canales (entrada_id INTEGER, canalEntrada_id INTEGER, canalEntrada_nombre TEXT, FOREIGN KEY(entrada_id) REFERENCES entrada(id), FOREIGN KEY(canalEntrada_id) REFERENCES canalesEntrada(id), PRIMARY KEY(entrada_id, canalEntrada_id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS files (id INTEGER PRIMARY KEY AUTOINCREMENT, ruta_archivo TEXT NOT NULL, entrada_id INTEGER NOT NULL, fecha TEXT, asunto TEXT, origen TEXT, observaciones TEXT, FOREIGN KEY(entrada_id) REFERENCES entrada(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS antecedentesFiles (id INTEGER PRIMARY KEY AUTOINCREMENT, ruta_archivo TEXT NOT NULL, entrada_id INTEGER NOT NULL, fecha TEXT, asunto TEXT, destino TEXT, observaciones TEXT, tipo TEXT, FOREIGN KEY(entrada_id) REFERENCES entrada(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS salidaFiles (id INTEGER PRIMARY KEY AUTOINCREMENT, ruta_archivo TEXT NOT NULL, entrada_id INTEGER NOT NULL, fecha TEXT, asunto TEXT, destino TEXT, visto_bueno_general INTEGER DEFAULT 0, FOREIGN KEY(entrada_id) REFERENCES entrada(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS vistoBuenoJefes (id INTEGER PRIMARY KEY AUTOINCREMENT, salida_file_id INTEGER NOT NULL, user_id INTEGER NOT NULL, visto_bueno_status INTEGER DEFAULT 0, FOREIGN KEY(salida_file_id) REFERENCES salidaFiles(id), FOREIGN KEY(user_id) REFERENCES user(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS comentario (id INTEGER PRIMARY KEY AUTOINCREMENT, entrada_id INTEGER NOT NULL, Fecha TEXT, Hora TEXT, Usuario TEXT, TextoComentario TEXT, visto INTEGER DEFAULT 0, posicionUsuario TEXT, user_id INTEGER, FOREIGN KEY(entrada_id) REFERENCES entrada(id), FOREIGN KEY(user_id) REFERENCES user(id))")
        conn.commit()
        
        # --- Test Data Population ---
        try:
            cursor.execute("INSERT OR IGNORE INTO user (id, user, password, reset) VALUES (1, 'testuser', 'hashed_testpassword', 0)")
            cursor.execute("INSERT OR IGNORE INTO user (id, user, password, reset, isJefe, posicionJefe) VALUES (3, 'jefeuser', 'hashed_jefepassword', 0, 1, 100)")
            cursor.execute("INSERT OR IGNORE INTO role (id, nombre_role, posicion, isJefe) VALUES (1, 'Admin', NULL, 0), (2, 'Manager', '1', 1), (3, 'Worker', '2', 0), (4, 'DepartmentA', NULL, 0), (5, 'WorkerArea', NULL, 0)")
            cursor.execute("INSERT OR IGNORE INTO usuario_role (user_id, role_id) VALUES (1, 3), (3, 2)")
            cursor.execute("INSERT OR IGNORE INTO canalesEntrada (id, nombre) VALUES (1, 'Email'), (2, 'Web Form')")
            cursor.execute("INSERT OR IGNORE INTO CATEGORIA (nombre) VALUES ('General'), ('Urgent'), ('Projects'), ('Tasks'), ('TestCategory')")
            cursor.execute("INSERT OR IGNORE INTO entrada (id, Asunto, Fecha, Area, Confidencial, Urgente, numeroEntrada, Tramitado, Tramitado_por_id) VALUES (1, 'Urgent Project Alpha', '2023-10-26', 'DepartmentA', 1, 1, 'E2023-001', 0, NULL)")
            cursor.execute("INSERT OR IGNORE INTO destinatario (entrada_id, role_name) VALUES (1, 'DepartmentA')")
            cursor.execute("INSERT OR IGNORE INTO CATEGORIA_ENTRADA (entrada_id, CATEGORIA) VALUES (1, 'Urgent'), (1, 'Projects')")
            cursor.execute("INSERT OR IGNORE INTO entrada_canales (entrada_id, canalEntrada_id, canalEntrada_nombre) VALUES (1, 1, 'Email')")
            cursor.execute("INSERT OR IGNORE INTO files (id, ruta_archivo, entrada_id, fecha, asunto, origen, observaciones) VALUES (1, '/files/e1_doc1.pdf', 1, '2023-10-26', 'Documento Principal E1', 'ClienteX', 'Obs doc1 e1')")
            cursor.execute("INSERT OR IGNORE INTO antecedentesFiles (id, ruta_archivo, entrada_id, fecha, asunto, destino, observaciones, tipo) VALUES (1, '/antecedentes/e1_ant1.docx', 1, '2023-10-25', 'Antecedente Clave E1', 'Interno', 'Obs ant1 e1', 'Informe')")
            cursor.execute("INSERT OR IGNORE INTO salidaFiles (id, ruta_archivo, entrada_id, fecha, asunto, destino, visto_bueno_general) VALUES (1, '/salidas/e1_sal1.pdf', 1, '2023-10-28', 'Respuesta ClienteX E1', 'ClienteX', 0)")
            cursor.execute("INSERT OR IGNORE INTO vistoBuenoJefes (salida_file_id, user_id, visto_bueno_status) VALUES (1, 3, 1)")
            cursor.execute("INSERT OR IGNORE INTO entrada (id, Asunto, Fecha, Area, Confidencial, Urgente, numeroEntrada, Tramitado, Tramitado_por_id) VALUES (2, 'General Inquiry Beta', '2023-10-27', 'WorkerArea', 0, 0, 'E2023-002', 1, 1)")
            cursor.execute("INSERT OR IGNORE INTO destinatario (entrada_id, role_name) VALUES (2, 'Worker')")
            cursor.execute("INSERT OR IGNORE INTO CATEGORIA_ENTRADA (entrada_id, CATEGORIA) VALUES (2, 'General')")
            cursor.execute("INSERT OR IGNORE INTO entrada_canales (entrada_id, canalEntrada_id, canalEntrada_nombre) VALUES (2, 2, 'Web Form')")
            cursor.execute("INSERT OR IGNORE INTO files (id, ruta_archivo, entrada_id, fecha, asunto, origen, observaciones) VALUES (2, '/files/e2_info.txt', 2, '2023-10-27', 'Info Adicional E2', 'Sistema', 'Obs info e2')")
            conn.commit()
        except sqlite3.Error as e: print(f"Error populating test data: {e}")
        
        # --- Test save_new_entrada ---
        print("\n--- Testing save_new_entrada ---")
        creator_user = models.Usuario(id=1, username="testuser", password_hash="", roles=[models.Role(nombre_role="Worker")])
        new_entrada_model = models.Entrada(id=None, asunto="Test Guardar Entrada Nueva", fecha=datetime.date(2023, 12, 1), area=models.Role(nombre_role="TestingArea"), confidencial=True, urgente=True, numero_entrada="EN2023-999", tramitado=False, observaciones="Observaciones de la prueba de guardado.", canal_entrada=None, tramitado_por=None, comentarios=[], archivos=[models.Archivo(id=None, ruta_archivo="/target/docs/01-12-2023/test_entrada_file.pdf", entrada_id=0, fecha=datetime.date(2023,12,1), asunto="Doc Entrada Test", origen_destino="Origen Test", observaciones="Obs Doc Ent")], antecedentes=[models.Archivo(id=None, ruta_archivo="/target/docs_antecedentes/01-12-2023/test_antec_file.docx", entrada_id=0, fecha=datetime.date(2023,12,1), asunto="Doc Antecedente Test", origen_destino="Destino Test Ant", observaciones="Obs Doc Ant", tipo="Informe Test")], salidas=[])
        dest_areas_nombres_test = ["DepartmentA", "WorkerArea"]; dest_jefes_nombres_test = ["Manager"]; categorias_nombres_test = ["General", "TestCategory"]; canal_nombre_test = "Email"
        saved_id = save_new_entrada(new_entrada_model, creator_user, dest_areas_nombres_test, dest_jefes_nombres_test, categorias_nombres_test, canal_nombre_test)
        if saved_id: print(f"save_new_entrada successful. New Entrada ID: {saved_id}") 
        else: print("save_new_entrada failed.")
        
        # --- Test update_entrada_details ---
        print("\n--- Testing update_entrada_details ---")
        if saved_id: 
            updated_entrada_obj = models.Entrada(id=saved_id, asunto="Test Guardar Entrada Nueva (ACTUALIZADO)", fecha=datetime.date(2023, 12, 2), area=models.Role(nombre_role="AreaActualizada"), confidencial=False, urgente=False, numero_entrada="EN2023-999-MOD", tramitado=True, observaciones="Observaciones actualizadas.", canal_entrada=None, tramitado_por=None, archivos=[], antecedentes=[], salidas=[], comentarios=[], categorias=[], destinatarios=[])
            updated_by_test_user = models.Usuario(id=3, username="jefeuser", password_hash="", roles=[])
            update_success = update_entrada_details(updated_entrada_obj, ["DepartmentA"], ["Manager", "Admin"], ["Urgent", "TestCategory"], "Web Form", updated_by_test_user)
            if update_success: print(f"update_entrada_details successful for ID: {saved_id}")
            else: print(f"update_entrada_details failed for ID: {saved_id}")

        # --- Test file operations ---
        print("\n--- Testing File Operations ---")
        test_entrada_id_for_files = 1 
        new_generic_file = models.Archivo(id=None, ruta_archivo="/target/new_generic.txt", entrada_id=test_entrada_id_for_files, fecha=datetime.date.today(), asunto="New Generic File", origen_destino="Test Origin", observaciones="New generic obs")
        new_generic_file_id = add_new_archivo_record(test_entrada_id_for_files, new_generic_file, "entrada")
        if new_generic_file_id:
            print(f"add_new_archivo_record (generic) successful, ID: {new_generic_file_id}")
            new_generic_file.id = new_generic_file_id; new_generic_file.asunto = "Updated Generic File Asunto"
            if update_archivo_metadata(new_generic_file, "entrada"): print(f"  Updated generic file metadata for ID: {new_generic_file_id}")
        if delete_archivo_record(2, "entrada"): print("delete_archivo_record successful for ID 2 (type entrada)") # Assumes file ID 2 exists
        
        # --- Test comment operations ---
        print("\n--- Testing Comment Operations ---")
        commenter_jefe = models.Usuario(id=3, username="jefeuser", password_hash="", roles=[])
        new_comment = models.Comentario(id=None, entrada_id=test_entrada_id_for_files, fecha=datetime.date.today(), hora=datetime.datetime.now().time(), usuario=commenter_jefe, texto_comentario="Este es un nuevo comentario del jefe.", visto=False, posicion_usuario="Jefe de Pruebas")
        new_comment_id = add_comentario_jefe(test_entrada_id_for_files, new_comment)
        if new_comment_id:
            print(f"add_comentario_jefe successful, ID: {new_comment_id}")
            new_comment.id = new_comment_id; new_comment.texto_comentario = "Comentario actualizado del jefe."; new_comment.visto = True
            if update_comentario_jefe(new_comment): print(f"  Updated comment for ID: {new_comment_id}")

        # --- Test get_entrada_details_by_id (now includes files and comments) ---
        print("\n--- Testing get_entrada_details_by_id (with files & comments) ---")
        details = get_entrada_details_by_id(1) 
        if details:
            print(f"Details for Entrada ID 1: Asunto: {details.asunto}")
            print(f"  Archivos ({len(details.archivos)}): {[f.ruta_archivo for f in details.archivos]}")
            print(f"  Comentarios ({len(details.comentarios)}): {[c.texto_comentario for c in details.comentarios]}")
        else: print("Entrada ID 1 not found for detail test.")

    except ValueError as ve: print(f"Configuration Error: {ve}")
    except sqlite3.Error as e: print(f"Main test block SQLite Error: {e}")
    finally: close_db_connection(); print("\nDatabase connection closed.")
    print("\nFinished database_manager.py tests.")

```

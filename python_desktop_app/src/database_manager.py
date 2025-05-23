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

def update_user_password(username: str, new_password: str) -> bool:
    """
    Updates a user's password in the database and clears reset flag.
    Returns True if successful, False otherwise.
    """
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        
        # Hash the new password before storing
        hashed_password = new_password
        try:
            from . import crypto_utils
            if hasattr(crypto_utils, 'hash_password'):
                hashed_password = crypto_utils.hash_password(new_password)
            else:
                print("Warning: crypto_utils.hash_password not available - storing plain password")
        except ImportError:
            print("Warning: crypto_utils module not available - storing plain password")
        
        # Update password and manage reset flag
        cursor.execute("PRAGMA table_info(user)")
        columns = [col[1] for col in cursor.fetchall()]
        
        if new_password == "reset":
            # Set password to 'reset' and set reset flag to 1 (needs reset)
            if 'reset' in columns:
                cursor.execute("UPDATE user SET password = 'reset', reset = 1 WHERE user = ?", (username,))
            else: # Fallback if no 'reset' column, just set password to 'reset'
                cursor.execute("UPDATE user SET password = 'reset' WHERE user = ?", (username,))
        else:
            # Hash the new password and clear reset flag (set to 0)
            final_password_to_store = new_password # Default if no crypto
            try:
                from . import crypto_utils
                if hasattr(crypto_utils, 'hash_password'):
                    final_password_to_store = crypto_utils.hash_password(new_password)
                else:
                    print("Warning: crypto_utils.hash_password not available - storing plain password if not 'reset'")
            except ImportError:
                print("Warning: crypto_utils module not available - storing plain password if not 'reset'")

            if 'reset' in columns:
                cursor.execute("UPDATE user SET password = ?, reset = 0 WHERE user = ?", (final_password_to_store, username))
            else:
                cursor.execute("UPDATE user SET password = ? WHERE user = ?", (final_password_to_store, username))
        
        conn.commit()
        return cursor.rowcount > 0
    except sqlite3.Error as e:
        print(f"Error updating password for {username}: {e}")
        return False

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
        cursor.execute("SELECT id, user, password, role, permiso FROM user WHERE user = ?", (username,))
        user_row = cursor.fetchone()

        if not user_row: return None
        user_id = user_row["id"]; stored_password_hash = user_row["password"]
        if stored_password_hash == "reset": return 2
        if not (user_row["permiso"] if "permiso" in user_row else True): return 3  # Código 3 para permiso denegado

        # Try to verify password with crypto_utils if available
        try:
            from . import crypto_utils
            if hasattr(crypto_utils, 'verify_password'):
                if not crypto_utils.verify_password(stored_password_hash, password_plaintext):
                    return None
        except ImportError:
            print("Warning: crypto_utils not available - falling back to plain text comparison")
            if stored_password_hash != password_plaintext:
                return None
        
        # Create user object with role from user table
        user_role = models.Role(
            id=0,  # No hay ID en la estructura real
            nombre_role=user_row["role"],
            posicion=None,
            is_jefe=False
        )
        
        return models.Usuario(
            id=user_id,
            username=user_row["user"],
            password_hash=stored_password_hash,
            roles=[user_role],
            permiso=bool(user_row["permiso"] if "permiso" in user_row else True)
        )
    except sqlite3.Error as e:
        print(f"Database error in login_user: {e}")
        return None

def get_user_by_username(username: str) -> Optional[models.Usuario]:
    """Get user object by username without password verification"""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, user, password, role, permiso FROM user WHERE user = ?", (username,))
        user_row = cursor.fetchone()
        if not user_row: return None
        
        # Create user object with role from user table
        user_role = models.Role(
            id=0,
            nombre_role=user_row["role"],
            posicion=None,
            is_jefe=False
        )
        
        return models.Usuario(
            id=user_row["id"],
            username=user_row["user"],
            password_hash=user_row["password"],
            roles=[user_role],
            permiso=bool(user_row["permiso"] if "permiso" in user_row else True)
        )
    except sqlite3.Error as e:
        print(f"Database error in get_user_by_username: {e}")
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
    """Obtiene negociados desde la tabla negociados """
    conn = None; roles_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        cursor.execute("SELECT id, nombre FROM negociados")
        for row in cursor.fetchall(): roles_list.append(models.Role(id=row["id"], nombre_role=row["nombre"]))
    except sqlite3.Error as e: print(f"Database error in get_negociados: {e}")
    return roles_list

def get_cargos() -> List[models.Role]:
    """Obtiene cargos desde la tabla JEFES donde posicion no es NULL"""
    conn = None; roles_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        cursor.execute("SELECT id, nombre, posicion FROM JEFES WHERE posicion IS NOT NULL ORDER BY posicion")
        for row in cursor.fetchall(): roles_list.append(models.Role(id=row["id"], nombre_role=row["nombre"], posicion=row["posicion"], is_jefe=True))
    except sqlite3.Error as e: print(f"Database error in get_cargos: {e}")
    return roles_list

def get_canales() -> List[models.CanalEntrada]:
    """Obtiene canales de entrada (solo nombre ya que no hay columna id)"""
    conn = None; canales_list = []
    try:
        conn = get_db_connection(); cursor = conn.cursor()
        cursor.execute("SELECT nombre FROM canalesEntrada")
        for row in cursor.fetchall(): canales_list.append(models.CanalEntrada(id=0, nombre=row["nombre"]))  # Usamos id=0 temporal
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
def get_entradas_for_user(
    user: models.Usuario, 
    date_from_str: Optional[str] = None, # Renamed from date_str for clarity if used as range
    date_to_str: Optional[str] = None,   # New parameter for date range
    area_filter: Optional[str] = None, 
    category_filter: Optional[str] = None, 
    search_term: Optional[str] = None, 
    search_field: Optional[str] = None,
    pending_processing: Optional[bool] = None,      # New parameter
    pending_view_for_jefe_id: Optional[int] = None  # New parameter
) -> List[models.Entrada]:

    if not user or not user.roles: return [] # Basic check, might need refinement based on how roles are used for filtering
    
    conn = None; entradas_list = []
    try:
        import logging
        logging.basicConfig(level=logging.DEBUG)
        logger = logging.getLogger(__name__)
        
        conn = get_db_connection(); cursor = conn.cursor()
        sql = """SELECT DISTINCT e.id, e.Asunto, e.Fecha, e.Area AS AreaName, e.Confidencial, e.Urgente, e.numeroEntrada, e.Tramitado, e.Observaciones, e.tramitadoPor, uc.user AS Tramitado_por_username, e.canalEntrada AS CanalNombre, e.canalEntrada AS CanalId
                FROM entrada e
                LEFT JOIN destinatario d ON e.id = d.entrada_id
                LEFT JOIN CATEGORIA_ENTRADA ce ON e.id = ce.entrada_id
                LEFT JOIN user uc ON e.tramitadoPor = uc.id
                LEFT JOIN negociados n ON d.negociado_id = n.id"""
        params = {} # Using named parameters for clarity
        
        # Base WHERE clause for user roles (assuming user sees entradas for their negociados)
        # This part needs to correctly determine which entradas a user can see based on their roles.
        # The existing query joins with 'destinatario d' and 'negociados n'.
        # It checks if 'n.nombre' is in user_role_names.
        # This assumes 'user.roles' contains Role objects whose 'nombre_role' matches 'negociados.nombre'.
        user_role_names = [role.nombre_role for role in user.roles if hasattr(role, 'nombre_role')]
        if not user_role_names:
             # If a user has no roles, they might see no entradas, or this check might be too restrictive.
             # For now, keeping original logic: if no roles, no entries.
            return [] 
        
        # Using IN clause with dynamically generated placeholders for role names
        # This is safer if role names can have special characters, but sqlite named params don't directly support lists.
        # A common workaround is to generate `(:role1, :role2, ...)` and add params.
        role_placeholders = ', '.join([f':role{i}' for i in range(len(user_role_names))])
        where_clauses.append(f"n.nombre IN ({role_placeholders})")
        for i, name in enumerate(user_role_names):
            params[f'role{i}'] = name

        # Date filtering
        if date_from_str:
            try: datetime.datetime.strptime(date_from_str, "%Y-%m-%d"); where_clauses.append("e.Fecha >= :date_from"); params['date_from'] = date_from_str
            except ValueError: print(f"Warning: Invalid date_from format '{date_from_str}'. Filter ignored.")
        if date_to_str:
            try: datetime.datetime.strptime(date_to_str, "%Y-%m-%d"); where_clauses.append("e.Fecha <= :date_to"); params['date_to'] = date_to_str
            except ValueError: print(f"Warning: Invalid date_to format '{date_to_str}'. Filter ignored.")
        
        if area_filter and area_filter.lower() != "todos": 
            where_clauses.append("e.Area = :area_filter"); params['area_filter'] = area_filter
        
        if category_filter and category_filter.lower() != "todas": 
            # This requires CATEGORIA_ENTRADA to be joined if not already part of the main FROM
            # Assuming 'ce' is the alias for CATEGORIA_ENTRADA if joined for this filter
            where_clauses.append("ce.CATEGORIA = :category_filter"); params['category_filter'] = category_filter
        
        if search_term and search_field in ["Asunto", "numeroEntrada", "Observaciones"]: # Use actual column names
             # Ensure search_field is safe if it comes directly from user input
            safe_search_field = search_field 
            if safe_search_field == "numeroEntrada": safe_search_field = "numeroEntrada" # map to db column
            elif safe_search_field == "Observaciones": safe_search_field = "Observaciones"
            else: safe_search_field = "Asunto" # Default
            where_clauses.append(f"e.{safe_search_field} LIKE :search_term"); params['search_term'] = f"%{search_term}%"

        # New filters
        if pending_processing:
            where_clauses.append("e.Tramitado = 0") # Assuming 0 is False for Tramitado

        if pending_view_for_jefe_id is not None:
            # This subquery checks if there's any comment on the entrada (e.id)
            # that has c.visto = 0. This is a simplified interpretation.
            # A more accurate one would involve a ComentarioVisto table.
            where_clauses.append("""EXISTS (
                SELECT 1 FROM comentario c
                WHERE c.entrada_id = e.id AND c.visto = 0
            )""")
            # And ensure the entrada is relevant to this jefe (already part of main user role filter)

        if where_clauses: 
            sql += " WHERE " + " AND ".join(where_clauses)
        
        sql += " ORDER BY e.Fecha DESC, e.id DESC"
        
        logger.debug(f"Executing SQL: {sql} with params: {params}")
        cursor.execute(sql, params)
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
        sql_update_entrada = "UPDATE entrada SET Asunto = ?, Fecha = ?, Area = ?, Confidencial = ?, Urgente = ?, numeroEntrada = ?, Tramitado = ?, Observaciones = ?, tramitadoPor = ? WHERE id = ?"
        primary_area_name = entrada_obj.area.nombre_role if entrada_obj.area else None
        fecha_str = entrada_obj.fecha.strftime("%Y-%m-%d") if isinstance(entrada_obj.fecha, datetime.date) else entrada_obj.fecha
        tramitado_por_id_to_set = updated_by_user.id if entrada_obj.tramitado else None # Simplified logic
        params_update_entrada = (entrada_obj.asunto, fecha_str, primary_area_name, entrada_obj.confidencial, entrada_obj.urgente, entrada_obj.numero_entrada, entrada_obj.tramitado, entrada_obj.observaciones, tramitado_por_id_to_set, entrada_obj.id)
        cursor.execute(sql_update_entrada, params_update_entrada)
        cursor.execute("DELETE FROM destinatario WHERE entrada_id = ?", (entrada_obj.id,))
        # Actualizar destinatarios negociados
        for negociado_nombre in destinatario_areas_nombres:
            cursor.execute("SELECT id FROM negociados WHERE nombre = ?", (negociado_nombre,))
            negociado_row = cursor.fetchone()
            if negociado_row:
                cursor.execute("INSERT INTO destinatario (entrada_id, negociado_id) VALUES (?, ?)",
                             (entrada_obj.id, negociado_row['id']))
        
        # Actualizar destinatarios jefes
        for jefe_nombre in destinatario_jefes_nombres:
            jefe_nombre_clean = jefe_nombre.split('(')[0].strip()
            cursor.execute("SELECT id FROM JEFES WHERE nombre = ?", (jefe_nombre_clean,))
            jefe_row = cursor.fetchone()
            if jefe_row:
                cursor.execute("INSERT INTO destinatarioJefe (entrada_id, jefe_id) VALUES (?, ?)",
                             (entrada_obj.id, jefe_row['id']))
        cursor.execute("DELETE FROM CATEGORIA_ENTRADA WHERE entrada_id = ?", (entrada_obj.id,))
        for cat_nombre in categoria_nombres: cursor.execute("INSERT INTO CATEGORIA_ENTRADA (entrada_id, CATEGORIA) VALUES (?, ?)", (entrada_obj.id, cat_nombre))
        cursor.execute("UPDATE entrada SET canalEntrada = ? WHERE id = ?", (canal_entrada_nombre, entrada_obj.id))
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
        print("DEBUG: Iniciando save_new_entrada")
        conn = get_db_connection(); cursor = conn.cursor(); conn.execute("BEGIN TRANSACTION")
        
        # Insertar en tabla entrada (incluyendo canalEntrada directamente)
        sql_entrada = """INSERT INTO entrada
                        (Asunto, Fecha, Area, Confidencial, Urgente, numeroEntrada,
                         Tramitado, Observaciones, tramitadoPor, canalEntrada)
                        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"""
        primary_area_name = entrada_data.area.nombre_role if entrada_data.area else None
        fecha_str = entrada_data.fecha.strftime("%Y-%m-%d") if isinstance(entrada_data.fecha, datetime.date) else entrada_data.fecha
        params_entrada = (entrada_data.asunto, fecha_str, primary_area_name,
                         entrada_data.confidencial, entrada_data.urgente,
                         entrada_data.numero_entrada, False, entrada_data.observaciones,
                         current_user.id, canal_entrada_nombre)
        
        cursor.execute(sql_entrada, params_entrada)
        new_entrada_id = cursor.lastrowid or cursor.execute("SELECT last_insert_rowid()").fetchone()[0]
        if not new_entrada_id:
            raise sqlite3.Error("Failed to get new_entrada_id")
        
        # Insertar en tablas relacionadas
        # 1. Destinatarios (negociados)
        for area_nombre in destinatario_areas_nombres:
            cursor.execute("INSERT INTO destinatario (entrada_id, negociado_id) VALUES (?, (SELECT id FROM negociados WHERE nombre = ?))",
                          (new_entrada_id, area_nombre))
        
        # 2. Destinatarios (jefes)
        for jefe_nombre in destinatario_jefes_nombres:
            cursor.execute("INSERT INTO destinatarioJefe (entrada_id, jefe_id) VALUES (?, (SELECT id FROM JEFES WHERE nombre = ?))",
                          (new_entrada_id, jefe_nombre))
        
        # 3. Categorías
        for categoria_nombre in categoria_nombres:
            cursor.execute("INSERT INTO CATEGORIA_ENTRADA (ENTRADA_ID, CATEGORIA) VALUES (?, ?)",
                          (new_entrada_id, categoria_nombre))
        
        conn.commit()
        return new_entrada_id
        # Insertar destinatarios negociados
        for negociado_nombre in destinatario_areas_nombres:
            cursor.execute("SELECT id FROM negociados WHERE nombre = ?", (negociado_nombre,))
            negociado_row = cursor.fetchone()
            if negociado_row:
                cursor.execute("INSERT INTO destinatario (entrada_id, negociado_id) VALUES (?, ?)",
                             (new_entrada_id, negociado_row['id']))
        
        # Insertar destinatarios jefes
        for jefe_nombre in destinatario_jefes_nombres:
            jefe_nombre_clean = jefe_nombre.split('(')[0].strip()
            cursor.execute("SELECT id FROM JEFES WHERE nombre = ?", (jefe_nombre_clean,))
            jefe_row = cursor.fetchone()
            if jefe_row:
                cursor.execute("INSERT INTO destinatarioJefe (entrada_id, jefe_id) VALUES (?, ?)",
                             (new_entrada_id, jefe_row['id']))
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
        sql_main = "SELECT e.id, e.Asunto, e.Fecha, e.Area AS AreaName, e.Confidencial, e.Urgente, e.numeroEntrada, e.Tramitado, e.Observaciones, e.tramitadoPor, u.user AS Tramitado_por_username, e.canalEntrada AS CanalNombre FROM entrada e LEFT JOIN user u ON e.tramitadoPor = u.id WHERE e.id = ?"
        cursor.execute(sql_main, (entrada_id,)); row = cursor.fetchone()
        if not row: return None
        dest_sql = "SELECT negociado_id FROM destinatario WHERE entrada_id = ?"; cursor.execute(dest_sql, (entrada_id,)); destinatario_roles = [models.Role(nombre_role=str(r_row["negociado_id"])) for r_row in cursor.fetchall() if r_row["negociado_id"]]
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


# --- User Management Specific Functions ---

def get_all_users_with_details() -> List[models.Usuario]:
    """Fetches all users with their roles and permiso status."""
    conn = None
    users_list = []
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # Assuming 'permiso' is a column in the 'user' table.
        # The DDL in if __name__ == '__main__' does not explicitly add 'permiso' to 'user' table.
        # Adding it to the query for now; if it doesn't exist, it might error or return None.
        # Ensure 'user' table has 'permiso' or adjust query.
        # For testing, let's assume 'permiso' column exists or we default it.
        cursor.execute("SELECT id, user, password, permiso FROM user") # Added 'permiso'
        all_users_rows = cursor.fetchall()

        for user_row in all_users_rows:
            user_id = user_row["id"]
            roles_query = """
                SELECT r.id, r.nombre_role, r.posicion, r.isJefe 
                FROM role r 
                JOIN usuario_role ur ON r.id = ur.role_id 
                WHERE ur.user_id = ?
            """
            cursor.execute(roles_query, (user_id,))
            user_roles_rows = cursor.fetchall()
            user_roles = [
                models.Role(id=r["id"], nombre_role=r["nombre_role"], posicion=r["posicion"], is_jefe=bool(r["isJefe"]))
                for r in user_roles_rows
            ]
            
            # Handle 'permiso' potentially missing from older schema in some DBs
            permiso_val = False # Default if column doesn't exist or is NULL
            if 'permiso' in user_row.keys(): # Check if column exists in result
                 permiso_val = bool(user_row["permiso"])
            else:
                # Fallback or specific logic if 'permiso' is managed differently (e.g. via a role)
                # For now, if no 'permiso' column, assume False or derive from roles if logic exists.
                # Example: if 'Admin' role implies permiso:
                # if any(r.nombre_role == 'Admin' for r in user_roles): permiso_val = True
                pass


            users_list.append(models.Usuario(
                id=user_id,
                username=user_row["user"],
                password_hash=user_row["password"], # Usually not needed for display but part of model
                roles=user_roles,
                permiso=permiso_val 
            ))
        return users_list
    except sqlite3.Error as e:
        print(f"Database error in get_all_users_with_details: {e}")
        return []

def get_all_roles() -> List[models.Role]:
    """Fetches all available roles from the role table."""
    conn = None
    roles_list = []
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        cursor.execute("SELECT id, nombre_role, posicion, isJefe FROM role ORDER BY nombre_role")
        for row in cursor.fetchall():
            roles_list.append(models.Role(
                id=row["id"], 
                nombre_role=row["nombre_role"],
                posicion=row["posicion"],
                is_jefe=bool(row["isJefe"])
            ))
        return roles_list
    except sqlite3.Error as e:
        print(f"Database error in get_all_roles: {e}")
        return []

def save_user_with_details(username: str, password_plaintext: str, role_ids: List[int], permiso: bool) -> Optional[int]:
    """Saves a new user with their roles and permission status."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        conn.execute("BEGIN TRANSACTION")

        hashed_password = password_plaintext # Default if no crypto
        try:
            from . import crypto_utils
            hashed_password = crypto_utils.hash_password(password_plaintext)
        except ImportError:
            print("Warning: crypto_utils not available for hashing new user password.")

        # Insert into user table. Ensure 'permiso' column exists or handle.
        # The DDL in if __name__ == '__main__' needs 'permiso' column for user table:
        # CREATE TABLE IF NOT EXISTS user (..., permiso INTEGER DEFAULT 0)
        cursor.execute(
            "INSERT INTO user (user, password, permiso, reset) VALUES (?, ?, ?, 0)",
            (username, hashed_password, 1 if permiso else 0) # Storing boolean as 0 or 1
        )
        new_user_id = cursor.lastrowid
        if not new_user_id:
            raise sqlite3.Error("Failed to get new_user_id after insert.")

        # Insert into usuario_role
        for role_id in role_ids:
            cursor.execute("INSERT INTO usuario_role (user_id, role_id) VALUES (?, ?)", (new_user_id, role_id))
        
        conn.commit()
        return new_user_id
    except sqlite3.IntegrityError as ie: # e.g. UNIQUE constraint failed for username
        if conn: conn.rollback()
        print(f"Database integrity error in save_user_with_details (user '{username}'): {ie}")
        return None
    except sqlite3.Error as e:
        if conn: conn.rollback()
        print(f"Database error in save_user_with_details (user '{username}'): {e}")
        return None

def update_user_permission(user_id: int, permiso: bool) -> bool:
    """Updates the permission status for a given user."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # Ensure 'permiso' column exists in 'user' table.
        cursor.execute("UPDATE user SET permiso = ? WHERE id = ?", (1 if permiso else 0, user_id))
        conn.commit()
        return cursor.rowcount > 0
    except sqlite3.Error as e:
        print(f"Database error in update_user_permission for user_id {user_id}: {e}")
        return False

def delete_user_cascade(user_id: int) -> bool:
    """Deletes a user and their role assignments. Does not handle FKs in other tables like 'entrada' yet."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        conn.execute("BEGIN TRANSACTION")

        # Delete role assignments
        cursor.execute("DELETE FROM usuario_role WHERE user_id = ?", (user_id,))
        
        # Delete user
        # Note: If user_id is referenced by 'entrada.tramitadoPor', this might fail
        # if no ON DELETE SET NULL/CASCADE is defined on that FK.
        # For this task, we attempt deletion. Production code would need robust FK handling.
        cursor.execute("DELETE FROM user WHERE id = ?", (user_id,))
        
        if cursor.rowcount == 0: # User not found or not deleted
            conn.rollback()
            print(f"User with ID {user_id} not found or could not be deleted (possibly due to FK constraints).")
            return False
            
        conn.commit()
        return True
    except sqlite3.Error as e:
        if conn: conn.rollback()
        print(f"Database error in delete_user_cascade for user_id {user_id}: {e}")
        return False

# --- End of User Management Specific Functions ---

# --- Metadata Management Functions (Negociados, Cargos, Canales, Categorias) ---

# Negociados
def add_negociado(name: str) -> Optional[int]:
    """Adds a new negociado. Returns new ID or None."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # 'negociados' table DDL: id INTEGER PRIMARY KEY, nombre TEXT UNIQUE
        cursor.execute("INSERT INTO negociados (nombre) VALUES (?)", (name,))
        conn.commit()
        return cursor.lastrowid
    except sqlite3.IntegrityError: 
        print(f"Negociado '{name}' already exists.")
        if conn: conn.rollback() # Rollback even for integrity error if transaction started implicitly
        return None
    except sqlite3.Error as e:
        print(f"Database error in add_negociado: {e}")
        if conn: conn.rollback()
        return None

def delete_negociado(negociado_id: int) -> bool:
    """Deletes a negociado by its ID."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # Note: FKs in 'destinatario' table reference 'negociados.id'.
        # Deleting a 'negociado' in use will fail if FK constraints are enforced without ON DELETE CASCADE/SET NULL.
        # For this task, direct deletion is attempted.
        cursor.execute("DELETE FROM negociados WHERE id = ?", (negociado_id,))
        conn.commit()
        return cursor.rowcount > 0
    except sqlite3.Error as e: # Catches IntegrityError if FK constraint fails
        print(f"Database error in delete_negociado (id: {negociado_id}): {e}")
        if conn: conn.rollback()
        return False

# Cargos (Jefes)
def add_cargo(name: str, posicion: Optional[str]) -> Optional[int]:
    """Adds a new cargo (Jefe). Returns new ID or None."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # 'JEFES' table DDL: id INTEGER PRIMARY KEY, nombre TEXT UNIQUE, user_id INTEGER (FK to user)
        # The 'posicion' is handled by the 'role' table in a more normalized schema.
        # Here, assuming 'JEFES' table is the target as per get_cargos().
        # The DDL for JEFES in __main__ doesn't show 'posicion'. get_cargos() does.
        # This implies 'JEFES' table might be a specific view or needs 'posicion'.
        # For now, assuming 'JEFES' has 'nombre' and 'posicion' can be added or is part of 'role' table.
        # Let's assume we are adding to 'role' table with is_jefe=True and 'posicion'.
        cursor.execute("INSERT INTO role (nombre_role, posicion, isJefe) VALUES (?, ?, ?)", (name, posicion, 1))
        conn.commit()
        return cursor.lastrowid
    except sqlite3.IntegrityError:
        print(f"Cargo/Role '{name}' already exists.")
        if conn: conn.rollback()
        return None
    except sqlite3.Error as e:
        print(f"Database error in add_cargo: {e}")
        if conn: conn.rollback()
        return None

def delete_cargo(cargo_id: int) -> bool:
    """Deletes a cargo/role by its ID."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # Assumes cargo_id refers to 'role.id' for a role that is a Jefe.
        # Consider FKs (e.g., 'usuario_role', 'destinatarioJefe' which uses JEFES.id).
        # If 'JEFES' table is separate and its ID is passed, then "DELETE FROM JEFES WHERE id = ?"
        # If it's from 'role' table:
        cursor.execute("DELETE FROM role WHERE id = ? AND isJefe = 1", (cargo_id,))
        conn.commit()
        return cursor.rowcount > 0
    except sqlite3.Error as e:
        print(f"Database error in delete_cargo (id: {cargo_id}): {e}")
        if conn: conn.rollback()
        return False

# Canales de Entrada
def add_canal(name: str) -> Optional[int]:
    """Adds a new canal de entrada. Returns new ID or None."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # 'canalesEntrada' table DDL: id INTEGER PRIMARY KEY, nombre TEXT UNIQUE
        cursor.execute("INSERT INTO canalesEntrada (nombre) VALUES (?)", (name,))
        conn.commit()
        return cursor.lastrowid
    except sqlite3.IntegrityError:
        print(f"Canal '{name}' already exists.")
        if conn: conn.rollback()
        return None
    except sqlite3.Error as e:
        print(f"Database error in add_canal: {e}")
        if conn: conn.rollback()
        return None

def delete_canal(canal_id: int) -> bool:
    """Deletes a canal de entrada by its ID."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # 'entrada.canalEntrada' is TEXT. Deleting from 'canalesEntrada' doesn't break FKs based on current schema.
        # If 'entrada.canalEntrada' were an ID, this would need more care.
        cursor.execute("DELETE FROM canalesEntrada WHERE id = ?", (canal_id,))
        conn.commit()
        return cursor.rowcount > 0
    except sqlite3.Error as e:
        print(f"Database error in delete_canal (id: {canal_id}): {e}")
        if conn: conn.rollback()
        return False

# Categorías
def add_categoria(name: str) -> Optional[int]:
    """Adds a new categoria. Returns new ID or None."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # 'CATEGORIA' table DDL: id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT UNIQUE
        cursor.execute("INSERT INTO CATEGORIA (nombre) VALUES (?)", (name,))
        conn.commit()
        return cursor.lastrowid
    except sqlite3.IntegrityError:
        print(f"Categoría '{name}' already exists.")
        if conn: conn.rollback()
        return None
    except sqlite3.Error as e:
        print(f"Database error in add_categoria: {e}")
        if conn: conn.rollback()
        return None

def delete_categoria(categoria_id: int) -> bool:
    """Deletes a categoria by its ID."""
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        # FK in 'CATEGORIA_ENTRADA' references 'CATEGORIA.nombre' not 'CATEGORIA.id' as per DDL.
        # If it referenced ID, this delete would need care.
        # To be safe, let's delete from CATEGORIA_ENTRADA first if using name, or ensure consistency.
        # For now, assuming direct delete from CATEGORIA by ID is the goal.
        # If CATEGORIA_ENTRADA.CATEGORIA is name, we'd need to fetch name for ID first to clean related table.
        # Given the DDL (id PK for CATEGORIA), we delete by ID.
        # If CATEGORIA_ENTRADA.CATEGORIA is a FK to CATEGORIA.nombre, then this is fine.
        # If CATEGORIA_ENTRADA.CATEGORIA is a FK to CATEGORIA.id (more typical), then this delete could fail if in use.
        cursor.execute("DELETE FROM CATEGORIA WHERE id = ?", (categoria_id,))
        conn.commit()
        return cursor.rowcount > 0
    except sqlite3.Error as e:
        print(f"Database error in delete_categoria (id: {categoria_id}): {e}")
        if conn: conn.rollback()
        return False

# --- End of Metadata Management Functions ---


def delete_entrada_cascade(entrada_id: int) -> bool:
    """
    Deletes an entrada and all its associated data, including files from the filesystem.
    """
    conn = None
    try:
        conn = get_db_connection()
        cursor = conn.cursor()
        conn.execute("BEGIN TRANSACTION")

        # 1. Fetch file paths for all associated files before deleting records
        file_paths_to_delete = []
        cursor.execute("SELECT ruta_archivo FROM files WHERE entrada_id = ?", (entrada_id,))
        file_paths_to_delete.extend([row['ruta_archivo'] for row in cursor.fetchall()])
        
        cursor.execute("SELECT ruta_archivo FROM antecedentesFiles WHERE entrada_id = ?", (entrada_id,))
        file_paths_to_delete.extend([row['ruta_archivo'] for row in cursor.fetchall()])
        
        # For salidaFiles, also need to delete from vistoBuenoJefes first
        cursor.execute("SELECT id, ruta_archivo FROM salidaFiles WHERE entrada_id = ?", (entrada_id,))
        salida_files_to_process = [{'id': row['id'], 'path': row['ruta_archivo']} for row in cursor.fetchall()]
        for sf in salida_files_to_process:
            file_paths_to_delete.append(sf['path'])
            cursor.execute("DELETE FROM vistoBuenoJefes WHERE salida_file_id = ?", (sf['id'],))

        # 2. Delete from related tables
        cursor.execute("DELETE FROM comentario WHERE entrada_id = ?", (entrada_id,))
        cursor.execute("DELETE FROM destinatario WHERE entrada_id = ?", (entrada_id,))
        cursor.execute("DELETE FROM destinatarioJefe WHERE entrada_id = ?", (entrada_id,))
        cursor.execute("DELETE FROM CATEGORIA_ENTRADA WHERE entrada_id = ?", (entrada_id,))
        
        # Delete from file tables
        cursor.execute("DELETE FROM files WHERE entrada_id = ?", (entrada_id,))
        cursor.execute("DELETE FROM antecedentesFiles WHERE entrada_id = ?", (entrada_id,))
        cursor.execute("DELETE FROM salidaFiles WHERE entrada_id = ?", (entrada_id,))
        
        # 3. Finally, delete the entrada itself
        cursor.execute("DELETE FROM entrada WHERE id = ?", (entrada_id,))
        
        # 4. Delete files from filesystem
        # Decrypting before deleting is not implemented as keys are not readily available/managed for this.
        # Files are deleted as they are (potentially encrypted).
        for file_path in file_paths_to_delete:
            if file_path: # Ensure path is not None or empty
                try:
                    # TODO: Implement decryption here if files are encrypted and need to be decrypted before deletion
                    # For now, assuming direct deletion or that crypto_utils handles encrypted files if necessary for os.remove
                    # If files are stored encrypted with a specific extension, that needs to be handled.
                    # If os.remove can delete the encrypted file directly, this is fine.
                    if os.path.exists(file_path):
                         os.remove(file_path)
                         print(f"Successfully deleted file: {file_path}")
                    else:
                         print(f"File not found, skipping deletion: {file_path}")
                except FileNotFoundError:
                    print(f"File not found during deletion attempt: {file_path}")
                except OSError as oe: # Catch other OS errors like permission issues
                    print(f"Error deleting file {file_path}: {oe}")
        
        conn.commit()
        return True

    except sqlite3.Error as e:
        if conn:
            conn.rollback()
        print(f"Database error in delete_entrada_cascade for entrada_id {entrada_id}: {e}")
        return False
    except Exception as ex: # Catch any other unexpected errors
        if conn:
            conn.rollback()
        print(f"Unexpected error in delete_entrada_cascade for entrada_id {entrada_id}: {ex}")
        return False


# Example of how to use the functions (optional, for testing)
if __name__ == '__main__':
    print("Running database_manager.py tests...")
    if not os.path.exists("CONFIG.CFG"):
        with open("CONFIG.CFG", "w") as f: f.write(f"BASE_DIR = {os.getcwd()}\nLOCAL_DIR = {os.getcwd()}\n")
    try:
        conn = get_db_connection(); print(f"DB Connection: {conn}"); cursor = conn.cursor()
        # DDL statements (user, role, usuario_role, canalesEntrada, CATEGORIA, negociados, JEFES, entrada, destinatario, destinatarioJefe, CATEGORIA_ENTRADA, files, antecedentesFiles, salidaFiles, vistoBuenoJefes, comentario)
        cursor.execute("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY, user TEXT UNIQUE, password TEXT, reset INTEGER DEFAULT 0, isJefe INTEGER DEFAULT 0, posicionJefe INTEGER)")
        cursor.execute("CREATE TABLE IF NOT EXISTS role (id INTEGER PRIMARY KEY, nombre_role TEXT UNIQUE, posicion TEXT, isJefe INTEGER DEFAULT 0)")
        cursor.execute("CREATE TABLE IF NOT EXISTS usuario_role (user_id INTEGER, role_id INTEGER, FOREIGN KEY(user_id) REFERENCES user(id), FOREIGN KEY(role_id) REFERENCES role(id), PRIMARY KEY(user_id, role_id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS canalesEntrada (id INTEGER PRIMARY KEY, nombre TEXT UNIQUE)")
        cursor.execute("CREATE TABLE IF NOT EXISTS CATEGORIA (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT UNIQUE)")
        cursor.execute("CREATE TABLE IF NOT EXISTS negociados (id INTEGER PRIMARY KEY, nombre TEXT UNIQUE)")
        cursor.execute("CREATE TABLE IF NOT EXISTS JEFES (id INTEGER PRIMARY KEY, nombre TEXT UNIQUE, user_id INTEGER, FOREIGN KEY(user_id) REFERENCES user(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS entrada (id INTEGER PRIMARY KEY AUTOINCREMENT, Asunto TEXT, Fecha TEXT, Area TEXT, Confidencial INTEGER DEFAULT 0, Urgente INTEGER DEFAULT 0, numeroEntrada TEXT UNIQUE, Tramitado INTEGER DEFAULT 0, Observaciones TEXT, tramitadoPor INTEGER, FOREIGN KEY(tramitadoPor) REFERENCES user(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS destinatario (id INTEGER PRIMARY KEY AUTOINCREMENT, entrada_id INTEGER, negociado_id INTEGER, FOREIGN KEY(entrada_id) REFERENCES entrada(id), FOREIGN KEY(negociado_id) REFERENCES negociados(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS destinatarioJefe (id INTEGER PRIMARY KEY AUTOINCREMENT, entrada_id INTEGER, jefe_id INTEGER, FOREIGN KEY(entrada_id) REFERENCES entrada(id), FOREIGN KEY(jefe_id) REFERENCES JEFES(id))")
        cursor.execute("CREATE TABLE IF NOT EXISTS CATEGORIA_ENTRADA (entrada_id INTEGER, CATEGORIA TEXT, FOREIGN KEY(entrada_id) REFERENCES entrada(id), PRIMARY KEY(entrada_id, CATEGORIA))")
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
            cursor.execute("INSERT OR IGNORE INTO user (id, user, password, reset, isJefe, posicionJefe) VALUES (62, 'coroneluser', 'hashed_password', 0, 1, 200)")
            cursor.execute("INSERT OR IGNORE INTO role (id, nombre_role, posicion, isJefe) VALUES (1, 'Admin', NULL, 0), (2, 'Manager', '1', 1), (3, 'Worker', '2', 0), (4, 'DepartmentA', NULL, 0), (5, 'WorkerArea', NULL, 0)")
            cursor.execute("INSERT OR IGNORE INTO negociados (id, nombre) VALUES (4, 'DepartmentA'), (5, 'WorkerArea'), (6, 'Registro')")
            cursor.execute("INSERT OR IGNORE INTO JEFES (id, nombre, user_id) VALUES (1, 'Manager Jefe', 3), (2, 'CORONEL', 62)")
            cursor.execute("INSERT OR IGNORE INTO usuario_role (user_id, role_id) VALUES (1, 3), (3, 2)")
            cursor.execute("INSERT OR IGNORE INTO canalesEntrada (id, nombre) VALUES (1, 'Email'), (2, 'Web Form')")
            cursor.execute("INSERT OR IGNORE INTO CATEGORIA (nombre) VALUES ('General'), ('Urgent'), ('Projects'), ('Tasks'), ('TestCategory')")
            cursor.execute("INSERT OR IGNORE INTO entrada (id, Asunto, Fecha, Area, Confidencial, Urgente, numeroEntrada, Tramitado, Tramitado_por_id) VALUES (1, 'Urgent Project Alpha', '2023-10-26', 'DepartmentA', 1, 1, 'E2023-001', 0, NULL)")
            cursor.execute("INSERT OR IGNORE INTO destinatario (entrada_id, negociado_id) VALUES (1, 4)")  # 4 = DepartmentA
            cursor.execute("INSERT OR IGNORE INTO CATEGORIA_ENTRADA (entrada_id, CATEGORIA) VALUES (1, 'Urgent'), (1, 'Projects')")
            cursor.execute("UPDATE entrada SET canalEntrada = 'Email' WHERE id = 1")
            cursor.execute("INSERT OR IGNORE INTO files (id, ruta_archivo, entrada_id, fecha, asunto, origen, observaciones) VALUES (1, '/files/e1_doc1.pdf', 1, '2023-10-26', 'Documento Principal E1', 'ClienteX', 'Obs doc1 e1')")
            cursor.execute("INSERT OR IGNORE INTO antecedentesFiles (id, ruta_archivo, entrada_id, fecha, asunto, destino, observaciones, tipo) VALUES (1, '/antecedentes/e1_ant1.docx', 1, '2023-10-25', 'Antecedente Clave E1', 'Interno', 'Obs ant1 e1', 'Informe')")
            cursor.execute("INSERT OR IGNORE INTO salidaFiles (id, ruta_archivo, entrada_id, fecha, asunto, destino, visto_bueno_general) VALUES (1, '/salidas/e1_sal1.pdf', 1, '2023-10-28', 'Respuesta ClienteX E1', 'ClienteX', 0)")
            cursor.execute("INSERT OR IGNORE INTO vistoBuenoJefes (salida_file_id, user_id, visto_bueno_status) VALUES (1, 3, 1)")
            cursor.execute("INSERT OR IGNORE INTO entrada (id, Asunto, Fecha, Area, Confidencial, Urgente, numeroEntrada, Tramitado, Tramitado_por_id) VALUES (2, 'General Inquiry Beta', '2023-10-27', 'WorkerArea', 0, 0, 'E2023-002', 1, 1)")
            cursor.execute("INSERT OR IGNORE INTO destinatario (entrada_id, negociado_id) VALUES (2, 5)")  # 5 = WorkerArea
            cursor.execute("INSERT OR IGNORE INTO CATEGORIA_ENTRADA (entrada_id, CATEGORIA) VALUES (2, 'General')")
            cursor.execute("UPDATE entrada SET canalEntrada = 'Web Form' WHERE id = 2")
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


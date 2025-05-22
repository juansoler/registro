import datetime
from typing import List, Optional

class Usuario:
    def __init__(self, id: int, username: str, password_hash: str, roles: List['Role']):
        self.id = id
        self.username = username
        self.password_hash = password_hash
        self.roles = roles

    # Placeholder for password checking/setting methods
    def set_password(self, password: str):
        # In a real app, hash the password here
        self.password_hash = f"hashed_{password}" # Replace with actual hashing
        print(f"Password set for {self.username}")

    def check_password(self, password: str) -> bool:
        # In a real app, compare hashed passwords
        return self.password_hash == f"hashed_{password}" # Replace with actual hash comparison

class Role:
    def __init__(self, nombre_role: str, id: Optional[int] = None, posicion: Optional[str] = None, is_jefe: bool = False):
        self.id = id
        self.nombre_role = nombre_role
        self.posicion = posicion
        self.is_jefe = is_jefe

class CanalEntrada:
    def __init__(self, nombre: str, id: Optional[int] = None):
        self.id = id
        self.nombre = nombre

class Categoria:
    def __init__(self, nombre: str):
        self.nombre = nombre # Assuming nombre is the primary identifier

class Comentario:
    def __init__(self, id: int, entrada_id: int, fecha: datetime.date, hora: datetime.time, 
                 visto: bool, usuario: Usuario, texto_comentario: str, posicion_usuario: Optional[str] = None):
        self.id = id
        self.entrada_id = entrada_id
        self.fecha = fecha
        self.hora = hora
        self.visto = visto
        self.usuario = usuario # Could be Usuario object or username string
        self.texto_comentario = texto_comentario
        self.posicion_usuario = posicion_usuario

class Archivo:
    def __init__(self, id: int, ruta_archivo: str, entrada_id: int, fecha: datetime.date, 
                 asunto: str, origen_destino: str, observaciones: Optional[str] = None, tipo: Optional[str] = None):
        self.id = id
        self.ruta_archivo = ruta_archivo
        self.entrada_id = entrada_id
        self.fecha = fecha
        self.asunto = asunto
        self.origen_destino = origen_destino # 'origen' for files, 'destino' for antecedentes
        self.observaciones = observaciones
        self.tipo = tipo # For 'antecedentesFiles'

class VistoBuenoJefe:
    def __init__(self, id: int, salida_file_id: int, usuario: Usuario, visto_bueno_status: bool):
        self.id = id
        self.salida_file_id = salida_file_id
        self.usuario = usuario # Could be Usuario object or username string
        self.visto_bueno_status = visto_bueno_status

class ArchivoSalida:
    def __init__(self, id: int, ruta_archivo: str, entrada_id: int, fecha: datetime.date, 
                 asunto: str, destino: str, visto_bueno_general: bool, 
                 visto_bueno_jefes: Optional[List[VistoBuenoJefe]] = None):
        self.id = id
        self.ruta_archivo = ruta_archivo
        self.entrada_id = entrada_id
        self.fecha = fecha
        self.asunto = asunto
        self.destino = destino
        self.visto_bueno_general = visto_bueno_general
        self.visto_bueno_jefes = visto_bueno_jefes if visto_bueno_jefes is not None else []

class Entrada:
    def __init__(self, id: int, asunto: str, fecha: datetime.date, area: Role, 
                 confidencial: bool, urgente: bool, numero_entrada: str, 
                 tramitado: bool, canal_entrada: CanalEntrada,
                 observaciones: Optional[str] = None, 
                 tramitado_por: Optional[Usuario] = None, 
                 comentarios: Optional[List[Comentario]] = None, 
                 archivos: Optional[List[Archivo]] = None, 
                 antecedentes: Optional[List[Archivo]] = None, 
                 salidas: Optional[List[ArchivoSalida]] = None, 
                 categorias: Optional[List[Categoria]] = None, 
                 destinatarios: Optional[List[Role]] = None):
        self.id = id
        self.asunto = asunto
        self.fecha = fecha
        self.area = area # Could be Role object or role name string
        self.confidencial = confidencial
        self.urgente = urgente
        self.observaciones = observaciones
        self.numero_entrada = numero_entrada
        self.tramitado = tramitado
        self.tramitado_por = tramitado_por # Could be Usuario object or username string
        self.canal_entrada = canal_entrada # Could be CanalEntrada object or name string
        self.comentarios = comentarios if comentarios is not None else []
        self.archivos = archivos if archivos is not None else [] # Generic files
        self.antecedentes = antecedentes if antecedentes is not None else [] # 'antecedentesFiles'
        self.salidas = salidas if salidas is not None else []
        self.categorias = categorias if categorias is not None else []
        self.destinatarios = destinatarios if destinatarios is not None else [] # List of Role objects or names

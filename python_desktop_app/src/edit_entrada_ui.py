import tkinter as tk
from tkinter import ttk, messagebox, filedialog, simpledialog
from tkcalendar import DateEntry
import datetime
import os
import shutil
import tempfile
import subprocess
import platform

# Attempt to import project modules
try:
    from . import database_manager
    from . import models
    from . import config_manager
    from . import crypto_utils
    from . import report_generator # Added for PDF reports
    _database_manager_available = True
    _config_manager_available = True
    _crypto_utils_available = True
    _report_generator_available = True
except ImportError:
    print("Warning: One or more modules (database_manager, models, config_manager, crypto_utils, report_generator) not found for EditEntradaWindow. Using mock data/fallbacks.")
    _database_manager_available = False
    _config_manager_available = False
    _crypto_utils_available = False
    _report_generator_available = False
    # Define Dummy classes if real models are not available
    class DummyUser:
        def __init__(self, username="test_user", id=1, roles_list=None): self.id = id; self.username = username; self.roles = roles_list if roles_list is not None else [DummyRole(nombre_role="DefaultRole")]
    class DummyRole:
        def __init__(self, nombre_role, id=None, **kwargs): self.id = id; self.nombre_role = nombre_role; self.posicion = kwargs.get('posicion')
    class DummyCanalEntrada:
        def __init__(self, nombre, id=None): self.id = id; self.nombre = nombre
    class DummyCategoria:
        def __init__(self, nombre): self.nombre = nombre
    class DummyArchivo:
        def __init__(self, id, ruta_archivo, fecha, asunto, origen_destino, observaciones, tipo=None):
            self.id=id; self.ruta_archivo=ruta_archivo; self.fecha=fecha; self.asunto=asunto
            self.origen_destino=origen_destino; self.observaciones=observaciones; self.tipo=tipo
    class DummyVistoBuenoJefe:
        def __init__(self, id, salida_file_id, usuario, visto_bueno_status):
            self.id=id; self.salida_file_id=salida_file_id; self.usuario=usuario; self.visto_bueno_status=visto_bueno_status
    class DummyArchivoSalida(DummyArchivo): 
        def __init__(self, id, ruta_archivo, fecha, asunto, destino, visto_bueno_general, visto_bueno_jefes=None, **kwargs):
            super().__init__(id, ruta_archivo, fecha, asunto, destino, kwargs.get('observaciones',''), kwargs.get('tipo'))
            self.destino = destino 
            self.visto_bueno_general = visto_bueno_general; self.visto_bueno_jefes = visto_bueno_jefes or []
    class DummyComentario: 
         def __init__(self, id, texto_comentario, fecha, hora, usuario, visto=False, entrada_id=0, posicion_usuario="Jefe Pruebas"):
            self.id=id; self.texto_comentario=texto_comentario; self.fecha=fecha; self.hora=hora
            self.usuario=usuario; self.visto=visto; self.entrada_id=entrada_id; self.posicion_usuario = posicion_usuario
    class DummyEntrada:
        def __init__(self, id, asunto, **kwargs): 
            self.id = id; self.asunto = asunto; self.fecha = kwargs.get('fecha', datetime.date.today())
            self.numero_entrada = kwargs.get('numero_entrada', 'NE001'); self.canal_entrada = kwargs.get('canal_entrada', DummyCanalEntrada(nombre='TestCanal'))
            self.observaciones = kwargs.get('observaciones', 'Obs Test'); self.confidencial = kwargs.get('confidencial', False)
            self.urgente = kwargs.get('urgente', False); self.tramitado = kwargs.get('tramitado', False)
            self.tramitado_por = kwargs.get('tramitado_por', DummyUser(username='TramitadorTest'))
            self.area = kwargs.get('area', DummyRole(nombre_role='AreaTest'))
            self.destinatarios = kwargs.get('destinatarios', [DummyRole(nombre_role='Dest1'), DummyRole(nombre_role='Dest2')])
            self.categorias = kwargs.get('categorias', [DummyCategoria(nombre='Cat1')])
            self.archivos = kwargs.get('archivos', [])
            self.antecedentes = kwargs.get('antecedentes', [])
            self.salidas = kwargs.get('salidas', [])
            self.comentarios = kwargs.get('comentarios', []) 

    models_module_content = {
        'Usuario': DummyUser, 'Role': DummyRole, 'CanalEntrada': DummyCanalEntrada,
        'Categoria': DummyCategoria, 'Archivo': DummyArchivo, 'ArchivoSalida': DummyArchivoSalida,
        'VistoBuenoJefe': DummyVistoBuenoJefe, 'Comentario': DummyComentario, 'Entrada': DummyEntrada
    }
    if not _models_available : models = type('models_dummy', (object,), models_module_content)
    if not _config_manager_available:
        class MockConfigManager:
            def get_base_dir(self): return "/tmp/py_desktop_app_mock_edit_ui"
        config_manager = MockConfigManager()
    if not _crypto_utils_available:
        class MockCryptoUtils:
            FIXED_KEY_STRING="mock_key"
            CryptoException = Exception # Define CryptoException for the mock
            def encrypt(self,k,i,o): shutil.copy2(i,o)
            def decrypt(self,k,i,o): shutil.copy2(i,o)
        crypto_utils = MockCryptoUtils()
    if not _report_generator_available:
        class MockReportGenerator:
            def generate_entrada_report(self,e,o): print(f"MOCK Report: {e.id} to {o}"); return True
        report_generator = MockReportGenerator()


class EditEntradaWindow(tk.Toplevel):
    def __init__(self, parent, entrada_id: int, user_object: models.Usuario, config_data: dict):
        super().__init__(parent)
        self.parent = parent
        self.entrada_id = entrada_id
        self.user_object = user_object
        self.config_data = config_data
        self.entrada_object: Optional[models.Entrada] = None
        
        self.title(f"Ver/Editar Entrada - ID: {self.entrada_id}")
        self.geometry("950x780") # Adjusted for better layout
        self.minsize(800, 600)

        main_canvas = tk.Canvas(self)
        scrollbar = ttk.Scrollbar(self, orient="vertical", command=main_canvas.yview)
        self.scrollable_frame = ttk.Frame(main_canvas)
        self.scrollable_frame.bind("<Configure>", lambda e: main_canvas.configure(scrollregion=main_canvas.bbox("all")))
        main_canvas.create_window((0, 0), window=self.scrollable_frame, anchor="nw")
        main_canvas.configure(yscrollcommand=scrollbar.set)
        main_canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")

        self.current_dest_areas = []
        self.current_dest_jefes = []
        self.current_categorias = []
        self.files_to_add = {"entrada": [], "antecedente": [], "salida": []}
        self.files_to_update = {"entrada": [], "antecedente": [], "salida": []}
        self.files_to_delete = {"entrada": [], "antecedente": [], "salida": []}
        self.comments_to_save = {}

        self._create_widgets(self.scrollable_frame)
        self._load_initial_combobox_data()
        self._load_and_display_entrada_data()

        self.protocol("WM_DELETE_WINDOW", self.destroy)
        self.grab_set()

    def _create_widgets(self, container):
        details_outer_frame = ttk.LabelFrame(container, text="Detalles de la Entrada", padding="10")
        details_outer_frame.pack(padx=10, pady=10, fill=tk.X, side=tk.TOP)
        self.details_frame = ttk.Frame(details_outer_frame); self.details_frame.pack(fill=tk.X)
        self._create_general_detail_widgets(self.details_frame)

        collections_frame = ttk.Frame(container) # Frame to hold all collection sections
        collections_frame.pack(padx=10, pady=5, fill=tk.X, side=tk.TOP)
        self._create_collection_widgets(collections_frame) # Populate this new frame

        data_notebook = ttk.Notebook(container)
        data_notebook.pack(padx=10, pady=10, fill=tk.BOTH, expand=True, side=tk.TOP)
        files_main_tab = ttk.Frame(data_notebook, padding="5"); data_notebook.add(files_main_tab, text="Archivos Adjuntos")
        self.files_notebook = ttk.Notebook(files_main_tab); self.files_notebook.pack(expand=True, fill=tk.BOTH)
        self._create_file_tabs_structure(self.files_notebook)
        comments_main_tab = ttk.Frame(data_notebook, padding="5"); data_notebook.add(comments_main_tab, text="Comentarios")
        self.comments_notebook = ttk.Notebook(comments_main_tab); self.comments_notebook.pack(expand=True, fill=tk.BOTH)

        action_button_frame = ttk.Frame(container, padding="10")
        action_button_frame.pack(fill=tk.X, side=tk.BOTTOM, pady=5)
        self.save_button = ttk.Button(action_button_frame, text="Guardar Cambios", command=self._on_save_changes, state="normal")
        self.save_button.pack(side=tk.RIGHT, padx=5)
        self.print_button = ttk.Button(action_button_frame, text="Imprimir Informe", command=self._on_print_report, state="normal") 
        self.print_button.pack(side=tk.RIGHT, padx=5)
        self.close_button = ttk.Button(action_button_frame, text="Cerrar", command=self.destroy)
        self.close_button.pack(side=tk.RIGHT, padx=5)

    def _create_general_detail_widgets(self, parent_frame): # Same as Turn 24
        self.detail_vars = {} 
        fields = [
            ("asunto", "Asunto:", "Entry", 0,0,3), ("fecha", "Fecha:", "DateEntry", 1,0,1), 
            ("numero_entrada", "Nº Entrada:", "Entry", 1,2,1), ("canal_entrada", "Canal Entrada:", "Combobox", 2,0,1),
            ("confidencial", "Confidencial", "Checkbutton", 2,2,1), ("urgente", "Urgente", "Checkbutton", 2,3,1),
            ("tramitado", "Tramitado", "Checkbutton", 3,0,1), ("tramitado_por", "Tramitado Por:", "Entry", 3,2,1),
            ("observaciones", "Observaciones:", "Text", 4,0,4)
        ]
        parent_frame.columnconfigure(1, weight=1); parent_frame.columnconfigure(3, weight=1)
        for key, text, w_type, r, c, cspan in fields:
            ttk.Label(parent_frame, text=text).grid(row=r, column=c, padx=5, pady=3, sticky="nw" if w_type=="Text" else "w")
            var = tk.StringVar() if w_type not in ["Checkbutton", "Text", "DateEntry"] else tk.BooleanVar() if w_type == "Checkbutton" else None
            if var is not None : self.detail_vars[key] = var
            widget = None; current_state = "normal"; 
            if key == "tramitado_por": current_state = "readonly"
            if w_type == "Entry": widget = ttk.Entry(parent_frame, textvariable=var, state=current_state)
            elif w_type == "DateEntry": widget = DateEntry(parent_frame, date_pattern='dd/MM/yyyy', state=current_state)
            elif w_type == "Text": widget = tk.Text(parent_frame, height=4, state=current_state)
            elif w_type == "Checkbutton": widget = ttk.Checkbutton(parent_frame, text="", variable=var, state=current_state)
            elif w_type == "Combobox": widget = ttk.Combobox(parent_frame, textvariable=var, state="readonly")
            if widget:
                sticky_val = "ew" if w_type != "Checkbutton" else "w"; 
                if w_type == "Text": sticky_val="nsew"
                widget.grid(row=r, column=c+1, columnspan=cspan, padx=5, pady=3, sticky=sticky_val)
                setattr(self, f"{key}_widget", widget)
    
    def _create_collection_widgets(self, container): # New method from Turn 24
        negociados_frame = ttk.LabelFrame(container, text="Destinatarios - Áreas/Negociados", padding="10")
        negociados_frame.pack(padx=0, pady=5, fill=tk.X, side=tk.TOP) # Changed padx to 0
        self.negociados_combobox = ttk.Combobox(negociados_frame, state="readonly", width=30); self.negociados_combobox.pack(side=tk.LEFT, padx=5)
        ttk.Button(negociados_frame, text="Añadir Área", command=self._add_negociado).pack(side=tk.LEFT, padx=5)
        self.selected_negociados_listbox = tk.Listbox(negociados_frame, height=3, width=40); self.selected_negociados_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        ttk.Button(negociados_frame, text="Quitar Área", command=self._remove_negociado).pack(side=tk.LEFT, padx=5)
        
        jefes_frame = ttk.LabelFrame(container, text="Destinatarios - Jefes/Cargos", padding="10")
        jefes_frame.pack(padx=0, pady=5, fill=tk.X, side=tk.TOP) # Changed padx to 0
        self.jefes_combobox = ttk.Combobox(jefes_frame, state="readonly", width=30); self.jefes_combobox.pack(side=tk.LEFT, padx=5)
        ttk.Button(jefes_frame, text="Añadir Jefe", command=self._add_jefe).pack(side=tk.LEFT, padx=5)
        self.selected_jefes_listbox = tk.Listbox(jefes_frame, height=3, width=40); self.selected_jefes_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        ttk.Button(jefes_frame, text="Quitar Jefe", command=self._remove_jefe).pack(side=tk.LEFT, padx=5)

        categorias_frame = ttk.LabelFrame(container, text="Categorías", padding="10")
        categorias_frame.pack(padx=0, pady=5, fill=tk.X, side=tk.TOP) # Changed padx to 0
        self.categorias_combobox = ttk.Combobox(categorias_frame, width=30); self.categorias_combobox.pack(side=tk.LEFT, padx=5)
        ttk.Button(categorias_frame, text="Añadir Categoría", command=self._add_categoria).pack(side=tk.LEFT, padx=5)
        self.selected_categorias_listbox = tk.Listbox(categorias_frame, height=3, width=40); self.selected_categorias_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        ttk.Button(categorias_frame, text="Quitar Categoría", command=self._remove_categoria).pack(side=tk.LEFT, padx=5)

    # ... (Rest of the methods: _load_initial_combobox_data, _populate_general_details, collection add/remove,
    #      file tab structure, file treeview creation, file population, comment population,
    #      file handlers (_add_file_handler, etc.), _ask_file_metadata, _open_selected_file,
    #      _on_save_changes, _on_print_report are as defined in Turn 26 / Turn 36)
    # The content for these methods will be taken from the previous successful generation of this file content.
    # For brevity, only showing the changed/new structure parts here.
    # Ensure all methods from Turn 26 (file/comment editing) and Turn 36 (print report) are included below.

    def _load_initial_combobox_data(self): # From Turn 24/26
        if not _database_manager_available:
            self.canal_entrada_widget['values'] = ["Mock Canal 1", "Mock Canal 2"]
            self.negociados_combobox['values'] = ["Mock Negociado A", "Mock Negociado B"]
            self.jefes_combobox['values'] = ["Mock Jefe X (Cargo Y)", "Mock Jefe Z (Cargo W)"]
            self.categorias_combobox['values'] = ["Mock Cat 1", "Mock Cat 2", "Mock Cat Nueva"]
            if self.canal_entrada_widget['values']: self.canal_entrada_widget.current(0)
            if self.negociados_combobox['values']: self.negociados_combobox.current(0)
            if self.jefes_combobox['values']: self.jefes_combobox.current(0)
            return
        try:
            self.canal_entrada_widget['values'] = [c.nombre for c in database_manager.get_canales()]
            negociados = database_manager.get_negociados(); self.negociados_combobox['values'] = [n.nombre_role for n in negociados]
            if negociados: self.negociados_combobox.current(0)
            cargos = database_manager.get_cargos(); self.jefes_combobox['values'] = [f"{c.nombre_role} ({c.posicion})" for c in cargos if c.posicion]
            if cargos and self.jefes_combobox['values']: self.jefes_combobox.current(0)
            self.categorias_combobox['values'] = [cat.nombre for cat in database_manager.get_categorias()]
        except Exception as e: messagebox.showerror("Error de Carga", f"No se pudieron cargar datos para comboboxes: {e}", parent=self)

    def _populate_general_details(self): # From Turn 24/26
        e = self.entrada_object; 
        if not e: return
        self.asunto_widget.config(state="normal"); self.asunto_widget.delete(0, tk.END); self.asunto_widget.insert(0, e.asunto or "")
        self.fecha_widget.set_date(e.fecha or datetime.date.today())
        self.numero_entrada_widget.config(state="normal"); self.numero_entrada_widget.delete(0, tk.END); self.numero_entrada_widget.insert(0, e.numero_entrada or "")
        current_canal_nombre = e.canal_entrada.nombre if e.canal_entrada and hasattr(e.canal_entrada, 'nombre') else ""
        if current_canal_nombre in self.canal_entrada_widget['values']: self.canal_entrada_widget.set(current_canal_nombre)
        elif self.canal_entrada_widget['values']: self.canal_entrada_widget.current(0)
        self.detail_vars["confidencial"].set(e.confidencial); self.detail_vars["urgente"].set(e.urgente); self.detail_vars["tramitado"].set(e.tramitado)
        tramitado_por_username = e.tramitado_por.username if e.tramitado_por and hasattr(e.tramitado_por, 'username') else "N/A"
        self.tramitado_por_widget.config(state="normal"); self.tramitado_por_widget.delete(0, tk.END); self.tramitado_por_widget.insert(0, tramitado_por_username); self.tramitado_por_widget.config(state="readonly")
        self.observaciones_widget.config(state="normal"); self.observaciones_widget.delete("1.0", tk.END); self.observaciones_widget.insert("1.0", e.observaciones or "")
        
        self.current_dest_areas = []; self.current_dest_jefes = []
        all_negociados_names = self.negociados_combobox['values'] if hasattr(self, 'negociados_combobox') else []
        all_jefe_display_names = self.jefes_combobox['values'] if hasattr(self, 'jefes_combobox') else []
        for role_obj in e.destinatarios:
            if hasattr(role_obj, 'nombre_role'):
                is_jefe = False
                for jefe_display_name in all_jefe_display_names:
                    if role_obj.nombre_role in jefe_display_name: 
                        if jefe_display_name not in self.current_dest_jefes: self.current_dest_jefes.append(jefe_display_name); is_jefe = True; break
                if not is_jefe and role_obj.nombre_role in all_negociados_names:
                     if role_obj.nombre_role not in self.current_dest_areas: self.current_dest_areas.append(role_obj.nombre_role)
                elif not is_jefe and role_obj.nombre_role not in self.current_dest_areas : self.current_dest_areas.append(role_obj.nombre_role)
        self.selected_negociados_listbox.delete(0, tk.END); 
        for area_name in self.current_dest_areas: self.selected_negociados_listbox.insert(tk.END, area_name)
        self.selected_jefes_listbox.delete(0, tk.END); 
        for jefe_name in self.current_dest_jefes: self.selected_jefes_listbox.insert(tk.END, jefe_name)
        self.current_categorias = [cat.nombre for cat in e.categorias if hasattr(cat, 'nombre')]
        self.selected_categorias_listbox.delete(0, tk.END); 
        for cat_name in self.current_categorias: self.selected_categorias_listbox.insert(tk.END, cat_name)

    def _add_negociado(self): item_name = self.negociados_combobox.get(); self._add_to_listbox_internal(item_name, self.current_dest_areas, self.selected_negociados_listbox)
    def _remove_negociado(self): self._remove_from_listbox_internal(self.current_dest_areas, self.selected_negociados_listbox)
    def _add_jefe(self): item_name = self.jefes_combobox.get(); self._add_to_listbox_internal(item_name, self.current_dest_jefes, self.selected_jefes_listbox)
    def _remove_jefe(self): self._remove_from_listbox_internal(self.current_dest_jefes, self.selected_jefes_listbox)
    def _add_categoria(self): item_name = self.categorias_combobox.get().strip(); self._add_to_listbox_internal(item_name, self.current_categorias, self.selected_categorias_listbox, True, self.categorias_combobox); self.categorias_combobox.set("")
    def _remove_categoria(self): self._remove_from_listbox_internal(self.current_categorias, self.selected_categorias_listbox)

    def _add_to_listbox_internal(self, item_name, internal_list, listbox_widget, allow_new_to_combo=False, combo_widget=None):
        if item_name and item_name not in internal_list:
            internal_list.append(item_name); listbox_widget.insert(tk.END, item_name)
            if allow_new_to_combo and combo_widget and item_name not in combo_widget['values']:
                combo_widget['values'] = list(combo_widget['values']) + [item_name]
        elif item_name in internal_list: messagebox.showinfo("Duplicado", f"'{item_name}' ya está en la lista.", parent=self)
        elif not item_name and allow_new_to_combo : messagebox.showwarning("Inválido", "El nombre no puede estar vacío.", parent=self)

    def _remove_from_listbox_internal(self, internal_list, listbox_widget):
        selected_indices = listbox_widget.curselection()
        if not selected_indices: return
        for index in sorted(selected_indices, reverse=True):
            item_name = listbox_widget.get(index); listbox_widget.delete(index)
            if item_name in internal_list: internal_list.remove(item_name)

    def _create_file_tabs_structure(self, files_notebook_widget): # From Turn 26
        self.tab_archivos_entrada = ttk.Frame(files_notebook_widget, padding="5"); files_notebook_widget.add(self.tab_archivos_entrada, text="Archivos Entrada"); self.archivos_entrada_tree = self._create_file_treeview_in_tab(self.tab_archivos_entrada, "entrada")
        self.tab_antecedentes = ttk.Frame(files_notebook_widget, padding="5"); files_notebook_widget.add(self.tab_antecedentes, text="Antecedentes"); self.archivos_antecedentes_tree = self._create_file_treeview_in_tab(self.tab_antecedentes, "antecedente")
        self.tab_salida = ttk.Frame(files_notebook_widget, padding="5"); files_notebook_widget.add(self.tab_salida, text="Archivos Salida"); self.archivos_salida_tree = self._create_file_treeview_in_tab(self.tab_salida, "salida")

    def _create_file_treeview_in_tab(self, parent_tab, tipo_archivo): # From Turn 26
        cols = (); col_map = {}
        if tipo_archivo == "entrada": cols = ("id", "nombre", "fecha", "asunto", "origen", "obs"); col_map = {"id":"ID", "nombre":"Nombre Archivo", "fecha":"Fecha", "asunto":"Asunto", "origen":"Origen", "obs":"Observaciones"}
        elif tipo_archivo == "antecedente": cols = ("id", "nombre", "tipo", "fecha", "asunto", "destino", "obs"); col_map = {"id":"ID", "nombre":"Nombre Archivo", "tipo":"Tipo", "fecha":"Fecha", "asunto":"Asunto", "destino":"Destino", "obs":"Observaciones"}
        elif tipo_archivo == "salida": cols = ("id", "nombre", "fecha", "asunto", "destino", "vb_gen"); col_map = {"id":"ID", "nombre":"Nombre Archivo", "fecha":"Fecha", "asunto":"Asunto", "destino":"Destino", "vb_gen":"VB General"}
        tree = ttk.Treeview(parent_tab, columns=cols, show="headings", height=6)
        for col in cols: tree.heading(col, text=col_map.get(col, col.title())); tree.column(col, width=80 if col !="nombre" else 150, stretch=(col=="nombre"))
        ysb = ttk.Scrollbar(parent_tab, orient=tk.VERTICAL, command=tree.yview); xsb = ttk.Scrollbar(parent_tab, orient=tk.HORIZONTAL, command=tree.xview)
        tree.configure(yscrollcommand=ysb.set, xscrollcommand=xsb.set); tree.grid(row=0, column=0, sticky="nsew"); ysb.grid(row=0, column=1, sticky="ns"); xsb.grid(row=1, column=0, sticky="ew")
        parent_tab.rowconfigure(0, weight=1); parent_tab.columnconfigure(0, weight=1)
        btn_frame = ttk.Frame(parent_tab); btn_frame.grid(row=2, column=0, sticky="ew", pady=(5,0))
        ttk.Button(btn_frame, text="Añadir Archivo", command=lambda t=tree, type_a=tipo_archivo: self._add_file_handler(t, type_a)).pack(side=tk.LEFT, padx=2)
        ttk.Button(btn_frame, text="Quitar Seleccionado", command=lambda t=tree, type_a=tipo_archivo: self._remove_file_handler(t, type_a)).pack(side=tk.LEFT, padx=2)
        ttk.Button(btn_frame, text="Editar Metadatos", command=lambda t=tree, type_a=tipo_archivo: self._edit_file_metadata_handler(t, type_a)).pack(side=tk.LEFT, padx=2)
        tree.bind("<Double-1>", lambda event, t=tree, type_a=tipo_archivo: self._open_selected_file(event, t, type_a))
        return tree
        
    def _populate_file_tabs(self): # From Turn 26
        if not self.entrada_object: return
        self._update_file_treeview_from_internal_lists(self.archivos_entrada_tree, "entrada")
        self._update_file_treeview_from_internal_lists(self.archivos_antecedentes_tree, "antecedente")
        self._update_file_treeview_from_internal_lists(self.archivos_salida_tree, "salida")

    def _update_file_treeview_from_internal_lists(self, tree_widget: ttk.Treeview, tipo_archivo: str): # From Turn 26
        for item in tree_widget.get_children(): tree_widget.delete(item)
        current_db_files = [];
        if self.entrada_object: # Make sure entrada_object is loaded
            if tipo_archivo == "entrada": current_db_files = self.entrada_object.archivos
            elif tipo_archivo == "antecedente": current_db_files = self.entrada_object.antecedentes
            elif tipo_archivo == "salida": current_db_files = self.entrada_object.salidas
        ids_to_delete = self.files_to_delete[tipo_archivo]
        display_list = [f for f in current_db_files if hasattr(f,'id') and f.id not in ids_to_delete] # Ensure f has id
        for i, new_file_dict in enumerate(self.files_to_add[tipo_archivo]): display_list.append(new_file_dict)
        if not display_list: tree_widget.insert("", tk.END, values=("", "No hay archivos.", "", "", "", "")); return
        for item_idx, file_item in enumerate(display_list):
            is_model_instance = hasattr(file_item, 'id') and file_item.id is not None
            filename = os.path.basename(file_item.ruta_archivo if is_model_instance else file_item.get("ruta_archivo","Error"))
            fecha_val = file_item.fecha if is_model_instance else file_item.get("fecha", datetime.date.today()); fecha_str = fecha_val.strftime("%d/%m/%Y") if isinstance(fecha_val, datetime.date) else str(fecha_val)
            asunto = file_item.asunto if is_model_instance else file_item.get("asunto","")
            item_id_for_tree = str(file_item.id) if is_model_instance else f"new_{item_idx}"
            vals = ();
            if tipo_archivo == "entrada": origen = file_item.origen_destino if is_model_instance else file_item.get("origen_destino",""); obs = file_item.observaciones if is_model_instance else file_item.get("observaciones",""); vals = (str(file_item.id) if is_model_instance else "Nuevo", filename, fecha_str, asunto, origen, obs)
            elif tipo_archivo == "antecedente": tipo_val = file_item.tipo if is_model_instance else file_item.get("tipo",""); destino = file_item.origen_destino if is_model_instance else file_item.get("origen_destino",""); obs = file_item.observaciones if is_model_instance else file_item.get("observaciones",""); vals = (str(file_item.id) if is_model_instance else "Nuevo", filename, tipo_val, fecha_str, asunto, destino, obs)
            elif tipo_archivo == "salida": destino_salida = file_item.destino if is_model_instance else file_item.get("destino_salida",""); vb_gen = file_item.visto_bueno_general if is_model_instance else file_item.get("visto_bueno_general",False); vb_gen_str = "Sí" if vb_gen else "No"; vals = (str(file_item.id) if is_model_instance else "Nuevo", filename, fecha_str, asunto, destino_salida, vb_gen_str)
            else: vals = (str(file_item.id) if is_model_instance else "Nuevo", filename)
            tree_widget.insert("", tk.END, values=vals, iid=item_id_for_tree)

    def _populate_comment_tabs(self): # From Turn 26
        for tab_id in self.comments_notebook.tabs(): self.comments_notebook.forget(tab_id)
        if not self.entrada_object or not self.entrada_object.comentarios:
            placeholder_tab = ttk.Frame(self.comments_notebook); ttk.Label(placeholder_tab, text="No hay comentarios para esta entrada.").pack(padx=10, pady=10); self.comments_notebook.add(placeholder_tab, text="Comentarios"); return
        for comentario in self.entrada_object.comentarios:
            tab_title = "Comentario"; commenter_user_id = None
            if comentario.usuario and hasattr(comentario.usuario, 'username'): tab_title = comentario.usuario.username; commenter_user_id = comentario.usuario.id
            elif hasattr(comentario, 'posicion_usuario') and comentario.posicion_usuario : tab_title = comentario.posicion_usuario
            else: tab_title = f"Comentario ID: {comentario.id}"
            comment_tab = ttk.Frame(self.comments_notebook, padding="5"); self.comments_notebook.add(comment_tab, text=tab_title)
            header_frame = ttk.Frame(comment_tab); header_frame.pack(fill=tk.X, pady=(0,5))
            fecha_str = comentario.fecha.strftime("%d/%m/%Y") if comentario.fecha else "N/A"; hora_str = comentario.hora.strftime("%H:%M:%S") if isinstance(comentario.hora, datetime.time) else str(comentario.hora or "N/A")
            ttk.Label(header_frame, text=f"Fecha: {fecha_str}").pack(side=tk.LEFT, padx=5); ttk.Label(header_frame, text=f"Hora: {hora_str}").pack(side=tk.LEFT, padx=5)
            visto_var = tk.BooleanVar(value=comentario.visto); chk_visto = ttk.Checkbutton(header_frame, text="Visto", variable=visto_var, state="disabled"); chk_visto.pack(side=tk.RIGHT, padx=5)
            text_widget = tk.Text(comment_tab, height=6, wrap=tk.WORD, state="disabled"); text_widget.pack(fill=tk.BOTH, expand=True)
            text_widget.config(state="normal"); text_widget.delete("1.0", tk.END); text_widget.insert("1.0", comentario.texto_comentario or ""); text_widget.config(state="disabled")
            if self.user_object and commenter_user_id == self.user_object.id: 
                text_widget.config(state="normal"); chk_visto.config(state="normal")
                self.comments_to_save[commenter_user_id] = {"widget": text_widget, "visto_var": visto_var, "object": comentario, "is_new": False}
            elif commenter_user_id is not None and commenter_user_id not in self.comments_to_save : self.comments_to_save[commenter_user_id] = {"object": comentario, "is_new": False, "widget": text_widget, "visto_var": visto_var}
    
    def _add_file_handler(self, tree_widget: ttk.Treeview, tipo_archivo: str): # From Turn 26
        source_filepath = filedialog.askopenfilename(parent=self); 
        if not source_filepath: return
        metadata = self._ask_file_metadata(tipo_archivo, is_new=True); 
        if metadata is None: return
        if not self.entrada_object or not self.entrada_object.fecha: messagebox.showerror("Error", "Fecha de entrada no definida.", parent=self); return
        base_dir = config_manager.get_base_dir() if _config_manager_available else "/tmp/py_desktop_app_mock_edit_ui"; date_folder_name = self.entrada_object.fecha.strftime("%d-%m-%Y")
        target_sub_dir_map = {"entrada": "DOCS", "antecedente": "DOCS_ANTECEDENTES", "salida": "DOCS_SALIDA"}
        target_sub_dir = target_sub_dir_map.get(tipo_archivo); 
        if not target_sub_dir: messagebox.showerror("Error", f"Tipo de archivo desconocido: {tipo_archivo}"); return
        target_dir = os.path.join(base_dir, target_sub_dir, date_folder_name); os.makedirs(target_dir, exist_ok=True)
        target_filename = os.path.basename(source_filepath); target_filepath = os.path.join(target_dir, target_filename)
        if os.path.exists(target_filepath) and not messagebox.askyesno("Sobrescribir", f"'{target_filename}' ya existe. ¿Sobrescribir?", parent=self): return
        try:
            if not _crypto_utils_available: messagebox.showerror("Error Cripto", "Módulo crypto no disponible.", parent=self); return
            crypto_utils.encrypt(crypto_utils.FIXED_KEY_STRING, source_filepath, target_filepath)
        except crypto_utils.CryptoException as ce: messagebox.showerror("Error de Cifrado", f"No se pudo cifrar: {ce}", parent=self); return
        except IOError as e: messagebox.showerror("Error de Archivo", f"Error al procesar: {e}", parent=self); return
        new_file_data = {"id": None, "ruta_archivo": target_filepath, "fecha": metadata.get("fecha", datetime.date.today()), "asunto": metadata.get("asunto", ""), "observaciones": metadata.get("observaciones", ""), "origen_destino": metadata.get("origen_destino", ""), "tipo": metadata.get("tipo", ""), "destino_salida": metadata.get("destino_salida", ""), "visto_bueno_general": metadata.get("visto_bueno_general", False)}
        self.files_to_add[tipo_archivo].append(new_file_data)
        self._update_file_treeview_from_internal_lists(tree_widget, tipo_archivo)

    def _remove_file_handler(self, tree_widget: ttk.Treeview, tipo_archivo: str): # From Turn 26
        selected_items = tree_widget.selection(); 
        if not selected_items: messagebox.showwarning("Nada Seleccionado", "Seleccione un archivo para quitar.", parent=self); return
        selected_iid = selected_items[0]; file_path_to_delete_physically = None
        try:
            file_id_to_remove = int(selected_iid)
            if file_id_to_remove > 0: 
                original_list_source = [];
                if tipo_archivo == "entrada": original_list_source = self.entrada_object.archivos
                elif tipo_archivo == "antecedente": original_list_source = self.entrada_object.antecedentes
                elif tipo_archivo == "salida": original_list_source = self.entrada_object.salidas
                for f_obj in original_list_source: 
                    if f_obj.id == file_id_to_remove: file_path_to_delete_physically = f_obj.ruta_archivo; break
                self.files_to_update[tipo_archivo] = [f for f in self.files_to_update[tipo_archivo] if f.id != file_id_to_remove]
                found_in_add = False
                for new_file_dict in self.files_to_add[tipo_archivo]:
                    if new_file_dict.get("_db_id_after_save") == file_id_to_remove: self.files_to_add[tipo_archivo].remove(new_file_dict); found_in_add = True; break
                if not found_in_add and file_id_to_remove not in self.files_to_delete[tipo_archivo]: self.files_to_delete[tipo_archivo].append(file_id_to_remove)
            if tipo_archivo == "entrada": self.entrada_object.archivos = [f for f in self.entrada_object.archivos if hasattr(f,'id') and f.id != file_id_to_remove]
            elif tipo_archivo == "antecedente": self.entrada_object.antecedentes = [f for f in self.entrada_object.antecedentes if hasattr(f,'id') and f.id != file_id_to_remove]
            elif tipo_archivo == "salida": self.entrada_object.salidas = [f for f in self.entrada_object.salidas if hasattr(f,'id') and f.id != file_id_to_remove]
        except ValueError:
            if str(selected_iid).startswith("new_"):
                try:
                    idx = int(str(selected_iid).split("_")[1])
                    if 0 <= idx < len(self.files_to_add[tipo_archivo]): file_path_to_delete_physically = self.files_to_add[tipo_archivo][idx]["ruta_archivo"]; del self.files_to_add[tipo_archivo][idx]
                except (IndexError, ValueError) as e: print(f"Error parsing new file IID: {selected_iid}, {e}")
            else: messagebox.showerror("Error", f"No se pudo quitar (IID: {selected_iid}).", parent=self)
        self._update_file_treeview_from_internal_lists(tree_widget, tipo_archivo)
        if file_path_to_delete_physically and os.path.exists(file_path_to_delete_physically) and messagebox.askyesno("Confirmar Eliminación Física", f"¿Eliminar archivo físico?\n{file_path_to_delete_physically}", parent=self):
            try: os.remove(file_path_to_delete_physically); messagebox.showinfo("Archivo Eliminado", "Archivo físico eliminado.", parent=self)
            except OSError as e: messagebox.showerror("Error Eliminando Archivo", f"No se pudo eliminar: {e}", parent=self)

    def _edit_file_metadata_handler(self, tree_widget: ttk.Treeview, tipo_archivo: str): # From Turn 26
        selected_items = tree_widget.selection(); 
        if not selected_items: messagebox.showwarning("Nada Seleccionado", "Seleccione un archivo.", parent=self); return
        selected_iid = selected_items[0]; file_obj_to_edit = None; is_new_file = False; temp_idx = -1
        if str(selected_iid).startswith("new_"):
            try: idx = int(str(selected_iid).split("_")[1]); file_obj_to_edit = self.files_to_add[tipo_archivo][idx]; is_new_file = True; temp_idx = idx
            except (IndexError, ValueError): pass
        else:
            try: file_id = int(selected_iid); source_list = getattr(self.entrada_object, tipo_archivo + ('s' if tipo_archivo != 'salida' else '')); file_obj_to_edit = next((f for f in source_list if hasattr(f,'id') and f.id == file_id), None)
            except ValueError: pass
        if not file_obj_to_edit: messagebox.showerror("Error", "No se encontró archivo para editar.", parent=self); return
        current_md = file_obj_to_edit if is_new_file else {k:getattr(file_obj_to_edit,k,None) for k in ["ruta_archivo","fecha","asunto","observaciones","origen_destino","tipo","destino","visto_bueno_general"]}
        if not is_new_file and hasattr(file_obj_to_edit, 'destino'): current_md["destino_salida"] = file_obj_to_edit.destino 
        new_md = self._ask_file_metadata(tipo_archivo, existing_metadata=current_md, is_new=False); 
        if new_md is None: return
        if is_new_file: self.files_to_add[tipo_archivo][temp_idx].update(new_md)
        else:
            for k,v in new_md.items(): 
                attr_name = k if k != "destino_salida" else "destino" 
                if hasattr(file_obj_to_edit, attr_name): setattr(file_obj_to_edit, attr_name, v)
            if file_obj_to_edit not in self.files_to_update.get(tipo_archivo, []):
                if tipo_archivo not in self.files_to_update: self.files_to_update[tipo_archivo] = []
                self.files_to_update[tipo_archivo].append(file_obj_to_edit)
        self._update_file_treeview_from_internal_lists(tree_widget, tipo_archivo)

    def _ask_file_metadata(self, tipo_archivo: str, existing_metadata: Optional[dict] = None, is_new: bool = True) -> Optional[dict]: # From Turn 26
        metadata = existing_metadata.copy() if existing_metadata else {}; title = "Metadatos del Archivo"
        asunto = simpledialog.askstring(title, "Asunto:", initialvalue=metadata.get("asunto", ""), parent=self)
        if asunto is None and is_new and tipo_archivo != 'salida': return None; # Asunto can be optional for salida if filename is descriptive
        metadata["asunto"] = asunto or ""
        fecha_initial = metadata.get("fecha").strftime("%Y-%m-%d") if isinstance(metadata.get("fecha"), datetime.date) else datetime.date.today().strftime("%Y-%m-%d")
        fecha_str = simpledialog.askstring(title, "Fecha (YYYY-MM-DD):", initialvalue=fecha_initial, parent=self)
        try: metadata["fecha"] = datetime.datetime.strptime(fecha_str, "%Y-%m-%d").date() if fecha_str else metadata.get("fecha", datetime.date.today())
        except ValueError: messagebox.showwarning("Fecha Inválida", "Formato incorrecto, usando fecha actual/previa.", parent=self); metadata["fecha"] = metadata.get("fecha", datetime.date.today())
        if tipo_archivo == "entrada": metadata["origen_destino"] = simpledialog.askstring(title, "Origen:", initialvalue=metadata.get("origen_destino", ""), parent=self) or ""
        elif tipo_archivo == "antecedente": metadata["origen_destino"] = simpledialog.askstring(title, "Destino (antecedente):", initialvalue=metadata.get("origen_destino", ""), parent=self) or ""; metadata["tipo"] = simpledialog.askstring(title, "Tipo antecedente:", initialvalue=metadata.get("tipo", ""), parent=self) or ""
        elif tipo_archivo == "salida": metadata["destino_salida"] = simpledialog.askstring(title, "Destino (salida):", initialvalue=metadata.get("destino_salida", ""), parent=self) or ""; metadata["visto_bueno_general"] = messagebox.askyesno("Visto Bueno General", "¿VB General?", initialvalue=metadata.get("visto_bueno_general", False), parent=self)
        metadata["observaciones"] = simpledialog.askstring(title, "Observaciones:", initialvalue=metadata.get("observaciones", ""), parent=self) or ""
        return metadata

    def _open_selected_file(self, event, tree_widget: ttk.Treeview, tipo_archivo: str): # From Turn 26
        selected_items = tree_widget.selection(); 
        if not selected_items: return
        selected_iid = selected_items[0]; file_path_encrypted = None
        if str(selected_iid).startswith("new_"):
            try: idx = int(str(selected_iid).split("_")[1]); file_path_encrypted = self.files_to_add[tipo_archivo][idx]["ruta_archivo"]
            except (IndexError, ValueError): pass
        else:
            try: file_id = int(selected_iid); 
                source_list_attr = tipo_archivo + ('s' if tipo_archivo != 'salida' else 's') # e.g. 'archivos', 'antecedentes', 'salidas'
                source_list = getattr(self.entrada_object, source_list_attr, [])
                file_obj = next((f for f in source_list if hasattr(f,'id') and f.id == file_id), None); 
                file_path_encrypted = file_obj.ruta_archivo if file_obj else None
            except ValueError: pass
        if not file_path_encrypted or not os.path.exists(file_path_encrypted): messagebox.showerror("Error", f"Ruta no válida o archivo no encontrado: {file_path_encrypted}", parent=self); return
        if not _crypto_utils_available: messagebox.showerror("Error Cripto", "Módulo crypto no disponible.", parent=self); return
        try:
            temp_dir = tempfile.mkdtemp(); original_filename = os.path.basename(file_path_encrypted); decrypted_path = os.path.join(temp_dir, original_filename)
            crypto_utils.decrypt(crypto_utils.FIXED_KEY_STRING, file_path_encrypted, decrypted_path)
            current_os = platform.system()
            if current_os == "Windows": os.startfile(decrypted_path)
            elif current_os == "Darwin": subprocess.call(['open', decrypted_path])
            else: subprocess.call(['xdg-open', decrypted_path])
            messagebox.showinfo("Archivo Abierto", f"Copia temporal descifrada en:\n{decrypted_path}\nEsta copia no se eliminará automáticamente.", parent=self)
        except crypto_utils.CryptoException as ce: messagebox.showerror("Error Descifrado", f"No se pudo descifrar: {ce}", parent=self)
        except Exception as e: messagebox.showerror("Error Abrir Archivo", f"Error inesperado: {e}", parent=self)
        
    def _on_save_changes(self): # From Turn 26, with file/comment processing
        if not self.entrada_object: messagebox.showerror("Error", "No hay datos para guardar.", parent=self); return
        self.entrada_object.asunto = self.asunto_widget.get(); self.entrada_object.fecha = self.fecha_widget.get_date(); self.entrada_object.numero_entrada = self.numero_entrada_widget.get()
        if self.current_dest_areas: self.entrada_object.area = models.Role(nombre_role=self.current_dest_areas[0]) 
        elif self.entrada_object.area : self.entrada_object.area = models.Role(nombre_role="") 
        else: self.entrada_object.area = None
        self.entrada_object.confidencial = self.detail_vars["confidencial"].get(); self.entrada_object.urgente = self.detail_vars["urgente"].get()
        was_tramitado = self.entrada_object.tramitado; is_now_tramitado = self.detail_vars["tramitado"].get(); self.entrada_object.tramitado = is_now_tramitado
        if is_now_tramitado:
            if not was_tramitado: self.entrada_object.tramitado_por = self.user_object
        else: self.entrada_object.tramitado_por = None
        if self.entrada_object.tramitado_por: self.tramitado_por_widget.config(state="normal"); self.tramitado_por_widget.delete(0, tk.END); self.tramitado_por_widget.insert(0, self.entrada_object.tramitado_por.username); self.tramitado_por_widget.config(state="readonly")
        else: self.tramitado_por_widget.config(state="normal"); self.tramitado_por_widget.delete(0, tk.END); self.tramitado_por_widget.insert(0, "N/A"); self.tramitado_por_widget.config(state="readonly")
        self.entrada_object.observaciones = self.observaciones_widget.get("1.0", tk.END).strip()
        canal_nombre_selected = self.canal_entrada_widget.get()
        if not _database_manager_available: messagebox.showerror("Error", "DB manager no disponible.", parent=self); return
        try:
            success = database_manager.update_entrada_details(self.entrada_object, self.current_dest_areas, self.current_dest_jefes, self.current_categorias, canal_nombre_selected, self.user_object)
            if success:
                # Process Files
                for tipo_arc, files_list in self.files_to_add.items():
                    for file_data_dict in files_list:
                        if "_db_id_after_save" in file_data_dict: continue 
                        file_model_args = {k:v for k,v in file_data_dict.items() if k != "nombre_display" and k != "destino_salida"} # Prep for model init
                        if tipo_arc == "salida": file_model_args["destino"] = file_data_dict.get("destino_salida"); file_model = models.ArchivoSalida(**file_model_args)
                        else: file_model = models.Archivo(**file_model_args)
                        new_fid = database_manager.add_new_archivo_record(self.entrada_object.id, file_model, tipo_arc)
                        if new_fid: file_data_dict["_db_id_after_save"] = new_fid; file_data_dict["id"] = new_fid
                        else: messagebox.showerror("Error Archivo", f"No se guardó: {file_data_dict.get('ruta_archivo')}", parent=self)
                for tipo_arc, files_list in self.files_to_update.items():
                    for file_model in files_list: database_manager.update_archivo_metadata(file_model, tipo_arc)
                for tipo_arc, file_ids_list in self.files_to_delete.items():
                    for file_id in file_ids_list: database_manager.delete_archivo_record(file_id, tipo_arc)
                # Process Comments
                for commenter_id, comment_data in self.comments_to_save.items():
                    text_w = comment_data.get("widget"); visto_v = comment_data.get("visto_var"); obj = comment_data.get("object"); is_new = comment_data.get("is_new", False)
                    if not text_w: continue
                    updated_text = text_w.get("1.0", tk.END).strip(); updated_visto = visto_v.get() if visto_v else False
                    if is_new and updated_text: # Add new comment
                        new_com_model = models.Comentario(id=None, entrada_id=self.entrada_object.id, fecha=datetime.date.today(), hora=datetime.datetime.now().time(), usuario=self.user_object, texto_comentario=updated_text, visto=updated_visto, posicion_usuario=self.user_object.roles[0].nombre_role if self.user_object.roles else "Usuario")
                        database_manager.add_comentario_jefe(self.entrada_object.id, new_com_model)
                    elif obj and (obj.texto_comentario != updated_text or obj.visto != updated_visto) : # Update existing
                        obj.texto_comentario = updated_text; obj.visto = updated_visto; obj.fecha = datetime.date.today(); obj.hora = datetime.datetime.now().time()
                        database_manager.update_comentario_jefe(obj)
                
                messagebox.showinfo("Éxito", "Entrada y cambios asociados guardados.", parent=self)
                self.files_to_add = {"entrada": [], "antecedente": [], "salida": []}; self.files_to_update = {"entrada": [], "antecedente": [], "salida": []}; self.files_to_delete = {"entrada": [], "antecedente": [], "salida": []}; self.comments_to_save = {}
                self._load_and_display_entrada_data() 
            else: messagebox.showerror("Error Guardando", "No se pudo actualizar la entrada (detalles principales).", parent=self)
        except Exception as e: messagebox.showerror("Error Crítico Guardando", f"Error: {e}", parent=self); print(f"Error in _on_save_changes: {e}")

    def _on_print_report(self): # From Turn 36
        if not self.entrada_object: messagebox.showwarning("Sin Datos", "No hay datos para generar informe.", parent=self); return
        if not _report_generator_available: messagebox.showerror("Error", "Generador de informes no disponible.", parent=self); return
        default_filename = f"Informe_Entrada_{self.entrada_object.id}_{self.entrada_object.fecha.strftime('%Y%m%d') if self.entrada_object.fecha else 'SF'}.pdf"
        output_path = filedialog.asksaveasfilename(parent=self, title="Guardar Informe PDF", defaultextension=".pdf", initialfile=default_filename, filetypes=[("PDF Documents", "*.pdf"), ("All Files", "*.*")])
        if not output_path: return
        try:
            success = report_generator.generate_entrada_report(self.entrada_object, output_path)
            if success:
                messagebox.showinfo("Informe Generado", f"Informe PDF guardado en:\n{output_path}", parent=self)
                current_os = platform.system()
                try:
                    if current_os == "Windows": os.startfile(output_path)
                    elif current_os == "Darwin": subprocess.call(['open', output_path])
                    else: subprocess.call(['xdg-open', output_path])
                except Exception as e_open: print(f"No se pudo abrir PDF: {e_open}"); messagebox.showwarning("Abrir PDF", "No se pudo abrir PDF automáticamente.", parent=self)
            else: messagebox.showerror("Error Informe", "No se pudo generar informe PDF.", parent=self)
        except Exception as e: messagebox.showerror("Error Crítico Informe", f"Error inesperado: {e}", parent=self); print(f"Error generando informe: {e}")

# --- Standalone Test ---
if __name__ == '__main__':
    root = tk.Tk()
    root.title("Main Test Window (EditEntrada)")
    root.withdraw()
    dummy_user_for_test_session = None
    if not _models_available : models = type('models_dummy', (object,), models_module_content)
    dummy_user_for_test_session = models.Usuario(username="test_editor", id=100, roles_list=[models.Role(nombre_role="EditorPrincipal")])
    dummy_config_for_test = {"BASE_DIR": "/tmp/py_desktop_app_test_edit"}
    if not os.path.exists(dummy_config_for_test["BASE_DIR"]): os.makedirs(dummy_config_for_test["BASE_DIR"], exist_ok=True)

    if not _database_manager_available:
        class MockDBManager: # Ensure all methods used by EditEntradaWindow are mocked
            def get_canales(self): return [models.CanalEntrada(id=i, nombre=f"Canal Mock {i}") for i in range(1,4)]
            def get_negociados(self): return [models.Role(id=i, nombre_role=f"Negociado Mock {chr(65+i)}") for i in range(3)]
            def get_cargos(self): return [models.Role(id=i+10, nombre_role=f"Jefe Mock {i}", posicion=f"Posición {i}") for i in range(2)]
            def get_categorias(self): return [models.Categoria(nombre=f"Categoría Mock {i}") for i in range(1,5)]
            def get_entrada_details_by_id(self, entrada_id):
                print(f"MOCK DB: get_entrada_details_by_id called for {entrada_id}")
                import datetime 
                mock_archivos = [ models.Archivo(id=1, ruta_archivo=os.path.join(dummy_config_for_test["BASE_DIR"],"entrada_file1.pdf.enc"), fecha=datetime.date(2023,1,10), asunto="Doc Principal", origen_destino="Cliente", observaciones="Observación A1")]
                if not os.path.exists(mock_archivos[0].ruta_archivo):
                    with open(mock_archivos[0].ruta_archivo, "wb") as f: f.write(os.urandom(50)) 
                mock_comentarios = [ models.Comentario(id=1, texto_comentario="Comentario Jefe Mock.", fecha=datetime.date(2023,1,11), hora=datetime.time(10,30), usuario=models.Usuario(username="JefeMock",id=50), visto=True, posicion_usuario="Jefe Mock")]
                return models.Entrada(id=entrada_id, asunto=f"Asunto Mock ID: {entrada_id}", fecha=datetime.date(2023, 1, 10), numero_entrada=f"NE2023-{entrada_id:03d}", canal_entrada=models.CanalEntrada(nombre="Email Mock"), observaciones="Observaciones de prueba.", confidencial=False, urgente=True, tramitado=False, tramitado_por=None, area=models.Role(nombre_role="Area Principal Mock"), destinatarios=[models.Role(nombre_role="Negociado Mock A"), models.Role(nombre_role="Jefe Mock 0", posicion="Posición 0")], categorias=[models.Categoria(nombre="Mock Cat A")], archivos=mock_archivos, antecedentes=[], salidas=[], comentarios=mock_comentarios)
            def update_entrada_details(self, entrada_obj, dest_areas, dest_jefes, cats, canal_nombre, user): print(f"MOCK DB: update_entrada_details for ID: {entrada_obj.id}, Asunto: {entrada_obj.asunto}"); return True
            def add_new_archivo_record(self, eid, arch, tipo): print(f"MOCK DB: add_new_archivo_record for entrada {eid}, type {tipo}, path {arch.ruta_archivo}"); return 900 + (arch.id or 0) # Ensure ID is not None
            def update_archivo_metadata(self, arch, tipo): print(f"MOCK DB: update_archivo_metadata for type {tipo}, file ID {arch.id}, new asunto {arch.asunto}"); return True
            def delete_archivo_record(self, aid, tipo): print(f"MOCK DB: delete_archivo_record for type {tipo}, file ID {aid}"); return True
            def add_comentario_jefe(self, eid, com): print(f"MOCK DB: add_comentario_jefe for entrada {eid}, user {com.usuario.username}, text: {com.texto_comentario}"); return 800 + (com.id or 0) # Ensure ID is not None
            def update_comentario_jefe(self, com): print(f"MOCK DB: update_comentario_jefe for comment ID {com.id}, text: {com.texto_comentario}, visto: {com.visto}"); return True
        database_manager = MockDBManager() 
        if 'src.database_manager' not in sys.modules: sys.modules['src.database_manager'] = database_manager
        print("Using MOCK database_manager for EditEntradaWindow test (full).")

    if not _config_manager_available: 
        if 'src.config_manager' not in sys.modules: sys.modules['src.config_manager'] = config_manager
    if not _crypto_utils_available:
        if 'src.crypto_utils' not in sys.modules: sys.modules['src.crypto_utils'] = crypto_utils
    if not _report_generator_available:
        if 'src.report_generator' not in sys.modules: sys.modules['src.report_generator'] = report_generator
        print("Using MOCK report_generator for EditEntradaWindow test.")

    def open_edit_entrada_dialog():
        dialog = EditEntradaWindow(root, entrada_id=123, user_object=dummy_user_for_test_session, config_data=dummy_config_for_test)
    
    ttk.Button(root, text="Abrir Editar Entrada Dialog (ID: 123)", command=open_edit_entrada_dialog).pack(padx=20, pady=20)
    root.deiconify()
    root.mainloop()

```

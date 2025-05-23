import tkinter as tk
from tkinter import ttk, messagebox, filedialog
from tkcalendar import DateEntry # Will need to install tkcalendar: pip install tkcalendar
import datetime
import os

# Attempt to import database_manager and models for data population
# Fallback to mock data if imports fail (for standalone testing)
try:
    from . import database_manager
    from . import models
    from . import config_manager # For BASE_DIR
    from . import crypto_utils   # For encryption
    _database_manager_available = True
    _config_manager_available = True
    _crypto_utils_available = True
except ImportError:
    print("Warning: database_manager, models, config_manager or crypto_utils not found for NuevaEntradaWindow. Using mock data/fallbacks.")
    _database_manager_available = False
    _config_manager_available = False
    _crypto_utils_available = False
    # Dummy classes for standalone testing if models are not available
    class DummyUser:
        def __init__(self, username="test_user", roles_list=None, id=1):
            self.id = id
            self.username = username
            self.roles = roles_list if roles_list else []
    class DummyRole:
        def __init__(self, nombre_role, id=None, **kwargs): self.id = id; self.nombre_role = nombre_role
    class DummyCanalEntrada:
        def __init__(self, nombre, id=None): self.id = id; self.nombre = nombre
    class DummyCategoria:
        def __init__(self, nombre): self.nombre = nombre
    # Replace actual models with dummies for the purpose of this UI test module
    models = type('models_dummy', (object,), {
        'Usuario': DummyUser, 'Role': DummyRole, 
        'CanalEntrada': DummyCanalEntrada, 'Categoria': DummyCategoria
    })


class NuevaEntradaWindow(tk.Toplevel):
    def __init__(self, parent, user_object: models.Usuario):
        super().__init__(parent)
        self.parent = parent
        self.user_object = user_object

        self.title("Nueva Entrada")
        self.geometry("800x700") # Adjusted size
        self.minsize(700, 600)

        # Data storage
        self.selected_negociados = []
        self.selected_jefes = []
        self.selected_categorias = []
        
        self.entrada_files = [] # List of dicts: {'path': full_path, 'asunto': '', 'fecha': '', ...}
        self.antecedentes_files = []
        self.salida_files = []

        # Main frame with scrollbar
        main_canvas = tk.Canvas(self)
        scrollbar = ttk.Scrollbar(self, orient="vertical", command=main_canvas.yview)
        self.scrollable_frame = ttk.Frame(main_canvas)

        self.scrollable_frame.bind(
            "<Configure>",
            lambda e: main_canvas.configure(
                scrollregion=main_canvas.bbox("all")
            )
        )
        main_canvas.create_window((0, 0), window=self.scrollable_frame, anchor="nw")
        main_canvas.configure(yscrollcommand=scrollbar.set)
        
        main_canvas.pack(side="left", fill="both", expand=True)
        scrollbar.pack(side="right", fill="y")

        self._create_widgets(self.scrollable_frame)
        self._load_initial_data()

        self.protocol("WM_DELETE_WINDOW", self._on_cancel)
        self.grab_set() # Make modal


    def _create_widgets(self, container):
        # --- Section for Entrada Details ---
        details_frame = ttk.LabelFrame(container, text="Detalles de la Entrada", padding="10")
        details_frame.pack(padx=10, pady=10, fill=tk.X)
        details_frame.columnconfigure(1, weight=1)
        details_frame.columnconfigure(3, weight=1)

        # Asunto
        ttk.Label(details_frame, text="Asunto:").grid(row=0, column=0, padx=5, pady=5, sticky="w")
        self.asunto_entry = ttk.Entry(details_frame, width=80) # Wider
        self.asunto_entry.grid(row=0, column=1, columnspan=3, padx=5, pady=5, sticky="ew")

        # Fecha
        ttk.Label(details_frame, text="Fecha:").grid(row=1, column=0, padx=5, pady=5, sticky="w")
        self.fecha_entry = DateEntry(details_frame, width=12, background='darkblue', foreground='white', borderwidth=2, date_pattern='dd/MM/yyyy')
        self.fecha_entry.set_date(datetime.date.today())
        self.fecha_entry.grid(row=1, column=1, padx=5, pady=5, sticky="w")

        # Nº Entrada
        ttk.Label(details_frame, text="Nº Entrada:").grid(row=1, column=2, padx=5, pady=5, sticky="w")
        self.numero_entrada_entry = ttk.Entry(details_frame)
        self.numero_entrada_entry.grid(row=1, column=3, padx=5, pady=5, sticky="ew")
        
        # Canal de Entrada
        ttk.Label(details_frame, text="Canal Entrada:").grid(row=2, column=0, padx=5, pady=5, sticky="w")
        self.canal_entrada_combobox = ttk.Combobox(details_frame, state="readonly")
        self.canal_entrada_combobox.grid(row=2, column=1, padx=5, pady=5, sticky="ew")

        # Confidencial & Urgente (Checkbuttons)
        self.confidencial_var = tk.BooleanVar()
        ttk.Checkbutton(details_frame, text="Confidencial", variable=self.confidencial_var).grid(row=2, column=2, padx=5, pady=5, sticky="w")
        self.urgente_var = tk.BooleanVar()
        ttk.Checkbutton(details_frame, text="Urgente", variable=self.urgente_var).grid(row=2, column=3, padx=5, pady=5, sticky="w")

        # Observaciones
        ttk.Label(details_frame, text="Observaciones:").grid(row=3, column=0, padx=5, pady=5, sticky="nw")
        self.observaciones_text = tk.Text(details_frame, height=4, width=60)
        obs_scrollbar = ttk.Scrollbar(details_frame, orient="vertical", command=self.observaciones_text.yview)
        self.observaciones_text.configure(yscrollcommand=obs_scrollbar.set)
        self.observaciones_text.grid(row=3, column=1, columnspan=3, padx=5, pady=5, sticky="ew")
        # obs_scrollbar.grid(row=3, column=4, sticky="ns") # If placing next to text

        # --- Destinatarios (Negociados/Areas) ---
        negociados_frame = ttk.LabelFrame(container, text="Destinatarios - Áreas/Negociados", padding="10")
        negociados_frame.pack(padx=10, pady=5, fill=tk.X)
        self.negociados_combobox = ttk.Combobox(negociados_frame, state="readonly", width=30)
        self.negociados_combobox.pack(side=tk.LEFT, padx=5)
        ttk.Button(negociados_frame, text="Añadir Área", command=self._add_negociado).pack(side=tk.LEFT, padx=5)
        self.selected_negociados_listbox = tk.Listbox(negociados_frame, height=3, width=40)
        self.selected_negociados_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        ttk.Button(negociados_frame, text="Quitar Área", command=self._remove_negociado).pack(side=tk.LEFT, padx=5)
        
        # --- Destinatarios (Jefes/Cargos) ---
        jefes_frame = ttk.LabelFrame(container, text="Destinatarios - Jefes/Cargos", padding="10")
        jefes_frame.pack(padx=10, pady=5, fill=tk.X)
        self.jefes_combobox = ttk.Combobox(jefes_frame, state="readonly", width=30)
        self.jefes_combobox.pack(side=tk.LEFT, padx=5)
        ttk.Button(jefes_frame, text="Añadir Jefe", command=self._add_jefe).pack(side=tk.LEFT, padx=5)
        self.selected_jefes_listbox = tk.Listbox(jefes_frame, height=3, width=40)
        self.selected_jefes_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        ttk.Button(jefes_frame, text="Quitar Jefe", command=self._remove_jefe).pack(side=tk.LEFT, padx=5)

        # --- Categorías ---
        categorias_frame = ttk.LabelFrame(container, text="Categorías", padding="10")
        categorias_frame.pack(padx=10, pady=5, fill=tk.X)
        self.categorias_combobox = ttk.Combobox(categorias_frame, width=30) # Not readonly to allow new entries
        self.categorias_combobox.pack(side=tk.LEFT, padx=5)
        ttk.Button(categorias_frame, text="Añadir Categoría", command=self._add_categoria).pack(side=tk.LEFT, padx=5)
        self.selected_categorias_listbox = tk.Listbox(categorias_frame, height=3, width=40)
        self.selected_categorias_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        ttk.Button(categorias_frame, text="Quitar Categoría", command=self._remove_categoria).pack(side=tk.LEFT, padx=5)

        # --- File Management Tabs ---
        file_notebook = ttk.Notebook(container)
        file_notebook.pack(padx=10, pady=10, fill=tk.BOTH, expand=True)

        self.tab_entrada_files = self._create_file_tab(file_notebook, "Archivos de Entrada", self.entrada_files, "entrada")
        self.tab_antecedentes_files = self._create_file_tab(file_notebook, "Archivos de Antecedentes", self.antecedentes_files, "antecedente")
        self.tab_salida_files = self._create_file_tab(file_notebook, "Archivos de Salida", self.salida_files, "salida")

        # --- Action Buttons ---
        action_button_frame = ttk.Frame(container, padding="10")
        action_button_frame.pack(fill=tk.X, side=tk.BOTTOM, pady=5)
        
        self.save_button = ttk.Button(action_button_frame, text="Guardar", command=self._on_save)
        self.save_button.pack(side=tk.RIGHT, padx=5)
        self.cancel_button = ttk.Button(action_button_frame, text="Cancelar", command=self._on_cancel)
        self.cancel_button.pack(side=tk.RIGHT, padx=5)

    def _create_file_tab(self, notebook, tab_text, file_list_ref, tipo_archivo):
        tab_frame = ttk.Frame(notebook, padding="5")
        notebook.add(tab_frame, text=tab_text)

        # Define columns based on tipo_archivo
        if tipo_archivo == "entrada":
            cols = ("nombre_archivo", "fecha", "asunto", "origen", "observaciones")
            col_texts = {"nombre_archivo": "Nombre", "fecha": "Fecha", "asunto": "Asunto", "origen": "Origen", "observaciones": "Observaciones"}
        elif tipo_archivo == "antecedente":
            cols = ("nombre_archivo", "tipo", "fecha", "asunto", "destino", "observaciones")
            col_texts = {"nombre_archivo": "Nombre", "tipo": "Tipo", "fecha": "Fecha", "asunto": "Asunto", "destino": "Destino", "observaciones": "Observaciones"}
        elif tipo_archivo == "salida":
            cols = ("nombre_archivo", "fecha", "asunto", "destino") # VB General will be handled differently or in metadata dialog
            col_texts = {"nombre_archivo": "Nombre", "fecha": "Fecha", "asunto": "Asunto", "destino": "Destino"}
        else:
            cols = ("nombre_archivo",)
            col_texts = {"nombre_archivo": "Nombre Archivo"}

        tree = ttk.Treeview(tab_frame, columns=cols, show="headings", height=5)
        for col in cols:
            tree.heading(col, text=col_texts.get(col, col.title()))
            tree.column(col, width=100, stretch=tk.YES if col == "nombre_archivo" or col == "asunto" else tk.NO)
        
        tree_scrollbar = ttk.Scrollbar(tab_frame, orient="vertical", command=tree.yview)
        tree.configure(yscrollcommand=tree_scrollbar.set)
        
        tree.pack(side=tk.TOP, fill=tk.BOTH, expand=True)
        tree_scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

        # Buttons for this tab
        btn_frame = ttk.Frame(tab_frame)
        btn_frame.pack(fill=tk.X, pady=5)
        ttk.Button(btn_frame, text="Añadir Archivo", command=lambda tl=file_list_ref, tr=tree, ta=tipo_archivo: self._add_file(tl, tr, ta)).pack(side=tk.LEFT, padx=5)
        ttk.Button(btn_frame, text="Quitar Seleccionado", command=lambda tl=file_list_ref, tr=tree: self._remove_file(tl, tr)).pack(side=tk.LEFT, padx=5)
        # Pass file_list_ref to _edit_file_metadata
        ttk.Button(btn_frame, text="Editar Metadatos", command=lambda fl=file_list_ref, tr=tree, ta=tipo_archivo: self._edit_file_metadata(fl, tr, ta)).pack(side=tk.LEFT, padx=5)
        
        setattr(self, f"{tipo_archivo}_tree", tree) # e.g., self.entrada_tree
        return tab_frame

    # --- Data Loading ---
    def _load_initial_data(self):
        if not _database_manager_available:
            # Populate with some mock data for UI testing
            self.canal_entrada_combobox['values'] = ["Mock Canal 1", "Mock Canal 2"]
            self.negociados_combobox['values'] = ["Mock Negociado A", "Mock Negociado B"]
            self.jefes_combobox['values'] = ["Mock Jefe X (Cargo Y)", "Mock Jefe Z (Cargo W)"]
            self.categorias_combobox['values'] = ["Mock Cat 1", "Mock Cat 2", "Mock Cat Nueva"]
            if self.canal_entrada_combobox['values']: self.canal_entrada_combobox.current(0)
            if self.negociados_combobox['values']: self.negociados_combobox.current(0)
            if self.jefes_combobox['values']: self.jefes_combobox.current(0)
            # self.categorias_combobox.current(0) # Don't preselect for new entry
            return

        try:
            canales = database_manager.get_canales()
            self.canal_entrada_combobox['values'] = [c.nombre for c in canales]
            if canales: self.canal_entrada_combobox.current(0)

            negociados = database_manager.get_negociados() # List of Role objects
            self.negociados_combobox['values'] = [n.nombre_role for n in negociados]
            if negociados: self.negociados_combobox.current(0)
            
            cargos = database_manager.get_cargos() # List of Role objects (jefes with positions)
            self.jefes_combobox['values'] = [f"{c.nombre_role} ({c.posicion})" for c in cargos]
            if cargos: self.jefes_combobox.current(0)

            categorias = database_manager.get_categorias() # List of Categoria objects
            self.categorias_combobox['values'] = [cat.nombre for cat in categorias]
            # self.categorias_combobox.current(0) # Don't preselect if allowing new entry

        except Exception as e:
            messagebox.showerror("Error de Carga", f"No se pudieron cargar datos iniciales: {e}", parent=self)
            print(f"Error loading initial data for NuevaEntradaWindow: {e}")

    # --- Helper methods for list management ---
    def _add_to_listbox(self, item_name, item_list, listbox_widget, allow_duplicates=False):
        if item_name:
            if not allow_duplicates and item_name in item_list:
                messagebox.showinfo("Duplicado", f"'{item_name}' ya está en la lista.", parent=self)
                return
            item_list.append(item_name)
            listbox_widget.insert(tk.END, item_name)
    
    def _remove_from_listbox(self, item_list, listbox_widget):
        selected_indices = listbox_widget.curselection()
        if not selected_indices:
            messagebox.showwarning("Nada Seleccionado", "Por favor, seleccione un ítem para quitar.", parent=self)
            return
        # Remove from bottom up to avoid index shifting issues
        for index in sorted(selected_indices, reverse=True):
            item_name = listbox_widget.get(index)
            listbox_widget.delete(index)
            if item_name in item_list: # Should always be true
                item_list.remove(item_name)

    def _add_negociado(self):
        neg_name = self.negociados_combobox.get()
        self._add_to_listbox(neg_name, self.selected_negociados, self.selected_negociados_listbox)
    def _remove_negociado(self):
        self._remove_from_listbox(self.selected_negociados, self.selected_negociados_listbox)
        
    def _add_jefe(self):
        jefe_name = self.jefes_combobox.get()
        self._add_to_listbox(jefe_name, self.selected_jefes, self.selected_jefes_listbox)
    def _remove_jefe(self):
        self._remove_from_listbox(self.selected_jefes, self.selected_jefes_listbox)

    def _add_categoria(self):
        cat_name = self.categorias_combobox.get().strip()
        if not cat_name:
            messagebox.showwarning("Categoría Vacía", "El nombre de la categoría no puede estar vacío.", parent=self)
            return
        # Add to combobox list if not already there (for future use in this session)
        current_cats = list(self.categorias_combobox['values'])
        if cat_name not in current_cats:
            self.categorias_combobox['values'] = current_cats + [cat_name]
        
        self._add_to_listbox(cat_name, self.selected_categorias, self.selected_categorias_listbox)
        self.categorias_combobox.set("") # Clear after adding
    def _remove_categoria(self):
        self._remove_from_listbox(self.selected_categorias, self.selected_categorias_listbox)

    # --- File Management ---
    def _add_file(self, file_list_ref, tree_widget, tipo_archivo):
        filepath = filedialog.askopenfilename(parent=self)
        if not filepath:
            return
        
        filename = os.path.basename(filepath)
        # Basic metadata for now, more can be added via _edit_file_metadata
        file_data = {
            "id": None, # New file, no ID yet
            "ruta_archivo": filepath, # Store full path internally
            "nombre_display": filename, # For display in tree
            "fecha": datetime.date.today().strftime("%d/%m/%Y"), 
            "asunto": "", "observaciones": "",
            "origen_destino": "", "tipo": "" # For 'antecedente'
        }
        
        file_list_ref.append(file_data)
        
        # Insert into tree
        if tipo_archivo == "entrada":
            tree_values = (filename, file_data["fecha"], "", "", "")
        elif tipo_archivo == "antecedente":
            tree_values = (filename, "", file_data["fecha"], "", "", "")
        elif tipo_archivo == "salida":
            tree_values = (filename, file_data["fecha"], "", "")
        else:
            tree_values = (filename,)
            
        tree_widget.insert("", tk.END, text=str(len(file_list_ref)-1), values=tree_values) # Use list index as iid for now

    def _remove_file(self, file_list_ref, tree_widget):
        selected_item_iid = tree_widget.selection()
        if not selected_item_iid:
            messagebox.showwarning("Nada Seleccionado", "Seleccione un archivo para quitar.", parent=self)
            return
        
        item_details = tree_widget.item(selected_item_iid[0])
        # Assuming 'text' stores the index in file_list_ref or a unique ID for existing files
        # For newly added files, 'text' is the index.
        try:
            list_index = int(item_details['text']) # For new files referenced by list index
            if 0 <= list_index < len(file_list_ref):
                del file_list_ref[list_index]
        except ValueError: # If 'text' is not an int (e.g. DB ID for existing files - not handled here yet)
            messagebox.showerror("Error", "No se pudo quitar el archivo (referencia inválida).", parent=self)
            return
            
        tree_widget.delete(selected_item_iid[0])


    def _edit_file_metadata(self, file_list_ref, tree_widget, tipo_archivo):
        selected_item_iid = tree_widget.selection()
        if not selected_item_iid:
            messagebox.showwarning("Nada Seleccionado", "Seleccione un archivo para editar sus metadatos.", parent=self)
            return

        item_details = tree_widget.item(selected_item_iid[0])
        try:
            # Assuming 'text' field of tree item stores the index in the respective file_list_ref
            file_index = int(item_details['text']) 
            if not (0 <= file_index < len(file_list_ref)):
                raise ValueError("Index out of bounds for file_list_ref")
        except (ValueError, KeyError):
            messagebox.showerror("Error", "No se pudo encontrar la referencia del archivo para editar.", parent=self)
            return

        file_data_to_edit = file_list_ref[file_index]

        # Create a new dialog for editing
        dialog = FileMetadataDialog(self, file_data_to_edit, tipo_archivo)
        self.wait_window(dialog) # Wait for dialog to close

        if dialog.updated_data: # If data was updated in dialog
            file_list_ref[file_index] = dialog.updated_data
            # Update tree view
            filename_display = dialog.updated_data.get("nombre_display", os.path.basename(dialog.updated_data["ruta_archivo"]))
            
            # Ensure date is formatted as string for tree view if it's a date object
            display_fecha = dialog.updated_data["fecha"]
            if isinstance(display_fecha, datetime.date):
                display_fecha = display_fecha.strftime("%d/%m/%Y")

            if tipo_archivo == "entrada":
                tree_values = (filename_display, display_fecha, dialog.updated_data["asunto"], dialog.updated_data["origen_destino"], dialog.updated_data["observaciones"])
            elif tipo_archivo == "antecedente":
                tree_values = (filename_display, dialog.updated_data["tipo"], display_fecha, dialog.updated_data["asunto"], dialog.updated_data["origen_destino"], dialog.updated_data["observaciones"])
            elif tipo_archivo == "salida": 
                tree_values = (filename_display, display_fecha, dialog.updated_data["asunto"], dialog.updated_data["origen_destino"])
            else: # Fallback
                tree_values = (filename_display,)
            
            tree_widget.item(selected_item_iid[0], values=tree_values)


    # --- Action Button Callbacks ---
    def _on_save(self):
        # Validation
        if not self.asunto_entry.get().strip():
            messagebox.showerror("Error de Validación", "El campo 'Asunto' es obligatorio.", parent=self)
            return
        # ... (other validations as before) ...

        entrada_obj = models.Entrada(
            id=None, 
            asunto=self.asunto_entry.get().strip(),
            fecha=self.fecha_entry.get_date(),
            area=models.Role(nombre_role=self.selected_negociados[0] if self.selected_negociados else ""), 
            confidencial=self.confidencial_var.get(),
            urgente=self.urgente_var.get(),
            numero_entrada=self.numero_entrada_entry.get().strip(),
            tramitado=False, # New entradas are not tramitado
            observaciones=self.observaciones_text.get("1.0", tk.END).strip(),
            canal_entrada=models.CanalEntrada(nombre=self.canal_entrada_combobox.get()),
            tramitado_por=None, 
            comentarios=[], archivos=[], antecedentes=[], salidas=[],
            categorias=[models.Categoria(nombre=cat_name) for cat_name in self.selected_categorias],
            destinatarios=[] 
        )
        
        destinatario_areas_nombres = list(self.selected_negociados)
        destinatario_jefes_nombres = list(self.selected_jefes)
        categoria_nombres = list(self.selected_categorias)
        canal_entrada_nombre = self.canal_entrada_combobox.get()

        try:
            if not _config_manager_available:
                messagebox.showerror("Error de Configuración", "Config manager no disponible. No se pueden guardar archivos.", parent=self)
                return
            base_dir = config_manager.get_base_dir()
            if not base_dir:
                messagebox.showerror("Error de Configuración", "BASE_DIR no está configurado.", parent=self)
                return

            date_folder_name = entrada_obj.fecha.strftime("%d-%m-%Y")

            # Process Archivos de Entrada
            target_docs_dir = os.path.join(base_dir, "DOCS", date_folder_name)
            os.makedirs(target_docs_dir, exist_ok=True)
            for file_meta in self.entrada_files:
                source_path = file_meta["ruta_archivo"] # This is the original source path
                target_filename = os.path.basename(source_path)
                target_path = os.path.join(target_docs_dir, target_filename)
                
                if not _crypto_utils_available: messagebox.showerror("Error Cripto", "Módulo crypto no disponible.", parent=self); return
                crypto_utils.encrypt(crypto_utils.FIXED_KEY_STRING, source_path, target_path) # Encrypt
                
                # Convert date string from file_meta to date object for the model
                fecha_obj = file_meta["fecha"]
                if isinstance(fecha_obj, str):
                    fecha_obj = datetime.datetime.strptime(fecha_obj, "%d/%m/%Y").date()

                entrada_obj.archivos.append(models.Archivo(
                    id=None, ruta_archivo=target_path, entrada_id=0, 
                    fecha_creacion=fecha_obj, # Model uses fecha_creacion
                    asunto_archivo=file_meta["asunto"], # Model uses asunto_archivo
                    origen_archivo=file_meta["origen_destino"], # Model uses origen_archivo
                    observaciones=file_meta["observaciones"]
                ))

            # Process Archivos de Antecedentes
            target_antec_dir = os.path.join(base_dir, "DOCS_ANTECEDENTES", date_folder_name)
            os.makedirs(target_antec_dir, exist_ok=True)
            for file_meta in self.antecedentes_files:
                source_path = file_meta["ruta_archivo"]
                target_path = os.path.join(target_antec_dir, os.path.basename(source_path))
                if not _crypto_utils_available: messagebox.showerror("Error Cripto", "Módulo crypto no disponible.", parent=self); return
                
                fecha_obj = file_meta["fecha"]
                if isinstance(fecha_obj, str):
                    fecha_obj = datetime.datetime.strptime(fecha_obj, "%d/%m/%Y").date()
                
                crypto_utils.encrypt(crypto_utils.FIXED_KEY_STRING, source_path, target_path) # Encrypt
                entrada_obj.antecedentes.append(models.Archivo( # Assuming Archivo model is used for antecedents too
                    id=None, ruta_archivo=target_path, entrada_id=0,
                    fecha_creacion=fecha_obj, # Model uses fecha_creacion
                    asunto_archivo=file_meta["asunto"], # Model uses asunto_archivo
                    origen_archivo=file_meta["origen_destino"], # Model uses origen_archivo
                    observaciones=file_meta["observaciones"], 
                    tipo_antecedente=file_meta["tipo"] # Model uses tipo_antecedente
                ))

            # Process Archivos de Salida
            target_salida_dir = os.path.join(base_dir, "DOCS_SALIDA", date_folder_name)
            os.makedirs(target_salida_dir, exist_ok=True)
            for file_meta in self.salida_files:
                source_path = file_meta["ruta_archivo"]
                target_path = os.path.join(target_salida_dir, os.path.basename(source_path))
                if not _crypto_utils_available: messagebox.showerror("Error Cripto", "Módulo crypto no disponible.", parent=self); return

                fecha_obj = file_meta["fecha"]
                if isinstance(fecha_obj, str):
                    fecha_obj = datetime.datetime.strptime(fecha_obj, "%d/%m/%Y").date()

                crypto_utils.encrypt(crypto_utils.FIXED_KEY_STRING, source_path, target_path) # Encrypt
                entrada_obj.salidas.append(models.ArchivoSalida(
                    id=None, ruta_archivo=target_path, entrada_id=0,
                    fecha_creacion=fecha_obj, # Model uses fecha_creacion
                    asunto_archivo=file_meta["asunto"], # Model uses asunto_archivo
                    destino_archivo=file_meta["origen_destino"], # Model uses destino_archivo
                    visto_bueno_general=False, # Default for new salida
                    visto_bueno_jefes=[]
                ))
        
        except crypto_utils.CryptoException as ce: # Catch crypto specific errors
            messagebox.showerror("Error de Cifrado", f"Error durante el cifrado de archivos: {ce}", parent=self)
            return
        except IOError as e:
            messagebox.showerror("Error de Archivo", f"Error al copiar archivo: {e}", parent=self)
            return
        except Exception as e:
            messagebox.showerror("Error", f"Error preparando archivos: {e}", parent=self)
            return

        if not _database_manager_available:
            messagebox.showerror("Error", "Database manager no disponible. No se puede guardar.", parent=self)
            return
            
        try:
            saved_id = database_manager.save_new_entrada(
                entrada_obj, self.user_object,
                destinatario_areas_nombres,
                destinatario_jefes_nombres,
                categoria_nombres,
                canal_entrada_nombre
            )
            if saved_id:
                messagebox.showinfo("Éxito", f"Nueva entrada guardada con ID: {saved_id}", parent=self)
                if hasattr(self.parent, '_load_entradas_list'): # Attempt to refresh main list
                    self.parent._load_entradas_list()
                self.destroy()
            else:
                messagebox.showerror("Error al Guardar", "No se pudo guardar la entrada en la base de datos.", parent=self)
        except Exception as e:
            messagebox.showerror("Error Crítico al Guardar", f"Ocurrió un error inesperado: {e}", parent=self)
            print(f"Critical error during save_new_entrada call: {e}")


    def _on_cancel(self):
        if messagebox.askokcancel("Cancelar", "¿Descartar nueva entrada y cerrar esta ventana?", parent=self):
            self.destroy()

class FileMetadataDialog(tk.Toplevel):
    def __init__(self, parent, file_data, tipo_archivo):
        super().__init__(parent)
        self.file_data_original = file_data # Keep original in case of cancel
        self.updated_data = None # This will store the new data if saved
        self.tipo_archivo = tipo_archivo

        self.title(f"Editar Metadatos - {file_data.get('nombre_display', 'Archivo')}")
        self.geometry("450x370") # Adjusted size for better layout
        self.transient(parent)
        self.grab_set()

        main_frame = ttk.Frame(self, padding="10")
        main_frame.pack(expand=True, fill=tk.BOTH)
        main_frame.columnconfigure(1, weight=1)

        # Common fields
        ttk.Label(main_frame, text="Nombre Archivo:").grid(row=0, column=0, padx=5, pady=5, sticky="w")
        self.nombre_display_entry = ttk.Entry(main_frame, width=40)
        self.nombre_display_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")
        self.nombre_display_entry.insert(0, file_data.get("nombre_display", os.path.basename(file_data.get("ruta_archivo", ""))))

        ttk.Label(main_frame, text="Fecha (dd/mm/yyyy):").grid(row=1, column=0, padx=5, pady=5, sticky="w")
        self.fecha_entry = DateEntry(main_frame, width=12, date_pattern='dd/MM/yyyy')
        try:
            current_fecha = file_data.get("fecha") # This can be str or date object
            if isinstance(current_fecha, str):
                self.fecha_entry.set_date(datetime.datetime.strptime(current_fecha, "%d/%m/%Y").date())
            elif isinstance(current_fecha, datetime.date): # If already a date object
                 self.fecha_entry.set_date(current_fecha)
            else: # Fallback if type is unexpected or None
                self.fecha_entry.set_date(datetime.date.today())
        except ValueError: # If string parsing fails
            self.fecha_entry.set_date(datetime.date.today())
        self.fecha_entry.grid(row=1, column=1, padx=5, pady=5, sticky="w")
        
        ttk.Label(main_frame, text="Asunto:").grid(row=2, column=0, padx=5, pady=5, sticky="w")
        self.asunto_entry = ttk.Entry(main_frame, width=40)
        self.asunto_entry.grid(row=2, column=1, padx=5, pady=5, sticky="ew")
        self.asunto_entry.insert(0, file_data.get("asunto", ""))

        # Origen/Destino label changes based on type
        origen_destino_label_text = "Origen/Destino:"
        if tipo_archivo == "entrada": origen_destino_label_text = "Origen:"
        elif tipo_archivo == "salida": origen_destino_label_text = "Destino:"
        elif tipo_archivo == "antecedente": origen_destino_label_text = "Origen/Destino:"

        ttk.Label(main_frame, text=origen_destino_label_text).grid(row=3, column=0, padx=5, pady=5, sticky="w")
        self.origen_destino_entry = ttk.Entry(main_frame, width=40)
        self.origen_destino_entry.grid(row=3, column=1, padx=5, pady=5, sticky="ew")
        self.origen_destino_entry.insert(0, file_data.get("origen_destino", ""))

        current_row = 4
        # Tipo (specific to 'antecedente')
        if tipo_archivo == "antecedente":
            ttk.Label(main_frame, text="Tipo Antecedente:").grid(row=current_row, column=0, padx=5, pady=5, sticky="w")
            self.tipo_antecedente_entry = ttk.Entry(main_frame, width=40)
            self.tipo_antecedente_entry.grid(row=current_row, column=1, padx=5, pady=5, sticky="ew")
            self.tipo_antecedente_entry.insert(0, file_data.get("tipo", ""))
            current_row += 1
        else:
            self.tipo_antecedente_entry = None


        ttk.Label(main_frame, text="Observaciones:").grid(row=current_row, column=0, padx=5, pady=5, sticky="nw")
        self.observaciones_text = tk.Text(main_frame, height=4, width=30) # Increased height slightly
        self.observaciones_text.grid(row=current_row, column=1, padx=5, pady=5, sticky="ew")
        self.observaciones_text.insert("1.0", file_data.get("observaciones", ""))
        
        # Buttons
        button_frame = ttk.Frame(main_frame)
        button_frame.grid(row=current_row + 1, column=0, columnspan=2, pady=10)
        
        ttk.Button(button_frame, text="Guardar Cambios", command=self._save_metadata).pack(side=tk.LEFT, padx=5)
        ttk.Button(button_frame, text="Cancelar", command=self.destroy).pack(side=tk.LEFT, padx=5)

    def _save_metadata(self):
        self.updated_data = self.file_data_original.copy() # Start with original
        self.updated_data["nombre_display"] = self.nombre_display_entry.get()
        self.updated_data["fecha"] = self.fecha_entry.get_date() # Store as date object for consistency
        self.updated_data["asunto"] = self.asunto_entry.get()
        self.updated_data["origen_destino"] = self.origen_destino_entry.get()
        self.updated_data["observaciones"] = self.observaciones_text.get("1.0", tk.END).strip()
        if self.tipo_antecedente_entry:
            self.updated_data["tipo"] = self.tipo_antecedente_entry.get()
        
        self.destroy()

# --- Standalone Test ---
if __name__ == '__main__':
    # Create a dummy root window
    root = tk.Tk()
    root.title("Main Test Window (NuevaEntrada)")
    # root.withdraw() # Hide dummy root for normal operation, show for testing dialog placement

    # Create dummy user object
    dummy_user_obj = None # Renamed to avoid conflict with DummyUser class
    if _database_manager_available and hasattr(models, 'Usuario'): 
        dummy_user_obj = models.Usuario(id=99, username="test_runner", password_hash="", roles=[])
    else: 
        dummy_user_obj = DummyUser(username="test_runner_dummy", id=99)


    # Mock database_manager, config_manager, crypto_utils if not available
    if not _database_manager_available:
        # Determine if we are using real models or dummy models for mock
        CanalModel = models.CanalEntrada if hasattr(models, 'CanalEntrada') else DummyCanalEntrada
        RoleModel = models.Role if hasattr(models, 'Role') else DummyRole
        CategoriaModel = models.Categoria if hasattr(models, 'Categoria') else DummyCategoria

        class MockDBManager:
            def get_canales(self): return [CanalModel(id=i, nombre=f"Canal Mock {i}") for i in range(1,4)]
            def get_negociados(self): return [RoleModel(id=i, nombre_role=f"Negociado Mock {chr(65+i)}") for i in range(3)]
            def get_cargos(self): return [RoleModel(id=i+10, nombre_role=f"Jefe Mock {i}", posicion=f"Posición {i}") for i in range(2)]
            def get_categorias(self): return [CategoriaModel(nombre=f"Categoría Mock {i}") for i in range(1,5)]
            def save_new_entrada(self, *args, **kwargs): 
                print("MOCK DB: save_new_entrada called with (args, kwargs):", args, kwargs)
                return 12345 # Simulate success ID
        
        database_manager = MockDBManager() 
        print("Using MOCK database_manager for NuevaEntradaWindow test.")

    if not _config_manager_available:
        class MockConfigManager:
            def get_base_dir(self): 
                # Use a local temp directory for base_dir to ensure it's writable
                # and doesn't interfere with other parts of the system.
                # It's also good practice for tests to clean up after themselves if possible.
                _base_dir = os.path.join(os.getcwd(), "TEMP_NUEVA_ENTRADA_BASE_DIR")
                os.makedirs(_base_dir, exist_ok=True)
                return _base_dir
        config_manager = MockConfigManager()
        print(f"Using MOCK config_manager with BASE_DIR: {config_manager.get_base_dir()}")

    if not _crypto_utils_available:
        import shutil # Make sure shutil is imported for the mock
        class MockCryptoUtils:
            FIXED_KEY_STRING = "mock_key_1234567890123456789012" # Dummy key
            class CryptoException(Exception): # Define the custom exception if not available
                pass 
            def encrypt(self, key, in_filepath, out_filepath):
                print(f"MOCK CRYPTO: 'Encrypting' {in_filepath} to {out_filepath}")
                # For testing, ensure the input file exists, otherwise create a dummy one
                if not os.path.exists(in_filepath):
                    print(f"MOCK CRYPTO: Source file {in_filepath} not found, creating dummy file for test.")
                    with open(in_filepath, 'wb') as f_in: # write some bytes
                        f_in.write(os.urandom(1024)) # 1KB dummy data
                shutil.copy2(in_filepath, out_filepath) # Simulate encryption by copying

            def decrypt(self, key, in_filepath, out_filepath):
                print(f"MOCK CRYPTO: 'Decrypting' {in_filepath} to {out_filepath}")
                shutil.copy2(in_filepath, out_filepath) # Simulate decryption by copying
        crypto_utils = MockCryptoUtils()
        print("Using MOCK crypto_utils for NuevaEntradaWindow test.")


    # Button to open the NuevaEntradaWindow
    def open_nueva_entrada_dialog():
        dialog = NuevaEntradaWindow(root, dummy_user_obj) # Use renamed dummy_user_obj
        # root.wait_window(dialog) # This makes the mainloop wait, useful if root is hidden

    main_button = ttk.Button(root, text="Abrir Nueva Entrada Dialog", command=open_nueva_entrada_dialog)
    main_button.pack(padx=20, pady=20)
    root.deiconify() # Show the button window
    root.mainloop()


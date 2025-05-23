import tkinter as tk
from tkinter import ttk, messagebox, filedialog
from tkcalendar import DateEntry # For date editing
import datetime
import os

# Assuming models.py and database_manager.py are accessible
from . import models
from . import database_manager
# For file operations and metadata dialog - will be used later
from .nueva_entrada_ui import FileMetadataDialog 
from . import config_manager 
from . import crypto_utils

class EntradaDetailViewWindow:
    def __init__(self, parent_window, entrada_id: int, db_manager_instance, current_user: models.Usuario):
        self.parent_window = parent_window
        self.entrada_id = entrada_id
        self.db_manager = db_manager_instance
        self.current_user = current_user # Store the current user
        self.entrada_data = None
        self.original_entrada_data_snapshot = None # For cancel
        self.edit_mode = False # Initialize edit_mode

        # To store references to input widgets for Datos Generales
        self.dg_input_widgets = {}
        self.dg_display_labels = {} # To store the labels that display data

        self.window = tk.Toplevel(parent_window)
        self.window.title(f"Detalle de Entrada - ID: {self.entrada_id}")
        self.window.geometry("900x700") 
        self.window.transient(parent_window) 
        self.window.grab_set() 

        if not self._load_entrada_details():
            return 

        self._setup_ui()

    def _load_entrada_details(self):
        """Fetches entrada details from the database."""
        try:
            self.entrada_data = self.db_manager.get_entrada_details_by_id(self.entrada_id)
            if self.entrada_data is None:
                messagebox.showerror("Error", f"No se encontró la Entrada con ID: {self.entrada_id}", parent=self.window)
                self.window.destroy()
                return False
            # Make a copy for snapshot if not already in edit mode or if snapshot is None
            if not self.edit_mode or self.original_entrada_data_snapshot is None:
                 # Basic snapshot - for complex objects, a deepcopy might be better if they are mutable internally
                self.original_entrada_data_snapshot = self.db_manager.get_entrada_details_by_id(self.entrada_id)
            return True
        except Exception as e:
            messagebox.showerror("Error de Carga", f"No se pudieron cargar los detalles de la entrada: {e}", parent=self.window)
            self.window.destroy()
            return False

    def _setup_ui(self):
        """Creates and lays out the UI elements."""
        main_frame = ttk.Frame(self.window, padding="10")
        main_frame.pack(expand=True, fill=tk.BOTH)

        # --- Datos Generales ---
        # This frame will be stored in self.dg_frame by the method
        self._create_datos_generales_section(main_frame) 

        # --- Comentarios ---
        self._create_comentarios_section(main_frame)

        # --- Archivos Adjuntos ---
        self._create_archivos_section(main_frame) 

        # --- Categorías ---
        self._create_categorias_section(main_frame)

        # --- Destinatarios ---
        self._create_destinatarios_section(main_frame)

        # --- Action Buttons ---
        self.action_button_frame = ttk.Frame(main_frame)
        self.action_button_frame.pack(fill=tk.X, pady=(10,0), side=tk.BOTTOM)

        self.edit_button = ttk.Button(self.action_button_frame, text="Editar", command=self._toggle_edit_mode)
        self.edit_button.pack(side=tk.LEFT, padx=5)

        self.save_button = ttk.Button(self.action_button_frame, text="Guardar Cambios", command=self._save_changes)
        self.save_button.pack(side=tk.LEFT, padx=5)
        
        self.cancel_edit_button = ttk.Button(self.action_button_frame, text="Cancelar Cambios", command=self._cancel_changes)
        self.cancel_edit_button.pack(side=tk.LEFT, padx=5)

        self.close_button = ttk.Button(self.action_button_frame, text="Cerrar", command=self.window.destroy)
        self.close_button.pack(side=tk.RIGHT, padx=5)
        
        self._set_ui_edit_state(self.edit_mode) # Apply initial button states too

    def _create_datos_generales_section(self, parent_frame):
        # If dg_frame exists and is a child of parent_frame, destroy its children first
        if hasattr(self, 'dg_frame') and self.dg_frame.winfo_exists():
            for widget in self.dg_frame.winfo_children():
                widget.destroy()
        else: # Create dg_frame if it doesn't exist or parent_frame is new
            self.dg_frame = ttk.Labelframe(parent_frame, text="Datos Generales", padding="10")
            self.dg_frame.pack(fill=tk.X, pady=5)
            self.dg_frame.columnconfigure(1, weight=1)

        self.dg_input_widgets.clear()
        self.dg_display_labels.clear()
        
        e = self.entrada_data # Use current self.entrada_data
        
        self.dg_field_definitions = {
            "id": {"label": "ID:", "type": "label"},
            "asunto": {"label": "Asunto:", "type": "entry", "width": 60},
            "fecha": {"label": "Fecha:", "type": "dateentry"},
            "negociado_principal": {"label": "Área/Negociado Principal:", "type": "combobox", 
                                    "data_loader": lambda: self.db_manager.get_negociados(include_jefes=False)},
            "confidencial": {"label": "Confidencial:", "type": "checkbutton"},
            "urgente": {"label": "Urgente:", "type": "checkbutton"},
            "numero_entrada": {"label": "Número de Entrada:", "type": "entry"},
            "tramitado": {"label": "Estado Tramitado:", "type": "checkbutton"},
            "observaciones": {"label": "Observaciones:", "type": "text", "height": 3, "width": 60},
            "tramitado_por": {"label": "Tramitado Por:", "type": "label"},
            "canal_entrada": {"label": "Canal de Entrada:", "type": "combobox", 
                              "data_loader": self.db_manager.get_canales}
        }

        for i, (key, config) in enumerate(self.dg_field_definitions.items()):
            lbl = ttk.Label(self.dg_frame, text=config["label"], font=('Helvetica', 10, 'bold'))
            lbl.grid(row=i, column=0, sticky=tk.NW if config["type"] == "text" else tk.W, padx=5, pady=2)
            
            # Get value from self.entrada_data
            current_value = getattr(self.entrada_data, key, None)
            if key == "negociado_principal":
                current_value_display = self.entrada_data.negociado_principal.nombre_role if self.entrada_data.negociado_principal else "N/A"
                current_value_edit = self.entrada_data.negociado_principal.id if self.entrada_data.negociado_principal else None
            elif key == "canal_entrada":
                current_value_display = self.entrada_data.canal_entrada.nombre if self.entrada_data.canal_entrada else "N/A"
                current_value_edit = self.entrada_data.canal_entrada.id if self.entrada_data.canal_entrada else None
            elif key == "tramitado_por":
                current_value_display = self.entrada_data.tramitado_por.username if self.entrada_data.tramitado_por else "N/A"
            elif key == "fecha":
                current_value_display = self.entrada_data.fecha.strftime("%d/%m/%Y %H:%M:%S") if self.entrada_data.fecha else "N/A"
                current_value_edit = self.entrada_data.fecha
            elif config["type"] == "checkbutton":
                current_value_display = "Sí" if current_value else "No"
                current_value_edit = bool(current_value)
            else:
                current_value_display = str(current_value) if current_value is not None else ""
                current_value_edit = current_value

            self.dg_display_labels[key] = ttk.Label(self.dg_frame, text=current_value_display, wraplength=600)
            self.dg_display_labels[key].grid(row=i, column=1, sticky=tk.W, padx=5, pady=2)

            if config["type"] == "entry":
                widget = ttk.Entry(self.dg_frame, width=config.get("width", 40))
            elif config["type"] == "text":
                widget = tk.Text(self.dg_frame, height=config["height"], width=config["width"])
            elif config["type"] == "checkbutton":
                var = tk.BooleanVar()
                widget = ttk.Checkbutton(self.dg_frame, variable=var)
                self.dg_input_widgets[key + "_var"] = var
            elif config["type"] == "dateentry":
                widget = DateEntry(self.dg_frame, width=12, date_pattern='dd/MM/yyyy')
            elif config["type"] == "combobox":
                widget = ttk.Combobox(self.dg_frame, state="readonly", width=config.get("width", 38))
            else: # 'label' type
                widget = None

            if widget:
                self.dg_input_widgets[key] = widget
                widget.grid(row=i, column=1, sticky=tk.EW if config["type"]=="combobox" else tk.W, padx=5, pady=2)
                widget.grid_remove() # Hide initially
        
        # This initial call ensures that the correct widgets (labels or inputs) are shown based on self.edit_mode
        self._apply_dg_widget_visibility_and_content()


    def _apply_dg_widget_visibility_and_content(self):
        """Shows/hides display labels vs input widgets and populates them."""
        for key, config in self.dg_field_definitions.items():
            display_label = self.dg_display_labels.get(key)
            input_widget = self.dg_input_widgets.get(key)

            current_value = getattr(self.entrada_data, key, None) # Default from current entrada_data

            # Special handling for display values and edit values
            if key == "negociado_principal":
                display_text = current_value.nombre_role if current_value else "N/A"
                edit_id = current_value.id if current_value else None
            elif key == "canal_entrada":
                display_text = current_value.nombre if current_value else "N/A"
                edit_id = current_value.id if current_value else None
            elif key == "tramitado_por":
                display_text = current_value.username if current_value else "N/A"
            elif key == "fecha":
                display_text = current_value.strftime("%d/%m/%Y %H:%M:%S") if current_value else "N/A"
                edit_val = current_value
            elif config["type"] == "checkbutton":
                display_text = "Sí" if current_value else "No"
                edit_val = bool(current_value)
            else:
                display_text = str(current_value) if current_value is not None else ""
                edit_val = current_value


            if self.edit_mode and config["type"] != "label":
                if display_label: display_label.grid_remove()
                if input_widget:
                    input_widget.grid()
                    if config["type"] == "entry":
                        input_widget.delete(0, tk.END)
                        input_widget.insert(0, str(edit_val if edit_val is not None else ""))
                    elif config["type"] == "text":
                        input_widget.delete("1.0", tk.END)
                        input_widget.insert("1.0", str(edit_val if edit_val is not None else ""))
                    elif config["type"] == "checkbutton":
                        self.dg_input_widgets[key + "_var"].set(edit_val)
                    elif config["type"] == "dateentry":
                        input_widget.set_date(edit_val if isinstance(edit_val, datetime.date) else datetime.date.today())
                    elif config["type"] == "combobox":
                        data_list = config["data_loader"]()
                        self.dg_input_widgets[key + "_data_list"] = data_list
                        input_widget['values'] = [item.nombre if hasattr(item, 'nombre') else item.nombre_role for item in data_list]
                        
                        current_value_set = False
                        if edit_id is not None:
                            for item in data_list:
                                if item.id == edit_id:
                                    input_widget.set(item.nombre if hasattr(item, 'nombre') else item.nombre_role)
                                    current_value_set = True
                                    break
                        if not current_value_set and input_widget['values']:
                            input_widget.set(input_widget['values'][0])
            else: # Read-only mode OR it's a fixed label
                if display_label:
                    display_label.grid()
                    display_label.config(text=display_text)
                if input_widget and config["type"] != "label": # Ensure input widgets are hidden if not a fixed label
                    input_widget.grid_remove()
        self._update_button_states()


    def _set_ui_edit_state(self, is_editing):
        self.edit_mode = is_editing
        self._apply_dg_widget_visibility_and_content() 
        self._apply_categorias_widget_visibility_and_content()
        self._apply_destinatarios_widget_visibility_and_content()
        # TODO: Extend this to handle other sections (Comentarios, Archivos, etc.)
        self._update_button_states()

    def _apply_categorias_widget_visibility_and_content(self):
        if not hasattr(self, 'categorias_frame_display_label'): # If widgets not created yet
            return

        if self.edit_mode:
            self.categorias_frame_display_label.grid_remove()
            self.categorias_edit_frame.grid()
            
            # Populate combobox
            all_categorias = self.db_manager.get_categorias() # Assumes get_categorias exists
            self.categorias_combobox['values'] = [c.nombre_categoria for c in all_categorias]
            self.categorias_combobox_data = all_categorias # Store for ID lookup
            if self.categorias_combobox['values']:
                self.categorias_combobox.current(0)

            # Populate listbox with current categories
            self.selected_categorias_listbox.delete(0, tk.END)
            self.current_selected_categoria_objects = [] # Store full objects for editing state
            if self.entrada_data.categorias:
                for cat in self.entrada_data.categorias:
                    self.selected_categorias_listbox.insert(tk.END, cat.nombre_categoria)
                    self.current_selected_categoria_objects.append(cat)
        else:
            self.categorias_frame_display_label.grid()
            if hasattr(self, 'categorias_edit_frame'): # Check if edit frame exists
                self.categorias_edit_frame.grid_remove()
            # Update display label from self.entrada_data
            cat_text = ", ".join([cat.nombre_categoria for cat in self.entrada_data.categorias]) if self.entrada_data.categorias else "No hay categorías."
            self.categorias_frame_display_label.config(text=cat_text)


    def _update_button_states(self):
        if self.edit_mode:
            self.edit_button.config(state="disabled")
            self.save_button.config(state="normal")
            self.cancel_edit_button.config(state="normal")
            self.close_button.config(state="disabled")
        else:
            self.edit_button.config(state="normal")
            self.save_button.config(state="disabled")
            self.cancel_edit_button.config(state="disabled")
            self.close_button.config(state="normal")

    def _toggle_edit_mode(self):
        if not self.edit_mode: # About to enter edit mode
            # Snapshot is now taken/updated in _load_entrada_details
            # or specifically before entering edit mode if _load_entrada_details isn't called
            if self.original_entrada_data_snapshot is None: # Ensure snapshot exists
                 self.original_entrada_data_snapshot = self.db_manager.get_entrada_details_by_id(self.entrada_id)
        self._set_ui_edit_state(not self.edit_mode)


    def _save_changes(self):
        updated_dg_data = {}
        for key, config in self.dg_field_definitions.items():
            if config["type"] == "label": continue 
            input_widget = self.dg_input_widgets.get(key)
            if not input_widget: continue

            if config["type"] == "entry": updated_dg_data[key] = input_widget.get()
            elif config["type"] == "text": updated_dg_data[key] = input_widget.get("1.0", tk.END).strip()
            elif config["type"] == "checkbutton": updated_dg_data[key] = self.dg_input_widgets[key + "_var"].get()
            elif config["type"] == "dateentry": updated_dg_data[key] = input_widget.get_date()
            elif config["type"] == "combobox":
                selected_name = input_widget.get()
                data_list = self.dg_input_widgets.get(key + "_data_list", [])
                selected_id = None
                for item in data_list:
                    item_name = item.nombre if hasattr(item, 'nombre') else item.nombre_role
                    if item_name == selected_name: selected_id = item.id; break
                id_field_name = key + "_id" # e.g. negociado_principal_id
                updated_dg_data[id_field_name] = selected_id
        
        if updated_dg_data.get("tramitado"):
            # If "tramitado" is checked AND (it was not tramitado before OR it was tramitado by someone else)
            if not self.entrada_data.tramitado or \
               (self.entrada_data.tramitado_por and self.entrada_data.tramitado_por.id != self.current_user.id):
                updated_dg_data["tramitado_por_id"] = self.current_user.id
            # If it was already tramitado by the current user, keep their ID (no change needed in dict unless forced)
            elif self.entrada_data.tramitado_por and self.entrada_data.tramitado_por.id == self.current_user.id:
                 updated_dg_data["tramitado_por_id"] = self.current_user.id
        else: # If "tramitado" is unchecked
            updated_dg_data["tramitado_por_id"] = None # Clear who processed it
        
        # --- Categorias Save Logic ---
        updated_categoria_ids = []
        if self.edit_mode and hasattr(self, 'current_selected_categoria_objects'): # Check if edit widgets for categorias exist
            updated_categoria_ids = [cat.id for cat in self.current_selected_categoria_objects]
        # else, categories were not being edited, so no changes to send for them unless we decide to always send current state.
        # For now, only send if they were in edit mode.
        
        # --- Destinatarios Save Logic ---
        updated_negociado_ids = []
        updated_jefe_ids = []
        if self.edit_mode: 
            if hasattr(self, 'current_selected_negociado_objects'):
                updated_negociado_ids = [n.id for n in self.current_selected_negociado_objects]
            if hasattr(self, 'current_selected_jefe_objects'):
                updated_jefe_ids = [j.id for j in self.current_selected_jefe_objects]

        # --- Archivos Save Logic ---
        # This part is complex: involves file system operations and multiple DB calls per file type.
        # For simulation, we'll just print what would happen.
        archivos_ops_summary = {
            "added": {t: len(fl) for t, fl in self.temp_added_files.items() if fl},
            "metadata_changed": {t: len(fl) for t, fl in self.changed_file_metadata.items() if fl},
            "deleted": {t: len(fl) for t, fl in self.files_marked_for_deletion.items() if fl}
        }
        
        print("Simulating save. Datos Generales:", updated_dg_data)
        print("Simulating save. Categorias IDs:", updated_categoria_ids)
        print("Simulating save. Negociado IDs:", updated_negociado_ids)
        print("Simulating save. Jefe IDs:", updated_jefe_ids)
        print("Simulating save. Archivos Ops:", archivos_ops_summary)

        try:
            # --- Actual Save Calls (Simulated) ---
            # 1. Datos Generales
            # success_dg = self.db_manager.update_entrada_datos_generales(self.entrada_id, updated_dg_data, self.current_user.id)
            success_dg = True 
            
            # 2. Categorías
            # success_cat = self.db_manager.update_entrada_categorias(self.entrada_id, updated_categoria_ids)
            success_cat = True
            
            # 3. Destinatarios
            # success_dest = self.db_manager.update_entrada_destinatarios(self.entrada_id, updated_negociado_ids, updated_jefe_ids)
            success_dest = True
            
            # 4. Archivos
            success_archivos = True # Overall success for file operations
            #   4a. Process new files
            #   for tipo, files_meta_list in self.temp_added_files.items():
            #       for file_meta in files_meta_list:
            #           # source_path = file_meta['ruta_archivo'] (original path)
            #           # target_path = construct target based on BASE_DIR, tipo, date, filename
            #           # crypto_utils.encrypt(...)
            #           # file_meta['ruta_archivo'] = target_path (update to new path)
            #           # db_success = self.db_manager.add_new_archivo_to_entrada(self.entrada_id, tipo, file_meta, self.current_user.id)
            #           # if not db_success: success_archivos = False; break
            #       # if not success_archivos: break
            #
            #   4b. Process metadata changes
            #   if success_archivos:
            #       for tipo, changed_files_dict in self.changed_file_metadata.items():
            #           for archivo_id, meta_dict in changed_files_dict.items():
            #               # db_success = self.db_manager.update_archivo_metadata(archivo_id, meta_dict, self.current_user.id)
            #               # if not db_success: success_archivos = False; break
            #           # if not success_archivos: break
            #
            #   4c. Process deletions
            #   if success_archivos:
            #       for tipo, id_set in self.files_marked_for_deletion.items():
            #           for archivo_id in id_set:
            #               # db_success = self.db_manager.delete_archivo_record(archivo_id, self.current_user.id)
            #               # Optionally: os.remove(physical_file_path_if_known_and_safe)
            #               # if not db_success: success_archivos = False; break
            #           # if not success_archivos: break

            if success_dg and success_cat and success_dest and success_archivos:
                messagebox.showinfo("Guardado", "Cambios guardados (simulado).", parent=self.window)
                self._clear_temp_file_changes() # Clear tracking lists for files
                self._load_entrada_details() 
                self._rebuild_all_editable_sections() 
                self._set_ui_edit_state(False) 
            else:
                error_parts = []
                if not success_dg: error_parts.append("Datos Generales")
                if not success_cat: error_parts.append("Categorías")
                if not success_dest: error_parts.append("Destinatarios")
                if not success_archivos: error_parts.append("Archivos")
                messagebox.showerror("Error al Guardar", f"No se pudieron guardar los cambios en: {', '.join(error_parts)} (simulado).", parent=self.window)
        except Exception as e:
            messagebox.showerror("Error Crítico", f"Error al guardar: {e}", parent=self.window)
            self._restore_and_rebuild_ui() 

    def _clear_temp_file_changes(self):
        """Clears all temporary lists and dicts used for tracking file changes during an edit session."""
        self.temp_added_files = {"entrada": [], "antecedente": [], "salida": []}
        self.files_marked_for_deletion = {"entrada": set(), "antecedente": set(), "salida": set()}
        self.changed_file_metadata = {"entrada": {}, "antecedente": {}, "salida": {}}

    def _cancel_changes(self):
        if messagebox.askokcancel("Cancelar Cambios", "¿Descartar los cambios y salir del modo edición?", parent=self.window):
            self._clear_temp_file_changes() # Clear any staged file changes
            self._restore_and_rebuild_ui()
            self._set_ui_edit_state(False) 
            
    def _restore_and_rebuild_ui(self):
        """Restores entrada_data from snapshot and rebuilds all editable UI sections."""
        if self.original_entrada_data_snapshot:
            self.entrada_data = self.original_entrada_data_snapshot
            self.original_entrada_data_snapshot = self.db_manager.get_entrada_details_by_id(self.entrada_id) 
        else:
            self._load_entrada_details() 
        
        self._rebuild_all_editable_sections()

    def _rebuild_all_editable_sections(self):
        """Rebuilds all sections that can be edited to reflect current self.entrada_data."""
        self._create_datos_generales_section(self.dg_frame.master) 
        self._create_categorias_section(self.categorias_frame.master if hasattr(self, 'categorias_frame') else self.window) 
        self._create_destinatarios_section(self.destinatarios_frame.master if hasattr(self, 'destinatarios_frame') else self.window)
        self._create_archivos_section(self.archivos_main_frame.master if hasattr(self, 'archivos_main_frame') else self.window)
        # TODO: Add calls to rebuild Comentarios when it becomes editable


    # --- Section Creation Methods ---
    def _create_comentarios_section(self, parent_frame):
        if hasattr(self, 'comentarios_frame') and self.comentarios_frame.winfo_exists():
            for widget in self.comentarios_frame.winfo_children():
                widget.destroy()
        else:
            self.comentarios_frame = ttk.Labelframe(parent_frame, text="Comentarios", padding="10")
            self.comentarios_frame.pack(fill=tk.BOTH, expand=True, pady=5)

        columns = ("id", "usuario", "fecha_hora", "texto", "visto")
        self.comentarios_tree = ttk.Treeview(self.comentarios_frame, columns=columns, show="headings", height=5)
        
        self.comentarios_tree.heading("id", text="ID"); self.comentarios_tree.column("id", width=0, stretch=tk.NO) # Hidden ID
        self.comentarios_tree.heading("usuario", text="Usuario"); self.comentarios_tree.column("usuario", width=100)
        self.comentarios_tree.heading("fecha_hora", text="Fecha y Hora"); self.comentarios_tree.column("fecha_hora", width=120)
        self.comentarios_tree.heading("texto", text="Comentario"); self.comentarios_tree.column("texto", width=300)
        self.comentarios_tree.heading("visto", text="Visto"); self.comentarios_tree.column("visto", width=50, anchor="center")
        
        scrollbar = ttk.Scrollbar(self.comentarios_frame, orient="vertical", command=self.comentarios_tree.yview)
        self.comentarios_tree.configure(yscrollcommand=scrollbar.set)
        self.comentarios_tree.pack(side=tk.TOP, fill=tk.BOTH, expand=True) # Changed to TOP
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y, before=self.comentarios_tree) # Pack scrollbar next to tree

        if self.entrada_data.comentarios:
            for c in self.entrada_data.comentarios:
                self.comentarios_tree.insert("", "end", iid=c.id, values=(
                    c.id, c.usuario.username if c.usuario else "N/A", 
                    c.fecha_comentario.strftime("%d/%m/%Y %H:%M") if c.fecha_comentario else "N/A", 
                    c.texto_comentario, "Sí" if c.visto else "No"
                ))
        else:
            # Insert a placeholder if no comments, but make it non-selectable or clearly indicative
            placeholder_item = self.comentarios_tree.insert("", "end", values=("", "N/A", "N/A", "No hay comentarios.", "N/A"))
            self.comentarios_tree.item(placeholder_item, tags=('no_comment_placeholder',))


        # --- Comment Action Buttons ---
        self.comentarios_action_frame = ttk.Frame(self.comentarios_frame)
        self.comentarios_action_frame.pack(fill=tk.X, pady=5, side=tk.BOTTOM)

        self.btn_add_comentario = ttk.Button(self.comentarios_action_frame, text="Añadir Comentario", command=self._add_new_comentario_ui)
        self.btn_add_comentario.pack(side=tk.LEFT, padx=2)
        
        self.btn_edit_comentario = ttk.Button(self.comentarios_action_frame, text="Editar Seleccionado", command=self._edit_selected_comentario_ui)
        self.btn_edit_comentario.pack(side=tk.LEFT, padx=2)

        self.btn_toggle_visto_comentario = ttk.Button(self.comentarios_action_frame, text="Marcar Visto/No Visto", command=self._toggle_selected_comentario_visto)
        self.btn_toggle_visto_comentario.pack(side=tk.LEFT, padx=2)
        
        self._apply_comentarios_widget_visibility()


    def _apply_comentarios_widget_visibility(self):
        if not hasattr(self, 'btn_add_comentario'): return # Widgets not created yet

        # Basic visibility based on edit mode
        is_jefe = any(role.is_jefe for role in self.current_user.roles) if self.current_user and self.current_user.roles else False
        
        if self.edit_mode and is_jefe: # Only Jefes can add/edit/mark visto in edit mode for now
            self.btn_add_comentario.config(state="normal")
            # Edit/Visto buttons depend on selection and ownership, handled in their respective methods for now
            # Or, disable them here and enable on selection if conditions met.
            self.btn_edit_comentario.config(state="normal") # Simplified: enable if jefe in edit mode
            self.btn_toggle_visto_comentario.config(state="normal") # Simplified: enable if jefe in edit mode
        else:
            self.btn_add_comentario.config(state="disabled")
            self.btn_edit_comentario.config(state="disabled")
            self.btn_toggle_visto_comentario.config(state="disabled")

    def _add_new_comentario_ui(self):
        new_text = tk.simpledialog.askstring("Nuevo Comentario", "Ingrese su comentario:", parent=self.window)
        if new_text:
            try:
                # success = self.db_manager.add_comentario_jefe(self.entrada_id, new_text, self.current_user.id)
                print(f"Simulated: Add Comentario: entrada_id={self.entrada_id}, texto='{new_text}', user_id={self.current_user.id}")
                success = True # Simulate success
                if success:
                    self._load_entrada_details() # Reload to get new comment ID and details
                    self._create_comentarios_section(self.comentarios_frame.master) # Rebuild comment section
                else:
                    messagebox.showerror("Error", "No se pudo añadir el comentario (simulado).", parent=self.window)
            except Exception as e:
                messagebox.showerror("Error", f"Error añadiendo comentario: {e}", parent=self.window)

    def _edit_selected_comentario_ui(self):
        selected_iids = self.comentarios_tree.selection()
        if not selected_iids:
            messagebox.showwarning("Nada Seleccionado", "Seleccione un comentario para editar.", parent=self.window); return
        
        comentario_id_str = selected_iids[0]
        if not comentario_id_str.isdigit(): # Skip if it's a placeholder or non-ID item
            messagebox.showwarning("Inválido", "No se puede editar este ítem.", parent=self.window); return
        comentario_id = int(comentario_id_str)

        # Find the comment to check ownership (simplified: assume only owner can edit if they are jefe)
        comment_to_edit = next((c for c in self.entrada_data.comentarios if c.id == comentario_id), None)
        if not comment_to_edit or comment_to_edit.usuario_id != self.current_user.id:
            messagebox.showwarning("Permiso Denegado", "Solo puede editar sus propios comentarios.", parent=self.window); return

        old_text = comment_to_edit.texto_comentario
        new_text = tk.simpledialog.askstring("Editar Comentario", "Modifique su comentario:", initialvalue=old_text, parent=self.window)

        if new_text and new_text != old_text:
            try:
                # success = self.db_manager.update_comentario_jefe(comentario_id, new_text, self.current_user.id)
                print(f"Simulated: Update Comentario: id={comentario_id}, nuevo_texto='{new_text}', user_id={self.current_user.id}")
                success = True # Simulate success
                if success:
                    self._load_entrada_details()
                    self._create_comentarios_section(self.comentarios_frame.master)
                else:
                    messagebox.showerror("Error", "No se pudo actualizar el comentario (simulado).", parent=self.window)
            except Exception as e:
                messagebox.showerror("Error", f"Error actualizando comentario: {e}", parent=self.window)

    def _toggle_selected_comentario_visto(self):
        selected_iids = self.comentarios_tree.selection()
        if not selected_iids:
            messagebox.showwarning("Nada Seleccionado", "Seleccione un comentario para marcar como Visto/No Visto.", parent=self.window); return

        comentario_id_str = selected_iids[0]
        if not comentario_id_str.isdigit(): return
        comentario_id = int(comentario_id_str)
        
        comment_to_toggle = next((c for c in self.entrada_data.comentarios if c.id == comentario_id), None)
        if not comment_to_toggle: return # Should not happen if ID is from tree

        new_visto_status = not comment_to_toggle.visto
        try:
            # success = self.db_manager.update_comentario_visto(comentario_id, new_visto_status, self.current_user.id)
            print(f"Simulated: Toggle Visto Comentario: id={comentario_id}, nuevo_visto={new_visto_status}, user_id={self.current_user.id}")
            success = True # Simulate success
            if success:
                self._load_entrada_details()
                self._create_comentarios_section(self.comentarios_frame.master)
            else:
                messagebox.showerror("Error", "No se pudo actualizar el estado 'visto' (simulado).", parent=self.window)
        except Exception as e:
            messagebox.showerror("Error", f"Error actualizando estado 'visto': {e}", parent=self.window)


    def _create_archivos_section(self, parent_frame):
        # Clear existing main frame content if it exists
        if hasattr(self, 'archivos_main_frame') and self.archivos_main_frame.winfo_exists():
            for widget in self.archivos_main_frame.winfo_children():
                widget.destroy()
        else:
            self.archivos_main_frame = ttk.Labelframe(parent_frame, text="Archivos Adjuntos", padding="10")
            self.archivos_main_frame.pack(fill=tk.BOTH, expand=True, pady=5)

        # Store newly added files temporarily until save
        if not hasattr(self, 'temp_added_files'):
            self.temp_added_files = {"entrada": [], "antecedente": [], "salida": []}
        # Store IDs of files marked for deletion
        if not hasattr(self, 'files_marked_for_deletion'):
            self.files_marked_for_deletion = {"entrada": set(), "antecedente": set(), "salida": set()}
        # Store metadata changes for existing files
        if not hasattr(self, 'changed_file_metadata'):
            self.changed_file_metadata = {"entrada": {}, "antecedente": {}, "salida": {}} # {archivo_id: new_metadata_dict}


        file_types_config = [
            {"label": "Archivos de Entrada", "data_attr": "archivos", "tree_attr": "tree_archivos_entrada", "tipo": "entrada", 
             "cols": ("nombre", "fecha", "asunto", "origen", "obs"), 
             "col_map": {"nombre": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "origen": "origen_archivo", "obs": "observaciones"}},
            {"label": "Archivos Antecedentes", "data_attr": "antecedentes", "tree_attr": "tree_archivos_antecedentes", "tipo": "antecedente",
             "cols": ("nombre", "fecha", "asunto", "tipo", "obs"), # Simplified 'destino' into 'obs' or specific field
             "col_map": {"nombre": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "tipo": "tipo_antecedente", "obs": "observaciones"}},
            {"label": "Archivos de Salida", "data_attr": "salidas", "tree_attr": "tree_archivos_salida", "tipo": "salida",
             "cols": ("nombre", "fecha", "asunto", "destino", "vb_general"),
             "col_map": {"nombre": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "destino": "destino_archivo", "vb_general": "visto_bueno_general"}},
        ]

        for config in file_types_config:
            frame = ttk.Labelframe(self.archivos_main_frame, text=config["label"], padding="5")
            frame.pack(fill=tk.X, pady=3, expand=True)

            tree = ttk.Treeview(frame, columns=config["cols"], show="headings", height=4)
            for col_key in config["cols"]:
                tree.heading(col_key, text=col_key.replace("_", " ").title())
                tree.column(col_key, width=100 if col_key != "nombre" else 150, anchor=tk.W)
            
            setattr(self, config["tree_attr"], tree) # e.g., self.tree_archivos_entrada = tree

            ysb = ttk.Scrollbar(frame, orient="vertical", command=tree.yview)
            tree.configure(yscrollcommand=ysb.set)
            tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
            ysb.pack(side=tk.RIGHT, fill=tk.Y)
            
            self._populate_archivo_tree(config["tipo"])

            # Action buttons for this file type (visible in edit mode)
            btn_frame = ttk.Frame(frame)
            btn_frame.pack(side=tk.BOTTOM, fill=tk.X, pady=(5,0))
            
            # Store buttons in a dictionary to manage their state easily
            if not hasattr(self, 'archivo_action_buttons'): self.archivo_action_buttons = {}
            self.archivo_action_buttons[config["tipo"]] = []

            b_add = ttk.Button(btn_frame, text="Añadir", command=lambda t=config["tipo"]: self._add_new_archivo_ui(t))
            b_add.pack(side=tk.LEFT, padx=2)
            self.archivo_action_buttons[config["tipo"]].append(b_add)

            b_edit = ttk.Button(btn_frame, text="Editar Meta", command=lambda t=config["tipo"]: self._edit_selected_archivo_metadata_ui(t))
            b_edit.pack(side=tk.LEFT, padx=2)
            self.archivo_action_buttons[config["tipo"]].append(b_edit)
            
            b_remove = ttk.Button(btn_frame, text="Quitar", command=lambda t=config["tipo"]: self._mark_archivo_for_deletion_ui(t))
            b_remove.pack(side=tk.LEFT, padx=2)
            self.archivo_action_buttons[config["tipo"]].append(b_remove)

        self._apply_archivos_widget_visibility()


    def _populate_archivo_tree(self, tipo_archivo):
        config_map = {
            "entrada": {"data_attr": "archivos", "tree_attr": "tree_archivos_entrada", "cols": ("nombre", "fecha", "asunto", "origen", "obs"), "col_map": {"nombre": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "origen": "origen_archivo", "obs": "observaciones"}},
            "antecedente": {"data_attr": "antecedentes", "tree_attr": "tree_archivos_antecedentes", "cols": ("nombre", "fecha", "asunto", "tipo", "obs"), "col_map": {"nombre": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "tipo": "tipo_antecedente", "obs": "observaciones"}},
            "salida": {"data_attr": "salidas", "tree_attr": "tree_archivos_salida", "cols": ("nombre", "fecha", "asunto", "destino", "vb_general"), "col_map": {"nombre": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "destino": "destino_archivo", "vb_general": "visto_bueno_general"}}
        }
        cfg = config_map[tipo_archivo]
        tree = getattr(self, cfg["tree_attr"])
        
        # Clear existing items
        for item in tree.get_children(): tree.delete(item)

        # Populate with existing files from self.entrada_data
        file_list = getattr(self.entrada_data, cfg["data_attr"], [])
        for archivo in file_list:
            if archivo.id in self.files_marked_for_deletion.get(tipo_archivo, set()):
                continue # Skip if marked for deletion for now, or style differently

            values = []
            for col_key in cfg["cols"]:
                attr_name = cfg["col_map"].get(col_key, col_key)
                val = getattr(archivo, attr_name, "")
                if isinstance(val, datetime.date): val = val.strftime("%d/%m/%Y")
                elif isinstance(val, bool) and col_key == "vb_general": val = "Sí" if val else "No"
                values.append(val)
            tree.insert("", "end", iid=f"db_{archivo.id}", values=tuple(values), tags=('db_file',))

        # Populate with temporarily added files (not yet saved to DB)
        for idx, temp_file_meta in enumerate(self.temp_added_files.get(tipo_archivo, [])):
            values = [] # Construct values based on temp_file_meta and cfg["cols"]
            # This part needs careful mapping from temp_file_meta keys to tree column order
            # For simplicity, assuming temp_file_meta has keys like 'nombre_display', 'fecha_str', 'asunto', etc.
            # that align with the expected tree values.
            # Example for 'entrada': (nombre, fecha, asunto, origen, obs)
            if tipo_archivo == "entrada":
                 values = (temp_file_meta.get('nombre_display', ''), temp_file_meta.get('fecha', ''), 
                           temp_file_meta.get('asunto', ''), temp_file_meta.get('origen_destino', ''), 
                           temp_file_meta.get('observaciones', ''))
            # Add similar mappings for 'antecedente' and 'salida'
            tree.insert("", "end", iid=f"temp_{idx}", values=tuple(values), tags=('temp_file',))
            
        tree.tag_configure('deleted_file', foreground='gray', font=(None, None, 'overstrike'))


    def _apply_archivos_widget_visibility(self):
        is_editing = self.edit_mode
        for tipo, buttons in self.archivo_action_buttons.items():
            for btn in buttons:
                btn.config(state="normal" if is_editing else "disabled")

    def _add_new_archivo_ui(self, tipo_archivo):
        source_filepath = filedialog.askopenfilename(parent=self.window, title=f"Seleccionar archivo para {tipo_archivo}")
        if not source_filepath: return

        filename = os.path.basename(source_filepath)
        # Default metadata for the dialog
        # 'ruta_archivo' here is the *source* path for now. It will be replaced by target path upon saving.
        initial_metadata = {
            "ruta_archivo": source_filepath, 
            "nombre_display": filename,
            "fecha": datetime.date.today(), # Stored as date object
            "asunto": "", "observaciones": "",
            "origen_destino": "", # Contextual (Origen for entrada, Destino for salida)
            "tipo": "" # Specific to 'antecedente'
        }

        dialog = FileMetadataDialog(self.window, initial_metadata, tipo_archivo)
        self.window.wait_window(dialog)

        if dialog.updated_data:
            # dialog.updated_data already includes 'ruta_archivo' (original source) and 'nombre_display'
            self.temp_added_files[tipo_archivo].append(dialog.updated_data)
            self._populate_archivo_tree(tipo_archivo)

    def _edit_selected_archivo_metadata_ui(self, tipo_archivo):
        tree_attr_map = {"entrada": "tree_archivos_entrada", "antecedente": "tree_archivos_antecedentes", "salida": "tree_archivos_salida"}
        tree = getattr(self, tree_attr_map[tipo_archivo], None)
        if not tree: return

        selected_iids = tree.selection()
        if not selected_iids:
            messagebox.showwarning("Nada Seleccionado", "Seleccione un archivo para editar sus metadatos.", parent=self.window)
            return
        selected_iid = selected_iids[0]
        
        file_data_to_edit = None
        is_temp_file = selected_iid.startswith("temp_")
        temp_idx = -1
        db_archivo_id = -1

        col_map_dialog_to_model = {
            "entrada": {"nombre_display": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "origen_destino": "origen_archivo", "observaciones": "observaciones"},
            "antecedente": {"nombre_display": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "tipo": "tipo_antecedente", "observaciones": "observaciones", "origen_destino":"origen_archivo"},
            "salida": {"nombre_display": "nombre_archivo", "fecha": "fecha_creacion", "asunto": "asunto_archivo", "origen_destino": "destino_archivo", "observaciones": "observaciones", "vb_general": "visto_bueno_general"}
        }
        current_col_map = col_map_dialog_to_model[tipo_archivo]

        if is_temp_file:
            try:
                prefix_len = len(f"temp_{tipo_archivo}_")
                temp_idx = int(selected_iid[prefix_len:])
                file_data_to_edit = self.temp_added_files[tipo_archivo][temp_idx].copy() # Edit a copy
            except (IndexError, ValueError):
                messagebox.showerror("Error", "No se pudo encontrar la referencia del archivo temporal.", parent=self.window); return
        else: # DB file
            try:
                db_archivo_id = int(selected_iid.split("_")[1])
                # Check if already edited and stored in changed_file_metadata
                if db_archivo_id in self.changed_file_metadata.get(tipo_archivo, {}):
                    file_data_to_edit = self.changed_file_metadata[tipo_archivo][db_archivo_id].copy()
                else: # Load from self.entrada_data
                    data_attr = {"entrada": "archivos", "antecedente": "antecedentes", "salida": "salidas"}[tipo_archivo]
                    db_file_list = getattr(self.entrada_data, data_attr, [])
                    found_file = next((f for f in db_file_list if f.id == db_archivo_id), None)
                    if not found_file: messagebox.showerror("Error", "Archivo original no encontrado.", parent=self.window); return
                    
                    file_data_to_edit = {}
                    for dialog_key, model_key in current_col_map.items():
                        file_data_to_edit[dialog_key] = getattr(found_file, model_key, "")
                    file_data_to_edit['id'] = db_archivo_id 
                    file_data_to_edit['ruta_archivo'] = getattr(found_file, 'ruta_archivo', '') # Needed by dialog? Maybe not for DB files.
            except (IndexError, ValueError):
                 messagebox.showerror("Error", "ID de archivo inválido.", parent=self.window); return
        
        if file_data_to_edit is None: messagebox.showerror("Error", "No se pudieron cargar los datos del archivo.", parent=self.window); return

        dialog = FileMetadataDialog(self.window, file_data_to_edit, tipo_archivo)
        self.window.wait_window(dialog)

        if dialog.updated_data:
            if is_temp_file:
                self.temp_added_files[tipo_archivo][temp_idx] = dialog.updated_data
            else: # DB file
                self.changed_file_metadata.setdefault(tipo_archivo, {})[db_archivo_id] = dialog.updated_data
            self._populate_archivo_tree(tipo_archivo)
        
    def _mark_archivo_for_deletion_ui(self, tipo_archivo):
        tree_attr_map = {"entrada": "tree_archivos_entrada", "antecedente": "tree_archivos_antecedentes", "salida": "tree_archivos_salida"}
        tree = getattr(self, tree_attr_map[tipo_archivo], None)
        if not tree: return

        selected_iids = tree.selection()
        if not selected_iids:
            messagebox.showwarning("Nada Seleccionado", "Seleccione un archivo para quitar/restaurar.", parent=self.window); return
        selected_iid = selected_iids[0]

        if selected_iid.startswith("temp_"): 
            try:
                prefix_len = len(f"temp_{tipo_archivo}_")
                temp_idx = int(selected_iid[prefix_len:])
                del self.temp_added_files[tipo_archivo][temp_idx]
                self._populate_archivo_tree(tipo_archivo) 
            except (IndexError, ValueError):
                messagebox.showerror("Error", "No se pudo quitar el archivo temporal.", parent=self.window)
        elif selected_iid.startswith("db_"): 
            try:
                archivo_id = int(selected_iid.split("_")[1])
                deletion_set = self.files_marked_for_deletion.setdefault(tipo_archivo, set())
                if archivo_id in deletion_set: # Already marked, so unmark
                    deletion_set.remove(archivo_id)
                    tree.item(selected_iid, tags=('db_file',)) 
                else: # Mark for deletion
                    deletion_set.add(archivo_id)
                    tree.item(selected_iid, tags=('db_file', 'deleted_file')) # 'deleted_file' tag should be configured
            except (IndexError, ValueError):
                messagebox.showerror("Error", "ID de archivo inválido para borrado.", parent=self.window)


    def _create_categorias_section(self, parent_frame):
        # If frame exists, clear it first
        if hasattr(self, 'categorias_frame') and self.categorias_frame.winfo_exists():
            for widget in self.categorias_frame.winfo_children():
                widget.destroy()
        else:
            self.categorias_frame = ttk.Labelframe(parent_frame, text="Categorías", padding="10")
            self.categorias_frame.pack(fill=tk.X, pady=5)

        # Display Label (Visible in read-only mode)
        cat_text = ", ".join([cat.nombre_categoria for cat in self.entrada_data.categorias]) if self.entrada_data.categorias else "No hay categorías asignadas."
        self.categorias_frame_display_label = ttk.Label(self.categorias_frame, text=cat_text, wraplength=self.categorias_frame.winfo_width() - 20)
        self.categorias_frame_display_label.grid(row=0, column=0, sticky=tk.W)

        # Editing UI (Initially hidden)
        self.categorias_edit_frame = ttk.Frame(self.categorias_frame)
        # self.categorias_edit_frame.grid(row=0, column=0, sticky=tk.EW) # Grid management done by _apply_...
        
        self.categorias_combobox = ttk.Combobox(self.categorias_edit_frame, width=30) # Not readonly to allow new? For now, readonly.
        self.categorias_combobox.pack(side=tk.LEFT, padx=5)
        
        ttk.Button(self.categorias_edit_frame, text="Añadir", command=self._add_categoria_to_selection).pack(side=tk.LEFT, padx=2)
        
        self.selected_categorias_listbox = tk.Listbox(self.categorias_edit_frame, height=3, width=40)
        self.selected_categorias_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        
        ttk.Button(self.categorias_edit_frame, text="Quitar", command=self._remove_categoria_from_selection).pack(side=tk.LEFT, padx=2)
        
        self.categorias_edit_frame.grid_remove() # Hide edit UI initially

        self._apply_categorias_widget_visibility_and_content() # Apply correct initial state

    def _add_categoria_to_selection(self):
        selected_cat_name = self.categorias_combobox.get()
        if not selected_cat_name: return

        # Check if already in listbox
        if selected_cat_name in self.selected_categorias_listbox.get(0, tk.END):
            messagebox.showinfo("Duplicado", f"Categoría '{selected_cat_name}' ya seleccionada.", parent=self.window)
            return

        # Find the full category object from combobox data
        added_cat_obj = None
        for cat_obj in self.categorias_combobox_data:
            if cat_obj.nombre_categoria == selected_cat_name:
                added_cat_obj = cat_obj
                break
        
        if added_cat_obj:
            self.selected_categorias_listbox.insert(tk.END, selected_cat_name)
            if not hasattr(self, 'current_selected_categoria_objects'):
                self.current_selected_categoria_objects = []
            self.current_selected_categoria_objects.append(added_cat_obj)

    def _remove_categoria_from_selection(self):
        selected_indices = self.selected_categorias_listbox.curselection()
        if not selected_indices: return

        for i in sorted(selected_indices, reverse=True):
            removed_cat_name = self.selected_categorias_listbox.get(i)
            self.selected_categorias_listbox.delete(i)
            # Remove from current_selected_categoria_objects
            for cat_obj in self.current_selected_categoria_objects:
                if cat_obj.nombre_categoria == removed_cat_name:
                    self.current_selected_categoria_objects.remove(cat_obj)
                    break

    def _create_destinatarios_section(self, parent_frame):
        # If frame exists, clear it first
        if hasattr(self, 'destinatarios_frame') and self.destinatarios_frame.winfo_exists():
            for widget in self.destinatarios_frame.winfo_children():
                widget.destroy()
        else:
            self.destinatarios_frame = ttk.Labelframe(parent_frame, text="Destinatarios", padding="10") # Simplified title
            self.destinatarios_frame.pack(fill=tk.X, pady=5)

        # Display Label (Visible in read-only mode)
        negociados_text = "Negociados: "
        jefes_text = "Jefes: "
        if self.entrada_data.destinatarios_roles:
            negociados_list = [r.nombre_role for r in self.entrada_data.destinatarios_roles if not r.is_jefe]
            jefes_list = [r.nombre_role for r in self.entrada_data.destinatarios_roles if r.is_jefe]
            negociados_text += ", ".join(negociados_list) if negociados_list else "Ninguno"
            jefes_text += ", ".join(jefes_list) if jefes_list else "Ninguno"
        else:
            negociados_text += "Ninguno"
            jefes_text += "Ninguno"
        
        full_display_text = f"{negociados_text}\n{jefes_text}"
        self.destinatarios_display_label = ttk.Label(self.destinatarios_frame, text=full_display_text, justify=tk.LEFT, wraplength=self.destinatarios_frame.winfo_width()-20)
        self.destinatarios_display_label.grid(row=0, column=0, sticky=tk.W)

        # --- Editing UI (Initially hidden) ---
        self.destinatarios_edit_frame = ttk.Frame(self.destinatarios_frame)
        # self.destinatarios_edit_frame.grid(row=0, column=0, sticky=tk.EW) # Managed by _apply_...

        # Negociados Edit
        neg_edit_subframe = ttk.Frame(self.destinatarios_edit_frame)
        neg_edit_subframe.pack(fill=tk.X, expand=True, pady=2)
        ttk.Label(neg_edit_subframe, text="Negociados:", width=12).pack(side=tk.LEFT, padx=(0,5))
        self.negociados_dest_combobox = ttk.Combobox(neg_edit_subframe, width=25)
        self.negociados_dest_combobox.pack(side=tk.LEFT, padx=5)
        ttk.Button(neg_edit_subframe, text="Añadir Neg.", command=self._add_negociado_dest_to_selection).pack(side=tk.LEFT, padx=2)
        self.selected_negociados_dest_listbox = tk.Listbox(neg_edit_subframe, height=3, width=30)
        self.selected_negociados_dest_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        ttk.Button(neg_edit_subframe, text="Quitar Neg.", command=self._remove_negociado_dest_from_selection).pack(side=tk.LEFT, padx=2)

        # Jefes Edit
        jef_edit_subframe = ttk.Frame(self.destinatarios_edit_frame)
        jef_edit_subframe.pack(fill=tk.X, expand=True, pady=2)
        ttk.Label(jef_edit_subframe, text="Jefes:", width=12).pack(side=tk.LEFT, padx=(0,5))
        self.jefes_dest_combobox = ttk.Combobox(jef_edit_subframe, width=25)
        self.jefes_dest_combobox.pack(side=tk.LEFT, padx=5)
        ttk.Button(jef_edit_subframe, text="Añadir Jefe", command=self._add_jefe_dest_to_selection).pack(side=tk.LEFT, padx=2)
        self.selected_jefes_dest_listbox = tk.Listbox(jef_edit_subframe, height=3, width=30)
        self.selected_jefes_dest_listbox.pack(side=tk.LEFT, padx=5, fill=tk.X, expand=True)
        ttk.Button(jef_edit_subframe, text="Quitar Jefe", command=self._remove_jefe_dest_from_selection).pack(side=tk.LEFT, padx=2)
        
        self.destinatarios_edit_frame.grid_remove() # Hide edit UI initially
        self._apply_destinatarios_widget_visibility_and_content()

    def _apply_destinatarios_widget_visibility_and_content(self):
        if not hasattr(self, 'destinatarios_display_label'): return

        if self.edit_mode:
            self.destinatarios_display_label.grid_remove()
            self.destinatarios_edit_frame.grid()

            # Populate Negociados
            all_negociados = self.db_manager.get_negociados(include_jefes=False) # Assuming this returns Role objects
            self.negociados_dest_combobox['values'] = [n.nombre_role for n in all_negociados]
            self.negociados_dest_combobox_data = all_negociados
            if self.negociados_dest_combobox['values']: self.negociados_dest_combobox.current(0)
            
            self.selected_negociados_dest_listbox.delete(0, tk.END)
            self.current_selected_negociado_objects = [r for r in self.entrada_data.destinatarios_roles if not r.is_jefe]
            for r_obj in self.current_selected_negociado_objects:
                self.selected_negociados_dest_listbox.insert(tk.END, r_obj.nombre_role)

            # Populate Jefes
            all_jefes = self.db_manager.get_cargos() # Assuming this returns Role objects that are jefes
            self.jefes_dest_combobox['values'] = [j.nombre_role for j in all_jefes] # Or format with posicion if needed
            self.jefes_dest_combobox_data = all_jefes
            if self.jefes_dest_combobox['values']: self.jefes_dest_combobox.current(0)

            self.selected_jefes_dest_listbox.delete(0, tk.END)
            self.current_selected_jefe_objects = [r for r in self.entrada_data.destinatarios_roles if r.is_jefe]
            for r_obj in self.current_selected_jefe_objects:
                self.selected_jefes_dest_listbox.insert(tk.END, r_obj.nombre_role)
        else:
            self.destinatarios_display_label.grid()
            if hasattr(self, 'destinatarios_edit_frame'): self.destinatarios_edit_frame.grid_remove()
            
            negociados_text = "Negociados: "
            jefes_text = "Jefes: "
            if self.entrada_data.destinatarios_roles:
                negociados_list = [r.nombre_role for r in self.entrada_data.destinatarios_roles if not r.is_jefe]
                jefes_list = [r.nombre_role for r in self.entrada_data.destinatarios_roles if r.is_jefe]
                negociados_text += ", ".join(negociados_list) if negociados_list else "Ninguno"
                jefes_text += ", ".join(jefes_list) if jefes_list else "Ninguno"
            else:
                negociados_text += "Ninguno"; jefes_text += "Ninguno"
            self.destinatarios_display_label.config(text=f"{negociados_text}\n{jefes_text}")

    def _add_negociado_dest_to_selection(self):
        selected_name = self.negociados_dest_combobox.get()
        if not selected_name: return
        if selected_name in self.selected_negociados_dest_listbox.get(0, tk.END): return

        for obj in self.negociados_dest_combobox_data:
            if obj.nombre_role == selected_name:
                self.selected_negociados_dest_listbox.insert(tk.END, selected_name)
                self.current_selected_negociado_objects.append(obj)
                break
    
    def _remove_negociado_dest_from_selection(self):
        idxs = self.selected_negociados_dest_listbox.curselection()
        if not idxs: return
        for i in sorted(idxs, reverse=True):
            name_to_remove = self.selected_negociados_dest_listbox.get(i)
            self.selected_negociados_dest_listbox.delete(i)
            self.current_selected_negociado_objects = [obj for obj in self.current_selected_negociado_objects if obj.nombre_role != name_to_remove]

    def _add_jefe_dest_to_selection(self):
        selected_name = self.jefes_dest_combobox.get()
        if not selected_name: return
        if selected_name in self.selected_jefes_dest_listbox.get(0, tk.END): return

        for obj in self.jefes_dest_combobox_data:
            if obj.nombre_role == selected_name: # Add more specific check if names are not unique
                self.selected_jefes_dest_listbox.insert(tk.END, selected_name)
                self.current_selected_jefe_objects.append(obj)
                break

    def _remove_jefe_dest_from_selection(self):
        idxs = self.selected_jefes_dest_listbox.curselection()
        if not idxs: return
        for i in sorted(idxs, reverse=True):
            name_to_remove = self.selected_jefes_dest_listbox.get(i)
            self.selected_jefes_dest_listbox.delete(i)
            self.current_selected_jefe_objects = [obj for obj in self.current_selected_jefe_objects if obj.nombre_role != name_to_remove]


# Example of how to test this window 
if __name__ == '__main__':
    class DummyUser: # Simplified
        def __init__(self, id=1, username="testuser", roles=None): self.id=id; self.username = username; self.roles = roles if roles else []
    class DummyRole: # Simplified
        def __init__(self, id=1, nombre_role="Test Role", is_jefe=False, posicion=""): self.id=id; self.nombre_role = nombre_role; self.is_jefe = is_jefe; self.posicion=posicion
    class DummyCanal: # Simplified
        def __init__(self, id=1, nombre="EMAIL"): self.id=id; self.nombre = nombre
    class DummyComentario: # Simplified
        def __init__(self, texto="Comentario", usuario=None, fecha=None, visto=True): 
            self.texto_comentario = texto; self.usuario = usuario or DummyUser(); 
            self.fecha_comentario = fecha or datetime.datetime.now(); self.visto = visto
    class DummyArchivo: # Simplified
        def __init__(self, id=1, nombre="arch.txt", fecha=None, asunto="Asunto", origen="Origen", obs="Obs", tipo_antecedente=None):
            self.id=id; self.nombre_archivo=nombre; self.fecha_creacion=fecha or datetime.date.today();
            self.asunto_archivo=asunto; self.origen_archivo=origen; self.observaciones=obs; self.tipo_antecedente=tipo_antecedente
    class DummyArchivoSalida(DummyArchivo): # Simplified
        def __init__(self, id=1, nombre="salida.txt", destino="Destino", vb_general=False, **kwargs):
            super().__init__(id=id, nombre=nombre, **kwargs); self.destino_archivo=destino; self.visto_bueno_general=vb_general; self.visto_bueno_jefes=[]
    class DummyCategoria: # Simplified
        def __init__(self, id=1, nombre="CAT"): self.id=id; self.nombre_categoria = nombre
    
    class DummyEntrada: # Updated to better reflect model structure
        def __init__(self, id_entrada=1): # Renamed id to id_entrada to avoid clash
            self.id = id_entrada
            self.asunto = "Asunto de Prueba Detallado"
            self.fecha = datetime.datetime.now()
            self.negociado_principal = DummyRole(id=101, nombre_role="Negociado Principal de Pruebas")
            self.confidencial = False
            self.urgente = True
            self.numero_entrada = f"NE2024-{id_entrada:03d}"
            self.tramitado = False
            self.observaciones = "Observaciones iniciales de la entrada de prueba."
            self.tramitado_por_id = None
            self.tramitado_por = None
            self.canal_entrada = DummyCanal(id=201, nombre="Canal de Prueba")
            self.comentarios = [DummyComentario(texto="Primer comentario de prueba.")]
            self.archivos = [DummyArchivo(id=301, nombre="documento_entrada.pdf")]
            self.antecedentes = [DummyArchivo(id=401, nombre="informe_previo.doc", tipo_antecedente="Previo")]
            self.salidas = [DummyArchivoSalida(id=501, nombre="respuesta_oficial.pdf")]
            self.categorias = [DummyCategoria(id=601, nombre="IMPORTANTE"), DummyCategoria(id=602, nombre="REVISION")]
            # Simulate how destinatarios_roles might be structured if it's a list of Role objects
            self.destinatarios_roles = [
                DummyRole(id=701, nombre_role="Negociado Destino 1", is_jefe=False),
                DummyRole(id=702, nombre_role="Jefe Destino A", is_jefe=True, posicion="Director de Area")
            ]

    class MockDBManager: # Updated Mock
        def get_entrada_details_by_id(self, entrada_id):
            print(f"MockDB: get_entrada_details_by_id({entrada_id})")
            return DummyEntrada(entrada_id=entrada_id) # Use renamed id parameter
        
        def get_negociados(self, include_jefes=True): # include_jefes might be True or False
            print(f"MockDB: get_negociados(include_jefes={include_jefes})")
            base_negociados = [DummyRole(id=i, nombre_role=f"Negociado {i}", is_jefe=False) for i in range(1, 4)]
            if include_jefes:
                 base_negociados.extend([DummyRole(id=i+10, nombre_role=f"Jefe {i} (como Neg.)", is_jefe=True) for i in range(1,3)])
            return base_negociados

        def get_canales(self):
            print("MockDB: get_canales()")
            return [DummyCanal(id=i, nombre=f"Canal {i}") for i in range(1, 4)]
        
        def get_categorias(self):
            print("MockDB: get_categorias()")
            return [DummyCategoria(id=i, nombre=f"Categoría {i}") for i in range(1, 5)]

        def get_cargos(self): # For Jefes combobox
            print("MockDB: get_cargos()")
            return [DummyRole(id=i, nombre_role=f"Jefe {i}", is_jefe=True, posicion=f"Cargo {i}") for i in range(20, 23)]
        
        # Add mock update methods if you want to test the save part more deeply later
        # def update_entrada_datos_generales(self, entrada_id, changes_dict, user_id): print("MockDB: update_entrada_datos_generales called"); return True
        # def update_entrada_categorias(self, entrada_id, categoria_ids): print("MockDB: update_entrada_categorias called"); return True
        # def update_entrada_destinatarios(self, entrada_id, negociado_ids, jefe_ids): print("MockDB: update_entrada_destinatarios called"); return True


    root = tk.Tk()
    root.withdraw() 
    dummy_user = DummyUser()
    db_mock = MockDBManager()
    app = EntradaDetailViewWindow(root, 1, db_mock, dummy_user)
    root.mainloop()

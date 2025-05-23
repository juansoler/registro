import tkinter as tk
from tkinter import ttk, messagebox, simpledialog
from . import models # For type hinting if needed, and for data conversion

class ConfigManagementWindow(tk.Toplevel):
    def __init__(self, parent_window, current_user: models.Usuario, db_manager_module_ref):
        super().__init__(parent_window)
        self.parent_window = parent_window
        self.current_user = current_user # May be used for permissions later if needed
        self.db_manager = db_manager_module_ref

        self.title("Gestión de Configuración de Metadatos")
        self.geometry("700x500")
        self.transient(parent_window)
        self.grab_set()

        self.notebook = ttk.Notebook(self)
        self.notebook.pack(expand=True, fill=tk.BOTH, padx=10, pady=10)

        # Store treeviews and selected item IDs for each tab
        self.treeviews = {}
        self.selected_item_ids = {}

        self._create_negociados_tab()
        self._create_cargos_tab()
        self._create_canales_tab()
        self._create_categorias_tab()

    def _create_metadata_tab(self, tab_key: str, tab_title: str, item_name_singular: str, 
                             columns_map: dict, data_loader_func: callable, 
                             add_handler: callable, delete_handler: callable, 
                             edit_handler: callable = None):
        """
        Generic helper to create a metadata management tab.
        columns_map: e.g., {"id": "ID", "nombre": "Nombre"}
        """
        tab_frame = ttk.Frame(self.notebook, padding="10")
        self.notebook.add(tab_frame, text=tab_title)

        # Treeview for display
        tree_cols = list(columns_map.keys())
        tree = ttk.Treeview(tab_frame, columns=tree_cols, show="headings", selectmode="browse")
        for col_key, col_heading in columns_map.items():
            width = 50 if col_key == "id" else (100 if col_key == "posicion" else 200)
            anchor = "center" if col_key == "id" else "w"
            tree.heading(col_key, text=col_heading)
            tree.column(col_key, width=width, anchor=anchor, stretch=tk.YES if col_key != "id" else tk.NO)
        
        tree_scrollbar = ttk.Scrollbar(tab_frame, orient="vertical", command=tree.yview)
        tree.configure(yscrollcommand=tree_scrollbar.set)
        tree.pack(side=tk.TOP, fill=tk.BOTH, expand=True)
        tree_scrollbar.pack(side=tk.RIGHT, fill=tk.Y, before=tree) # ensure scrollbar is next to tree

        self.treeviews[tab_key] = tree
        self.selected_item_ids[tab_key] = None

        # Action Buttons
        button_frame = ttk.Frame(tab_frame, padding=(0, 10, 0, 0))
        button_frame.pack(fill=tk.X, side=tk.BOTTOM)

        btn_add = ttk.Button(button_frame, text=f"Añadir {item_name_singular}", command=add_handler)
        btn_add.pack(side=tk.LEFT, padx=5)

        btn_edit = None
        if edit_handler:
            btn_edit = ttk.Button(button_frame, text=f"Editar Seleccionado", command=lambda: edit_handler(tab_key), state="disabled")
            btn_edit.pack(side=tk.LEFT, padx=5)

        btn_delete = ttk.Button(button_frame, text=f"Eliminar Seleccionado", command=lambda: delete_handler(tab_key), state="disabled")
        btn_delete.pack(side=tk.LEFT, padx=5)
        
        btn_refresh = ttk.Button(button_frame, text="Refrescar Lista", command=lambda tk=tab_key, dlf=data_loader_func, cm=columns_map: self._load_data_to_treeview(tk, dlf, cm))
        btn_refresh.pack(side=tk.RIGHT, padx=5)

        # Bind selection to enable/disable buttons
        tree.bind("<<TreeviewSelect>>", lambda event, tk=tab_key, be=btn_edit, bd=btn_delete: self._on_item_select(event, tk, be, bd))
        
        # Initial data load
        self._load_data_to_treeview(tab_key, data_loader_func, columns_map)
        return tab_frame

    def _on_item_select(self, event, tab_key: str, btn_edit: Optional[ttk.Button], btn_delete: ttk.Button):
        tree = self.treeviews.get(tab_key)
        if not tree: return
        
        selected_items = tree.selection()
        if selected_items:
            item_values = tree.item(selected_items[0], "values")
            self.selected_item_ids[tab_key] = item_values[0] # Assuming ID is the first column
            if btn_edit: btn_edit.config(state="normal")
            btn_delete.config(state="normal")
        else:
            self.selected_item_ids[tab_key] = None
            if btn_edit: btn_edit.config(state="disabled")
            btn_delete.config(state="disabled")

    def _load_data_to_treeview(self, tab_key: str, data_loader_func: callable, columns_map: dict):
        tree = self.treeviews.get(tab_key)
        if not tree: return

        # Clear existing items
        for item in tree.get_children():
            tree.delete(item)
        
        self.selected_item_ids[tab_key] = None # Reset selection
        # Find edit/delete buttons for this tab to disable them
        # This is a bit of a hack to find them back if not passed directly.
        # A better way would be to store button references per tab.
        for child in tree.master.winfo_children(): # tree.master is the tab_frame's button_frame
            if isinstance(child, ttk.Frame): # button_frame
                 for btn in child.winfo_children():
                     if "Editar" in btn.cget("text"): btn.config(state="disabled")
                     if "Eliminar" in btn.cget("text"): btn.config(state="disabled")
        try:
            items = data_loader_func() # Expects list of objects or dicts
            for item in items:
                values = []
                for col_key in columns_map.keys():
                    if isinstance(item, dict):
                        val = item.get(col_key)
                    else: # Assuming model object
                        val = getattr(item, col_key, "")
                    values.append(val)
                tree.insert("", "end", values=tuple(values))
        except Exception as e:
            messagebox.showerror("Error de Carga", f"No se pudieron cargar los datos para '{tab_key}':\n{e}", parent=self)

    # --- Tab-specific methods ---

    # Negociados
    def _create_negociados_tab(self):
        self._create_metadata_tab(
            tab_key="negociados", tab_title="Negociados/Áreas", item_name_singular="Negociado",
            columns_map={"id": "ID", "nombre_role": "Nombre"}, # Assuming model.Role with nombre_role
            data_loader_func=lambda: self.db_manager.get_negociados(include_jefes=False), # Get only non-jefe roles
            add_handler=self._add_negociado,
            delete_handler=self._delete_item_generic
            # edit_handler=self._edit_negociado # Optional
        )
    def _add_negociado(self):
        name = simpledialog.askstring("Nuevo Negociado", "Nombre del Negociado:", parent=self.window)
        if name:
            try:
                if self.db_manager.add_negociado(name): # Assumes add_negociado returns True on success
                    self._load_data_to_treeview("negociados", lambda: self.db_manager.get_negociados(include_jefes=False), {"id": "ID", "nombre_role": "Nombre"})
                else: messagebox.showwarning("Fallo", "No se pudo añadir el negociado.", parent=self.window)
            except Exception as e: messagebox.showerror("Error", f"Error añadiendo negociado: {e}", parent=self.window)

    # Cargos (Jefes)
    def _create_cargos_tab(self):
        self._create_metadata_tab(
            tab_key="cargos", tab_title="Cargos (Jefes)", item_name_singular="Cargo",
            columns_map={"id": "ID", "nombre_role": "Nombre del Cargo", "posicion": "Posición"},
            data_loader_func=self.db_manager.get_cargos, # Assumes get_cargos returns roles that are jefes
            add_handler=self._add_cargo,
            delete_handler=self._delete_item_generic
        )
    def _add_cargo(self):
        name = simpledialog.askstring("Nuevo Cargo", "Nombre del Cargo/Jefe:", parent=self.window)
        if not name: return
        posicion = simpledialog.askstring("Nuevo Cargo", f"Posición para '{name}':", parent=self.window)
        if posicion is not None: # Allow empty string for posicion
            try:
                if self.db_manager.add_cargo(name, posicion):
                     self._load_data_to_treeview("cargos", self.db_manager.get_cargos, {"id": "ID", "nombre_role": "Nombre del Cargo", "posicion": "Posición"})
                else: messagebox.showwarning("Fallo", "No se pudo añadir el cargo.", parent=self.window)
            except Exception as e: messagebox.showerror("Error", f"Error añadiendo cargo: {e}", parent=self.window)

    # Canales
    def _create_canales_tab(self):
        self._create_metadata_tab(
            tab_key="canales", tab_title="Canales de Entrada", item_name_singular="Canal",
            columns_map={"id": "ID", "nombre": "Nombre del Canal"}, # Assuming CanalEntrada has 'id' and 'nombre'
            data_loader_func=self.db_manager.get_canales,
            add_handler=self._add_canal,
            delete_handler=self._delete_item_generic
        )
    def _add_canal(self):
        name = simpledialog.askstring("Nuevo Canal", "Nombre del Canal:", parent=self.window)
        if name:
            try:
                if self.db_manager.add_canal(name):
                    self._load_data_to_treeview("canales", self.db_manager.get_canales, {"id": "ID", "nombre": "Nombre del Canal"})
                else: messagebox.showwarning("Fallo", "No se pudo añadir el canal.", parent=self.window)
            except Exception as e: messagebox.showerror("Error", f"Error añadiendo canal: {e}", parent=self.window)

    # Categorías
    def _create_categorias_tab(self):
        self._create_metadata_tab(
            tab_key="categorias", tab_title="Categorías", item_name_singular="Categoría",
            columns_map={"id": "ID", "nombre": "Nombre de Categoría"}, # Assuming Categoria has 'id' and 'nombre'
            data_loader_func=self.db_manager.get_categorias,
            add_handler=self._add_categoria,
            delete_handler=self._delete_item_generic
        )
    def _add_categoria(self):
        name = simpledialog.askstring("Nueva Categoría", "Nombre de la Categoría:", parent=self.window)
        if name:
            try:
                if self.db_manager.add_categoria(name):
                    self._load_data_to_treeview("categorias", self.db_manager.get_categorias, {"id": "ID", "nombre": "Nombre de Categoría"})
                else: messagebox.showwarning("Fallo", "No se pudo añadir la categoría.", parent=self.window)
            except Exception as e: messagebox.showerror("Error", f"Error añadiendo categoría: {e}", parent=self.window)

    # Generic Delete Handler
    def _delete_item_generic(self, tab_key: str):
        item_id = self.selected_item_ids.get(tab_key)
        if not item_id:
            messagebox.showwarning("Nada Seleccionado", "Por favor, seleccione un ítem para eliminar.", parent=self.window)
            return

        item_name_for_confirm = ""
        tree = self.treeviews.get(tab_key)
        if tree:
            selected_tree_items = tree.selection()
            if selected_tree_items:
                 # Assuming name is the second column (index 1) after ID (index 0)
                item_name_for_confirm = tree.item(selected_tree_items[0], "values")[1] 
        
        confirm_msg = f"¿Está seguro de que desea eliminar '{item_name_for_confirm}' (ID: {item_id})?" \
                      if item_name_for_confirm else f"¿Está seguro de que desea eliminar el ítem ID: {item_id}?"

        if messagebox.askyesno("Confirmar Eliminación", confirm_msg, parent=self.window):
            delete_func = None
            refresh_loader = None
            refresh_cols_map = None

            if tab_key == "negociados": 
                delete_func = self.db_manager.delete_negociado
                refresh_loader = lambda: self.db_manager.get_negociados(include_jefes=False)
                refresh_cols_map = {"id": "ID", "nombre_role": "Nombre"}
            elif tab_key == "cargos": 
                delete_func = self.db_manager.delete_cargo
                refresh_loader = self.db_manager.get_cargos
                refresh_cols_map = {"id": "ID", "nombre_role": "Nombre del Cargo", "posicion": "Posición"}
            elif tab_key == "canales": 
                delete_func = self.db_manager.delete_canal
                refresh_loader = self.db_manager.get_canales
                refresh_cols_map = {"id": "ID", "nombre": "Nombre del Canal"}
            elif tab_key == "categorias": 
                delete_func = self.db_manager.delete_categoria
                refresh_loader = self.db_manager.get_categorias
                refresh_cols_map = {"id": "ID", "nombre": "Nombre de Categoría"}

            if delete_func and refresh_loader and refresh_cols_map:
                try:
                    if delete_func(int(item_id)): # Ensure ID is int
                        self._load_data_to_treeview(tab_key, refresh_loader, refresh_cols_map)
                    else:
                        messagebox.showwarning("Fallo", "No se pudo eliminar el ítem. Puede estar en uso.", parent=self.window)
                except AttributeError as ae:
                     messagebox.showerror("No Implementado", f"La función de borrado para '{tab_key}' no está disponible.", parent=self.window)
                except Exception as e:
                    messagebox.showerror("Error", f"Error eliminando ítem: {e}", parent=self.window)
            else:
                messagebox.showerror("Error Interno", f"Configuración de borrado no encontrada para {tab_key}.", parent=self.window)


if __name__ == '__main__':
    # Mock parent window and user for testing
    class MockParent(tk.Tk):
        def __init__(self):
            super().__init__()
            self.title("Mock Parent")
            ttk.Button(self, text="Open Config Management", command=self.open_config_mgmt).pack(padx=20, pady=20)

        def open_config_mgmt(self):
            # Mock current_user (admin)
            admin_role = models.Role(id=1, nombre_role="Admin", is_jefe=True)
            current_admin_user = models.Usuario(id=1, username="admin", roles=[admin_role], permiso=True)
            
            # Mock DB Manager with sample data and add/delete functions
            class MockDBManager:
                def __init__(self):
                    self.negociados = [models.Role(id=1, nombre_role="Contabilidad"), models.Role(id=2, nombre_role="RRHH")]
                    self.cargos = [models.Role(id=3, nombre_role="Jefe Contabilidad", posicion="P001", is_jefe=True)]
                    self.canales = [models.CanalEntrada(id=1, nombre="Email"), models.CanalEntrada(id=2, nombre="Teléfono")]
                    self.categorias = [models.Categoria(id=1, nombre="General"), models.Categoria(id=2, nombre="Urgente")]

                def get_negociados(self, include_jefes=True): print(f"MockDB: get_negociados(include_jefes={include_jefes})"); return [r for r in self.negociados if not r.is_jefe or include_jefes]
                def add_negociado(self, name): print(f"MockDB: add_negociado({name})"); self.negociados.append(models.Role(id=len(self.negociados)+100, nombre_role=name, is_jefe=False)); return True
                def delete_negociado(self, item_id): print(f"MockDB: delete_negociado({item_id})"); self.negociados = [n for n in self.negociados if n.id != item_id]; return True

                def get_cargos(self): print("MockDB: get_cargos()"); return self.cargos
                def add_cargo(self, name, posicion): print(f"MockDB: add_cargo({name}, {posicion})"); self.cargos.append(models.Role(id=len(self.cargos)+200, nombre_role=name, posicion=posicion, is_jefe=True)); return True
                def delete_cargo(self, item_id): print(f"MockDB: delete_cargo({item_id})"); self.cargos = [c for c in self.cargos if c.id != item_id]; return True
                
                def get_canales(self): print("MockDB: get_canales()"); return self.canales
                def add_canal(self, name): print(f"MockDB: add_canal({name})"); self.canales.append(models.CanalEntrada(id=len(self.canales)+300, nombre=name)); return True
                def delete_canal(self, item_id): print(f"MockDB: delete_canal({item_id})"); self.canales = [c for c in self.canales if c.id != item_id]; return True

                def get_categorias(self): print("MockDB: get_categorias()"); return self.categorias
                def add_categoria(self, name): print(f"MockDB: add_categoria({name})"); self.categorias.append(models.Categoria(id=len(self.categorias)+400, nombre=name)); return True
                def delete_categoria(self, item_id): print(f"MockDB: delete_categoria({item_id})"); self.categorias = [c for c in self.categorias if c.id != item_id]; return True

            mock_db = MockDBManager()
            config_window = ConfigManagementWindow(self, current_admin_user, mock_db)
            # config_window.grab_set() # Already in __init__

    app = MockParent()
    app.mainloop()

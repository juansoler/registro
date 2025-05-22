import tkinter as tk
from tkinter import ttk, messagebox, simpledialog

# Attempt to import database_manager and models
try:
    from . import database_manager
    from . import models # For type hints if needed, e.g. models.Role
    _database_manager_available = True
except ImportError:
    print("Warning: AdminConfigEditorWindow cannot import database_manager or models. Using mock data/placeholders.")
    _database_manager_available = False
    # Dummy classes for standalone testing
    class DummyRole:
        def __init__(self, id, nombre_role, posicion=None, isJefe=False): 
            self.id = id; self.nombre_role = nombre_role; self.posicion = posicion; self.isJefe = isJefe
    class DummyCanalEntrada:
        def __init__(self, id, nombre): self.id = id; self.nombre = nombre
    class DummyCategoria:
        def __init__(self, nombre): self.nombre = nombre # Assuming name is identifier for now

    models = type('models_dummy', (object,), {
        'Role': DummyRole, 'CanalEntrada': DummyCanalEntrada, 'Categoria': DummyCategoria
    })


class AdminConfigEditorWindow(tk.Toplevel):
    def __init__(self, parent, current_user_object): # current_user_object might not be needed here
        super().__init__(parent)
        self.parent = parent
        # self.current_user_object = current_user_object # Not used in this UI directly

        self.title("Gestión de Datos de Configuración")
        self.geometry("700x500")
        self.minsize(500, 350)

        self._create_widgets()
        self._load_all_data() # Load initial data for all tabs

        self.protocol("WM_DELETE_WINDOW", self._close_window)
        self.grab_set()

    def _create_widgets(self):
        main_frame = ttk.Frame(self, padding="5")
        main_frame.pack(fill=tk.BOTH, expand=True)

        self.notebook = ttk.Notebook(main_frame)
        self.notebook.pack(fill=tk.BOTH, expand=True, padx=5, pady=5)

        # Create tabs
        self._create_negociados_tab()
        self._create_cargos_tab()
        self._create_canales_tab()
        self._create_categorias_tab()

        # Close button
        close_button = ttk.Button(main_frame, text="Cerrar", command=self._close_window)
        close_button.pack(pady=10, side=tk.BOTTOM)

    def _create_tab_frame(self, tab_name: str, item_label: str, load_command, add_command, delete_command, has_extra_field: bool = False, extra_field_label: str = ""):
        tab = ttk.Frame(self.notebook, padding="10")
        self.notebook.add(tab, text=tab_name)
        
        # Listbox to display items
        list_frame = ttk.Frame(tab)
        list_frame.pack(pady=5, fill=tk.BOTH, expand=True)
        
        listbox = tk.Listbox(list_frame, height=10)
        listbox_scroll = ttk.Scrollbar(list_frame, orient="vertical", command=listbox.yview)
        listbox.configure(yscrollcommand=listbox_scroll.set)
        listbox.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        listbox_scroll.pack(side=tk.RIGHT, fill=tk.Y)
        setattr(self, f"{tab_name.lower().replace(' ', '_').replace('(','').replace(')','').replace('é','e')}_listbox", listbox) # e.g. self.negociados_listbox

        # Input frame for adding new items
        input_frame = ttk.Frame(tab)
        input_frame.pack(fill=tk.X, pady=5)

        ttk.Label(input_frame, text=f"{item_label}:").grid(row=0, column=0, padx=5, pady=5, sticky="w")
        entry = ttk.Entry(input_frame, width=30)
        entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")
        setattr(self, f"new_{tab_name.lower().replace(' ', '_').replace('(','').replace(')','').replace('é','e')}_entry", entry) # e.g. self.new_negociados_entry

        extra_entry = None
        if has_extra_field:
            ttk.Label(input_frame, text=f"{extra_field_label}:").grid(row=1, column=0, padx=5, pady=5, sticky="w")
            extra_entry = ttk.Entry(input_frame, width=10) # Or Spinbox
            extra_entry.grid(row=1, column=1, padx=5, pady=5, sticky="w")
            setattr(self, f"new_{tab_name.lower().replace(' ', '_').replace('(','').replace(')','').replace('é','e')}_extra_entry", extra_entry)

        # Buttons
        button_frame = ttk.Frame(tab)
        button_frame.pack(fill=tk.X, pady=5)
        
        add_btn_command = add_command
        if extra_entry: # Pass extra entry if it exists
            add_btn_command = lambda e=entry, ex=extra_entry, cmd=add_command: cmd(e, ex)
        else:
            add_btn_command = lambda e=entry, cmd=add_command: cmd(e)
            
        ttk.Button(button_frame, text="Añadir", command=add_btn_command).pack(side=tk.LEFT, padx=5)
        ttk.Button(button_frame, text="Eliminar Seleccionado", command=lambda lb=listbox, cmd=delete_command: cmd(lb)).pack(side=tk.LEFT, padx=5)
        ttk.Button(button_frame, text="Refrescar Lista", command=load_command).pack(side=tk.RIGHT, padx=5)

    def _create_negociados_tab(self):
        self._create_tab_frame("Negociados", "Nombre Negociado", self._load_negociados, self._add_negociado, self._delete_negociado)
    
    def _create_cargos_tab(self):
        self._create_tab_frame("Cargos (Jefes)", "Nombre Cargo", self._load_cargos, self._add_cargo, self._delete_cargo, True, "Posición")

    def _create_canales_tab(self):
        self._create_tab_frame("Canales de Entrada", "Nombre Canal", self._load_canales, self._add_canal, self._delete_canal)

    def _create_categorias_tab(self):
        self._create_tab_frame("Categorías", "Nombre Categoría", self._load_categorias, self._add_categoria, self._delete_categoria)

    def _load_all_data(self):
        self._load_negociados()
        self._load_cargos()
        self._load_canales()
        self._load_categorias()

    # --- Negociados ---
    def _load_negociados(self):
        self.negociados_listbox.delete(0, tk.END)
        if _database_manager_available:
            try:
                for item in database_manager.get_negociados(): # Returns Role objects
                    self.negociados_listbox.insert(tk.END, item.nombre_role)
            except Exception as e: print(f"Error loading negociados: {e}")
        else: self.negociados_listbox.insert(tk.END, "Mock Negociado 1"); self.negociados_listbox.insert(tk.END, "Mock Negociado 2")
    
    def _add_negociado(self, entry_widget):
        name = entry_widget.get().strip()
        if not name: messagebox.showwarning("Inválido", "Nombre de negociado no puede estar vacío.", parent=self); return
        if _database_manager_available:
            try:
                if database_manager.add_negociado(name): self._load_negociados(); entry_widget.delete(0, tk.END)
                else: messagebox.showerror("Error", f"No se pudo añadir negociado '{name}'.", parent=self)
            except AttributeError: messagebox.showinfo("Info", f"DB: Añadir negociado '{name}' (placeholder).", parent=self); self._load_negociados(); entry_widget.delete(0, tk.END) # Placeholder
            except Exception as e: messagebox.showerror("Error", f"Error añadiendo negociado: {e}", parent=self)
        else: messagebox.showinfo("Info", f"Añadir negociado '{name}' (mock).", parent=self); self._load_negociados(); entry_widget.delete(0, tk.END)

    def _delete_negociado(self, listbox_widget):
        selected = listbox_widget.curselection()
        if not selected: messagebox.showwarning("Inválido", "Seleccione un negociado para eliminar.", parent=self); return
        name = listbox_widget.get(selected[0])
        if messagebox.askyesno("Confirmar", f"¿Eliminar negociado '{name}'?", parent=self):
            if _database_manager_available:
                try: 
                    if database_manager.delete_negociado(name): self._load_negociados()
                    else: messagebox.showerror("Error", f"No se pudo eliminar negociado '{name}'.", parent=self)
                except AttributeError: messagebox.showinfo("Info", f"DB: Eliminar negociado '{name}' (placeholder).", parent=self); self._load_negociados() # Placeholder
                except Exception as e: messagebox.showerror("Error", f"Error eliminando negociado: {e}", parent=self)
            else: messagebox.showinfo("Info", f"Eliminar negociado '{name}' (mock).", parent=self); self._load_negociados()


    # --- Cargos (Jefes) ---
    def _load_cargos(self):
        self.cargos_jefes_listbox.delete(0, tk.END)
        if _database_manager_available:
            try:
                for item in database_manager.get_cargos(): # Returns Role objects
                    self.cargos_jefes_listbox.insert(tk.END, f"{item.nombre_role} (Pos: {item.posicion})")
            except Exception as e: print(f"Error loading cargos: {e}")
        else: self.cargos_jefes_listbox.insert(tk.END, "Mock Cargo 1 (Pos: 1)"); self.cargos_jefes_listbox.insert(tk.END, "Mock Cargo 2 (Pos: 2)")

    def _add_cargo(self, name_entry, pos_entry):
        name = name_entry.get().strip()
        pos_str = pos_entry.get().strip()
        if not name or not pos_str: messagebox.showwarning("Inválido", "Nombre y posición son requeridos.", parent=self); return
        try: pos = int(pos_str)
        except ValueError: messagebox.showwarning("Inválido", "Posición debe ser un número.", parent=self); return
        
        if _database_manager_available:
            try:
                if database_manager.add_cargo(name, pos): self._load_cargos(); name_entry.delete(0, tk.END); pos_entry.delete(0, tk.END)
                else: messagebox.showerror("Error", f"No se pudo añadir cargo '{name}'.", parent=self)
            except AttributeError: messagebox.showinfo("Info", f"DB: Añadir cargo '{name}', Pos: {pos} (placeholder).", parent=self); self._load_cargos(); name_entry.delete(0, tk.END); pos_entry.delete(0, tk.END)
            except Exception as e: messagebox.showerror("Error", f"Error añadiendo cargo: {e}", parent=self)
        else: messagebox.showinfo("Info", f"Añadir cargo '{name}', Pos: {pos} (mock).", parent=self); self._load_cargos(); name_entry.delete(0, tk.END); pos_entry.delete(0, tk.END)


    def _delete_cargo(self, listbox_widget):
        selected = listbox_widget.curselection()
        if not selected: messagebox.showwarning("Inválido", "Seleccione un cargo para eliminar.", parent=self); return
        # Extract name from "Name (Pos: X)"
        full_text = listbox_widget.get(selected[0])
        name = full_text.split(" (Pos:")[0]
        if messagebox.askyesno("Confirmar", f"¿Eliminar cargo '{name}'?", parent=self):
            if _database_manager_available:
                try:
                    if database_manager.delete_cargo(name): self._load_cargos()
                    else: messagebox.showerror("Error", f"No se pudo eliminar cargo '{name}'.", parent=self)
                except AttributeError: messagebox.showinfo("Info", f"DB: Eliminar cargo '{name}' (placeholder).", parent=self); self._load_cargos()
                except Exception as e: messagebox.showerror("Error", f"Error eliminando cargo: {e}", parent=self)
            else: messagebox.showinfo("Info", f"Eliminar cargo '{name}' (mock).", parent=self); self._load_cargos()


    # --- Canales de Entrada ---
    def _load_canales(self):
        self.canales_de_entrada_listbox.delete(0, tk.END)
        if _database_manager_available:
            try:
                for item in database_manager.get_canales(): self.canales_de_entrada_listbox.insert(tk.END, item.nombre)
            except Exception as e: print(f"Error loading canales: {e}")
        else: self.canales_de_entrada_listbox.insert(tk.END, "Mock Canal A"); self.canales_de_entrada_listbox.insert(tk.END, "Mock Canal B")
    
    def _add_canal(self, entry_widget):
        name = entry_widget.get().strip()
        if not name: messagebox.showwarning("Inválido", "Nombre de canal no puede estar vacío.", parent=self); return
        if _database_manager_available:
            try:
                if database_manager.add_canal_entrada(name): self._load_canales(); entry_widget.delete(0, tk.END)
                else: messagebox.showerror("Error", f"No se pudo añadir canal '{name}'.", parent=self)
            except AttributeError: messagebox.showinfo("Info", f"DB: Añadir canal '{name}' (placeholder).", parent=self); self._load_canales(); entry_widget.delete(0, tk.END)
            except Exception as e: messagebox.showerror("Error", f"Error añadiendo canal: {e}", parent=self)
        else: messagebox.showinfo("Info", f"Añadir canal '{name}' (mock).", parent=self); self._load_canales(); entry_widget.delete(0, tk.END)

    def _delete_canal(self, listbox_widget):
        selected = listbox_widget.curselection()
        if not selected: messagebox.showwarning("Inválido", "Seleccione un canal para eliminar.", parent=self); return
        name = listbox_widget.get(selected[0])
        if messagebox.askyesno("Confirmar", f"¿Eliminar canal '{name}'?", parent=self):
            if _database_manager_available:
                try:
                    if database_manager.delete_canal_entrada(name): self._load_canales()
                    else: messagebox.showerror("Error", f"No se pudo eliminar canal '{name}'.", parent=self)
                except AttributeError: messagebox.showinfo("Info", f"DB: Eliminar canal '{name}' (placeholder).", parent=self); self._load_canales()
                except Exception as e: messagebox.showerror("Error", f"Error eliminando canal: {e}", parent=self)
            else: messagebox.showinfo("Info", f"Eliminar canal '{name}' (mock).", parent=self); self._load_canales()

    # --- Categorías ---
    def _load_categorias(self):
        self.categorias_listbox.delete(0, tk.END)
        if _database_manager_available:
            try:
                for item in database_manager.get_categorias(): self.categorias_listbox.insert(tk.END, item.nombre)
            except Exception as e: print(f"Error loading categorias: {e}")
        else: self.categorias_listbox.insert(tk.END, "Mock Categoria X"); self.categorias_listbox.insert(tk.END, "Mock Categoria Y")

    def _add_categoria(self, entry_widget):
        name = entry_widget.get().strip()
        if not name: messagebox.showwarning("Inválido", "Nombre de categoría no puede estar vacío.", parent=self); return
        if _database_manager_available:
            try:
                if database_manager.add_categoria(name): self._load_categorias(); entry_widget.delete(0, tk.END)
                else: messagebox.showerror("Error", f"No se pudo añadir categoría '{name}'.", parent=self)
            except AttributeError: messagebox.showinfo("Info", f"DB: Añadir categoría '{name}' (placeholder).", parent=self); self._load_categorias(); entry_widget.delete(0, tk.END)
            except Exception as e: messagebox.showerror("Error", f"Error añadiendo categoría: {e}", parent=self)
        else: messagebox.showinfo("Info", f"Añadir categoría '{name}' (mock).", parent=self); self._load_categorias(); entry_widget.delete(0, tk.END)

    def _delete_categoria(self, listbox_widget):
        selected = listbox_widget.curselection()
        if not selected: messagebox.showwarning("Inválido", "Seleccione una categoría para eliminar.", parent=self); return
        name = listbox_widget.get(selected[0])
        if messagebox.askyesno("Confirmar", f"¿Eliminar categoría '{name}'?", parent=self):
            if _database_manager_available:
                try:
                    if database_manager.delete_categoria(name): self._load_categorias()
                    else: messagebox.showerror("Error", f"No se pudo eliminar categoría '{name}'.", parent=self)
                except AttributeError: messagebox.showinfo("Info", f"DB: Eliminar categoría '{name}' (placeholder).", parent=self); self._load_categorias()
                except Exception as e: messagebox.showerror("Error", f"Error eliminando categoría: {e}", parent=self)
            else: messagebox.showinfo("Info", f"Eliminar categoría '{name}' (mock).", parent=self); self._load_categorias()

    def _close_window(self):
        self.destroy()

# --- Standalone Test ---
if __name__ == '__main__':
    root = tk.Tk()
    root.title("Main Test Window (AdminConfigEditor)")
    root.withdraw() 

    admin_user_for_test = None # Not strictly needed for this UI, but good for context
    if _database_manager_available:
         admin_user_for_test = models.Usuario(id=0, username="test_admin_runner", password_hash="", roles=[])
    else:
         admin_user_for_test = type('DummyUser', (object,), {'id':0, 'username':'test_admin'})()


    # Mock database_manager for standalone UI testing
    if not _database_manager_available:
        class MockDBManager:
            _negociados = [models.Role(id=1, nombre_role="Negociado Alpha"), models.Role(id=2, nombre_role="Negociado Beta")]
            _cargos = [models.Role(id=10, nombre_role="Jefe de Sección", posicion=1), models.Role(id=11, nombre_role="Coordinador", posicion=2)]
            _canales = [models.CanalEntrada(id=1, nombre="Email Corporativo"), models.CanalEntrada(id=2, nombre="Formulario Web")]
            _categorias = [models.Categoria(nombre="General"), models.Categoria(nombre="Urgente"), models.Categoria(nombre="Proyectos Especiales")]

            def get_negociados(self): print("MOCK DB: get_negociados"); return self._negociados[:]
            def add_negociado(self, name): print(f"MOCK DB: add_negociado '{name}'"); self._negociados.append(models.Role(id=len(self._negociados)+100, nombre_role=name)); return True
            def delete_negociado(self, name): print(f"MOCK DB: delete_negociado '{name}'"); self._negociados = [n for n in self._negociados if n.nombre_role != name]; return True
            
            def get_cargos(self): print("MOCK DB: get_cargos"); return self._cargos[:]
            def add_cargo(self, name, pos): print(f"MOCK DB: add_cargo '{name}' Pos: {pos}"); self._cargos.append(models.Role(id=len(self._cargos)+200, nombre_role=name, posicion=pos)); return True
            def delete_cargo(self, name): print(f"MOCK DB: delete_cargo '{name}'"); self._cargos = [c for c in self._cargos if c.nombre_role != name]; return True

            def get_canales(self): print("MOCK DB: get_canales"); return self._canales[:]
            def add_canal_entrada(self, name): print(f"MOCK DB: add_canal_entrada '{name}'"); self._canales.append(models.CanalEntrada(id=len(self._canales)+300, nombre=name)); return True
            def delete_canal_entrada(self, name): print(f"MOCK DB: delete_canal_entrada '{name}'"); self._canales = [c for c in self._canales if c.nombre != name]; return True

            def get_categorias(self): print("MOCK DB: get_categorias"); return self._categorias[:]
            def add_categoria(self, name): print(f"MOCK DB: add_categoria '{name}'"); self._categorias.append(models.Categoria(nombre=name)); return True
            def delete_categoria(self, name): print(f"MOCK DB: delete_categoria '{name}'"); self._categorias = [c for c in self._categorias if c.nombre != name]; return True

        database_manager = MockDBManager()
        print("Using MOCK database_manager for AdminConfigEditorWindow test.")


    def open_admin_config_dialog():
        dialog = AdminConfigEditorWindow(root, admin_user_for_test)
        # root.wait_window(dialog) # Makes test hang if dialog closed by button

    ttk.Button(root, text="Abrir Gestión de Configuración", command=open_admin_config_dialog).pack(padx=20, pady=20)
    root.deiconify()
    root.mainloop()

```

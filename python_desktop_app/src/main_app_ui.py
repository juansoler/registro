import tkinter as tk
from tkinter import ttk, messagebox
from . import models # Assuming models.py is in the same directory (src)
from . import database_manager as db_manager_module # For passing to detail view
from .entrada_detail_view_ui import EntradaDetailViewWindow # Import the new detail view

class MainApplicationWindow:
    def __init__(self, root_tk, user_object: models.Usuario, config_data: dict):
        self.root = root_tk # This will be the main Tk() instance from main.py
        self.user_object = user_object
        self.config_data = config_data
        
        # Flags for special filters
        self.filter_pending_processing = False
        self.filter_pending_view_for_jefe_id = None

        # Use the passed root_tk as the main window
        # self.window = tk.Toplevel(root_tk) # If root_tk is a hidden main Tk, use Toplevel
        self.window = self.root # If root_tk is meant to be the main window
        
        # Configure the root window (which was previously hidden)
        self.window.deiconify() # Show the window
        
        username = "N/A"
        if hasattr(user_object, 'username') and user_object.username:
            username = user_object.username
        elif hasattr(user_object, 'nombre_usuario') and user_object.nombre_usuario: # Fallback if attribute name differs
            username = user_object.nombre_usuario
            
        self.window.title(f"Main Application - {username}")
        self.window.geometry("800x600") # Default size

        self._create_menu()
        self._create_main_content_area()

        self.window.protocol("WM_DELETE_WINDOW", self._on_exit)

    def _on_exit(self):
        if messagebox.askokcancel("Exit", "Do you really want to exit the application?"):
            self.window.destroy() # This will terminate the mainloop if this is the root Tk window

    def _create_menu(self):
        menubar = tk.Menu(self.window)
        self.window.config(menu=menubar)

        # File Menu
        file_menu = tk.Menu(menubar, tearoff=0)
        menubar.add_cascade(label="File", menu=file_menu)
        file_menu.add_command(label="Exit", command=self._on_exit)

        # Entradas Menu
        self.entradas_menu = tk.Menu(menubar, tearoff=0)
        menubar.add_cascade(label="Entradas", menu=self.entradas_menu)
        
        # Mostrar opción de crear entrada solo si el usuario tiene permiso
        if hasattr(self.user_object, 'permiso') and self.user_object.permiso:
            self.entradas_menu.add_command(label="Nueva entrada", command=self._crear_nueva_entrada)
        else:
            self.entradas_menu.add_command(label="Nueva entrada", state="disabled")

        # Admin Menu (Placeholder)
        self.admin_menu = tk.Menu(menubar, tearoff=0)
        menubar.add_cascade(label="Admin", menu=self.admin_menu, state="disabled") # Initially disabled
        self.admin_menu.add_command(label="Admin Option 1 (Placeholder)", command=lambda: print("Admin Option 1 clicked"))

        # Enable Admin menu based on role (example logic)
        is_admin_user = False
        if self.user_object and hasattr(self.user_object, 'roles'):
            for role in self.user_object.roles:
                # Assuming 'Admin' is the specific role name for admin privileges
                # or username 'admin' has implicit admin rights.
                if (hasattr(role, 'nombre_role') and role.nombre_role.lower() == "admin") or \
                   (hasattr(self.user_object, 'username') and self.user_object.username.lower() == "admin"):
                    is_admin_user = True
                    break
        
        if is_admin_user:
            menubar.entryconfig("Admin", state="normal")
            from .user_management_ui import UserManagementWindow # Import here to avoid circularity if any
            from .config_management_ui import ConfigManagementWindow # Import for config management

            self.admin_menu.add_command(
                label="Gestión de Usuarios",
                command=lambda: UserManagementWindow(self.window, self.user_object, db_manager_module)
            )
            self.admin_menu.add_command(
                label="Gestión de Configuración",
                command=lambda: ConfigManagementWindow(self.window, self.user_object, db_manager_module)
            )
        else:
            # Ensure admin menu is disabled if user is not admin (might already be default)
            menubar.entryconfig("Admin", state="disabled")


    def _create_main_content_area(self):
        main_frame = ttk.Frame(self.window, padding="10")
        main_frame.pack(expand=True, fill=tk.BOTH)

        # Welcome Message
        welcome_text = "Welcome!"
        if hasattr(self.user_object, 'username') and self.user_object.username:
            welcome_text = f"Welcome, {self.user_object.username}!"
        elif hasattr(self.user_object, 'nombre_usuario') and self.user_object.nombre_usuario: # Fallback
            welcome_text = f"Welcome, {self.user_object.nombre_usuario}!"
        
        welcome_label = ttk.Label(main_frame, text=welcome_text, font=("Arial", 16))
        welcome_label.pack(pady=10)

        # Display BASE_DIR
        base_dir = self.config_data.get("BASE_DIR", "Not configured")
        base_dir_label = ttk.Label(main_frame, text=f"Application Base Directory: {base_dir}")
        base_dir_label.pack(pady=5)

        # Display User Roles
        roles_frame = ttk.Labelframe(main_frame, text="Your Roles/Negociados", padding="10")
        roles_frame.pack(pady=10, fill=tk.X)

        if self.user_object and hasattr(self.user_object, 'roles') and self.user_object.roles:
            self.roles_combobox = ttk.Combobox(roles_frame, state="readonly")
            role_names = [role.nombre_role for role in self.user_object.roles if hasattr(role, 'nombre_role')]
            if role_names:
                self.roles_combobox['values'] = role_names
                self.roles_combobox.current(0)
            else:
                self.roles_combobox['values'] = ["No roles assigned"]
                self.roles_combobox.current(0)
            self.roles_combobox.pack(fill=tk.X)
        else:
            no_roles_label = ttk.Label(roles_frame, text="No roles information available.")
            no_roles_label.pack()

        # --- Filter Frame ---
        filter_frame = ttk.LabelFrame(main_frame, text="Filtros y Búsqueda", padding="10")
        filter_frame.pack(pady=10, fill=tk.X)
        
        # Row 1: Search Term and Type
        search_frame = ttk.Frame(filter_frame)
        search_frame.pack(fill=tk.X, pady=5)
        ttk.Label(search_frame, text="Buscar:").pack(side=tk.LEFT, padx=(0,5))
        self.search_term_entry = ttk.Entry(search_frame, width=30)
        self.search_term_entry.pack(side=tk.LEFT, padx=5)
        
        ttk.Label(search_frame, text="en:").pack(side=tk.LEFT, padx=(10,5))
        self.search_type_combobox = ttk.Combobox(search_frame, state="readonly", values=["Asunto", "Número Entrada"], width=15)
        self.search_type_combobox.current(0) # Default to "Asunto"
        self.search_type_combobox.pack(side=tk.LEFT, padx=5)

        # Row 2: Area and Category Filters
        filter_select_frame = ttk.Frame(filter_frame)
        filter_select_frame.pack(fill=tk.X, pady=5)
        ttk.Label(filter_select_frame, text="Área:").pack(side=tk.LEFT, padx=(0,5))
        self.area_filter_combobox = ttk.Combobox(filter_select_frame, state="readonly", width=20)
        self.area_filter_combobox.pack(side=tk.LEFT, padx=5)
        self._populate_area_filter_combobox()

        ttk.Label(filter_select_frame, text="Categoría:").pack(side=tk.LEFT, padx=(10,5))
        self.category_filter_combobox = ttk.Combobox(filter_select_frame, state="readonly", width=20)
        self.category_filter_combobox.pack(side=tk.LEFT, padx=5)
        self._populate_category_filter_combobox()
        
        # Row 3: Date Filters
        date_filter_frame = ttk.Frame(filter_frame)
        date_filter_frame.pack(fill=tk.X, pady=5)
        
        self.date_from_placeholder = "YYYY-MM-DD"
        self.date_to_placeholder = "YYYY-MM-DD"

        ttk.Label(date_filter_frame, text="Fecha Desde:").pack(side=tk.LEFT, padx=(0,5))
        self.date_from_entry = ttk.Entry(date_filter_frame, width=12)
        self.date_from_entry.insert(0, self.date_from_placeholder)
        self.date_from_entry.bind("<FocusIn>", lambda args: self._clear_placeholder(self.date_from_entry, self.date_from_placeholder))
        self.date_from_entry.bind("<FocusOut>", lambda args: self._restore_placeholder(self.date_from_entry, self.date_from_placeholder))
        self.date_from_entry.pack(side=tk.LEFT, padx=5)

        ttk.Label(date_filter_frame, text="Fecha Hasta:").pack(side=tk.LEFT, padx=(10,5))
        self.date_to_entry = ttk.Entry(date_filter_frame, width=12)
        self.date_to_entry.insert(0, self.date_to_placeholder)
        self.date_to_entry.bind("<FocusIn>", lambda args: self._clear_placeholder(self.date_to_entry, self.date_to_placeholder))
        self.date_to_entry.bind("<FocusOut>", lambda args: self._restore_placeholder(self.date_to_entry, self.date_to_placeholder))
        self.date_to_entry.pack(side=tk.LEFT, padx=5)

        # Row 4: Search Button
        button_frame = ttk.Frame(filter_frame) 
        button_frame.pack(fill=tk.X, pady=5)
        
        self.search_button = ttk.Button(button_frame, text="Buscar / Actualizar", command=self._filter_and_load_entradas_data)
        self.search_button.pack(side=tk.LEFT, padx=5)

        self.btn_pending_processing = ttk.Button(button_frame, text="Mostrar Pdte. Tramit.", command=self._show_pending_processing)
        self.btn_pending_processing.pack(side=tk.LEFT, padx=5)

        self.btn_pending_view = ttk.Button(button_frame, text="Mostrar Pdte. Ver", command=self._show_pending_view)
        self.btn_pending_view.pack(side=tk.LEFT, padx=5)
        
        self._update_special_filter_button_states()


        # Tabla de Entradas
        entradas_frame = ttk.Labelframe(main_frame, text="Listado de Entradas", padding="10")
        entradas_frame.pack(pady=10, expand=True, fill=tk.BOTH)

        # Crear Treeview para mostrar las entradas
        columns = ("id", "numero", "fecha", "asunto", "canal", "urgente", "confidencial", "tramitado", "observaciones", "tramitado_por")
        self.entradas_tree = ttk.Treeview(
            entradas_frame,
            columns=columns,
            show="headings",
            selectmode="browse"
        )

        # Configurar columnas
        self.entradas_tree.heading("id", text="ID")
        self.entradas_tree.heading("numero", text="Número")
        self.entradas_tree.heading("fecha", text="Fecha")
        self.entradas_tree.heading("asunto", text="Asunto")
        self.entradas_tree.heading("canal", text="Canal")
        self.entradas_tree.heading("urgente", text="Urgente")
        self.entradas_tree.heading("confidencial", text="Confidencial")
        self.entradas_tree.heading("tramitado", text="Tramitado")
        self.entradas_tree.heading("observaciones", text="Observaciones")
        self.entradas_tree.heading("tramitado_por", text="Tramitado Por")

        # Ajustar anchos de columnas
        self.entradas_tree.column("id", width=30, anchor="center")
        self.entradas_tree.column("numero", width=80, anchor="center")
        self.entradas_tree.column("fecha", width=80, anchor="center")
        self.entradas_tree.column("asunto", width=150)
        self.entradas_tree.column("canal", width=100, anchor="center")
        self.entradas_tree.column("urgente", width=70, anchor="center")
        self.entradas_tree.column("confidencial", width=70, anchor="center")
        self.entradas_tree.column("tramitado", width=70, anchor="center")
        self.entradas_tree.column("observaciones", width=150)
        self.entradas_tree.column("tramitado_por", width=100, anchor="center")
        
        # Configurar tag para filas urgentes
        self.entradas_tree.tag_configure('urgent_row', background='pink')

        # Scrollbar vertical
        scrollbar = ttk.Scrollbar(entradas_frame, orient="vertical", command=self.entradas_tree.yview)
        self.entradas_tree.configure(yscrollcommand=scrollbar.set)
        scrollbar.pack(side="right", fill="y")

        self.entradas_tree.pack(expand=True, fill=tk.BOTH)
        
        # Bind double-click event
        self.entradas_tree.bind("<Double-1>", self._on_entrada_double_click)

        # Cargar datos de entradas
        self._load_entradas_data()

    def _on_entrada_double_click(self, event):
        """Handler for double-click event on an entrada row."""
        try:
            if not self.entradas_tree.selection(): # Check if anything is selected
                return

            item_id = self.entradas_tree.selection()[0]
            item_values = self.entradas_tree.item(item_id, 'values')
            
            if item_values and len(item_values) > 0:
                entrada_id_str = item_values[0] # Assuming ID is the first column
                try:
                    entrada_id = int(entrada_id_str)
                    detail_view_window = EntradaDetailViewWindow(self.window, entrada_id, db_manager_module, self.user_object)
                    self.window.wait_window(detail_view_window.window) # Wait for the detail window to close
                    
                    # Check if the entrada was deleted in the detail view
                    if hasattr(detail_view_window, 'entrada_deleted') and detail_view_window.entrada_deleted:
                        self._load_entradas_data() # Refresh the list
                except ValueError:
                    messagebox.showerror("Error", f"ID de entrada inválido: {entrada_id_str}", parent=self.window)
            else:
                messagebox.showwarning("Información", "No se pudieron obtener los valores de la entrada seleccionada.", parent=self.window)

        except IndexError:
            print("No item selected or item no longer exists.")
        except Exception as e:
            messagebox.showerror("Error", f"Error processing double-click: {e}")


    def _crear_nueva_entrada(self):
        """Handler para crear una nueva entrada"""
        try:
            from .nueva_entrada_ui import NuevaEntradaWindow
            dialog = NuevaEntradaWindow(self.window, self.user_object)
            self.window.wait_window(dialog)
        except ImportError as e:
            messagebox.showerror("Error", f"No se pudo cargar el módulo de nueva entrada: {e}")
        except Exception as e:
            messagebox.showerror("Error", f"Error al crear nueva entrada: {e}")

        # Actualizar tabla después de crear nueva entrada
        self._load_entradas_data()

    def _load_entradas_data(self):
        """Carga los datos de entradas desde la base de datos y los muestra en la tabla"""
        try:
            import logging
            logging.basicConfig(level=logging.DEBUG)
            logger = logging.getLogger(__name__)
            
            from .database_manager import get_entradas_for_user, get_negociados, get_categorias
            logger.debug(f"User object roles: {[r.nombre_role for r in self.user_object.roles] if self.user_object and hasattr(self.user_object, 'roles') else 'No roles'}")

            # Get filter values
            search_term = self.search_term_entry.get().strip()
            search_type_raw = self.search_type_combobox.get()
            # Map search type to model field name or specific query type for backend
            search_field_map = {"Asunto": "asunto", "Número Entrada": "numero_entrada"}
            search_field = search_field_map.get(search_type_raw, "asunto") # Default to asunto

            area_filter = self.area_filter_combobox.get()
            if area_filter == "Todos": area_filter = None
            
            category_filter = self.category_filter_combobox.get()
            if category_filter == "Todas": category_filter = None

            date_from_str = self.date_from_entry.get()
            if date_from_str == self.date_from_placeholder: date_from_str = None
            
            date_to_str = self.date_to_entry.get()
            if date_to_str == self.date_to_placeholder: date_to_str = None

            # Include special filter flags
            current_pending_processing = self.filter_pending_processing
            current_pending_view_jefe_id = self.filter_pending_view_for_jefe_id

            logger.debug(f"Filtering with: term='{search_term}', field='{search_field}', area='{area_filter}', category='{category_filter}', "
                         f"from='{date_from_str}', to='{date_to_str}', "
                         f"pending_processing='{current_pending_processing}', pending_view_jefe_id='{current_pending_view_jefe_id}'")

            entradas = get_entradas_for_user(
                self.user_object,
                search_term=search_term,
                search_field=search_field,
                area_filter=area_filter,
                category_filter=category_filter,
                date_from_str=date_from_str,
                date_to_str=date_to_str,
                pending_processing=current_pending_processing,
                pending_view_for_jefe_id=current_pending_view_jefe_id
            )
            logger.debug(f"Entradas recibidas: {len(entradas)}")
            
            # Limpiar tabla existente
            for item in self.entradas_tree.get_children():
                self.entradas_tree.delete(item)
            
            # Insertar nuevos datos
            for i, entrada in enumerate(entradas):
                logger.debug(f"Entrada {i}: ID={entrada.id}, Num={entrada.numero_entrada}, Asunto={entrada.asunto}, Tramitado={entrada.tramitado}, Urgente={entrada.urgente}")
                
                tags = ()
                if entrada.urgente:
                    tags += ('urgent_row',)

                # Determine username for "Tramitado Por"
                tramitado_por_username = "N/A"
                if hasattr(entrada, 'Tramitado_por_username') and entrada.Tramitado_por_username:
                    tramitado_por_username = entrada.Tramitado_por_username
                elif entrada.tramitado_por: # Fallback to the object if Tramitado_por_username is not directly available
                    tramitado_por_username = entrada.tramitado_por.username if entrada.tramitado_por.username else "Error"


                self.entradas_tree.insert("", "end", values=(
                    entrada.id,
                    entrada.numero_entrada,
                    entrada.fecha.strftime("%d/%m/%Y") if entrada.fecha else "",
                    entrada.asunto,
                    entrada.canal_entrada.nombre if entrada.canal_entrada else "",
                    "✅" if entrada.urgente else "❌",
                    "✅" if entrada.confidencial else "❌",
                    "✅" if entrada.tramitado else "❌",
                    entrada.observaciones if entrada.observaciones else "",
                    tramitado_por_username if entrada.tramitado else "" # Show username only if tramitado
                ), tags=tags)
                
        except Exception as e:
            import traceback
            traceback.print_exc()
            messagebox.showerror("Error", f"No se pudieron cargar las entradas: {str(e)}")

# Example Usage (for testing this module standalone - limited functionality)
if __name__ == '__main__':
    # This standalone test requires a dummy User object and config data.
    # It won't have the full application context (login, config loading from files).
    
    # Create a dummy models.py if not found (very basic for this test)
    try:
        from . import models
    except ImportError:
        class DummyRole:
            def __init__(self, nombre_role):
                self.nombre_role = nombre_role
        class DummyUsuario:
            def __init__(self, username, roles_list):
                self.username = username
                self.roles = [DummyRole(r) for r in roles_list]
        
        # Replace models.Usuario with DummyUsuario for this test
        models = type('models', (object,), {
            'Usuario': DummyUsuario, 
            'Role': DummyRole, 
            'Categoria': type('Categoria', (object,), {'nombre': 'Test Cat'})
        })


    print("Running main_app_ui.py standalone test...")
    
    class MockDBManager:
        def get_negociados(self, include_jefes=True):
            print(f"MockDB: get_negociados(include_jefes={include_jefes})")
            roles = [models.Role(nombre_role=f"Mock Negociado {i}") for i in range(1,3)]
            roles.append(models.Role(nombre_role="Jefe Area 1", is_jefe=True))
            return roles

        def get_categorias(self):
            print("MockDB: get_categorias called")
            return [models.Categoria(nombre=f"Mock Categoria {i}") for i in range(1,3)]
        
        def get_entradas_for_user(self, user, search_term=None, search_field=None, area_filter=None, 
                                  category_filter=None, date_from_str=None, date_to_str=None,
                                  pending_processing=None, pending_view_for_jefe_id=None): # Added new params
            print(f"MockDB: get_entradas_for_user called with filters: term='{search_term}', field='{search_field}', "
                  f"area='{area_filter}', cat='{category_filter}', from='{date_from_str}', to='{date_to_str}', "
                  f"pending_processing='{pending_processing}', pending_view_jefe_id='{pending_view_for_jefe_id}'")
            
            class DummyEntrada: # Simplified for test
                def __init__(self, id, asunto, tramitado=False, urgente=False):
                    self.id = id; self.numero_entrada = f"N{id:03}"; import datetime; self.fecha = datetime.date.today()
                    self.asunto = asunto; self.canal_entrada = type('Canal', (object,), {'nombre': "TestCanal"})()
                    self.urgente = urgente; self.confidencial = False; self.tramitado = tramitado
                    self.observaciones = "Obs"; self.tramitado_por = None; self.Tramitado_por_username = None
            
            if pending_processing: return [DummyEntrada(10, "Asunto Pendiente Tramitación", tramitado=False, urgente=True)]
            if pending_view_for_jefe_id: return [DummyEntrada(20, f"Asunto Pendiente Ver por Jefe {pending_view_for_jefe_id}", urgente=True)]
            return [DummyEntrada(1, "Asunto Normal 1"), DummyEntrada(2, "Asunto Normal 2", tramitado=True)]

    original_db_manager = db_manager_module 
    db_manager_module = MockDBManager()

    test_root = tk.Tk()
    
    # Test users
    user_registro = models.Usuario(username="registro_user", roles_list=[], permiso=True, id=1) # Registro
    user_jefe = models.Usuario(username="jefe_user", roles_list=[models.Role(nombre_role="Jefe Contabilidad", is_jefe=True)], permiso=False, id=2) # Jefe
    user_normal = models.Usuario(username="normal_user", roles_list=[models.Role(nombre_role="Empleado")], permiso=False, id=3) # Normal
    
    # Choose user to test with:
    current_test_user = user_jefe 
    
    dummy_config = {"BASE_DIR": "/test/base/dir", "LOCAL_DIR": "/test/local/dir"}
    app = MainApplicationWindow(test_root, current_test_user, dummy_config)
    
    test_root.mainloop()
    db_manager_module = original_db_manager 
    print("Standalone test finished.")


# --- Class Methods for UI Helpers ---
def _clear_placeholder(self, entry_widget, placeholder_text):
    if self.get() == placeholder_text: # Changed to self.get() assuming entry_widget is self
        self.delete(0, tk.END)
        self.config(foreground='black')

def _restore_placeholder(self, entry_widget, placeholder_text): # entry_widget seems to be self here too
    if not self.get():
        self.insert(0, placeholder_text)
        self.config(foreground='grey')

MainApplicationWindow._clear_placeholder = _clear_placeholder
MainApplicationWindow._restore_placeholder = _restore_placeholder


def _populate_area_filter_combobox(self):
    try:
        # Assuming get_negociados returns list of Role objects with 'nombre_role'
        negociados = db_manager_module.get_negociados(include_jefes=True) 
        area_names = ["Todos"] + [n.nombre_role for n in negociados if hasattr(n, 'nombre_role')]
        self.area_filter_combobox['values'] = area_names
        if area_names: self.area_filter_combobox.current(0)
    except Exception as e:
        print(f"Error populating area filter: {e}")
        self.area_filter_combobox['values'] = ["Todos"]
        self.area_filter_combobox.current(0)

def _populate_category_filter_combobox(self):
    try:
        # Assuming get_categorias returns list of Categoria objects with 'nombre'
        categorias = db_manager_module.get_categorias()
        cat_names = ["Todas"] + [c.nombre for c in categorias if hasattr(c, 'nombre')]
        self.category_filter_combobox['values'] = cat_names
        if cat_names: self.category_filter_combobox.current(0)
    except Exception as e:
        print(f"Error populating category filter: {e}")
        self.category_filter_combobox['values'] = ["Todas"]
        self.category_filter_combobox.current(0)

# New methods for special filter button actions
def _show_pending_processing(self):
    self.filter_pending_processing = True
    self.filter_pending_view_for_jefe_id = None # Clear other special filter
    self._load_entradas_data()

def _show_pending_view(self):
    self.filter_pending_view_for_jefe_id = self.user_object.id
    self.filter_pending_processing = False # Clear other special filter
    self._load_entradas_data()

def _filter_and_load_entradas_data(self):
    """Resets special filters and then loads data with general filters."""
    self.filter_pending_processing = False
    self.filter_pending_view_for_jefe_id = None
    self._load_entradas_data()
    
def _update_special_filter_button_states(self):
    is_jefe = any(hasattr(role, 'is_jefe') and role.is_jefe for role in self.user_object.roles) if self.user_object.roles else False
    is_registro = hasattr(self.user_object, 'permiso') and self.user_object.permiso
    
    if is_jefe or is_registro:
        self.btn_pending_processing.config(state="normal")
    else:
        self.btn_pending_processing.config(state="disabled")

    if is_jefe:
        self.btn_pending_view.config(state="normal")
    else:
        self.btn_pending_view.config(state="disabled")

MainApplicationWindow._populate_area_filter_combobox = _populate_area_filter_combobox
MainApplicationWindow._populate_category_filter_combobox = _populate_category_filter_combobox
MainApplicationWindow._show_pending_processing = _show_pending_processing
MainApplicationWindow._show_pending_view = _show_pending_view
MainApplicationWindow._filter_and_load_entradas_data = _filter_and_load_entradas_data
MainApplicationWindow._update_special_filter_button_states = _update_special_filter_button_states

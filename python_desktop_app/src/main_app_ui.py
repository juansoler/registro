import tkinter as tk
from tkinter import ttk, messagebox
from . import models # Assuming models.py is in the same directory (src)

class MainApplicationWindow:
    def __init__(self, root_tk, user_object: models.Usuario, config_data: dict):
        self.root = root_tk # This will be the main Tk() instance from main.py
        self.user_object = user_object
        self.config_data = config_data

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
        if self.user_object and hasattr(self.user_object, 'roles'):
            for role in self.user_object.roles:
                if role.nombre_role.lower() == "admin": # Example: check for 'admin' role
                    menubar.entryconfig("Admin", state="normal")
                    break
                # More complex logic can be added here, e.g., checking role.is_jefe
                # or specific permissions if those were part of the Role model.
                # For instance, if a role named 'SuperAdmin' enables it:
                # if role.nombre_role == "SuperAdmin":
                # menubar.entryconfig("Admin", state="normal")
                # break


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

        # Tabla de Entradas
        entradas_frame = ttk.Labelframe(main_frame, text="Listado de Entradas", padding="10")
        entradas_frame.pack(pady=10, expand=True, fill=tk.BOTH)

        # Crear Treeview para mostrar las entradas
        columns = ("id", "numero", "fecha", "asunto", "canal", "urgente", "confidencial")
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

        # Ajustar anchos de columnas
        self.entradas_tree.column("id", width=50, anchor="center")
        self.entradas_tree.column("numero", width=100, anchor="center")
        self.entradas_tree.column("fecha", width=100, anchor="center")
        self.entradas_tree.column("asunto", width=200)
        self.entradas_tree.column("canal", width=100, anchor="center")
        self.entradas_tree.column("urgente", width=70, anchor="center")
        self.entradas_tree.column("confidencial", width=70, anchor="center")

        # Scrollbar vertical
        scrollbar = ttk.Scrollbar(entradas_frame, orient="vertical", command=self.entradas_tree.yview)
        self.entradas_tree.configure(yscrollcommand=scrollbar.set)
        scrollbar.pack(side="right", fill="y")

        self.entradas_tree.pack(expand=True, fill=tk.BOTH)

        # Cargar datos de entradas
        self._load_entradas_data()

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
            
            from .database_manager import get_entradas_for_user
            logger.debug(f"User object roles: {[r.nombre_role for r in self.user_object.roles] if self.user_object and hasattr(self.user_object, 'roles') else 'No roles'}")
            
            entradas = get_entradas_for_user(self.user_object)
            logger.debug(f"Entradas recibidas: {len(entradas)}")
            
            # Limpiar tabla existente
            for item in self.entradas_tree.get_children():
                self.entradas_tree.delete(item)
            
            # Insertar nuevos datos
            for i, entrada in enumerate(entradas):
                logger.debug(f"Entrada {i}: ID={entrada.id}, Num={entrada.numero_entrada}, Asunto={entrada.asunto}")
                self.entradas_tree.insert("", "end", values=(
                    entrada.id,
                    entrada.numero_entrada,
                    entrada.fecha.strftime("%d/%m/%Y") if entrada.fecha else "",
                    entrada.asunto,
                    entrada.canal_entrada.nombre if entrada.canal_entrada else "",
                    "Sí" if entrada.urgente else "No",
                    "Sí" if entrada.confidencial else "No"
                ))
                
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
        models = type('models', (object,), {'Usuario': DummyUsuario})


    print("Running main_app_ui.py standalone test...")
    
    # Create a root Tkinter window for the test
    test_root = tk.Tk()
    # test_root.withdraw() # MainApplicationWindow will deiconify it.

    # Dummy user object
    dummy_user = models.Usuario(username="testuser", roles_list=["Operator", "Viewer"])
    # dummy_user_admin = models.Usuario(username="adminuser", roles_list=["admin", "Operator"])


    # Dummy config data
    dummy_config = {"BASE_DIR": "/test/base/dir", "LOCAL_DIR": "/test/local/dir"}

    # Create and run the main application window
    app = MainApplicationWindow(test_root, dummy_user, dummy_config)
    # To test admin menu enabling:
    # app_admin = MainApplicationWindow(test_root, dummy_user_admin, dummy_config)
    
    test_root.mainloop()
    print("Standalone test finished.")



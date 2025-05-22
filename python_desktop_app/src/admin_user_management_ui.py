import tkinter as tk
from tkinter import ttk, messagebox

# Attempt to import database_manager and models
try:
    from . import database_manager
    from . import models
    _database_manager_available = True
except ImportError:
    print("Warning: database_manager or models not found for AdminUserManagementWindow. Using mock data.")
    _database_manager_available = False
    # Dummy classes for standalone testing
    class DummyUser:
        def __init__(self, id, username, roles_data=None): # roles_data is list of dicts
            self.id = id
            self.username = username
            self.roles = []
            if roles_data:
                for r_data in roles_data:
                    # Simplified role for this UI mock - real Role model is more complex
                    self.roles.append(type('DummyRole', (object,), r_data)()) 
    
    class DummyRole: # Simpler version for this UI's immediate needs if models.py isn't loaded
        def __init__(self, nombre_role="Test Role", permiso="Lectura", isJefe=False):
            self.nombre_role = nombre_role
            self.permiso = permiso # Assuming 'permiso' attribute exists
            self.isJefe = isJefe


    models = type('models_dummy', (object,), {'Usuario': DummyUser, 'Role': DummyRole})


class AdminUserManagementWindow(tk.Toplevel):
    def __init__(self, parent, current_user_object: models.Usuario):
        super().__init__(parent)
        self.parent = parent
        self.current_user_object = current_user_object # Admin user

        self.title("Gestión de Usuarios")
        self.geometry("800x500")
        self.minsize(600, 400)

        self._create_widgets()
        self._load_users()

        self.protocol("WM_DELETE_WINDOW", self._close_window)
        self.grab_set()

    def _create_widgets(self):
        main_frame = ttk.Frame(self, padding="10")
        main_frame.pack(fill=tk.BOTH, expand=True)

        # User List Treeview
        list_frame = ttk.Frame(main_frame)
        list_frame.pack(fill=tk.BOTH, expand=True, pady=(0, 10))

        cols = ("id", "username", "roles", "permisos")
        col_texts = {"id": "ID", "username": "Username", "roles": "Roles", "permisos": "Permisos"}
        col_widths = {"id": 50, "username": 150, "roles": 300, "permisos": 150}

        self.user_tree = ttk.Treeview(list_frame, columns=cols, show="headings", selectmode="browse")
        for col_key in cols:
            self.user_tree.heading(col_key, text=col_texts.get(col_key, col_key.title()))
            self.user_tree.column(col_key, width=col_widths.get(col_key, 100), stretch=tk.YES)
        
        scrollbar = ttk.Scrollbar(list_frame, orient=tk.VERTICAL, command=self.user_tree.yview)
        self.user_tree.configure(yscrollcommand=scrollbar.set)
        self.user_tree.pack(side=tk.LEFT, fill=tk.BOTH, expand=True)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

        # Action Buttons Frame
        button_frame = ttk.Frame(main_frame)
        button_frame.pack(fill=tk.X, pady=(5,0))

        action_buttons = [
            ("Añadir Usuario...", self._add_user),
            ("Resetear Contraseña", self._reset_password),
            ("Eliminar Usuario", self._delete_user),
            ("Cambiar Permisos", self._change_permissions),
            ("Refrescar Lista", self._refresh_list),
            ("Cerrar", self._close_window)
        ]

        for i, (text, command) in enumerate(action_buttons):
            btn = ttk.Button(button_frame, text=text, command=command)
            btn.pack(side=tk.LEFT, padx=5, pady=5)
            if text == "Cerrar": # Push Cerrar to the right
                 btn.pack(side=tk.RIGHT, padx=5, pady=5)
            elif text == "Refrescar Lista":
                 btn.pack(side=tk.RIGHT, padx=(20,5), pady=5)


    def _load_users(self):
        # Clear existing items
        for item in self.user_tree.get_children():
            self.user_tree.delete(item)

        users_data = []
        if _database_manager_available:
            try:
                # This function will be fully implemented in the next subtask for database_manager.py
                # For now, it might return dummy data or a simplified list.
                users_data = database_manager.get_all_users_with_roles() 
            except AttributeError: # If function doesn't exist yet
                 print("Warning: database_manager.get_all_users_with_roles not yet implemented. Using mock data.")
                 users_data = self._get_mock_users_data()
            except Exception as e:
                messagebox.showerror("Error de Carga", f"No se pudieron cargar los usuarios: {e}", parent=self)
                print(f"Error loading users: {e}")
                users_data = self._get_mock_users_data() # Fallback to mock
        else: # DB manager not available at all
            users_data = self._get_mock_users_data()
            
        for user_obj in users_data:
            user_id = user_obj.id
            username = user_obj.username
            
            # Process roles and permissions
            roles_str_list = []
            permisos_list = [] # Assuming a user might have roles with different permissions
                               # For simplicity, we'll just show the first encountered or a summary.
            
            if hasattr(user_obj, 'roles') and user_obj.roles:
                for role in user_obj.roles:
                    roles_str_list.append(getattr(role, 'nombre_role', 'N/A'))
                    # Assuming 'permiso' is an attribute on the role object from usuario_role join
                    permiso_val = getattr(role, 'permiso', None) 
                    if permiso_val is not None: # 0 for Read, 1 for Read/Write (example)
                        permisos_list.append("Lectura/Escritura" if permiso_val == 1 else "Lectura")
                    else:
                        permisos_list.append("No Definido") # Default if no 'permiso' attribute
            
            roles_display = ", ".join(roles_str_list) if roles_str_list else "Sin Roles"
            # For display, show unique permissions or the most common/highest one.
            # Here, just showing the first one found or a summary if multiple.
            permisos_display = list(set(permisos_list))[0] if permisos_list else "N/A" 
            if len(set(permisos_list)) > 1:
                permisos_display = "Mixto" # Or more detailed summary

            self.user_tree.insert("", tk.END, iid=str(user_id), values=(user_id, username, roles_display, permisos_display))

    def _get_mock_users_data(self):
        # Returns list of DummyUser or models.Usuario with dummy data
        print("Using MOCK user data for Admin UI.")
        return [
            models.Usuario(id=1, username="admin", roles_data=[{'nombre_role': 'Administrador', 'permiso': 1}]),
            models.Usuario(id=2, username="jefe_ventas", roles_data=[{'nombre_role': 'Jefe Ventas', 'permiso': 1, 'isJefe':True}]),
            models.Usuario(id=3, username="operador_stock", roles_data=[{'nombre_role': 'Operador Stock', 'permiso': 0}]),
            models.Usuario(id=4, username="usuario_lectura", roles_data=[{'nombre_role': 'Consultas', 'permiso': 0}])
        ]

    def _get_selected_user_info(self):
        selected_items = self.user_tree.selection()
        if not selected_items:
            messagebox.showwarning("Nada Seleccionado", "Por favor, seleccione un usuario de la lista.", parent=self)
            return None, None
        
        user_id_str = selected_items[0] # This is the iid, which we set to user_id
        user_values = self.user_tree.item(user_id_str, "values")
        username = user_values[1] # Username is at index 1
        return int(user_id_str), username


    def _add_user(self):
        messagebox.showinfo("Info", "Funcionalidad Añadir Usuario no implementada aún.", parent=self)
        # Future: Open a new dialog to add user details and roles.

    def _reset_password(self):
        user_id, username = self._get_selected_user_info()
        if not username: return

        if messagebox.askyesno("Confirmar Reset", f"¿Está seguro de que desea resetear la contraseña para el usuario '{username}'?", parent=self):
            if _database_manager_available:
                try:
                    # This function will be implemented in database_manager.py
                    # success = database_manager.reset_password(username) 
                    # if success:
                    #    messagebox.showinfo("Éxito", f"Contraseña reseteada para '{username}'. El usuario deberá cambiarla al próximo login.", parent=self)
                    # else:
                    #    messagebox.showerror("Error", f"No se pudo resetear la contraseña para '{username}'.", parent=self)
                    messagebox.showinfo("Simulación", f"Simulando reseteo de contraseña para '{username}'. (DB func no llamada)", parent=self) # Placeholder
                except Exception as e:
                    messagebox.showerror("Error", f"Error al resetear contraseña: {e}", parent=self)
            else:
                messagebox.showinfo("Simulación", f"Simulando reseteo de contraseña para '{username}'. (DB no disponible)", parent=self)
            self._load_users() # Refresh list

    def _delete_user(self):
        user_id, username = self._get_selected_user_info()
        if not username: return

        if username.lower() == "admin":
            messagebox.showerror("Error", "No se puede eliminar al usuario 'admin'.", parent=self)
            return

        if messagebox.askyesno("Confirmar Eliminación", f"¿Está seguro de que desea eliminar al usuario '{username}'? Esta acción no se puede deshacer.", parent=self):
            if _database_manager_available:
                try:
                    # success = database_manager.delete_user(username) # Or by ID
                    # if success:
                    #    messagebox.showinfo("Éxito", f"Usuario '{username}' eliminado.", parent=self)
                    # else:
                    #    messagebox.showerror("Error", f"No se pudo eliminar al usuario '{username}'.", parent=self)
                    messagebox.showinfo("Simulación", f"Simulando eliminación de '{username}'. (DB func no llamada)", parent=self) # Placeholder
                except Exception as e:
                    messagebox.showerror("Error", f"Error al eliminar usuario: {e}", parent=self)
            else:
                messagebox.showinfo("Simulación", f"Simulando eliminación de '{username}'. (DB no disponible)", parent=self)
            self._load_users()

    def _change_permissions(self):
        user_id, username = self._get_selected_user_info()
        if not username: return
        
        # Placeholder for permission changing logic
        # This would typically involve fetching current permissions, then opening a dialog
        # to select new roles/permissions, then calling a DB update function.
        messagebox.showinfo("Info", f"Funcionalidad 'Cambiar Permisos' para '{username}' no implementada completamente.", parent=self)
        # Example of what it might do:
        # current_perm = self.user_tree.item(str(user_id), "values")[3] # Get current displayed permission
        # new_perm_action = "Conceder Escritura" if "Lectura" == current_perm else "Revocar Escritura"
        # if messagebox.askyesno("Cambiar Permiso", f"Usuario: {username}\nPermiso actual: {current_perm}\n\n¿Desea {new_perm_action}?", parent=self):
        #     # new_perm_state = 1 if "Conceder" in new_perm_action else 0
        #     # database_manager.cambiar_permisos_usuario(user_id, new_perm_state)
        #     self._load_users()


    def _refresh_list(self):
        self._load_users()

    def _close_window(self):
        self.destroy()

# --- Standalone Test ---
if __name__ == '__main__':
    root = tk.Tk()
    root.title("Main Test Window (AdminUserManagement)")
    root.withdraw() 

    # Dummy admin user object for testing the window context
    admin_user_for_test = None
    if _database_manager_available: # Try to use real models if db manager is found for this dummy
        admin_user_for_test = models.Usuario(id=0, username="test_admin_runner", roles_data=[{'nombre_role': 'admin', 'permiso': 1}])
    else: # Fallback to pure dummy models if models module itself was not loaded
        admin_user_for_test = DummyUser(id=0, username="test_admin_runner_dummy", roles_data=[{'nombre_role': 'admin', 'permiso': 1}])


    # Mock database_manager for standalone UI testing
    if not _database_manager_available:
        class MockDBManager:
            def get_all_users_with_roles(self):
                print("MOCK DB: get_all_users_with_roles called")
                # Using the same mock data function as the UI's internal fallback
                return AdminUserManagementWindow(None, None)._get_mock_users_data() # Pass None as parent/user not needed for this call

            def reset_password(self, username):
                print(f"MOCK DB: reset_password called for {username}")
                return True # Simulate success

            def delete_user(self, username):
                print(f"MOCK DB: delete_user called for {username}")
                if username.lower() == "admin": return False # Simulate admin deletion prevention
                return True # Simulate success
            
            def cambiar_permisos_usuario(self, user_id, new_perm_state):
                print(f"MOCK DB: cambiar_permisos_usuario for {user_id} to {new_perm_state}")
                return True
        
        database_manager = MockDBManager() # Replace module with mock instance
        print("Using MOCK database_manager for AdminUserManagementWindow test.")


    def open_admin_user_dialog():
        dialog = AdminUserManagementWindow(root, admin_user_for_test)
        # root.wait_window(dialog) # Makes test hang if dialog closed by button

    ttk.Button(root, text="Abrir Gestión de Usuarios", command=open_admin_user_dialog).pack(padx=20, pady=20)
    root.deiconify()
    root.mainloop()

```

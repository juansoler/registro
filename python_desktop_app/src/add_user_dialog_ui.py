import tkinter as tk
from tkinter import ttk, messagebox

# Attempt to import database_manager and models for role list
try:
    from . import database_manager
    from . import models # For models.Role type hint if needed
    _database_manager_available = True
except ImportError:
    print("Warning: AddUserDialog cannot import database_manager. Role list will be mocked.")
    _database_manager_available = False
    class DummyRole: # Minimal for UI if models not loaded
        def __init__(self, id, nombre_role, **kwargs): self.id = id; self.nombre_role = nombre_role
    models = type('models_dummy', (object,), {'Role': DummyRole})


class AddUserDialog(tk.Toplevel):
    def __init__(self, parent, existing_usernames: list, all_roles: list):
        super().__init__(parent)
        self.parent = parent
        self.existing_usernames = [u.lower() for u in existing_usernames]
        self.all_roles = all_roles # List of models.Role objects

        self.title("Añadir Nuevo Usuario")
        self.geometry("450x350") # Adjusted size
        self.resizable(False, False)

        self.new_user_data = None # To store the result

        self._create_widgets()
        self._populate_roles_combobox()

        self.protocol("WM_DELETE_WINDOW", self._on_cancel)
        self.grab_set()
        self.wait_window() # Make it behave like a modal dialog call

    def _create_widgets(self):
        main_frame = ttk.Frame(self, padding="10")
        main_frame.pack(fill=tk.BOTH, expand=True)
        main_frame.columnconfigure(1, weight=1)

        # Username
        ttk.Label(main_frame, text="Username:").grid(row=0, column=0, padx=5, pady=5, sticky="w")
        self.username_entry = ttk.Entry(main_frame, width=40)
        self.username_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")

        # Password
        ttk.Label(main_frame, text="Contraseña:").grid(row=1, column=0, padx=5, pady=5, sticky="w")
        self.password_entry = ttk.Entry(main_frame, width=40, show="*")
        self.password_entry.grid(row=1, column=1, padx=5, pady=5, sticky="ew")
        ttk.Label(main_frame, text="(Dejar en blanco para 'reset')").grid(row=2, column=1, padx=5, pady=2, sticky="w")


        # Roles Management
        roles_frame = ttk.LabelFrame(main_frame, text="Asignar Roles", padding="5")
        roles_frame.grid(row=3, column=0, columnspan=2, padx=5, pady=10, sticky="ew")
        roles_frame.columnconfigure(0, weight=1) # Make combobox expand

        self.role_combobox = ttk.Combobox(roles_frame, state="readonly", width=30)
        self.role_combobox.grid(row=0, column=0, padx=5, pady=5, sticky="ew")
        
        self.permiso_escritura_var = tk.BooleanVar(value=False) # Default to Read-only
        self.permiso_checkbutton = ttk.Checkbutton(roles_frame, text="Permiso Escritura", variable=self.permiso_escritura_var)
        self.permiso_checkbutton.grid(row=0, column=1, padx=5, pady=5)

        ttk.Button(roles_frame, text="Añadir Rol", command=self._add_role_to_selection).grid(row=0, column=2, padx=5, pady=5)

        self.selected_roles_listbox = tk.Listbox(roles_frame, height=4, width=50)
        self.selected_roles_listbox.grid(row=1, column=0, columnspan=2, padx=5, pady=5, sticky="ew")
        ttk.Button(roles_frame, text="Quitar Rol Seleccionado", command=self._remove_role_from_selection).grid(row=1, column=2, padx=5, pady=5, sticky="n")
        
        self.assigned_roles_data = [] # List of {'role_id': id, 'role_name': name, 'permiso': bool}


        # Action Buttons
        button_frame = ttk.Frame(main_frame)
        button_frame.grid(row=4, column=0, columnspan=2, pady=10, sticky="e")

        ttk.Button(button_frame, text="Guardar", command=self._on_save).pack(side=tk.LEFT, padx=5)
        ttk.Button(button_frame, text="Cancelar", command=self._on_cancel).pack(side=tk.LEFT, padx=5)

    def _populate_roles_combobox(self):
        if not self.all_roles:
            self.role_combobox['values'] = ["No hay roles disponibles"]
        else:
            self.role_combobox['values'] = [role.nombre_role for role in self.all_roles]
        
        if self.role_combobox['values']:
            self.role_combobox.current(0)

    def _add_role_to_selection(self):
        selected_role_name = self.role_combobox.get()
        if not selected_role_name or selected_role_name == "No hay roles disponibles":
            return

        # Find the role_id from self.all_roles
        selected_role_obj = next((r for r in self.all_roles if r.nombre_role == selected_role_name), None)
        if not selected_role_obj:
            messagebox.showerror("Error", "Rol seleccionado no válido.", parent=self)
            return
        
        role_id = selected_role_obj.id
        has_write_permission = self.permiso_escritura_var.get()

        # Check if role already added
        for r_data in self.assigned_roles_data:
            if r_data['role_id'] == role_id:
                messagebox.showinfo("Info", f"El rol '{selected_role_name}' ya ha sido asignado.", parent=self)
                return
        
        display_text = f"{selected_role_name} (Permiso: {'Escritura' if has_write_permission else 'Lectura'})"
        self.selected_roles_listbox.insert(tk.END, display_text)
        self.assigned_roles_data.append({'role_id': role_id, 'role_name': selected_role_name, 'permiso': has_write_permission})

    def _remove_role_from_selection(self):
        selected_indices = self.selected_roles_listbox.curselection()
        if not selected_indices:
            messagebox.showwarning("Nada Seleccionado", "Seleccione un rol de la lista para quitar.", parent=self)
            return
        
        # Remove from bottom up
        for index in sorted(selected_indices, reverse=True):
            self.selected_roles_listbox.delete(index)
            del self.assigned_roles_data[index] # Assumes listbox and list are in sync

    def _on_save(self):
        username = self.username_entry.get().strip()
        password = self.password_entry.get() # No strip on password

        if not username:
            messagebox.showerror("Error de Validación", "El nombre de usuario no puede estar vacío.", parent=self)
            return
        
        if username.lower() in self.existing_usernames:
            messagebox.showerror("Error de Validación", f"El nombre de usuario '{username}' ya existe.", parent=self)
            return
            
        if not self.assigned_roles_data:
            messagebox.showwarning("Sin Roles", "El usuario no tendrá roles asignados. ¿Continuar?", parent=self)
            # If user clicks No, then return. For now, let's assume they can create user with no roles.

        if not password: # If password left blank, treat as "reset"
            password = "reset"
            if not messagebox.askyesno("Confirmar Contraseña", 
                                       "La contraseña está vacía. Se establecerá para que el usuario la cambie en el primer inicio de sesión ('reset').\n¿Continuar?", 
                                       parent=self):
                return


        self.new_user_data = {
            "username": username,
            "password_plaintext": password, # Will be hashed by caller or DB layer
            "roles_data": self.assigned_roles_data # List of {'role_id': id, 'permiso': bool}
        }
        self.destroy()

    def _on_cancel(self):
        self.new_user_data = None
        self.destroy()

    def show(self): # Not strictly needed if using wait_window() after creation
        self.deiconify()
        self.wait_window()
        return self.new_user_data


# --- Standalone Test ---
if __name__ == '__main__':
    root = tk.Tk()
    root.title("Main Test Window (AddUserDialog)")
    # root.withdraw() # Hide if only testing dialog, show if button is used

    mock_existing_users = ["admin", "testuser"]
    mock_all_roles = []
    if _database_manager_available: # Should be True if run after DB manager is complete
        try:
            # These calls would ideally be to a more lightweight get_roles function if available
            negociados = database_manager.get_negociados()
            cargos = database_manager.get_cargos()
            mock_all_roles.extend(negociados)
            mock_all_roles.extend(cargos)
            if not mock_all_roles: # Fallback if DB is empty
                 mock_all_roles = [models.Role(id=1, nombre_role="Mock Role A"), models.Role(id=2, nombre_role="Mock Role B")]
        except Exception as e:
            print(f"Error getting roles from DB for mock: {e}")
            mock_all_roles = [models.Role(id=1, nombre_role="Mock Role A"), models.Role(id=2, nombre_role="Mock Role B")]
    else: # DB manager not available
        mock_all_roles = [DummyRole(id=1, nombre_role="Mock Role A (No DB)"), DummyRole(id=2, nombre_role="Mock Role B (No DB)")]


    def open_add_user_dialog():
        print("Opening Add User Dialog...")
        dialog = AddUserDialog(root, mock_existing_users, mock_all_roles)
        # new_data = dialog.show() # If using show() method
        # wait_window() is called in __init__, so result is implicitly via instance attribute
        new_data = dialog.new_user_data
        
        if new_data:
            print("Dialog Guardado. Datos recibidos:")
            print(f"  Username: {new_data['username']}")
            print(f"  Password: {new_data['password_plaintext']}")
            print(f"  Roles Data: {new_data['roles_data']}")
        else:
            print("Dialog Cancelado o cerrado.")

    ttk.Button(root, text="Abrir Add User Dialog", command=open_add_user_dialog).pack(padx=20, pady=20)
    root.mainloop()
```

import tkinter as tk
from tkinter import ttk, messagebox, simpledialog
# Assuming models.py and database_manager.py are in the same directory or accessible via src path
from . import models 
# We will pass the db_manager_module as a reference, so direct import here might not be needed
# unless for type hinting, which can be done carefully.

class UserManagementWindow(tk.Toplevel):
    def __init__(self, parent_window, current_user: models.Usuario, db_manager_module_ref):
        super().__init__(parent_window)
        self.parent_window = parent_window
        self.current_user = current_user
        self.db_manager = db_manager_module_ref # Store the reference

        self.title("Gestión de Usuarios")
        self.geometry("800x500")
        self.transient(parent_window)
        self.grab_set()

        self._setup_ui()
        self._load_and_display_users() # Initial load

    def _setup_ui(self):
        main_frame = ttk.Frame(self, padding="10")
        main_frame.pack(expand=True, fill=tk.BOTH)

        # --- User List Treeview ---
        tree_frame = ttk.Frame(main_frame)
        tree_frame.pack(expand=True, fill=tk.BOTH, pady=(0, 10))

        columns = ("id", "username", "roles", "permiso")
        self.users_tree = ttk.Treeview(
            tree_frame,
            columns=columns,
            show="headings",
            selectmode="browse"
        )
        self.users_tree.heading("id", text="ID")
        self.users_tree.heading("username", text="Username")
        self.users_tree.heading("roles", text="Roles")
        self.users_tree.heading("permiso", text="Permiso")

        self.users_tree.column("id", width=50, anchor="center")
        self.users_tree.column("username", width=150)
        self.users_tree.column("roles", width=200)
        self.users_tree.column("permiso", width=150, anchor="center")

        scrollbar = ttk.Scrollbar(tree_frame, orient="vertical", command=self.users_tree.yview)
        self.users_tree.configure(yscrollcommand=scrollbar.set)
        
        self.users_tree.pack(side=tk.LEFT, expand=True, fill=tk.BOTH)
        scrollbar.pack(side=tk.RIGHT, fill=tk.Y)

        # --- Action Buttons ---
        button_frame = ttk.Frame(main_frame)
        button_frame.pack(fill=tk.X, pady=5)

        self.btn_add_user = ttk.Button(button_frame, text="Añadir Usuario", command=self._add_user_dialog_stub) # Stub for now
        self.btn_add_user.pack(side=tk.LEFT, padx=5)

        self.btn_edit_user = ttk.Button(button_frame, text="Editar Usuario", command=self._edit_user_dialog_stub, state="disabled") # Stub
        self.btn_edit_user.pack(side=tk.LEFT, padx=5)

        self.btn_delete_user = ttk.Button(button_frame, text="Eliminar Usuario", command=self._delete_user_stub, state="disabled") # Stub
        self.btn_delete_user.pack(side=tk.LEFT, padx=5)
        
        self.btn_refresh_list = ttk.Button(button_frame, text="Actualizar Lista", command=self._load_and_display_users)
        self.btn_refresh_list.pack(side=tk.RIGHT, padx=5)

        # Enable/disable edit/delete buttons based on selection
        self.users_tree.bind("<<TreeviewSelect>>", self._on_user_select)

    def _on_user_select(self, event=None):
        if self.users_tree.selection():
            self.btn_edit_user.config(state="normal")
            self.btn_delete_user.config(state="normal")
        else:
            self.btn_edit_user.config(state="disabled")
            self.btn_delete_user.config(state="disabled")

    def _load_and_display_users(self):
        # Clear existing items
        for item in self.users_tree.get_children():
            self.users_tree.delete(item)
        
        self._on_user_select() # Update button states after clearing

        try:
            # This function will be implemented in database_manager.py
            users_data = self.db_manager.get_all_users_with_details() 
            
            for user_obj in users_data:
                roles_str = ", ".join([role.nombre_role for role in user_obj.roles]) if user_obj.roles else "N/A"
                permiso_str = "Lectura/Escritura" if user_obj.permiso else "Solo Lectura" # Example mapping
                
                self.users_tree.insert("", "end", values=(
                    user_obj.id,
                    user_obj.username,
                    roles_str,
                    permiso_str
                ))
        except AttributeError as ae: # If db_manager doesn't have the function yet
             if 'get_all_users_with_details' in str(ae):
                messagebox.showwarning("Función no implementada", 
                                       "La función 'get_all_users_with_details' aún no está disponible en el gestor de base de datos.", 
                                       parent=self.window)
                # Insert dummy data for UI testing
                self.users_tree.insert("", "end", values=(1, "admin_test", "Admin, Jefe", "Lectura/Escritura"))
                self.users_tree.insert("", "end", values=(2, "user_test", "Empleado", "Solo Lectura"))
             else:
                raise # Re-raise other AttributeErrors
        except Exception as e:
            messagebox.showerror("Error de Carga", f"No se pudieron cargar los usuarios: {e}", parent=self.window)

    # --- Stubs for button actions ---
    def _add_user_dialog_stub(self):
        messagebox.showinfo("WIP", "Funcionalidad 'Añadir Usuario' no implementada aún.", parent=self.window)
        # Later: Open AddUserDialog
        dialog = AddUserDialog(self.window, self.db_manager)
        self.window.wait_window(dialog.window)
        if hasattr(dialog, 'user_saved') and dialog.user_saved:
            self._load_and_display_users()


    def _edit_user_dialog_stub(self): # Will be replaced by more specific edit actions
        selected_item = self.users_tree.selection()
        if not selected_item:
            messagebox.showwarning("Nada Seleccionado", "Por favor, seleccione un usuario para editar.", parent=self.window)
            return
        user_id = self.users_tree.item(selected_item[0], "values")[0]
        username = self.users_tree.item(selected_item[0], "values")[1]
        messagebox.showinfo("WIP", f"Funcionalidad 'Editar Usuario' (ID: {user_id}, User: {username}) no implementada aún.", parent=self.window)
        # Later: Open EditUserDialog or specific action dialogs (Reset PW, Toggle Permiso)

    def _delete_user_stub(self):
        selected_item = self.users_tree.selection()
        if not selected_item:
            messagebox.showwarning("Nada Seleccionado", "Por favor, seleccione un usuario para eliminar.", parent=self.window)
            return
        user_id = self.users_tree.item(selected_item[0], "values")[0]
        username = self.users_tree.item(selected_item[0], "values")[1]
        
        if messagebox.askyesno("Confirmar Eliminación", 
                               f"¿Está seguro de que desea eliminar al usuario '{username}' (ID: {user_id})?\nEsta acción no se puede deshacer.",
                               parent=self.window):
            try:
                success = self.db_manager.delete_user_cascade(int(user_id))
                if success:
                    messagebox.showinfo("Eliminado", f"Usuario '{username}' eliminado correctamente.", parent=self.window)
                    self._load_and_display_users()
                else:
                    messagebox.showerror("Error", f"No se pudo eliminar al usuario '{username}'.", parent=self.window)
            except AttributeError:
                 messagebox.showerror("No Implementado", "La función 'delete_user_cascade' no está disponible en el gestor de DB.", parent=self.window)
            except Exception as e:
                messagebox.showerror("Error Crítico", f"Error eliminando usuario: {e}", parent=self.window)


class AddUserDialog(tk.Toplevel):
    def __init__(self, parent, db_manager, existing_user_data: Optional[models.Usuario] = None):
        super().__init__(parent)
        self.db_manager = db_manager
        self.existing_user_data = existing_user_data
        self.user_saved = False # Flag to indicate if save was successful

        if self.existing_user_data:
            self.title(f"Editar Usuario: {self.existing_user_data.username}")
        else:
            self.title("Añadir Nuevo Usuario")
        
        self.geometry("450x400") # Adjusted size
        self.transient(parent)
        self.grab_set()
        self.window = self # For wait_window in parent

        main_frame = ttk.Frame(self, padding="10")
        main_frame.pack(expand=True, fill=tk.BOTH)
        main_frame.columnconfigure(1, weight=1)

        # Username
        ttk.Label(main_frame, text="Username:").grid(row=0, column=0, padx=5, pady=5, sticky="w")
        self.username_entry = ttk.Entry(main_frame, width=30)
        self.username_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")

        # Password
        ttk.Label(main_frame, text="Password:").grid(row=1, column=0, padx=5, pady=5, sticky="w")
        self.password_entry = ttk.Entry(main_frame, width=30, show="*")
        self.password_entry.grid(row=1, column=1, padx=5, pady=5, sticky="ew")

        # Confirm Password
        ttk.Label(main_frame, text="Confirmar Passwd:").grid(row=2, column=0, padx=5, pady=5, sticky="w")
        self.confirm_password_entry = ttk.Entry(main_frame, width=30, show="*")
        self.confirm_password_entry.grid(row=2, column=1, padx=5, pady=5, sticky="ew")
        
        # Permiso
        self.permiso_var = tk.BooleanVar()
        permiso_check = ttk.Checkbutton(main_frame, text="Permiso de Escritura (Registro)", variable=self.permiso_var)
        permiso_check.grid(row=3, column=0, columnspan=2, padx=5, pady=5, sticky="w")

        # Roles
        roles_frame = ttk.LabelFrame(main_frame, text="Roles Asignados", padding="5")
        roles_frame.grid(row=4, column=0, columnspan=2, padx=5, pady=5, sticky="nsew")
        roles_frame.columnconfigure(0, weight=1) # Make listbox expand
        roles_frame.rowconfigure(0, weight=1)    # Make listbox expand

        self.roles_listbox = tk.Listbox(roles_frame, selectmode=tk.MULTIPLE, exportselection=False, height=5)
        self.roles_listbox.grid(row=0, column=0, sticky="nsew", padx=5, pady=5)
        
        roles_scrollbar = ttk.Scrollbar(roles_frame, orient="vertical", command=self.roles_listbox.yview)
        self.roles_listbox.configure(yscrollcommand=roles_scrollbar.set)
        roles_scrollbar.grid(row=0, column=1, sticky="ns")
        
        self._populate_roles_listbox()

        # Buttons
        button_frame_dialog = ttk.Frame(main_frame)
        button_frame_dialog.grid(row=5, column=0, columnspan=2, pady=(10,0))

        ttk.Button(button_frame_dialog, text="Guardar", command=self._on_save).pack(side=tk.LEFT, padx=10)
        ttk.Button(button_frame_dialog, text="Cancelar", command=self.destroy).pack(side=tk.LEFT, padx=10)

        if self.existing_user_data:
            self._fill_for_edit()
            if self.existing_user_data.username == "admin": # Cannot edit admin user's username or roles easily
                self.username_entry.config(state="disabled")
                # Password fields are for setting a new one, so they remain active.
                # Roles for admin might also be restricted. For now, allow for testing.

    def _populate_roles_listbox(self):
        try:
            self.all_roles_data = self.db_manager.get_all_roles() # List of Role objects
            for role in self.all_roles_data:
                self.roles_listbox.insert(tk.END, f"{role.nombre_role} (ID: {role.id})")
        except AttributeError:
             messagebox.showerror("Error", "Función 'get_all_roles' no disponible.", parent=self)
             self.all_roles_data = [] # Ensure it exists
        except Exception as e:
            messagebox.showerror("Error Cargando Roles", f"No se pudieron cargar los roles: {e}", parent=self)
            self.all_roles_data = []

    def _fill_for_edit(self):
        self.username_entry.insert(0, self.existing_user_data.username)
        self.permiso_var.set(self.existing_user_data.permiso)
        
        # Select roles in listbox
        if self.all_roles_data and self.existing_user_data.roles:
            user_role_ids = {role.id for role in self.existing_user_data.roles}
            for i, role_in_listbox in enumerate(self.all_roles_data):
                if role_in_listbox.id in user_role_ids:
                    self.roles_listbox.selection_set(i)
        
        # For editing, password fields are for *new* password. Blank means no change.
        # If we want to force reset, then "reset" could be a special value.
        # For now, blank = no change to password.

    def _on_save(self):
        username = self.username_entry.get().strip()
        password = self.password_entry.get() # No strip, might be intentional spaces
        confirm_password = self.confirm_password_entry.get()
        permiso = self.permiso_var.get()
        
        selected_indices = self.roles_listbox.curselection()
        selected_role_ids = [self.all_roles_data[i].id for i in selected_indices]

        if not username:
            messagebox.showerror("Error de Validación", "El nombre de usuario no puede estar vacío.", parent=self)
            return

        if not self.existing_user_data: # Adding new user
            if not password:
                messagebox.showerror("Error de Validación", "La contraseña es obligatoria para nuevos usuarios.", parent=self)
                return
            if password != confirm_password:
                messagebox.showerror("Error de Validación", "Las contraseñas no coinciden.", parent=self)
                return
            
            try:
                # Assume save_user_with_details hashes the password
                new_user_id = self.db_manager.save_user_with_details(username, password, selected_role_ids, permiso)
                if new_user_id:
                    messagebox.showinfo("Éxito", f"Usuario '{username}' añadido con ID: {new_user_id}.", parent=self)
                    self.user_saved = True
                    self.destroy()
                else:
                    messagebox.showerror("Error al Guardar", f"No se pudo añadir el usuario '{username}'.", parent=self)
            except AttributeError:
                 messagebox.showerror("No Implementado", "La función 'save_user_with_details' no está disponible.", parent=self)
            except Exception as e:
                messagebox.showerror("Error Crítico", f"Error añadiendo usuario: {e}", parent=self)

        else: # Editing existing user
            # Handle password change
            if password: # If password field is not empty, user wants to change it
                if password != confirm_password:
                    messagebox.showerror("Error de Validación", "Las contraseñas no coinciden.", parent=self)
                    return
                # Call update_user_password (it should hash)
                try:
                    pw_success = self.db_manager.update_user_password(username, password) # username should be disabled for edit if not admin
                    if not pw_success: messagebox.showwarning("Aviso", "No se pudo actualizar la contraseña.", parent=self); return # Or continue if other changes are fine
                except AttributeError: messagebox.showerror("No Implementado", "Función 'update_user_password' no disponible.", parent=self); return
                except Exception as e: messagebox.showerror("Error Contraseña", f"Error: {e}", parent=self); return


            # Handle permission change
            if self.existing_user_data.permiso != permiso:
                try:
                    perm_success = self.db_manager.update_user_permission(self.existing_user_data.id, permiso)
                    if not perm_success: messagebox.showwarning("Aviso", "No se pudo actualizar el permiso.", parent=self); # Continue or return
                except AttributeError: messagebox.showerror("No Implementado", "Función 'update_user_permission' no disponible.", parent=self); return
                except Exception as e: messagebox.showerror("Error Permiso", f"Error: {e}", parent=self); return

            # Handle role change (more complex, might need update_user_roles function)
            # For now, we can print what would happen
            print(f"TODO: Implementar actualización de roles para User ID {self.existing_user_data.id} a IDs: {selected_role_ids}")
            # Placeholder for actual role update call:
            # success_roles = self.db_manager.update_user_roles(self.existing_user_data.id, selected_role_ids)
            
            messagebox.showinfo("Éxito (Parcial)", "Cambios de usuario (simulados/parciales) guardados.", parent=self) # Update message later
            self.user_saved = True # Assume some success for now
            self.destroy()


if __name__ == '__main__':
    # This is for basic testing of UserManagementWindow structure.
    # It requires a mock parent window and a mock db_manager.
    
    class MockDBManager:
        def get_all_users_with_details(self):
            print("MockDBManager: get_all_users_with_details called")
            # Simulate models.Usuario and models.Role structure
            role_admin = models.Role(id=1, nombre_role="Admin", posicion=None, is_jefe=True)
            role_empl = models.Role(id=2, nombre_role="Empleado", posicion=None, is_jefe=False)
            
            user1 = models.Usuario(id=1, username="testadmin", password_hash="xyz", roles=[role_admin], permiso=True)
            user2 = models.Usuario(id=2, username="testuser", password_hash="abc", roles=[role_empl], permiso=False)
            return [user1, user2]

    root = tk.Tk()
    root.title("Parent Window")
    
    # Dummy current_user for testing (an admin)
    admin_role_for_test = models.Role(id=99, nombre_role="SuperAdmin", is_jefe=True)
    current_admin_user = models.Usuario(id=999, username="CurrentAdmin", roles=[admin_role_for_test], permiso=True)

    # Button to open the UserManagementWindow
    def open_user_management():
        db_mock = MockDBManager()
        user_mgmt_window = UserManagementWindow(root, current_admin_user, db_mock)
        # user_mgmt_window.window.mainloop() # This would block if not handled by root's mainloop
    
    ttk.Button(root, text="Abrir Gestión de Usuarios", command=open_user_management).pack(padx=20, pady=20)
    root.mainloop()

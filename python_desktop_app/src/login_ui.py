import tkinter as tk
from tkinter import ttk, messagebox

# Attempt to import database_manager and models
# These imports assume that the script is run as part of the package,
# or that the src directory is in PYTHONPATH.
try:
    # Try relative import first (when used as module)
    from . import database_manager
    from . import models
    _database_manager_available = True
except ImportError:
    try:
        # Fallback to absolute import (when run directly)
        from src import database_manager
        from src import models
        _database_manager_available = True
    except ImportError:
        print("Warning: database_manager or models not found. Running with mock login logic.")
        _database_manager_available = False
    # Define dummy classes if models are not available, for standalone testing
    class DummyUser:
        def __init__(self, id, username, roles):
            self.id = id
            self.username = username
            self.roles = roles
    class DummyRole:
        def __init__(self, nombre_role, **kwargs):
            self.nombre_role = nombre_role

class LoginWindow:
    def __init__(self, parent):
        print("Creating LoginWindow...")
        self.parent = parent
        print(f"Using parent window: {parent}")
        self.window = parent  # Use the parent window directly
        self.window.title("Login - Sistema de Registro")
        print("Window configured as login")
        
        print("Configuring window geometry...")
        self.window.resizable(False, False)
        
        # Calculate center position
        parent_x = parent.winfo_x()
        parent_y = parent.winfo_y()
        parent_width = parent.winfo_width()
        parent_height = parent.winfo_height()
        
        # Fallback if parent is withdrawn or not yet sized (e.g. root.withdraw())
        if parent_width < 50 or parent_height < 50 : # Arbitrary small values
            screen_width = self.window.winfo_screenwidth()
            screen_height = self.window.winfo_screenheight()
            win_x = (screen_width - 300) // 2 # Approx window width
            win_y = (screen_height - 200) // 2 # Approx window height
        else:
            win_x = parent_x + (parent_width - 300) // 2
            win_y = parent_y + (parent_height - 200) // 2
        
        print(f"Setting window geometry to 300x200+{win_x}+{win_y}")
        self.window.geometry(f"300x200+{win_x}+{win_y}")
        print("Window geometry set")

        self.user = None  # To store the logged-in user object

        # Frame for content
        self.content_frame = ttk.Frame(self.window, padding="10 10 10 10")
        self.content_frame.pack(expand=True, fill=tk.BOTH)

        # Username
        ttk.Label(self.content_frame, text="Username:").grid(row=0, column=0, padx=5, pady=5, sticky="w")
        self.username_var = tk.StringVar()
        self.username_entry = ttk.Entry(self.content_frame, width=30, textvariable=self.username_var)
        self.username_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")

        # Password
        ttk.Label(self.content_frame, text="Password:").grid(row=1, column=0, padx=5, pady=5, sticky="w")
        self.password_var = tk.StringVar()
        self.password_entry = ttk.Entry(self.content_frame, show="*", width=30, textvariable=self.password_var)
        self.password_entry.grid(row=1, column=1, padx=5, pady=5, sticky="ew")
        
        # Improved key bindings
        self.username_entry.bind("<Return>", lambda e: (self.password_entry.focus_set(), self.username_entry.update()))
        self.password_entry.bind("<Return>", lambda e: (self.password_entry.update(), self.attempt_login(e)))


        # Status Label
        self.status_label = ttk.Label(self.content_frame, text="", foreground="red", wraplength=280)
        self.status_label.grid(row=2, column=0, columnspan=2, padx=5, pady=(10, 5), sticky="ew")

        # Login Button
        self.login_button = ttk.Button(self.content_frame, text="Login", command=self.attempt_login)
        self.login_button.grid(row=3, column=0, columnspan=2, padx=5, pady=10)
        
        # Focus on username entry initially
        self.username_entry.focus_set()

        # Handle window close button
        self.window.protocol("WM_DELETE_WINDOW", self._on_close)
        print("Window close handler configured")
        self.username_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")
        print("Username widgets created")

        print("Creating password widgets...")
        ttk.Label(self.content_frame, text="Password:").grid(row=1, column=0, padx=5, pady=5, sticky="w")
        self.password_entry = ttk.Entry(self.content_frame, show="*", width=30)
        self.password_entry.grid(row=1, column=1, padx=5, pady=5, sticky="ew")
        print("Password widgets created")


    def _on_close(self):
        self.user = None # Ensure user is None if window is closed
        self.window.destroy()

    def attempt_login(self, event=None):
        """Handle login attempt with proper result processing"""
        username = self.username_entry.get().strip()
        password = self.password_entry.get()
        
        # Validate inputs
        if not username or not password:
            self.status_label.config(text="Username and password cannot be empty.", foreground="red")
            return

        # Get login result
        result = self._get_login_result(username, password)

        # Process result
        if isinstance(result, (models.Usuario if _database_manager_available else DummyUser)):
            self._handle_successful_login(result)
        elif result == 2:
            self._show_password_reset_ui(username)
        elif result is None or result == -1:
            self.status_label.config(text="Invalid username or password.", foreground="red")
        else:
            self.status_label.config(text=f"Unexpected login result: {result}", foreground="red")

    def _get_login_result(self, username, password):
        """Get login result from appropriate source"""
        if _database_manager_available:
            try:
                # First check if password is "reset" in database
                user = database_manager.get_user_by_username(username)
                if user and hasattr(user, 'password') and user.password == "reset":
                    return 2  # Special code for password reset
                
                # Normal login attempt
                return database_manager.login_user(username, password)
            except Exception as e:
                print(f"Login error: {e}")
                self.status_label.config(text="System error. Check logs.", foreground="red")
                if not (username == "testuser" and password == "testpassword"):
                    return self._mock_login_user(username, password, use_dummy_models=True)
        return self._mock_login_user(username, password, use_dummy_models=True)

    def _handle_successful_login(self, user):
        """Handle successful login"""
        self.status_label.config(text="Login successful!", foreground="green")
        self.user = user
        self.window.after(1000, self.window.destroy)
            
    def _save_new_password(self, username):
        """Handle saving new password with encryption"""
        new_pass = self.new_pass_var.get()
        confirm_pass = self.confirm_pass_var.get()
        
        if not new_pass or not confirm_pass:
            self.status_label.config(text="Both fields are required", foreground="red")
            return
            
        if new_pass != confirm_pass:
            self.status_label.config(text="Passwords don't match", foreground="red")
            return
            
        try:
            # Encrypt and save new password
            if _database_manager_available:
                try:
                    from .database_manager import update_user_password
                    if update_user_password(username, new_pass):
                        self.status_label.config(text="Password updated successfully!", foreground="green")
                        self.window.after(1000, self.window.destroy)
                    else:
                        self.status_label.config(text="Failed to update password", foreground="red")
                except Exception as e:
                    print(f"Error updating password: {e}")
                    self.status_label.config(text=f"Error updating password: {e}", foreground="red")
            else:
                # Mock implementation
                print(f"MOCK: Updated password for {username}")
                self.status_label.config(text="Password updated (mock)", foreground="green")
                self.window.after(1000, self.window.destroy)
        except Exception as e:
            self.status_label.config(text=f"Error updating password: {e}", foreground="red")
            
    def _show_password_reset_ui(self, username):
        """Show UI for password reset"""
        # Clear existing widgets
        for widget in self.content_frame.winfo_children():
            widget.destroy()
            
        # New password fields
        ttk.Label(self.content_frame, text="New Password:").grid(row=0, column=0, padx=5, pady=5, sticky="w")
        self.new_pass_var = tk.StringVar()
        self.new_pass_entry = ttk.Entry(self.content_frame, show="*", width=30, textvariable=self.new_pass_var)
        self.new_pass_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")
        
        ttk.Label(self.content_frame, text="Confirm Password:").grid(row=1, column=0, padx=5, pady=5, sticky="w")
        self.confirm_pass_var = tk.StringVar()
        self.confirm_pass_entry = ttk.Entry(self.content_frame, show="*", width=30, textvariable=self.confirm_pass_var)
        self.confirm_pass_entry.grid(row=1, column=1, padx=5, pady=5, sticky="ew")
        
        # Save button
        self.save_button = ttk.Button(self.content_frame, text="Guardar nuevo password",
                                    command=lambda: self._save_new_password(username))
        self.save_button.grid(row=2, column=0, columnspan=2, padx=5, pady=10)
        
        # Status label
        self.status_label = ttk.Label(self.content_frame, text="Enter and confirm new password", foreground="orange")
        self.status_label.grid(row=3, column=0, columnspan=2, padx=5, pady=(10,5), sticky="ew")
        
        self.new_pass_entry.focus_set()


    def _mock_login_user(self, username, password, use_dummy_models=False):
        """Mock login function for testing UI without full backend."""
        print(f"MOCK LOGIN: Attempting with user='{username}' pass='{password}'")
        if use_dummy_models:
            MockUserClass = DummyUser
            MockRoleClass = DummyRole
        else:
            MockUserClass = models.Usuario
            MockRoleClass = models.Role

        if username == "testuser" and password == "testpassword":
            print("MOCK LOGIN: Success for testuser")
            # Ensure Role class is available or use a simpler structure
            try:
                role = MockRoleClass(nombre_role="Test Role", id=1, posicion="Tester", is_jefe=False)
                user_obj = MockUserClass(id=1, username="testuser", password_hash="mock_hash", roles=[role])
                return user_obj
            except TypeError as e: # Handle case where models.Role init might change
                 print(f"MOCK LOGIN: Error creating mock user object: {e}")
                 return None # Or a simplified object
        elif username == "resetme" and password == "reset":
            print("MOCK LOGIN: Reset required for resetme")
            return 2
        else:
            print("MOCK LOGIN: Failure for other users")
            return None

    def show(self):
        print("Showing login window...")
        self.window.update()  # Force UI update
        self.window.deiconify()  # Ensure window is visible
        print("Login window ready")
        
        # Start main loop
        self.window.mainloop()
        return self.user

def show_login_dialog(parent_window):
    """
    Creates and displays the login window.
    Returns the models.Usuario object upon successful login, or None otherwise.
    """
    login_dialog = LoginWindow(parent_window)
    return login_dialog.show()

# Test code moved to test_login_ui.py

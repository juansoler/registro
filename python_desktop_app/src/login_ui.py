import tkinter as tk
from tkinter import ttk, messagebox

# Attempt to import database_manager and models
# These imports assume that the script is run as part of the package,
# or that the src directory is in PYTHONPATH.
try:
    from . import database_manager
    from . import models
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
        self.parent = parent
        self.window = tk.Toplevel(parent)
        self.window.title("Login")
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
        
        self.window.geometry(f"300x200+{win_x}+{win_y}")

        self.user = None  # To store the logged-in user object

        # Frame for content
        content_frame = ttk.Frame(self.window, padding="10 10 10 10")
        content_frame.pack(expand=True, fill=tk.BOTH)

        # Username
        ttk.Label(content_frame, text="Username:").grid(row=0, column=0, padx=5, pady=5, sticky="w")
        self.username_entry = ttk.Entry(content_frame, width=30)
        self.username_entry.grid(row=0, column=1, padx=5, pady=5, sticky="ew")

        # Password
        ttk.Label(content_frame, text="Password:").grid(row=1, column=0, padx=5, pady=5, sticky="w")
        self.password_entry = ttk.Entry(content_frame, show="*", width=30)
        self.password_entry.grid(row=1, column=1, padx=5, pady=5, sticky="ew")
        
        # Bind Enter key to attempt_login
        self.username_entry.bind("<Return>", lambda event: self.password_entry.focus_set())
        self.password_entry.bind("<Return>", self.attempt_login)


        # Status Label
        self.status_label = ttk.Label(content_frame, text="", foreground="red", wraplength=280)
        self.status_label.grid(row=2, column=0, columnspan=2, padx=5, pady=(10, 5), sticky="ew")

        # Login Button
        self.login_button = ttk.Button(content_frame, text="Login", command=self.attempt_login)
        self.login_button.grid(row=3, column=0, columnspan=2, padx=5, pady=10)
        
        # Focus on username entry initially
        self.username_entry.focus_set()

        # Make window modal
        self.window.protocol("WM_DELETE_WINDOW", self._on_close) # Handle window close button
        self.window.transient(parent) # Set to be on top of parent
        self.window.grab_set()  # Make modal

    def _on_close(self):
        self.user = None # Ensure user is None if window is closed
        self.window.destroy()

    def attempt_login(self, event=None): # event=None for button click, event object for Enter key
        username = self.username_entry.get().strip()
        password = self.password_entry.get() # No strip on password

        if not username or not password:
            self.status_label.config(text="Username and password cannot be empty.", foreground="red")
            # messagebox.showerror("Error", "Username and password cannot be empty.", parent=self.window)
            return

        result = None
        if _database_manager_available:
            try:
                result = database_manager.login_user(username, password)
            except Exception as e:
                print(f"Error calling database_manager.login_user: {e}")
                # This could be due to db.sqlite not existing, CONFIG.CFG missing, etc.
                self.status_label.config(text="System error. Check logs.", foreground="red")
                # Fallback to mock if there's a system issue to allow UI testing
                if not (username == "testuser" and password == "testpassword"): # Avoid successful mock login on system error
                     result = self._mock_login_user(username, password, use_dummy_models=True)

        else: # Fallback to mock login if database_manager is not available
            result = self._mock_login_user(username, password, use_dummy_models=True)


        if isinstance(result, (models.Usuario if _database_manager_available else DummyUser)):
            self.status_label.config(text="Login successful!", foreground="green")
            self.user = result
            self.window.after(1000, self.window.destroy) # Close after 1 sec
        elif result == 2:
            self.status_label.config(text="Password reset required. Enter new password.", foreground="orange")
            # Further UI for password reset is not part of this subtask.
            # For now, it will just show the message.
        elif result is None or result == -1: # None or -1 for login failure
            self.status_label.config(text="Invalid username or password.", foreground="red")
        else: # Unexpected result
             self.status_label.config(text=f"Unexpected login result: {result}", foreground="red")


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
        self.window.wait_window()  # Wait until window is destroyed
        return self.user

def show_login_dialog(parent_window):
    """
    Creates and displays the login window.
    Returns the models.Usuario object upon successful login, or None otherwise.
    """
    login_dialog = LoginWindow(parent_window)
    return login_dialog.show()

# Example Usage (for testing this module standalone)
if __name__ == '__main__':
    root = tk.Tk()
    root.title("Main Application Window")
    # root.geometry("600x400")
    root.withdraw() # Hide the main root window for this test

    # --- Setup for standalone testing if database_manager is not directly available ---
    # This attempts to ensure that if run standalone, the necessary modules can be found.
    # It assumes a certain directory structure: project_root/src/login_ui.py
    if not _database_manager_available:
        import sys
        import os
        
        # Get the absolute path to the directory containing login_ui.py (src)
        current_dir = os.path.dirname(os.path.abspath(__file__))
        # Get the absolute path to the parent directory of src (project_root)
        project_root = os.path.dirname(current_dir)
        
        # Add project_root to sys.path so 'from src import ...' works
        if project_root not in sys.path:
            sys.path.insert(0, project_root)
            print(f"Added {project_root} to sys.path for standalone testing.")

        # Now try importing again if they weren't found initially
        try:
            from src import database_manager
            from src import models
            from src import config_manager # db_manager might need this
            _database_manager_available = True
            print("Successfully imported database_manager and models for standalone test.")
            
            # If using database_manager, it might need CONFIG.CFG
            # Create a dummy CONFIG.CFG if it doesn't exist in the project root
            # The database_manager expects BASE_DIR/db.sqlite
            # For standalone testing, we can set BASE_DIR to the project root.
            config_file_path = os.path.join(project_root, "CONFIG.CFG")
            if not os.path.exists(config_file_path) and _database_manager_available:
                print(f"Creating dummy CONFIG.CFG at {config_file_path} for testing.")
                with open(config_file_path, "w") as f:
                    f.write(f"BASE_DIR = {project_root}\n")
                    f.write(f"LOCAL_DIR = {project_root}\n")
                # Also, ensure database_manager can create/find db.sqlite
                # This might require running the database_manager's own __main__ block
                # or having a pre-existing db.sqlite.
                # For now, we assume database_manager.login_user will handle it or fail gracefully.
                # Let's try to initialize the DB with some test data if possible.
                try:
                    print("Attempting to initialize database for standalone login_ui test...")
                    conn = database_manager.get_db_connection() # This should create db.sqlite if not exists
                    cursor = conn.cursor()
                    # Create tables (idempotent)
                    cursor.execute("CREATE TABLE IF NOT EXISTS user (id INTEGER PRIMARY KEY, user TEXT UNIQUE, password TEXT, reset INTEGER DEFAULT 0)")
                    cursor.execute("CREATE TABLE IF NOT EXISTS role (id INTEGER PRIMARY KEY, nombre_role TEXT UNIQUE, posicion TEXT, isJefe INTEGER DEFAULT 0)")
                    cursor.execute("CREATE TABLE IF NOT EXISTS usuario_role (user_id INTEGER, role_id INTEGER, FOREIGN KEY(user_id) REFERENCES user(id), FOREIGN KEY(role_id) REFERENCES role(id), PRIMARY KEY(user_id, role_id))")
                    # Insert a test user that login_user expects (password "testpassword" for placeholder)
                    # The placeholder in database_manager.login_user is "testpassword"
                    cursor.execute("INSERT OR IGNORE INTO user (id, user, password, reset) VALUES (1, 'testuser', 'actual_hashed_password', 0)") 
                    cursor.execute("INSERT OR IGNORE INTO user (id, user, password, reset) VALUES (2, 'resetme', 'reset', 1)")
                    conn.commit()
                    print("Database initialized with test user 'testuser' (pass: 'testpassword') and 'resetme' (pass: 'reset').")
                except Exception as e:
                    print(f"Error initializing database for standalone test: {e}")
                    print("Login UI test will likely rely on MOCK backend if DB connection failed.")
                    _database_manager_available = False # Fallback to mock if DB setup fails
        except ImportError as e:
            print(f"Could not import database_manager/models even after path adjustment: {e}")
            print("Running login_ui.py test with MOCK backend.")
            _database_manager_available = False
        except Exception as e:
            print(f"An unexpected error occurred during standalone setup: {e}")
            _database_manager_available = False


    print("\nShowing login dialog...")
    # Create a dummy main window to act as parent (as it's usually withdrawn)
    # If root is withdrawn, Toplevel might not behave as expected regarding centering.
    # So, we can briefly make it visible and then hide, or use screen dimensions.
    # The LoginWindow class now handles centering even if parent is withdrawn.
    
    user_object = show_login_dialog(root)

    if user_object:
        print(f"Logged in as: {user_object.username}")
        if hasattr(user_object, 'roles') and user_object.roles:
            print(f"Roles: {[role.nombre_role for role in user_object.roles]}")
    else:
        print("Login failed or cancelled.")
    
    # Clean up dummy config if created
    # config_file_path_to_clean = os.path.join(project_root if 'project_root' in locals() else ".", "CONFIG.CFG")
    # if "dummy CONFIG.CFG" in open(config_file_path_to_clean).read(): # Basic check
    #    os.remove(config_file_path_to_clean)
    #    print(f"Cleaned up dummy {config_file_path_to_clean}")

    root.destroy() # Ensure main Tkinter loop exits
    print("Login UI test finished.")
```

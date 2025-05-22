import tkinter as tk
from tkinter import messagebox
import sys
import os

# --- Adjust sys.path to allow relative imports ---
# This is often needed when running scripts directly from within a package structure.
# Get the absolute path to the directory containing main.py (src)
current_script_dir = os.path.dirname(os.path.abspath(__file__))
# Get the absolute path to the parent directory of src (project_root)
project_root_dir = os.path.dirname(current_script_dir)

# Add project_root_dir to sys.path if it's not already there
if project_root_dir not in sys.path:
    sys.path.insert(0, project_root_dir)
# --- End of sys.path adjustment ---

try:
    from src import config_manager
    from src import login_ui
    from src import main_app_ui
    from src import database_manager # For closing DB connection
except ImportError as e:
    # This message box will likely not work if Tkinter isn't initialized or if it's very early.
    # Printing to console is more reliable for early import errors.
    print(f"Critical Error: Could not import necessary modules: {e}")
    print("Please ensure the application is run from the correct directory or PYTHONPATH is set.")
    # Fallback for GUI error message if Tk can be initialized
    try:
        root_err = tk.Tk()
        root_err.withdraw()
        messagebox.showerror("Startup Error", f"Could not import necessary modules: {e}\nApplication will exit.")
        root_err.destroy()
    except tk.TclError: # In case Tkinter itself is the problem
        pass
    sys.exit(1)


def main():
    # 1. Create the main Tkinter root window and hide it
    root = tk.Tk()
    root.withdraw()

    # 2. Load configuration
    loaded_config = None
    try:
        # Attempt to load from a standard location, e.g., project root or user directory
        # For this example, let's assume CONFIG.CFG is in the project root.
        config_file_path = os.path.join(project_root_dir, "CONFIG.CFG")
        
        # Create a dummy CONFIG.CFG if it doesn't exist for the sake of running the app
        # In a real app, this might be handled by an installer or initial setup.
        if not os.path.exists(config_file_path):
            print(f"Warning: CONFIG.CFG not found at {config_file_path}. Creating a dummy file.")
            try:
                with open(config_file_path, "w") as f:
                    # Default BASE_DIR to project_root_dir for testing purposes
                    f.write(f"BASE_DIR = {project_root_dir}\n") 
                    f.write(f"LOCAL_DIR = {os.path.join(project_root_dir, 'local_data')}\n")
                if not os.path.exists(os.path.join(project_root_dir, 'local_data')):
                    os.makedirs(os.path.join(project_root_dir, 'local_data'))
            except IOError as e_io:
                messagebox.showerror("Config Error", f"Could not create dummy CONFIG.CFG: {e_io}")
                root.destroy()
                sys.exit(1)

        loaded_config = config_manager.load_config(config_file_path)
        if not loaded_config or not config_manager.get_base_dir():
            # load_config might print its own errors, but we add a clear GUI message
            messagebox.showerror("Configuration Error",
                                 f"Failed to load configuration from {config_file_path}.\n"
                                 "BASE_DIR might be missing or the file is invalid.\n"
                                 "Application will exit.")
            root.destroy()
            sys.exit(1)
        
        # Also load version info (optional, but good practice)
        version_file_path = os.path.join(project_root_dir, "VER.CFG")
        if not os.path.exists(version_file_path):
             print(f"Warning: VER.CFG not found at {version_file_path}. Creating a dummy file.")
             try:
                with open(version_file_path, "w") as f:
                    f.write("VERSION = 0.1.0-alpha\n")
             except IOError as e_io:
                print(f"Could not create dummy VER.CFG: {e_io}") # Non-critical, just print
        
        config_manager.load_version_info(version_file_path)


    except Exception as e:
        messagebox.showerror("Configuration Error", f"An unexpected error occurred during configuration loading: {e}")
        root.destroy()
        sys.exit(1)

    # 3. Show login dialog
    user_object = login_ui.show_login_dialog(root)

    # 4. Process login result
    if user_object:
        # Login successful
        # Create and run the main application window
        # The MainApplicationWindow will use 'root' as its main window and call deiconify()
        try:
            main_app = main_app_ui.MainApplicationWindow(root, user_object, loaded_config)
            root.mainloop() # Start the main event loop for the now-visible main window
        except Exception as e:
            messagebox.showerror("Application Error", f"An error occurred while starting the main application: {e}")
            # Fall through to cleanup
    else:
        # Login failed or cancelled
        messagebox.showinfo("Login", "Login failed or was cancelled. Application will exit.")
        # No root.mainloop() will be called, so the app effectively exits after this.

    # 5. Cleanup (called when root.mainloop() exits or if login fails)
    print("Application is shutting down...")
    try:
        database_manager.close_db_connection()
        print("Database connection closed.")
    except Exception as e:
        print(f"Error closing database connection: {e}")
    
    # root.destroy() is implicitly called when mainloop ends for the root window,
    # or if it was never started and the script ends.
    # If root was used by Toplevels, explicit destroy might be needed if mainloop wasn't on root.
    # In our case, MainApplicationWindow uses 'root', so its mainloop manages 'root'.
    print("Exiting application.")


if __name__ == "__main__":
    main()

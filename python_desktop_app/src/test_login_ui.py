"""Standalone test for login_ui module"""
import tkinter as tk
import os
import sys
from login_ui import show_login_dialog, LoginWindow

def setup_test_environment():
    """Configure paths and imports for standalone testing"""
    # Get project root path
    current_dir = os.path.dirname(os.path.abspath(__file__))
    project_root = os.path.dirname(current_dir)
    
    # Add to Python path if needed
    if project_root not in sys.path:
        sys.path.insert(0, project_root)
        print(f"Added {project_root} to sys.path")

    # Try to import required modules
    try:
        from src import database_manager, models, config_manager
        return True
    except ImportError as e:
        print(f"Could not import required modules: {e}")
        return False

def test_login_ui():
    """Run the login UI test"""
    print("\nStarting login UI test...")
    
    # Setup main window
    root = tk.Tk()
    root.title("Login Test - Main Window")
    root.withdraw()  # Hide main window
    
    # Show login dialog
    print("Showing login dialog...")
    user = show_login_dialog(root)
    
    # Process results
    if user:
        print(f"\nLogin successful! User: {user.username}")
        if hasattr(user, 'roles'):
            print(f"Roles: {[r.nombre_role for r in user.roles]}")
    else:
        print("\nLogin failed or was cancelled")
    
    root.destroy()
    print("Test completed")

if __name__ == '__main__':
    if setup_test_environment():
        test_login_ui()
    else:
        print("Running with mock backend only")
        test_login_ui()  # Will use mock implementations
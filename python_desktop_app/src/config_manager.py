import os

# Module-level variables to store configuration
_config_data = {
    "BASE_DIR": None,
    "LOCAL_DIR": None,
    "VERSION": None,
}

def load_config(config_path="CONFIG.CFG"):
    """
    Reads the CONFIG.CFG file, parses BASE_DIR and LOCAL_DIR,
    and stores them. Handles comments and extra spaces.
    Returns a dictionary with the configuration values.
    """
    global _config_data
    config_values = {}
    
    try:
        with open(config_path, 'r') as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith('//') or line.startswith('#'):
                    continue
                
                parts = line.split('=', 1)
                if len(parts) == 2:
                    key = parts[0].strip()
                    value = parts[1].strip()
                    if key in ["BASE_DIR", "LOCAL_DIR"]:
                        config_values[key] = value
                        _config_data[key] = value
            
            # If keys are missing, set to None or some default
            if "BASE_DIR" not in config_values:
                print(f"Warning: BASE_DIR not found in {config_path}")
                _config_data["BASE_DIR"] = None # Or a default path
            if "LOCAL_DIR" not in config_values:
                print(f"Warning: LOCAL_DIR not found in {config_path}")
                _config_data["LOCAL_DIR"] = None # Or a default path

    except FileNotFoundError:
        print(f"Error: Configuration file '{config_path}' not found.")
        # Set defaults or handle as critical error
        _config_data["BASE_DIR"] = None
        _config_data["LOCAL_DIR"] = None
        # Optionally, re-raise the error or exit
        # raise
    
    return config_values

def get_base_dir():
    """Returns the loaded BASE_DIR."""
    return _config_data["BASE_DIR"]

def get_local_dir():
    """Returns the loaded LOCAL_DIR."""
    return _config_data["LOCAL_DIR"]

def load_version_info(version_config_path="VER.CFG"):
    """
    Placeholder function to read VER.CFG and return the version string.
    Handles FileNotFoundError gracefully.
    """
    global _config_data
    version = None
    try:
        with open(version_config_path, 'r') as f:
            for line in f:
                line = line.strip()
                if not line or line.startswith('//') or line.startswith('#'):
                    continue
                
                parts = line.split('=', 1)
                if len(parts) == 2:
                    key = parts[0].strip()
                    value = parts[1].strip()
                    if key == "VERSION":
                        version = value
                        _config_data["VERSION"] = version
                        break # Assuming only one VERSION key
        if version is None:
             print(f"Warning: VERSION not found in {version_config_path}")

    except FileNotFoundError:
        print(f"Error: Version configuration file '{version_config_path}' not found.")
        _config_data["VERSION"] = None # Default or previously loaded value
        # raise
        
    return version

# Example of loading configuration when the module is imported (optional)
# load_config() 
# load_version_info()

if __name__ == '__main__':
    # Example Usage (for testing purposes)
    # Create dummy CONFIG.CFG and VER.CFG for testing
    with open("TEST_CONFIG.CFG", "w") as f:
        f.write("# Main configuration\n")
        f.write("BASE_DIR = /example/base/dir\n")
        f.write("LOCAL_DIR = /example/local/dir  \n")
        f.write("OTHER_VAR = some_value\n")
        f.write("// LOCAL_DIR = /commented/out\n")

    with open("TEST_VER.CFG", "w") as f:
        f.write("VERSION = 1.0.2\n")

    print("--- Testing load_config ---")
    config = load_config("TEST_CONFIG.CFG")
    print(f"Loaded config: {config}")
    print(f"get_base_dir(): {get_base_dir()}")
    print(f"get_local_dir(): {get_local_dir()}")

    print("\n--- Testing load_config with non-existent file ---")
    config_missing = load_config("NON_EXISTENT_CONFIG.CFG")
    print(f"Loaded config (missing): {config_missing}")
    print(f"get_base_dir() (after missing): {get_base_dir()}")
    print(f"get_local_dir() (after missing): {get_local_dir()}")


    print("\n--- Testing load_version_info ---")
    version = load_version_info("TEST_VER.CFG")
    print(f"Loaded version: {version}")
    print(f"_config_data['VERSION']: {_config_data['VERSION']}")
    
    print("\n--- Testing load_version_info with non-existent file ---")
    version_missing = load_version_info("NON_EXISTENT_VER.CFG")
    print(f"Loaded version (missing): {version_missing}")
    print(f"_config_data['VERSION'] (after missing): {_config_data['VERSION']}")

    # Clean up dummy files
    os.remove("TEST_CONFIG.CFG")
    os.remove("TEST_VER.CFG")

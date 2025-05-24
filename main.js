const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
const fs = require('fs'); // For reading config/version files

// --- Database and Auth Utilities ---
const dbService = require('./src/services/databaseService'); // Assuming path
const passwordUtils = require('./src/services/passwordUtils'); // Assuming path

// --- Configuration Placeholders (to be replaced with actual loading) ---
let BASE_DIR = './'; // Default for development, should come from CONFIG.CFG
let LOCAL_DIR = './'; // Default, should come from CONFIG.CFG
// ---
let activeUser = null; // Store active user after login

function createWindow (mainWindow) { // mainWindow passed in
    // ... existing code ...
    mainWindow.loadFile('index.html');
    
    // Send version info after window is ready
    mainWindow.webContents.on('did-finish-load', () => {
        // Simulate version loading for now
        const versions = getCurrentVersions(); // Implement this function
        mainWindow.webContents.send('version-info', versions);
        
        // Example: Connect to DB when window loads
        try {
            dbService.connectDb(); 
            console.log("Database connected on window load.");
        } catch (error) {
            console.error("Failed to connect database on window load:", error);
            // Optionally send an error to renderer to display a critical DB error
        }
    });
    // ... existing code ...
}

app.whenReady().then(() => {
    const mainWindow = new BrowserWindow({ // create here
        width: 800,
        height: 600,
        webPreferences: {
            preload: path.join(__dirname, 'preload.js'),
            contextIsolation: true, // Recommended for security
            nodeIntegration: false // Recommended for security
        }
    });
    createWindow(mainWindow); // pass mainWindow

    app.on('activate', function () {
        if (BrowserWindow.getAllWindows().length === 0) {
             const newMainWindow = new BrowserWindow({ // Same options as above
                width: 800,
                height: 600,
                webPreferences: {
                    preload: path.join(__dirname, 'preload.js'),
                    contextIsolation: true,
                    nodeIntegration: false
                }
             });
             createWindow(newMainWindow);
        }
    });
});

app.on('window-all-closed', function () {
    dbService.closeDb(); // Close DB when app closes
    if (process.platform !== 'darwin') app.quit();
});

// --- IPC Handlers ---

ipcMain.handle('login', async (_event, credentials) => {
   const { username, password, newPassword } = credentials;
   try {
       dbService.connectDb(); // Ensure DB is connected
       
       if (newPassword) { // This means we are in the "set new password" flow
           const saltedHash = await passwordUtils.getSaltedHash(newPassword);
           const userRecord = dbService.getUserByUsername(username); 
           if (!userRecord) return { success: false, message: "Usuario no encontrado." };

           const stmt = dbService.getDb().prepare('UPDATE user SET password = ? WHERE user = ?');
           const info = stmt.run(saltedHash, username);
           
           if (info.changes > 0) {
               return { success: true, passwordChanged: true, message: "Contraseña cambiada con éxito." };
           } else {
               return { success: false, message: "Error al cambiar la contraseña." };
           }
       } else {
           // Standard login
           const userRecord = dbService.getUserByUsername(username);
           if (!userRecord) {
               return { success: false, message: "El usuario no existe." };
           }
           if (userRecord.password === 'reset') {
               // Send an event to the renderer to trigger the password reset UI
               // _event.sender.send('request-password-reset'); // This will be handled by preload's handleRequestPasswordReset
               return { success: false, resetPassword: true, message: "Debe cambiar su contraseña." };
           }
           const passwordMatch = await passwordUtils.check(password, userRecord.password);
           if (passwordMatch) {
               activeUser = dbService.getUsuario(userRecord.id); // Store user
               // The renderer will navigate upon receiving success.
               return { success: true, user: activeUser, targetWindow: 'main_app.html' };
           } else {
               return { success: false, message: "Contraseña incorrecta." };
           }
       }
   } catch (error) {
       console.error('Login error:', error);
       return { success: false, message: `Error en el servidor: ${error.message}` };
   }
});

ipcMain.handle('get-initial-ui-data', async () => {
    try {
        dbService.connectDb(); // Ensure DB is connected
        const negociados = dbService.getNegociados();
        const categorias = dbService.getCategorias();
        // Ensure activeUser is set from login process
        // If main_app.html is a new window, activeUser might need to be passed to it or requested separately.
        return { 
            negociados, 
            categorias, 
            currentUser: activeUser // Send current user info
        };
    } catch (error) {
        console.error('Error fetching initial UI data:', error);
        return { error: error.message };
    }
});

// Placeholder for getVersions IPC handler
ipcMain.handle('get-versions', async () => {
    console.log("IPC: get-versions called");
    return getCurrentVersions(); // Use the existing function
});

// Placeholder for trigger-update IPC handler
ipcMain.on('trigger-update', () => {
    console.log("IPC: trigger-update called. Would launch actu.exe");
    // const { execFile } = require('child_process');
    // const updaterPath = path.join(BASE_DIR, 'actu.exe'); // Ensure BASE_DIR is correctly set
    // try {
    //    execFile(updaterPath, (error, stdout, stderr) => {
    //        if (error) throw error;
    //        console.log(stdout);
    //    });
    //    app.quit(); // Quit after launching updater
    // } catch (e) { console.error("Failed to launch updater:", e); }
});

// Function to read version files (implement actual logic)
function getCurrentVersions() {
    try {
        // Placeholder: replace with actual fs.readFileSync from an appropriate path
        // const localVerContent = fs.readFileSync(path.join(LOCAL_DIR, 'VER.CFG'), 'utf8');
        // const remoteVerContent = fs.readFileSync(path.join(BASE_DIR, 'VER.CFG'), 'utf8');
        // const localVersion = parseVersion(localVerContent); // parse "VERSION=1.0"
        // const remoteVersion = parseVersion(remoteVerContent);
        // return { local: localVersion, remote: remoteVersion, needsUpdate: parseFloat(localVersion) < parseFloat(remoteVersion) };
        // Mock data for now, ensuring it's different from renderer's initial placeholders if any
        return { local: '1.0.0-main', remote: '1.1.0-main', needsUpdate: true }; 
    } catch (err) {
        console.error("Error reading version files:", err);
        return { local: 'Error', remote: 'Error', needsUpdate: false };
    }
}

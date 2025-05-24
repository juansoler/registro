// preload.js
const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('electronAPI', {
    login: (credentials) => ipcRenderer.invoke('login', credentials),
    saveNewPassword: (data) => ipcRenderer.invoke('save-new-password', data), // Added for completeness, though login handler covers it
    getVersions: () => ipcRenderer.invoke('get-versions'),
    triggerUpdate: () => ipcRenderer.send('trigger-update'),
    getInitialUIData: () => ipcRenderer.invoke('get-initial-ui-data'), // Added
    // For admin actions, can be added later
    // openAdminDialog: (dialogName) => ipcRenderer.send('open-admin-dialog', dialogName)
    
    // Functions for main to renderer communication (callbacks)
    // These allow the main process to send events to the renderer.
    // The renderer will set up listeners for these events.
    handleLoginResult: (callback) => ipcRenderer.on('login-result', (_event, result) => callback(result)),
    handlePasswordChangeResult: (callback) => ipcRenderer.on('password-change-result', (_event, result) => callback(result)),
    handleVersionInfo: (callback) => ipcRenderer.on('version-info', (_event, versions) => callback(versions)),
    requestPasswordReset: (callback) => ipcRenderer.on('request-password-reset', (_event) => callback()) // Main can tell renderer to show reset UI
    // For log panel updates (if pushed from main)
    // handleLogUpdate: (channel, callback) => ipcRenderer.on(channel, (_event, message) => callback(message)),
});

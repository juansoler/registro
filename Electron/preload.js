const { contextBridge, ipcRenderer } = require('electron');

contextBridge.exposeInMainWorld('api', {
  loadConfig: () => ipcRenderer.invoke('load-config'),
  validateUser: (data) => ipcRenderer.invoke('validate-user', data),
  listEntries: () => ipcRenderer.invoke('list-entries'),
  listUsers: () => ipcRenderer.invoke('list-users'),
  addUser: (data) => ipcRenderer.invoke('add-user', data),
  resetPassword: (data) => ipcRenderer.invoke('reset-password', data),
  createEntry: (data) => ipcRenderer.invoke('create-entry', data),
});

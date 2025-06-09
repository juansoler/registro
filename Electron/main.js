const { app, BrowserWindow, ipcMain } = require('electron');
const path = require('path');
const fs = require('fs');
const sqlite3 = require('sqlite3').verbose();
const crypto = require('crypto');

let cachedConfig = null;

function loadConfig() {
  if (cachedConfig) return cachedConfig;
  const cfgPath = path.join(__dirname, '..', 'CONFIG.CFG');
  const raw = fs.readFileSync(cfgPath, 'utf8');
  const cfg = {};
  raw.split(/\r?\n/).forEach(line => {
    line = line.trim();
    if (!line || line.startsWith('//')) return;
    const parts = line.split('=');
    if (parts.length === 2) {
      cfg[parts[0].trim()] = parts[1].trim();
    }
  });
  cachedConfig = cfg;
  return cfg;
}

function initializeSchema(db) {
  db.serialize(() => {
    db.run("CREATE TABLE IF NOT EXISTS USER(id INTEGER PRIMARY KEY AUTOINCREMENT, user TEXT UNIQUE, password TEXT)");
    db.run("CREATE TABLE IF NOT EXISTS usuario_role(id INTEGER PRIMARY KEY AUTOINCREMENT, role TEXT, user_id INTEGER, permiso INTEGER DEFAULT 0, isJefe INTEGER DEFAULT 0)");
    db.run("CREATE TABLE IF NOT EXISTS entrada(id INTEGER PRIMARY KEY AUTOINCREMENT, Asunto TEXT, Fecha TEXT, Area TEXT, Confidencial INTEGER, Urgente INTEGER, Observaciones TEXT, NumeroEntrada TEXT)");
    db.run("CREATE TABLE IF NOT EXISTS comentario(id INTEGER PRIMARY KEY AUTOINCREMENT, entrada_id INTEGER, usuario_id INTEGER, comentario TEXT, fecha TEXT, hora TEXT, visto INTEGER DEFAULT 0)");
  });
}

function openDB() {
  const cfg = loadConfig();
  let dbPath;
  if (cfg.BASE_DIR) {
    dbPath = path.join(cfg.BASE_DIR, 'db.sqlite');
  } else {
    dbPath = path.join(__dirname, 'db.sqlite');
  }
  const first = !fs.existsSync(dbPath);
  const db = new sqlite3.Database(dbPath);
  if (first) {
    initializeSchema(db);
  }
  return db;
}

function createWindow() {
  const win = new BrowserWindow({
    width: 800,
    height: 600,
    webPreferences: {
      preload: path.join(__dirname, 'preload.js'),
    },
  });

  win.loadFile('index.html');
}

app.whenReady().then(() => {
  createWindow();

  app.on('activate', function () {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

app.on('window-all-closed', function () {
  if (process.platform !== 'darwin') app.quit();
});

// Placeholder: register ipc handlers for login, entries, configuration etc.
ipcMain.handle('load-config', async () => {
  try {
    return loadConfig();
  } catch (e) {
    console.error(e);
    return {};
  }
});

ipcMain.handle('validate-user', async (evt, { username, password }) => {
  return new Promise((resolve) => {
    const db = openDB();
    db.get('SELECT password FROM USER WHERE USER=?', [username.toLowerCase()], (err, row) => {
      if (err || !row) {
        db.close();
        return resolve(false);
      }
      const [salt, stored] = row.password.split('$');
      crypto.pbkdf2(password, Buffer.from(salt, 'base64'), 20000, 32, 'sha1', (e, derived) => {
        db.close();
        if (e) return resolve(false);
        resolve(derived.toString('base64') === stored);
      });
    });
  });
});

ipcMain.handle('list-entries', async () => {
  return new Promise((resolve) => {
    const db = openDB();
    db.all('SELECT id, Asunto, Fecha, Area FROM entrada ORDER BY id DESC LIMIT 20', (err, rows) => {
      db.close();
      if (err) resolve([]);
      else resolve(rows);
    });
  });
});

function hashPassword(pass) {
  const salt = crypto.randomBytes(16).toString('base64');
  const derived = crypto.pbkdf2Sync(pass, Buffer.from(salt, 'base64'), 20000, 32, 'sha1');
  return `${salt}$${derived.toString('base64')}`;
}

ipcMain.handle('list-users', async () => {
  return new Promise((resolve) => {
    const db = openDB();
    db.all('SELECT id, user FROM USER', (err, rows) => {
      db.close();
      if (err) resolve([]); else resolve(rows);
    });
  });
});

ipcMain.handle('add-user', async (evt, { user, password, role }) => {
  return new Promise((resolve) => {
    const db = openDB();
    const pwd = hashPassword(password);
    db.run('INSERT INTO USER(user, password) VALUES(?,?)', [user.toLowerCase(), pwd], function(err) {
      if (err) { db.close(); return resolve(false); }
      const userId = this.lastID;
      db.run('INSERT INTO usuario_role(role, user_id, permiso, isJefe) VALUES(?,?,0,0)', [role, userId], (e) => {
        db.close();
        if (e) resolve(false); else resolve(true);
      });
    });
  });
});

ipcMain.handle('reset-password', async (evt, { id, newPassword }) => {
  return new Promise((resolve) => {
    const db = openDB();
    const pwd = hashPassword(newPassword);
    db.run('UPDATE USER SET password=? WHERE id=?', [pwd, id], (err) => {
      db.close();
      resolve(!err);
    });
  });
});

ipcMain.handle('create-entry', async (evt, entry) => {
  return new Promise((resolve) => {
    const db = openDB();
    db.run(
      'INSERT INTO entrada(Asunto, Fecha, Area, Confidencial, Urgente, Observaciones, NumeroEntrada) VALUES(?,?,?,?,?,?,?)',
      [entry.Asunto, entry.Fecha, entry.Area, entry.Confidencial ? 1 : 0, entry.Urgente ? 1 : 0, entry.Observaciones, entry.NumeroEntrada],
      function(err) {
        db.close();
        if (err) return resolve(null);
        resolve(this.lastID);
      }
    );
  });
});

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
  db.run("CREATE TABLE IF NOT EXISTS entrada(id INTEGER PRIMARY KEY AUTOINCREMENT, Asunto TEXT, Fecha TEXT, Area TEXT, Canal TEXT, Confidencial INTEGER, Urgente INTEGER, Observaciones TEXT, NumeroEntrada TEXT)");
  db.run("CREATE TABLE IF NOT EXISTS destinatario(id INTEGER PRIMARY KEY AUTOINCREMENT, entry_id INTEGER, area TEXT)");
  db.run("CREATE TABLE IF NOT EXISTS jefe_destinatario(id INTEGER PRIMARY KEY AUTOINCREMENT, entry_id INTEGER, jefe TEXT)");
  db.run("CREATE TABLE IF NOT EXISTS files(id INTEGER PRIMARY KEY AUTOINCREMENT, entry_id INTEGER, tipo TEXT, path TEXT)");
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
    db.all('SELECT id, Asunto, Fecha, Area, Canal FROM entrada ORDER BY id DESC LIMIT 20', (err, rows) => {
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
      'INSERT INTO entrada(Asunto, Fecha, Area, Canal, Confidencial, Urgente, Observaciones, NumeroEntrada) VALUES(?,?,?,?,?,?,?,?)',
      [
        entry.Asunto,
        entry.Fecha,
        entry.Area,
        entry.Canal,
        entry.Confidencial ? 1 : 0,
        entry.Urgente ? 1 : 0,
        entry.Observaciones,
        entry.NumeroEntrada,
      ],
      function (err) {
        if (err) {
          db.close();
          return resolve(null);
        }
        const entryId = this.lastID;

        const saveDest = db.prepare('INSERT INTO destinatario(entry_id, area) VALUES(?,?)');
        (entry.Destinatarios || []).forEach((d) => {
          saveDest.run(entryId, d);
        });
        saveDest.finalize();

        const saveJefe = db.prepare('INSERT INTO jefe_destinatario(entry_id, jefe) VALUES(?,?)');
        (entry.Jefes || []).forEach((j) => {
          saveJefe.run(entryId, j);
        });
        saveJefe.finalize();

        const cfg = loadConfig();
        const base = cfg.BASE_DIR || __dirname;
        const dateDir = entry.Fecha;

        function saveFiles(type, files) {
          const dirMap = {
            entrada: 'DOCS',
            antecedente: 'DOCS_ANTECEDENTES',
            salida: 'DOCS_SALIDA',
          };
          if (!files || !files.length) return;
          const destDir = path.join(base, dirMap[type], dateDir);
          fs.mkdirSync(destDir, { recursive: true });
          const stmt = db.prepare('INSERT INTO files(entry_id, tipo, path) VALUES(?,?,?)');
          files.forEach((f) => {
            const target = path.join(destDir, path.basename(f));
            try {
              fs.copyFileSync(f, target);
            } catch (e) {}
            stmt.run(entryId, type, target);
          });
          stmt.finalize();
        }

        saveFiles('entrada', entry.Files || []);
        saveFiles('antecedente', entry.Antecedentes || []);
        saveFiles('salida', entry.Salida || []);

        db.close();
        resolve(entryId);
      }
    );
  });
});

ipcMain.handle('get-entry', async (evt, id) => {
  return new Promise((resolve) => {
    const db = openDB();
    db.get(
      'SELECT id, Asunto, Fecha, Area, Canal, Confidencial, Urgente, Observaciones, NumeroEntrada FROM entrada WHERE id=?',
      [id],
      (err, row) => {
        if (err || !row) {
          db.close();
          return resolve(null);
        }
        const entry = row;
        db.all('SELECT area FROM destinatario WHERE entry_id=?', [id], (e1, destRows) => {
          entry.Destinatarios = destRows ? destRows.map((r) => r.area) : [];
          db.all('SELECT jefe FROM jefe_destinatario WHERE entry_id=?', [id], (e2, jefeRows) => {
            entry.Jefes = jefeRows ? jefeRows.map((r) => r.jefe) : [];
            db.all('SELECT tipo, path FROM files WHERE entry_id=?', [id], (e3, fileRows) => {
              entry.Files = { entrada: [], antecedente: [], salida: [] };
              if (fileRows) {
                fileRows.forEach((r) => {
                  entry.Files[r.tipo] = entry.Files[r.tipo] || [];
                  entry.Files[r.tipo].push(r.path);
                });
              }
              db.all(
                'SELECT id, usuario_id, comentario, fecha, hora, visto FROM comentario WHERE entrada_id=?',
                [id],
                (e4, comRows) => {
                  entry.Comentarios = comRows || [];
                  db.close();
                  resolve(entry);
                }
              );
            });
          });
        });
      }
    );
  });
});

ipcMain.handle('update-entry', async (evt, entry) => {
  return new Promise((resolve) => {
    const db = openDB();
    db.run(
      'UPDATE entrada SET Asunto=?, Fecha=?, Area=?, Canal=?, Confidencial=?, Urgente=?, Observaciones=?, NumeroEntrada=? WHERE id=?',
      [
        entry.Asunto,
        entry.Fecha,
        entry.Area,
        entry.Canal,
        entry.Confidencial ? 1 : 0,
        entry.Urgente ? 1 : 0,
        entry.Observaciones,
        entry.NumeroEntrada,
        entry.id,
      ],
      (err) => {
        if (err) {
          db.close();
          return resolve(false);
        }

        db.run('DELETE FROM destinatario WHERE entry_id=?', [entry.id], () => {
          const stmt = db.prepare('INSERT INTO destinatario(entry_id, area) VALUES(?,?)');
          (entry.Destinatarios || []).forEach((d) => stmt.run(entry.id, d));
          stmt.finalize();
        });

        db.run('DELETE FROM jefe_destinatario WHERE entry_id=?', [entry.id], () => {
          const stmt = db.prepare('INSERT INTO jefe_destinatario(entry_id, jefe) VALUES(?,?)');
          (entry.Jefes || []).forEach((j) => stmt.run(entry.id, j));
          stmt.finalize();
        });

        const stmtCom = db.prepare(
          'INSERT INTO comentario(entrada_id, usuario_id, comentario, fecha, hora, visto) VALUES(?,?,?,?,?,?)'
        );
        (entry.NewComments || []).forEach((c) => {
          stmtCom.run(entry.id, c.usuario_id, c.comentario, c.fecha, c.hora, c.visto ? 1 : 0);
        });
        stmtCom.finalize();

        const cfg = loadConfig();
        const base = cfg.BASE_DIR || __dirname;
        const dateDir = entry.Fecha;
        function saveFiles(type, files) {
          const dirMap = {
            entrada: 'DOCS',
            antecedente: 'DOCS_ANTECEDENTES',
            salida: 'DOCS_SALIDA',
          };
          if (!files || !files.length) return;
          const destDir = path.join(base, dirMap[type], dateDir);
          fs.mkdirSync(destDir, { recursive: true });
          const stmt = db.prepare('INSERT INTO files(entry_id, tipo, path) VALUES(?,?,?)');
          files.forEach((f) => {
            const target = path.join(destDir, path.basename(f));
            try {
              fs.copyFileSync(f, target);
            } catch (e) {}
            stmt.run(entry.id, type, target);
          });
          stmt.finalize();
        }

        saveFiles('entrada', entry.NewFiles ? entry.NewFiles.entrada : []);
        saveFiles('antecedente', entry.NewFiles ? entry.NewFiles.antecedente : []);
        saveFiles('salida', entry.NewFiles ? entry.NewFiles.salida : []);

        db.close();
        resolve(true);
      }
    );
  });
});

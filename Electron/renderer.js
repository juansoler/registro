window.addEventListener('DOMContentLoaded', () => {
  const loginForm = document.getElementById('login');
  loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    const username = document.getElementById('user').value;
    const password = document.getElementById('pass').value;
    const ok = await window.api.validateUser({ username, password });
    if (ok) {
      document.getElementById('login-container').style.display = 'none';
      document.getElementById('main').style.display = 'block';
      showSection('entries-section');
      loadEntries();
    } else {
      document.getElementById('login-error').textContent = 'Usuario o contraseña incorrectos';
    }
  });

  document.getElementById('show-entries').addEventListener('click', () => {
    showSection('entries-section');
    loadEntries();
  });
  document.getElementById('show-new-entry').addEventListener('click', () => {
    showSection('new-entry-section');
  });
  document.getElementById('show-users').addEventListener('click', () => {
    showSection('users-section');
    loadUsers();
  });

  document.getElementById('add-user-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const user = document.getElementById('new-user').value;
    const password = document.getElementById('new-pass').value;
    const role = document.getElementById('new-role').value;
    const ok = await window.api.addUser({ user, password, role });
    document.getElementById('user-result').textContent = ok ? 'Usuario creado' : 'Error al crear';
    if (ok) {
      document.getElementById('add-user-form').reset();
      loadUsers();
    }
  });

  document.getElementById('new-entry-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const entry = {
      Asunto: document.getElementById('entry-asunto').value,
      Fecha: document.getElementById('entry-fecha').value,
      Area: document.getElementById('entry-area').value,
      Canal: document.getElementById('entry-canal').value,
      Confidencial: document.getElementById('entry-confidencial').checked,
      Urgente: document.getElementById('entry-urgente').checked,
      Observaciones: document.getElementById('entry-observaciones').value,
      NumeroEntrada: document.getElementById('entry-numero').value,
      Destinatarios: document.getElementById('entry-destinatarios').value
        .split(';')
        .map((s) => s.trim())
        .filter((s) => s),
      Jefes: document.getElementById('entry-jefes').value
        .split(';')
        .map((s) => s.trim())
        .filter((s) => s),
      Files: Array.from(document.getElementById('entry-files').files).map((f) => f.path),
      Antecedentes: Array.from(document.getElementById('entry-antecedentes').files).map((f) => f.path),
      Salida: Array.from(document.getElementById('entry-salida').files).map((f) => f.path),
    };
    const id = await window.api.createEntry(entry);
    document.getElementById('entry-result').textContent = id ? 'Guardado con ID ' + id : 'Error al guardar';
    if (id) {
      document.getElementById('new-entry-form').reset();
      loadEntries();
    }
  });

  async function loadEntries() {
    const entries = await window.api.listEntries();
    const tbody = document.querySelector('#entries tbody');
    tbody.innerHTML = '';
    entries.forEach((e) => {
      const row = document.createElement('tr');
      row.dataset.id = e.id;
      row.innerHTML = `<td>${e.id}</td><td>${e.Asunto}</td><td>${e.Fecha}</td><td>${e.Area}</td><td>${e.Canal ?? ''}</td>`;
      tbody.appendChild(row);
    });
  }

  async function loadUsers() {
    const users = await window.api.listUsers();
    const ul = document.getElementById('user-list');
    ul.innerHTML = '';
    users.forEach((u) => {
      const li = document.createElement('li');
      li.textContent = `${u.id}: ${u.user}`;
      ul.appendChild(li);
    });
  }

  function showSection(id) {
    document.getElementById('entries-section').style.display = 'none';
    document.getElementById('new-entry-section').style.display = 'none';
    document.getElementById('edit-entry-section').style.display = 'none';
    document.getElementById('users-section').style.display = 'none';
    document.getElementById(id).style.display = 'block';
  }

  document.querySelector('#entries tbody').addEventListener('click', async (e) => {
    const tr = e.target.closest('tr');
    if (!tr) return;
    const id = parseInt(tr.dataset.id, 10);
    const entry = await window.api.getEntry(id);
    if (!entry) return;
    document.getElementById('edit-id').value = entry.id;
    document.getElementById('edit-asunto').value = entry.Asunto;
    document.getElementById('edit-fecha').value = entry.Fecha;
    document.getElementById('edit-area').value = entry.Area;
    document.getElementById('edit-canal').value = entry.Canal || '';
    document.getElementById('edit-confidencial').checked = !!entry.Confidencial;
    document.getElementById('edit-urgente').checked = !!entry.Urgente;
    document.getElementById('edit-observaciones').value = entry.Observaciones || '';
    document.getElementById('edit-numero').value = entry.NumeroEntrada || '';
    document.getElementById('edit-destinatarios').value = (entry.Destinatarios || []).join('; ');
    document.getElementById('edit-jefes').value = (entry.Jefes || []).join('; ');
    const filesEntrada = document.getElementById('existing-entrada');
    filesEntrada.innerHTML = (entry.Files.entrada || []).map(f => `<li>${f}</li>`).join('');
    const filesAnt = document.getElementById('existing-antecedente');
    filesAnt.innerHTML = (entry.Files.antecedente || []).map(f => `<li>${f}</li>`).join('');
    const filesSal = document.getElementById('existing-salida');
    filesSal.innerHTML = (entry.Files.salida || []).map(f => `<li>${f}</li>`).join('');
    showSection('edit-entry-section');
  });

  document.getElementById('edit-entry-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const entry = {
      id: parseInt(document.getElementById('edit-id').value, 10),
      Asunto: document.getElementById('edit-asunto').value,
      Fecha: document.getElementById('edit-fecha').value,
      Area: document.getElementById('edit-area').value,
      Canal: document.getElementById('edit-canal').value,
      Confidencial: document.getElementById('edit-confidencial').checked,
      Urgente: document.getElementById('edit-urgente').checked,
      Observaciones: document.getElementById('edit-observaciones').value,
      NumeroEntrada: document.getElementById('edit-numero').value,
      Destinatarios: document.getElementById('edit-destinatarios').value
        .split(';')
        .map(s => s.trim())
        .filter(s => s),
      Jefes: document.getElementById('edit-jefes').value
        .split(';')
        .map(s => s.trim())
        .filter(s => s),
      NewComments: [
        {
          usuario_id: 0,
          comentario: document.getElementById('edit-comentario').value,
          fecha: new Date().toISOString().slice(0, 10),
          hora: new Date().toTimeString().slice(0,5),
          visto: document.getElementById('edit-visto').checked,
        },
      ],
      NewFiles: {
        entrada: Array.from(document.getElementById('edit-files-entrada').files).map(f => f.path),
        antecedente: Array.from(document.getElementById('edit-files-antecedente').files).map(f => f.path),
        salida: Array.from(document.getElementById('edit-files-salida').files).map(f => f.path),
      },
    };
    await window.api.updateEntry(entry);
    document.getElementById('edit-result').textContent = 'Guardado';
    loadEntries();
    showSection('entries-section');
  });
});

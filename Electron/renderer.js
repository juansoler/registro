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
      Confidencial: document.getElementById('entry-confidencial').checked,
      Urgente: document.getElementById('entry-urgente').checked,
      Observaciones: document.getElementById('entry-observaciones').value,
      NumeroEntrada: document.getElementById('entry-numero').value,
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
      row.innerHTML = `<td>${e.id}</td><td>${e.Asunto}</td><td>${e.Fecha}</td><td>${e.Area}</td>`;
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
    document.getElementById('users-section').style.display = 'none';
    document.getElementById(id).style.display = 'block';
  }
});

const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const newPasswordInput = document.getElementById('new-password');
const showPasswordCheckbox = document.getElementById('show-password');
const connectButton = document.getElementById('connect-button');
const errorMessageElement = document.getElementById('error-message');
const newPasswordGroup = document.getElementById('new-password-group');

// Caps Lock Detection
function checkCapsLock(event, warningElementId) {
    const warningElement = document.getElementById(warningElementId);
    if (event.getModifierState && event.getModifierState('CapsLock')) {
        warningElement.style.display = 'inline';
    } else {
        warningElement.style.display = 'none';
    }
}

usernameInput.addEventListener('keyup', (e) => checkCapsLock(e, 'username-caps-warning'));
usernameInput.addEventListener('focus', (e) => checkCapsLock(e, 'username-caps-warning'));
passwordInput.addEventListener('keyup', (e) => {
    checkCapsLock(e, 'password-caps-warning');
    if (e.key === 'Enter') connectButton.click();
});
passwordInput.addEventListener('focus', (e) => checkCapsLock(e, 'password-caps-warning'));
newPasswordInput.addEventListener('keyup', (e) => {
    checkCapsLock(e, 'new-password-caps-warning');
    if (e.key === 'Enter') connectButton.click();
});
newPasswordInput.addEventListener('focus', (e) => checkCapsLock(e, 'new-password-caps-warning'));

// Show/Hide Password
showPasswordCheckbox.addEventListener('change', () => {
    const type = showPasswordCheckbox.checked ? 'text' : 'password';
    passwordInput.type = type;
    if (newPasswordGroup.style.display !== 'none') {
        newPasswordInput.type = type;
    }
});

// Connect Button Logic
connectButton.addEventListener('click', async () => { // make async
    const username = usernameInput.value;
    const password = passwordInput.value;
    const newPassword = newPasswordInput.value;

    errorMessageElement.textContent = ''; // Clear previous errors

    if (!username) {
        errorMessageElement.textContent = 'El nombre de usuario no puede estar vacío.';
        return;
    }
    const isResetFlow = newPasswordGroup.style.display !== 'none';

    if (!isResetFlow && !password) {
        errorMessageElement.textContent = 'La constraseña no puede estar vacia.';
        return;
    }
    if (isResetFlow && !newPassword) {
        errorMessageElement.textContent = 'La nueva constraseña no puede estar vacia.';
        return;
    }
    
    try {
        // Use newPassword if the new password field is visible, otherwise use the regular password.
        // If it's a reset flow, password might be empty/disabled, newPassword is the one to send.
        const credentials = { 
            username, 
            password: isResetFlow ? null : password, // Send null for password if it's a new password entry scenario
            newPassword: isResetFlow ? newPassword : null 
        };
        
        console.log("Sending to main:", credentials);
        const result = await window.electronAPI.login(credentials);
        console.log("Received from main:", result);

        if (result.success) {
            if (result.passwordChanged) {
                // UI update for password changed successfully
                newPasswordGroup.style.display = 'none';
                passwordInput.disabled = false;
                passwordInput.value = ''; // Clear old password field
                newPasswordInput.value = ''; // Clear new password field
                connectButton.textContent = 'Conectar';
                errorMessageElement.textContent = result.message || 'Contraseña cambiada. Ingrese con la nueva contraseña.';
                errorMessageElement.style.color = 'green';
                passwordInput.focus();
            } else {
                // UI update for login successful
                console.log('Login successful! User:', result.user);
                // Ensure result.user and result.user.username exist before trying to display them
                const displayUser = result.user && result.user.username ? result.user.username : 'usuario';
                errorMessageElement.textContent = `Login successful! Hola, ${displayUser}`;
                errorMessageElement.style.color = 'green';
                // Navigate to main application view
                if (result.targetWindow) {
                    window.location.href = result.targetWindow;
                }
            }
        } else {
            if (result.resetPassword) {
                // UI update for password reset needed
                newPasswordGroup.style.display = 'block';
                passwordInput.disabled = true;
                passwordInput.value = ''; // Clear the disabled password field
                connectButton.textContent = 'Cambiar contraseña';
                // Ensure new password field visibility matches checkbox state
                newPasswordInput.type = showPasswordCheckbox.checked ? 'text' : 'password';
                newPasswordInput.focus();
                errorMessageElement.textContent = result.message || 'Debe cambiar su contraseña.';
            } else {
                errorMessageElement.textContent = result.message || 'Error desconocido.';
            }
        }
    } catch (error) {
        console.error('IPC Login Error:', error);
        errorMessageElement.textContent = `Error de comunicación: ${error.message}`;
    }
});

// --- Handle data from Main process ---
window.electronAPI.handleVersionInfo((versions) => {
    console.log('Versions received from main:', versions);
    const localVersionElement = document.getElementById('local-version');
    const remoteVersionElement = document.getElementById('remote-version');
    const updateButton = document.getElementById('update-button');

    if (localVersionElement) localVersionElement.textContent = versions.local;
    
    if (versions.needsUpdate && remoteVersionElement && updateButton) {
        if (remoteVersionElement) remoteVersionElement.textContent = versions.remote;
        if (updateButton) {
            updateButton.style.display = 'inline-block';
            updateButton.classList.add('highlight'); // Ensure this class has distinctive styling
            updateButton.onclick = () => window.electronAPI.triggerUpdate();
        }
    } else if (updateButton) {
        updateButton.style.display = 'none';
    }
});

// Handle request from main to show password reset UI (if main process determines this itself)
window.electronAPI.handleRequestPasswordReset(() => {
    newPasswordGroup.style.display = 'block';
    passwordInput.disabled = true;
    passwordInput.value = '';
    connectButton.textContent = 'Cambiar contraseña';
    newPasswordInput.type = showPasswordCheckbox.checked ? 'text' : 'password';
    newPasswordInput.focus();
    errorMessageElement.textContent = 'Se requiere cambio de contraseña.';
});

// The version info is now sent by main.js on 'did-finish-load',
// so no explicit call to getVersions() is needed here on startup.

console.log('Renderer process script loaded and IPC listeners configured.');

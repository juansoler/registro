// main_app_renderer.js
document.addEventListener('DOMContentLoaded', () => {
    const searchType = document.getElementById('search-type');
    const searchTerm = document.getElementById('search-term');
    const areaFilter = document.getElementById('area-filter');
    const categoryFilter = document.getElementById('category-filter');
    const datePicker = document.getElementById('date-picker');
    const prevDayButton = document.getElementById('prev-day-button');
    const nextDayButton = document.getElementById('next-day-button');
    const newEntryButton = document.getElementById('new-entry-button');
document.addEventListener('DOMContentLoaded', async () => { // make async
    const searchType = document.getElementById('search-type');
    const searchTerm = document.getElementById('search-term');
    const areaFilter = document.getElementById('area-filter');
    const categoryFilter = document.getElementById('category-filter');
    const datePicker = document.getElementById('date-picker');
    const prevDayButton = document.getElementById('prev-day-button');
    const nextDayButton = document.getElementById('next-day-button');
    const newEntryButton = document.getElementById('new-entry-button');
    const refreshButton = document.getElementById('refresh-button');
    const pendingProcessButton = document.getElementById('pending-process-button');
    const pendingViewButton = document.getElementById('pending-view-button');
    // const logPanel = document.getElementById('log-panel'); // Original log panel div

    const logDbStatus = document.getElementById('log-db-status');
    const logUserArea = document.getElementById('log-user-area');
    const logLastUpdate = document.getElementById('log-last-update');

    // Set today's date for date picker
    datePicker.valueAsDate = new Date();

    // Event Listeners (simplified logging for brevity, actual actions to be implemented)
    searchType.addEventListener('change', () => console.log(`Search type: ${searchType.value}`));
    searchTerm.addEventListener('keypress', (e) => { 
        if (e.key === 'Enter') console.log(`Search term: ${searchTerm.value}`);
    });
    areaFilter.addEventListener('change', () => console.log(`Area filter: ${areaFilter.value}`));
    categoryFilter.addEventListener('change', () => console.log(`Category filter: ${categoryFilter.value}`));
    datePicker.addEventListener('change', () => console.log(`Date selected: ${datePicker.value}`));
    
    prevDayButton.addEventListener('click', () => {
        const currentDate = datePicker.valueAsDate || new Date();
        currentDate.setDate(currentDate.getDate() - 1);
        datePicker.valueAsDate = currentDate;
        console.log(`Date changed to (prev day): ${datePicker.value}`);
    });
    nextDayButton.addEventListener('click', () => {
        const currentDate = datePicker.valueAsDate || new Date();
        currentDate.setDate(currentDate.getDate() + 1);
        datePicker.valueAsDate = currentDate;
        console.log(`Date changed to (next day): ${datePicker.value}`);
    });

    newEntryButton.addEventListener('click', () => console.log('New Entry button clicked'));
    pendingProcessButton.addEventListener('click', () => console.log('Pending Process button clicked'));
    pendingViewButton.addEventListener('click', () => console.log('Pending View button clicked'));
    
    function populateSelect(selectElement, items, defaultValue, addTodos = true) {
        selectElement.innerHTML = ''; // Clear existing options
        if (addTodos) {
            const defaultOption = document.createElement('option');
            defaultOption.value = 'Todos'; // Standard value for "all"
            defaultOption.textContent = 'Todos';
            selectElement.appendChild(defaultOption);
        }
        if (items && Array.isArray(items)) {
            items.forEach(itemText => {
                const option = document.createElement('option');
                option.value = itemText;
                option.textContent = itemText;
                selectElement.appendChild(option);
            });
        }
        if (defaultValue) {
             selectElement.value = defaultValue;
        } else if (addTodos) {
             selectElement.value = 'Todos';
        }
    }
    
    try {
        const data = await window.electronAPI.getInitialUIData();
        if (data.error) {
            console.error("Error fetching initial data:", data.error);
            logDbStatus.textContent = "DB: Error";
            return;
        }

        logDbStatus.textContent = "DB: Conectado";

        // Populate filters
        populateSelect(areaFilter, data.negociados || [], 'Todos');
        populateSelect(categoryFilter, data.categorias || [], 'Todas');

        // Set user info and default area filter
        if (data.currentUser) {
            const user = data.currentUser;
            // Ensure user.username and user.role are defined before using them
            const username = user.username || 'N/A';
            const userRole = user.role || 'N/A';
            logUserArea.textContent = `Usuario: ${username} | Area: ${userRole}`;
            
            // Set default area if user is not Jefe or Registro (approximating original logic)
            // Check if user.isJefe is defined; it might be boolean false or undefined
            const isJefe = typeof user.isJefe === 'boolean' ? user.isJefe : false; 
            
            if (!isJefe && userRole !== 'Registro' && data.negociados && data.negociados.includes(userRole)) {
                areaFilter.value = userRole;
                areaFilter.disabled = true; 
            } else {
                areaFilter.disabled = false;
            }
        } else {
             logUserArea.textContent = "Usuario: Desconocido";
        }
        
        updateLastUpdateTime(); // Initial call

    } catch (e) {
        console.error('Error in DOMContentLoaded:', e);
        logDbStatus.textContent = "DB: Error de Carga";
        logUserArea.textContent = "Usuario: Error";
        logLastUpdate.textContent = "Última Actualización: Error";
    }
    
    // Update last update time
    function updateLastUpdateTime() {
        const now = new Date();
        logLastUpdate.textContent = `Última Actualización: ${now.toLocaleTimeString()}`;
    }
    
    refreshButton.addEventListener('click', () => {
        console.log('Refresh button clicked');
        // TODO: Add actual refresh logic (fetch table data)
        updateLastUpdateTime();
    });

    console.log('Main application renderer loaded and initial data fetched.');
});

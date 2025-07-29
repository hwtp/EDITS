// Custom JavaScript for the academic memory management system

document.addEventListener('DOMContentLoaded', function() {
    // Initialize tooltips
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    var popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    var popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });

    // Auto-hide alerts after 5 seconds
    setTimeout(function() {
        var alerts = document.querySelectorAll('.alert');
        alerts.forEach(function(alert) {
            var bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        });
    }, 5000);

    // Form validation
    var forms = document.querySelectorAll('.needs-validation');
    Array.prototype.slice.call(forms).forEach(function(form) {
        form.addEventListener('submit', function(event) {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        }, false);
    });

    // Search form auto-submit on filter change
    var searchFilters = document.querySelectorAll('.search-filter');
    searchFilters.forEach(function(filter) {
        filter.addEventListener('change', function() {
            this.closest('form').submit();
        });
    });

    // Confirm delete actions
    var deleteButtons = document.querySelectorAll('[data-confirm-delete]');
    deleteButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            var message = this.getAttribute('data-confirm-delete') || 'Êtes-vous sûr de vouloir supprimer cet élément ?';
            if (!confirm(message)) {
                e.preventDefault();
                return false;
            }
        });
    });

    // Loading state for forms
    var submitButtons = document.querySelectorAll('form button[type="submit"]');
    submitButtons.forEach(function(button) {
        button.closest('form').addEventListener('submit', function() {
            button.disabled = true;
            var originalText = button.innerHTML;
            button.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status"></span>Traitement...';
            
            setTimeout(function() {
                button.disabled = false;
                button.innerHTML = originalText;
            }, 3000);
        });
    });

    // Dynamic table row highlighting
    var tableRows = document.querySelectorAll('table tbody tr');
    tableRows.forEach(function(row) {
        row.addEventListener('mouseenter', function() {
            this.style.backgroundColor = 'rgba(0, 123, 255, 0.1)';
        });
        
        row.addEventListener('mouseleave', function() {
            this.style.backgroundColor = '';
        });
    });

    // Sidebar toggle for mobile
    var sidebarToggle = document.getElementById('sidebarToggle');
    if (sidebarToggle) {
        sidebarToggle.addEventListener('click', function() {
            document.body.classList.toggle('sidebar-toggled');
        });
    }

    // Date picker configuration
    var datePickers = document.querySelectorAll('input[type="date"]');
    datePickers.forEach(function(picker) {
        // Set min date to today for future dates
        if (picker.classList.contains('future-date')) {
            var today = new Date().toISOString().split('T')[0];
            picker.setAttribute('min', today);
        }
        
        // Set max date to today for past dates
        if (picker.classList.contains('past-date')) {
            var today = new Date().toISOString().split('T')[0];
            picker.setAttribute('max', today);
        }
    });

    // Time picker configuration
    var timePickers = document.querySelectorAll('input[type="time"]');
    timePickers.forEach(function(picker) {
        // Set default working hours
        if (!picker.value) {
            picker.value = '09:00';
        }
    });

    // Auto-resize textareas
    var textareas = document.querySelectorAll('textarea');
    textareas.forEach(function(textarea) {
        textarea.addEventListener('input', function() {
            this.style.height = 'auto';
            this.style.height = (this.scrollHeight) + 'px';
        });
    });

    // Search suggestions
    var searchInputs = document.querySelectorAll('input[type="search"], input[name="search"]');
    searchInputs.forEach(function(input) {
        var searchHistory = JSON.parse(localStorage.getItem('searchHistory') || '[]');
        
        input.addEventListener('focus', function() {
            // Show search history if available
            if (searchHistory.length > 0) {
                // Implementation for search suggestions dropdown
                // This would require additional HTML structure
            }
        });
        
        input.addEventListener('keyup', function(e) {
            if (e.key === 'Enter') {
                var searchTerm = this.value.trim();
                if (searchTerm && !searchHistory.includes(searchTerm)) {
                    searchHistory.unshift(searchTerm);
                    searchHistory = searchHistory.slice(0, 10); // Keep only last 10 searches
                    localStorage.setItem('searchHistory', JSON.stringify(searchHistory));
                }
            }
        });
    });

    // Status change confirmation
    var statusSelects = document.querySelectorAll('select[name="statut"]');
    statusSelects.forEach(function(select) {
        var originalValue = select.value;
        
        select.addEventListener('change', function() {
            var newValue = this.value;
            var confirmMessage = 'Êtes-vous sûr de vouloir changer le statut ?';
            
            if (newValue === 'REJETE' || newValue === 'ANNULEE') {
                confirmMessage = 'Attention : cette action changera le statut de manière définitive. Continuer ?';
            }
            
            if (!confirm(confirmMessage)) {
                this.value = originalValue;
                return false;
            }
            
            originalValue = newValue;
        });
    });

    // Print functionality
    var printButtons = document.querySelectorAll('[data-print]');
    printButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            window.print();
        });
    });

    // Export functionality
    var exportButtons = document.querySelectorAll('[data-export]');
    exportButtons.forEach(function(button) {
        button.addEventListener('click', function(e) {
            e.preventDefault();
            var format = this.getAttribute('data-export');
            var table = document.querySelector('table');
            
            if (format === 'csv' && table) {
                exportTableToCSV(table, 'export.csv');
            }
        });
    });

    // Function to export table to CSV
    function exportTableToCSV(table, filename) {
        var csv = [];
        var rows = table.querySelectorAll('tr');
        
        for (var i = 0; i < rows.length; i++) {
            var row = [], cols = rows[i].querySelectorAll('td, th');
            
            for (var j = 0; j < cols.length - 1; j++) { // Exclude actions column
                var cellText = cols[j].innerText.replace(/"/g, '""');
                row.push('"' + cellText + '"');
            }
            
            csv.push(row.join(','));
        }
        
        downloadCSV(csv.join('\n'), filename);
    }
    
    function downloadCSV(csv, filename) {
        var csvFile = new Blob([csv], { type: 'text/csv' });
        var downloadLink = document.createElement('a');
        
        downloadLink.download = filename;
        downloadLink.href = window.URL.createObjectURL(csvFile);
        downloadLink.style.display = 'none';
        
        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
    }

    // Dark mode toggle (if implemented)
    var darkModeToggle = document.getElementById('darkModeToggle');
    if (darkModeToggle) {
        var isDarkMode = localStorage.getItem('darkMode') === 'true';
        
        if (isDarkMode) {
            document.body.classList.add('dark-mode');
            darkModeToggle.checked = true;
        }
        
        darkModeToggle.addEventListener('change', function() {
            if (this.checked) {
                document.body.classList.add('dark-mode');
                localStorage.setItem('darkMode', 'true');
            } else {
                document.body.classList.remove('dark-mode');
                localStorage.setItem('darkMode', 'false');
            }
        });
    }

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl + N for new item
        if (e.ctrlKey && e.key === 'n') {
            e.preventDefault();
            var newButton = document.querySelector('a[href*="/new"]');
            if (newButton) {
                newButton.click();
            }
        }
        
        // Ctrl + S for save (in forms)
        if (e.ctrlKey && e.key === 's') {
            e.preventDefault();
            var submitButton = document.querySelector('form button[type="submit"]');
            if (submitButton) {
                submitButton.click();
            }
        }
        
        // Escape to close modals
        if (e.key === 'Escape') {
            var openModal = document.querySelector('.modal.show');
            if (openModal) {
                var modal = bootstrap.Modal.getInstance(openModal);
                if (modal) {
                    modal.hide();
                }
            }
        }
    });

    // Initialize any additional components
    initializeComponents();
});

function initializeComponents() {
    // Initialize any custom components here
    console.log('Custom components initialized');
}

// Utility functions
function showToast(message, type = 'info') {
    // Implementation for toast notifications
    console.log(`Toast: ${type} - ${message}`);
}

function formatDate(date) {
    return new Date(date).toLocaleDateString('fr-FR');
}

function formatDateTime(datetime) {
    return new Date(datetime).toLocaleString('fr-FR');
}
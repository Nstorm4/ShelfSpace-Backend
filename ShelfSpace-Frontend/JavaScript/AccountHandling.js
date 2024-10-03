document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Simulierter Login-Prozess. Später durch eine Datenbankabfrage ersetzen.
    if (username === 'admin' && password === 'admin') {
        // Login erfolgreich, zur Suchseite weiterleiten
        window.location.href = 'SearchView.html';
    } else {
        // Fehlermeldung anzeigen
        document.getElementById('error-message').classList.remove('hidden');
    }
});

document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // Simulierter Login-Prozess. Später durch eine Datenbankabfrage ersetzen.
    if (username === 'admin' && password === 'admin') {
        // Login erfolgreich, zur Suchseite weiterleiten
        window.location.href = 'OverviewPage.html';
        console.log("login successful");
    } else {
        // Fehlermeldung anzeigen
        console.log("login was not successful");
        document.getElementById('error-message').classList.remove('hidden');
    }
});

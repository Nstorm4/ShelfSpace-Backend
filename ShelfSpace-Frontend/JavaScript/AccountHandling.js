document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // API-Anruf für den Login
    fetch('http://localhost:8080/api/users/login', { // Passe die URL entsprechend an
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            username: username,
            password: password
        }),
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('Login fehlgeschlagen');
            }
            return response.json();
        })
        .then(data => {
            // Überprüfen, ob der Login erfolgreich war
            if (data) { // Assuming your backend returns a response body for successful login
                console.log("Login erfolgreich");
                // Weiterleitung zur Suchseite
                window.location.href = 'OverviewPage.html';
            } else {
                console.log("Login war nicht erfolgreich");
                document.getElementById('error-message').classList.remove('hidden');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('error-message').classList.remove('hidden');
        });
});

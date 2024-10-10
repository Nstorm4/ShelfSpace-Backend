document.getElementById('loginForm').addEventListener('submit', function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // API-Anruf für den Login
    fetch('https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/users/login', {
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
            console.log(response);
            if (!response.ok) {
                throw new Error('Login fehlgeschlagen');
            }
            return response.text();  // Da die API das Token als String zurückgibt
        })
        .then(token => {
            if (token) {
                console.log("Login erfolgreich");
console.log(token);
                // Speichere das Token im Local Storage (oder Session Storage)
                localStorage.setItem('authToken', token);

                // Weiterleitung zu HomePage.html nach erfolgreichem Login
                window.location.href = 'HomePage.html';
            } else {
                document.getElementById('error-message').classList.remove('hidden');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            document.getElementById('error-message').classList.remove('hidden');
        });
});



// Event für den "Sign Up"-Button
document.getElementById('signUpButton').addEventListener('click', function(event) {
    event.preventDefault();

    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    // API-Anruf für die Registrierung
    fetch('https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/users/register', {
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
            console.log(response);
            if (!response.ok) {
                throw new Error('Registrierung fehlgeschlagen');
            }
            return response.text();
        })
        .then(data => {
            console.log("Registrierung erfolgreich");
            alert(data); // Zeige Erfolgsmeldung an
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Registrierung fehlgeschlagen: ' + error.message);
        });
});

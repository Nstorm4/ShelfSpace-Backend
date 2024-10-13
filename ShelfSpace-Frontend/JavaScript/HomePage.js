document.addEventListener("DOMContentLoaded", function () {
    const greetingElement = document.getElementById("greeting");

    // Uhrzeitbasiertes Begrüßungssystem
    const currentHour = new Date().getHours();
    let greetingText = "Guten Tag";

    if (currentHour < 12) {
        greetingText = "Guten Morgen";
    } else if (currentHour >= 12 && currentHour < 18) {
        greetingText = "Guten Mittag";
    } else {
        greetingText = "Guten Abend";
    }

    greetingElement.textContent = `${greetingText}`;

    // Button-Verlinkungen
    document.getElementById("addShelfButton").addEventListener("click", function () {
        const shelfName = prompt("Bitte geben Sie den Namen des neuen Regals ein:");
        if (shelfName) {
            addShelf(shelfName);
        }
    });

    document.getElementById("searchButton").addEventListener("click", function () {
        window.location.href = "SearchView.html"; // Verlinkt zur Suchseite
    });

    document.getElementById('backToAccountButton').addEventListener('click', function() {
        // Hier wird der Logout-Prozess gestartet
        logout();
    });

    // Regale beim Laden der Seite abrufen
    loadShelves();

    function loadShelves() {
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('Sie müssen sich einloggen, um die Regale anzuzeigen.');
            return;
        }

        fetch("https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/shelves/userShelves", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Fehler beim Laden der Regale");
                }
                return response.json();
            })
            .then(shelves => {
                shelves.forEach(shelf => {
                    appendShelfToDOM(shelf);
                });
            })
            .catch(error => {
                alert(error.message);
            });
    }

    function appendShelfToDOM(shelf) {
        const newShelf = document.createElement('div');
        newShelf.classList.add('shelf');

        const booksHTML = shelf.books.length > 0
            ? shelf.books.map(book => `<div class="book">${book.title} von ${book.author}</div>`).join('')
            : '<div class="book">+ Add book</div>';

        newShelf.innerHTML = `
        <h3>${shelf.name}</h3>
        <div class="bookshelf">
            ${booksHTML}
        </div>
        `;

        const shelvesContainer = document.getElementById('shelves');
        shelvesContainer.appendChild(newShelf);
    }

    function addShelf(shelfName) {
        const newShelf = {
            name: shelfName,
            books: []
        };

        const token = localStorage.getItem('authToken');

        if (!token) {
            alert('Sie müssen sich einloggen, um ein Regal hinzuzufügen.');
            return;
        }

        fetch("https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/shelves/newShelf", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(newShelf)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Fehler beim Hinzufügen des Regals: " + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                appendShelfToDOM(newShelf);
                alert("Regal erfolgreich hinzugefügt!");
            })
            .catch(error => {
                alert(error.message);
            });
    }

    // Funktion zum Ausloggen
    function logout() {
        const token = localStorage.getItem('authToken');

        if (!token) {
            alert("Kein gültiges Token gefunden.");
            return;
        }

        // API-Request zum Logout
        fetch("https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/users/logout", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}` // Sende das Token im Header
            }
        })
            .then(response => {
                if (response.ok) {
                    // Erfolgreich ausgeloggt, Token entfernen und zur Login-Seite weiterleiten
                    localStorage.removeItem('authToken');
                    window.location.href = 'AccountHandling.html';
                } else {
                    throw new Error("Fehler beim Ausloggen: " + response.statusText);
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }
});

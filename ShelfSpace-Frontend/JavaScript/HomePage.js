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
        // Eingabefenster für den Regalnamen
        const shelfName = prompt("Bitte geben Sie den Namen des neuen Regals ein:");
        if (shelfName) {
            addShelf(shelfName);
        }
    });

    document.getElementById("searchButton").addEventListener("click", function () {
        window.location.href = "SearchView.html"; // Verlinkt zur Suchseite
    });

    document.getElementById('backToAccountButton').addEventListener('click', function() {
        window.location.href = 'AccountHandling.html'; // Link zur Account-Seite
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
                // Alle Regale nacheinander hinzufügen
                shelves.forEach(shelf => {
                    appendShelfToDOM(shelf);
                });
            })
            .catch(error => {
                alert(error.message); // Fehlermeldung anzeigen
            });
    }

    // Funktion zum Darstellen eines Regals im DOM, inklusive der Bücher
    function appendShelfToDOM(shelf) {
        // Neues Shelf-Element erstellen
        const newShelf = document.createElement('div');
        newShelf.classList.add('shelf');

        // Bücher-HTML vorbereiten, falls vorhanden
        const booksHTML = shelf.books.length > 0
            ? shelf.books.map(book => `<div class="book">${book.title} von ${book.author}</div>`).join('')
            : '<div class="book">+ Add book</div>';

        // HTML-Struktur für das neue Regal, inklusive Bücher
        newShelf.innerHTML = `
        <h3>${shelf.name}</h3>
        <div class="bookshelf">
            ${booksHTML}  <!-- Bücher in das Regal einfügen -->
        </div>
        `;

        // Neues Regal in den DOM einfügen
        const shelvesContainer = document.getElementById('shelves');
        shelvesContainer.appendChild(newShelf);
    }

    // Funktion zum Hinzufügen eines neuen Regals (mit API-Call)
    function addShelf(shelfName) {
        // Neues Shelf-Objekt erstellen
        const newShelf = {
            name: shelfName,
            books: [] // Leeres Bücherregal beim Erstellen
        };

        // Abrufen des Tokens aus dem localStorage
        const token = localStorage.getItem('authToken');

        if (!token) {
            alert('Sie müssen sich einloggen, um ein Regal hinzuzufügen.');
            return;
        }

        // API-Request (POST) um ein neues Regal hinzuzufügen, Token wird im Header mitgesendet
        fetch("https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/shelves/newShelf", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`  // Token im Authorization Header
            },
            body: JSON.stringify(newShelf) // Das neue Regal in JSON umwandeln
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Fehler beim Hinzufügen des Regals: " + response.statusText);
                }
                return response.json(); // JSON-Antwort zurückgeben
            })
            .then(data => {
                // Erfolgreiches Hinzufügen, Regal in den DOM einfügen
                appendShelfToDOM(newShelf); // Hier das ursprüngliche newShelf Objekt verwenden
                alert("Regal erfolgreich hinzugefügt!");
            })
            .catch(error => {
                alert(error.message); // Fehlermeldung anzeigen
            });
    }
});

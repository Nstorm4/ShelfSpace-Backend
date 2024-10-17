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
        logout();
    });

    // Regale beim Laden der Seite abrufen
    loadShelves();

    // Funktion, um die Regale des Benutzers abzurufen und auf der Seite darzustellen
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

    // Funktion zum Hinzufügen eines neuen Regals ins DOM
    function appendShelfToDOM(shelf) {
        const newShelf = document.createElement('div');
        newShelf.classList.add('shelf');

        const booksHTML = shelf.books.length > 0
            ? shelf.books.map(book => `<div class="book"><img src="${book.coverUrl}" alt="Buchcover"><p>${book.title}</p><p>${book.author}</p></div>`).join('')
            : ''; // Entfernen des "(add a book)" Textes

        newShelf.innerHTML = `
            <h3>${shelf.name}</h3>
            <div class="bookshelf">
                ${booksHTML}
                <div class="add-book-button" onclick="addBookToShelf('${shelf.name}')">+ Add book</div>
            </div>
        `;

        const shelvesContainer = document.getElementById('shelves');
        shelvesContainer.appendChild(newShelf);
    }

    // Funktion, um ein neues Regal hinzuzufügen
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

    // Funktion zum Entfernen eines Regals
    function deleteShelf(shelfName) {
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('Sie müssen sich einloggen, um ein Regal zu entfernen.');
            return;
        }

        const shelfToDelete = { name: shelfName };

        fetch("https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/shelves/deleteShelf", {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(shelfToDelete)
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Fehler beim Löschen des Regals: " + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                alert(`Regal "${data.shelfName}" erfolgreich gelöscht!`);
                removeShelfFromDOM(shelfName);
            })
            .catch(error => {
                alert(error.message);
            });
    }

    // Funktion zum Entfernen eines Regals aus dem DOM
    function removeShelfFromDOM(shelfName) {
        const shelves = document.querySelectorAll('.shelf');
        shelves.forEach(shelf => {
            const shelfTitle = shelf.querySelector('h3').textContent;
            if (shelfTitle === shelfName) {
                shelf.remove();
            }
        });
    }


    // Funktion zum Ausloggen
    function logout() {
        const token = localStorage.getItem('authToken');

        if (!token) {
            alert("Kein gültiges Token gefunden.");
            return;
        }

        fetch("https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/users/logout", {
            method: "POST",
            headers: {
                "Authorization": `Bearer ${token}`
            }
        })
            .then(response => {
                if (response.ok) {
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

    // Initiales Laden der Regale zum Löschen
    document.getElementById('deleteShelfButton').addEventListener('click', function() {
        fetchAndDisplayShelves();
    });

    // Funktion zum Abrufen und Anzeigen der Regale für das Löschen
    function fetchAndDisplayShelves() {
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
                    throw new Error("Fehler beim Abrufen der Regale");
                }
                return response.json();
            })
            .then(shelves => {
                const shelfNames = shelves.map(shelf => shelf.name);
                const shelfToDelete = prompt("Wählen Sie ein Regal zum Löschen aus:\n" + shelfNames.join("\n"));
                if (shelfToDelete && shelfNames.includes(shelfToDelete)) {
                    deleteShelf(shelfToDelete);
                } else {
                    alert("Ungültiges Regal ausgewählt.");
                }
            })
            .catch(error => {
                alert(error.message);
            });
    }
});


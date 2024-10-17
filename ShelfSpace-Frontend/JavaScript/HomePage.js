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
            <div class="delete-shelf-button" onclick="deleteShelf('${shelf.name}')">- Delete shelf</div> <!-- Correct onclick -->
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

    // Funktion zum Hinzufügen eines neuen Buches ins Regal
    window.addBookToShelf = function(shelfName) {
        const title = prompt("Bitte geben Sie den Titel des Buches ein:");
        if (!title) {
            alert("Titel ist erforderlich!");
            return;
        }

        const author = prompt("Bitte geben Sie den Autor des Buches ein:");
        if (!author) {
            alert("Autor ist erforderlich!");
            return;
        }

        const coverUrl = prompt("Bitte geben Sie die URL für das Buchcover ein:");
        if (!coverUrl) {
            alert("Cover-URL ist erforderlich!");
            return;
        }

        const newBook = {title, author, coverUrl};
        addBook(shelfName, newBook)
    }

    function addBook(shelfName, newBook) {
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('Sie müssen sich einloggen, um ein Buch hinzuzufügen.');
            return;
        }

        fetch(`https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/shelves/addBook`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ shelfName, book: newBook })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Fehler beim Hinzufügen des Buches: " + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                appendBookToShelf(data.book, shelfName);
                alert("Buch erfolgreich hinzugefügt!");
            })
            .catch(error => {
                alert(error.message);
            });
    };


    // Funktion zum Hinzufügen eines Buches ins DOM
    function appendBookToShelf(book, shelfName) {
        const shelves = document.querySelectorAll('.shelf'); // Alle Regale abrufen

        // Durch alle Regale iterieren, um das richtige Regal zu finden
        shelves.forEach(shelf => {
            const shelfTitle = shelf.querySelector('h3').textContent;
            if (shelfTitle === shelfName) {
                const bookDiv = document.createElement('div');
                bookDiv.classList.add('book');

                // Buch wird jetzt mit Cover, Titel und Autor angezeigt
                bookDiv.innerHTML = `
                    <img src="${book.coverUrl}" alt="Buchcover">
                    <p>${book.title}</p>
                    <p>${book.author}</p>
                `;

                const bookshelf = shelf.querySelector('.bookshelf');
                bookshelf.appendChild(bookDiv);
            }
        });
    }

    // Funktion zum Entfernen eines Regals aus dem Backend und dem DOM
    window.deleteShelf = function(shelfName) { // <-- Hinzufügen zu window
        const token = localStorage.getItem('authToken');
        if (!token) {
            alert('Sie müssen sich einloggen, um ein Regal zu entfernen.');
            return;
        }

        fetch(`https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/shelves/deleteShelf`, {
            method: "DELETE",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({ name: shelfName })
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error("Fehler beim Löschen des Regals: " + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                removeShelfFromDOM(shelfName);
                alert(`Regal "${shelfName}" erfolgreich gelöscht!`);
            })
            .catch(error => {
                alert(error.message);
            });
    };


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
});

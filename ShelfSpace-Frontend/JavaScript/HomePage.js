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

    // Funktion zum Hinzufügen eines neuen Regals
    function addShelf(shelfName) {
        // Neues Shelf-Element erstellen
        const newShelf = document.createElement('div');
        newShelf.classList.add('shelf');

        // HTML-Struktur für das neue Regal
        newShelf.innerHTML = `
            <h3>${shelfName}</h3>
            <div class="bookshelf">
                <div class="book">+ Add book</div>
            </div>
        `;

        // Neues Regal in den DOM einfügen
        const shelvesContainer = document.getElementById('shelves');
        shelvesContainer.appendChild(newShelf);
    }
});

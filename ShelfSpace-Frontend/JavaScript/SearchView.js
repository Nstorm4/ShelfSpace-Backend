document.getElementById('searchButton').addEventListener('click', function () {
    const title = document.getElementById('titleInput').value;
    const url = `https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/books?title=${encodeURIComponent(title)}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Netzwerkantwort war nicht okay');
            }
            return response.json();
        })
        .then(data => {

            const resultsList = document.getElementById('resultsList');
            resultsList.innerHTML = ''; // Alte Ergebnisse löschen

            // Smooth transition: Inhalt nach oben schieben
            document.getElementById('centered-content').classList.add('hidden');

            // Zeige "Zurück"-Button mit smoother Transition
            setTimeout(() => {
                document.getElementById('backButton').classList.add('show');
                document.getElementById('backButton').style.display = 'inline-block';
            }, 500); // Warte, bis die Animation des Inhalts beendet ist

            // Smooth einblenden der Ergebnisse
            setTimeout(() => {
                // Google Books API liefert ein 'items'-Array
                const books = data.items || [];

                // Filtere Bücher ohne Titel heraus und beschränke die Anzeige auf maximal 10 Bücher
                const filteredBooks = books
                    .filter(item => item.volumeInfo && item.volumeInfo.title) // Filtere Bücher ohne Titel
                    .slice(0, 10); // Begrenze auf 10 Ergebnisse

                filteredBooks.forEach(item => {
                    const book = item.volumeInfo; // 'volumeInfo' enthält die Buchdetails in der Google Books API

                    const li = document.createElement('li');

                    // Thumbnail-Bild (wenn vorhanden)
                    const img = document.createElement('img');
                    img.src = (book.imageLinks && book.imageLinks.thumbnail) || 'https://via.placeholder.com/50';
                    img.alt = `${book.title} Cover`;

                    // Buchtitel
                    const titleText = document.createElement('strong');
                    titleText.textContent = `${book.title}`;

                    // Autoren (falls vorhanden)
                    let text = '';
                    if (book.authors) {
                        text += `, Autoren: ${book.authors.join(', ')}`;
                    } else {
                        text += ', Autoren: Unbekannt';
                    }

                    const textNode = document.createTextNode(text);

                    // Plus-Button
                    const addButton = document.createElement('button');
                    addButton.textContent = '+';
                    addButton.classList.add('add-button');

                    // Füge einen Event-Listener für den Plus-Button hinzu
                    addButton.addEventListener('click', (event) => {
                        event.stopPropagation(); // Verhindere, dass der Listeneintrag-Click das Modal öffnet
                        alert(`${book.title} wurde hinzugefügt!`);

                        // TODO:
                        // Weitere Logik zum Hinzufügen des Buchs zu einem Regal kann hier implementiert werden


                    });

                    // Elemente hinzufügen
                    li.appendChild(img);
                    li.appendChild(titleText);
                    li.appendChild(textNode);
                    li.appendChild(addButton); // Plus-Button hinzufügen

                    // **Füge hier den Event-Listener zum Öffnen des Modals hinzu**
                    li.addEventListener('click', function () {
                        openModal(book); // Öffne Modal mit den Buchinformationen
                    });

                    resultsList.appendChild(li);
                });

                resultsList.classList.add('show'); // Ergebnisse langsam einblenden
            }, 600); // Warte, bis "Zurück"-Button eingeblendet ist
        })
        .catch(error => {
            console.error('Fehler:', error);
        });
});

// "Zurück"-Button Funktionalität
document.getElementById('backButton').addEventListener('click', function () {
    // Smooth zurück zur Suchansicht
    document.getElementById('centered-content').classList.remove('hidden');
    document.getElementById('backButton').classList.remove('show'); // Button ausblenden
    document.getElementById('resultsList').classList.remove('show'); // Ergebnisse ausblenden

    setTimeout(() => {
        document.getElementById('resultsList').innerHTML = ''; // Ergebnisse löschen
        document.getElementById('backButton').style.display = 'none'; // Button verstecken
    }, 500); // Warte, bis die Transition abgeschlossen ist

    // Such-Button und Eingabefeld wieder anzeigen
    setTimeout(() => {
        document.getElementById('searchButton').style.display = 'inline-block';
        document.getElementById('titleInput').style.display = 'inline-block';
    }, 500); // Zeige sie nach der Animation wieder
});

document.getElementById('backToShelfspace').addEventListener('click', function() {
    window.location.href = 'OverviewPage.html'; // Link zur Account-Seite
});
// Öffnet das Modal mit den Buchdetails
function openModal(bookInfo) {
    document.getElementById('modalTitle').textContent = bookInfo.title || 'Kein Titel';
    document.getElementById('modalAuthors').textContent = bookInfo.authors ? bookInfo.authors.join(', ') : 'Keine Autorenangabe';
    document.getElementById('modalPublishedDate').textContent = bookInfo.publishedDate || 'Keine Angaben';
    document.getElementById('modalPageCount').textContent = bookInfo.pageCount ? bookInfo.pageCount + ' Seiten' : 'Keine Angaben';
    document.getElementById('modalDescription').textContent = bookInfo.description || 'Keine Beschreibung verfügbar';
    document.getElementById('modalCover').src = bookInfo.imageLinks ? bookInfo.imageLinks.thumbnail : 'https://via.placeholder.com/100';

    document.getElementById('bookModal').style.display = 'flex';
    document.body.classList.add('modal-open'); // Verhindert Scrollen des Bodys
}

// Schließt das Modal
function closeModal() {
    document.getElementById('bookModal').style.display = 'none';
    document.body.classList.remove('modal-open'); // Erlaubt wieder das Scrollen
}

// Klick auf Modal-Hintergrund schließt das Modal
window.onclick = function(event) {
    const modal = document.getElementById('bookModal');
    if (event.target == modal) {
        modal.style.display = 'none';
        document.body.classList.remove('modal-open'); // Erlaubt wieder das Scrollen
    }
}

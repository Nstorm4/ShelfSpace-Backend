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
                data.forEach(book => {
                    const li = document.createElement('li');

                    const img = document.createElement('img');
                    img.src = book.thumbnail || 'https://via.placeholder.com/50';
                    img.alt = `${book.title} Cover`;

                    const titleText = document.createElement('strong');
                    titleText.textContent = `${book.title}`;

                    let text = '';
                    if (book.authors) {
                        text += `, Autoren: ${book.authors.join(', ')}`;
                    } else {
                        text += ', Autoren: Unbekannt';
                    }

                    const textNode = document.createTextNode(text);

                    li.appendChild(img);
                    li.appendChild(titleText);
                    li.appendChild(textNode);

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
document.getElementById('backToAccountButton').addEventListener('click', function() {
    window.location.href = 'AccountHandling.html'; // Link zur Account-Seite
});
document.getElementById('backButton').classList.add('show');
document.getElementById('backToAccountButton').classList.add('show');


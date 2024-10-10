document.addEventListener('DOMContentLoaded', function() {
    document.getElementById('searchButton').addEventListener('click', performSearch);
    document.getElementById('backButton').addEventListener('click', goBackToSearch);
    document.getElementById('backToShelfspace').addEventListener('click', goBackToHomePage);
    document.getElementById('titleInput').addEventListener('keydown', handleEnterKey);
});

function performSearch() {
    const title = document.getElementById('titleInput').value;
    const url = `https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/books/search?title=${encodeURIComponent(title)}`;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error('Netzwerkantwort war nicht okay');
            }
            return response.json();
        })
        .then(books => {
            displaySearchResults(books);
        })
        .catch(error => {
            console.error('Fehler:', error);
            alert('Es gab einen Fehler bei der Suche. Bitte versuchen Sie es später erneut.');
        });
}

function displaySearchResults(books) {
    const resultsList = document.getElementById('resultsList');
    resultsList.innerHTML = '';

    document.getElementById('centered-content').classList.add('hidden');

    setTimeout(() => {
        document.getElementById('backButton').classList.add('show');
        document.getElementById('backButton').style.display = 'inline-block';
    }, 500);

    setTimeout(() => {
        const filteredBooks = books.slice(0, 10);

        filteredBooks.forEach(book => {
            const li = createBookListItem(book);
            resultsList.appendChild(li);
        });

        resultsList.classList.add('show');
    }, 600);
}

function createBookListItem(book) {
    const li = document.createElement('li');

    const img = document.createElement('img');
    img.src = book.thumbnailUrl || 'https://via.placeholder.com/50';
    img.alt = `${book.title} Cover`;

    const titleText = document.createElement('strong');
    titleText.textContent = book.title;

    let text = book.authors ? `, Autoren: ${book.authors.join(', ')}` : ', Autoren: Unbekannt';
    const textNode = document.createTextNode(text);

    const addButton = document.createElement('button');
    addButton.textContent = '+ add to shelf';
    addButton.classList.add('add-button');

    addButton.addEventListener('click', (event) => {
        event.stopPropagation();
        addBookToShelf(book);
    });

    li.appendChild(img);
    li.appendChild(titleText);
    li.appendChild(textNode);
    li.appendChild(addButton);

    li.addEventListener('click', function () {
        openModal(book);
    });

    return li;
}

function addBookToShelf(book) {
    fetch('https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/books/bookshelves')
        .then(response => response.json())
        .then(bookshelves => {
            const shelfName = prompt(`Zu welchem Regal möchten Sie das Buch hinzufügen?\n${bookshelves.map(shelf => shelf.name).join(', ')}`);
            if (shelfName) {
                return fetch('https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/books/bookshelf/addBook', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        shelfName: shelfName,
                        bookId: book.id
                    })
                });
            }
        })
        .then(response => {
            if (response && response.ok) {
                return response.json();
            } else {
                throw new Error('Failed to add book to shelf');
            }
        })
        .then(() => {
            alert(`${book.title} wurde zum Regal hinzugefügt!`);
        })
        .catch(error => {
            console.error('Fehler beim Hinzufügen des Buchs:', error);
            alert('Fehler beim Hinzufügen des Buchs zum Regal.');
        });
}

function openModal(book) {
    document.getElementById('modalTitle').textContent = book.title || 'Kein Titel';
    document.getElementById('modalAuthors').textContent = book.authors ? book.authors.join(', ') : 'Keine Autorenangabe';
    document.getElementById('modalPublishedDate').textContent = 'Keine Angaben';
    document.getElementById('modalPageCount').textContent = 'Keine Angaben';
    document.getElementById('modalDescription').textContent = book.description || 'Keine Beschreibung verfügbar';
    document.getElementById('modalCover').src = book.thumbnailUrl || 'https://via.placeholder.com/100';

    document.getElementById('bookModal').style.display = 'flex';
    document.body.classList.add('modal-open');
}

function closeModal() {
    document.getElementById('bookModal').style.display = 'none';
    document.body.classList.remove('modal-open');
}

function goBackToSearch() {
    document.getElementById('centered-content').classList.remove('hidden');
    document.getElementById('backButton').classList.remove('show');
    document.getElementById('resultsList').classList.remove('show');

    setTimeout(() => {
        document.getElementById('resultsList').innerHTML = '';
        document.getElementById('backButton').style.display = 'none';
    }, 500);

    setTimeout(() => {
        document.getElementById('searchButton').style.display = 'inline-block';
        document.getElementById('titleInput').style.display = 'inline-block';
    }, 500);
}

function goBackToHomePage() {
    window.location.href = 'HomePage.html';
}

function handleEnterKey(event) {
    if (event.key === 'Enter') {
        document.getElementById('searchButton').click();
    }
}

// Klick auf Modal-Hintergrund schließt das Modal
window.onclick = function(event) {
    const modal = document.getElementById('bookModal');
    if (event.target == modal) {
        closeModal();
    }
};
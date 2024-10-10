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

    // Load and display bookshelves
    loadBookshelves();

    // Button-Verlinkungen
    document.getElementById("addShelfButton").addEventListener("click", function () {
        const shelfName = prompt("Geben Sie einen Namen für das neue Regal ein:");
        if (shelfName) {
            createBookshelf(shelfName);
        }
    });

    document.getElementById("searchButton").addEventListener("click", function () {
        window.location.href = "SearchView.html";
    });

    document.getElementById('backToAccountButton').addEventListener('click', function() {
        window.location.href = 'AccountHandling.html';
    });
});

function loadBookshelves() {
    fetch('https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/books/bookshelves')
        .then(response => response.json())
        .then(bookshelves => {
            const shelvesContainer = document.getElementById('shelves');
            shelvesContainer.innerHTML = '';

            bookshelves.forEach(bookshelf => {
                const shelfElement = createShelfElement(bookshelf);
                shelvesContainer.appendChild(shelfElement);
            });
        })
        .catch(error => console.error('Error loading bookshelves:', error));
}

function createShelfElement(bookshelf) {
    const shelfDiv = document.createElement('div');
    shelfDiv.className = 'shelf';

    const titleH3 = document.createElement('h3');
    titleH3.textContent = bookshelf.name;
    shelfDiv.appendChild(titleH3);

    const bookshelfDiv = document.createElement('div');
    bookshelfDiv.className = 'bookshelf';

    bookshelf.books.forEach(book => {
        const bookDiv = document.createElement('div');
        bookDiv.className = 'book';
        bookDiv.textContent = book.title;
        bookshelfDiv.appendChild(bookDiv);
    });

    const addBookDiv = document.createElement('div');
    addBookDiv.className = 'book';
    addBookDiv.textContent = '+ Add book';
    addBookDiv.addEventListener('click', () => {
        window.location.href = 'SearchView.html';
    });
    bookshelfDiv.appendChild(addBookDiv);

    shelfDiv.appendChild(bookshelfDiv);
    return shelfDiv;
}

function createBookshelf(name) {
    fetch('https://shelfspacebackend-happy-gecko-kb.apps.01.cf.eu01.stackit.cloud/api/books/bookshelf', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({ name: name, bookIds: [] })
    })
    .then(response => response.json())
    .then(() => {
        loadBookshelves();
    })
    .catch(error => console.error('Error creating bookshelf:', error));
}
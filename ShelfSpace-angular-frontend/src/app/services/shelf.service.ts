import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class ShelfService {
  private shelves: any[] = [
    { name: 'Fiction', books: [] },
    { name: 'Non-Fiction', books: [] },
  ];

  getShelves(): Observable<any[]> {
    return of(this.shelves);
  }

  addShelf(shelfName: string): Observable<void> {
    console.log('Adding shelf:', shelfName);
    this.shelves.push({ name: shelfName, books: [] });
    return of();
  }

  addBook(shelfName: string, book: any): Observable<void> {
    const shelf = this.shelves.find((s) => s.name === shelfName);
    if (shelf) {
      shelf.books.push(book);
    }
    return of();
  }

  deleteShelf(shelfName: string): Observable<void> {
    this.shelves = this.shelves.filter((s) => s.name !== shelfName);
    return of();
  }

  deleteBook(shelfName: string, book: any): Observable<void> {
    const shelf = this.shelves.find((s) => s.name === shelfName);
    if (shelf) {
      shelf.books = shelf.books.filter((b: any) => b !== book);
    }
    return of();
  }

  logout(): Observable<void> {
    // Simulate a logout operation
    return of();
  }
}

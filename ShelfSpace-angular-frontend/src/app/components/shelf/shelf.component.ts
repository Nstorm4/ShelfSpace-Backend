// src/app/components/shelf/shelf.component.ts
import { Component, OnInit } from '@angular/core';
import { ShelfService } from '../../services/shelf.service';

@Component({
  selector: 'app-shelf',
  templateUrl: './shelf.component.html',
  styleUrls: ['./shelf.component.css']
})
export class ShelfComponent implements OnInit {
  shelves: any[] = [];
  newShelfName: string = '';

  constructor(private shelfService: ShelfService) {}

  ngOnInit(): void {
    this.loadShelves();
  }

  loadShelves(): void {
    this.shelfService.getShelves().subscribe((shelves) => {
      this.shelves = shelves;
    });
  }

  addShelf(): void {
    console.log('Add Shelf button clicked');
    console.log('Shelf Name:', this.newShelfName);
    if (this.newShelfName.trim()) {
      this.shelfService.addShelf(this.newShelfName).subscribe(() => {
        console.log('Shelf added successfully');
        this.loadShelves();
        this.newShelfName = ''; // Clear the input after adding
      });
    }
  }

  addBook(shelfName: string): void {
    const title = prompt('Enter the book title:');
    const author = prompt('Enter the book author:');
    const coverUrl = prompt('Enter the book cover URL:');
    if (title && author && coverUrl) {
      const newBook = { title, author, coverUrl };
      this.shelfService.addBook(shelfName, newBook).subscribe(
        () => this.loadShelves(),
        (error) => alert(error.message)
      );
    }
  }

  deleteShelf(shelfName: string): void {
    this.shelfService.deleteShelf(shelfName).subscribe(
      () => this.loadShelves(),
      (error) => alert(error.message)
    );
  }

  deleteBook(shelfName: string, book: any): void {
    this.shelfService.deleteBook(shelfName, book).subscribe(
      () => this.loadShelves(),
      (error) => alert(error.message)
    );
  }

  logout(): void {
    this.shelfService.logout().subscribe(
      () => {
        localStorage.removeItem('authToken');
        window.location.href = 'AccountHandling.html';
      },
      (error) => alert(error.message)
    );
  }
}

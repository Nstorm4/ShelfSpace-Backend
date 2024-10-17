import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms'; // Import FormsModule

import { AppComponent } from './app.component';
import { ShelfComponent } from './components/shelf/shelf.component';

@NgModule({
  declarations: [
    ShelfComponent,
    // ... other components ...
  ],
  imports: [
    BrowserModule,
    FormsModule, // Add FormsModule here
    // ... other modules ...
  ],
  providers: []
})
export class AppModule { }

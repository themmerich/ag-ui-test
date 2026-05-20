import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { SupplementTable } from './supplement/supplement-table';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SupplementTable],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('frontend');
}

import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ChatDrawer } from './chat/chat-drawer';
import { SupplementTable } from './supplement/supplement-table';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, SupplementTable, ChatDrawer],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('frontend');
}

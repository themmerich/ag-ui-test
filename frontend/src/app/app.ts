import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { ChatDrawer } from './chat/chat-drawer';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ChatDrawer],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {
  protected readonly title = signal('Supplement Übersicht');
}

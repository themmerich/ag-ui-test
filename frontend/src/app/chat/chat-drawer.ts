import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { agUiResource } from '@internal/ag-ui-client';
import { MarkdownComponent } from 'ngx-markdown';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';
import { TextareaModule } from 'primeng/textarea';

/** Right-side chat sidebar (PrimeNG p-drawer) for a multi-turn chat with the AG-UI agent. */
@Component({
  selector: 'app-chat-drawer',
  imports: [FormsModule, DrawerModule, ButtonModule, TextareaModule, MarkdownComponent],
  templateUrl: './chat-drawer.html',
  styleUrl: './chat-drawer.scss',
})
export class ChatDrawer {
  protected readonly visible = signal(false);
  protected readonly draft = signal('');

  /** AG-UI agent (Manfred Steyer lib) — multi-turn chat endpoint. */
  protected readonly chat = agUiResource({
    url: 'http://localhost:8080/api/agui/chat',
    tools: [],
  });

  protected open(): void {
    this.visible.set(true);
  }

  protected send(): void {
    const text = this.draft().trim();
    if (!text || this.chat.isLoading()) {
      return;
    }
    this.chat.sendMessage({ role: 'user', content: text });
    this.draft.set('');
  }
}

import { Component, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { agUiResource } from '@internal/ag-ui-client';
import { MarkdownComponent } from 'ngx-markdown';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';
import { TextareaModule } from 'primeng/textarea';
import { openSupplementFormTool } from '../supplement/tools/open-supplement-form.tool';

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

  /** AG-UI agent (Manfred Steyer lib) — multi-turn chat endpoint with the client tool. */
  protected readonly chat = agUiResource({
    url: 'http://localhost:8080/api/agui/chat',
    tools: [openSupplementFormTool],
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

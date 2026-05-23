import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { agUiResource } from '@internal/ag-ui-client';
import { ButtonModule } from 'primeng/button';
import { TableModule } from 'primeng/table';
import { ToolbarModule } from 'primeng/toolbar';
import { SupplementRow } from './supplement.model';
import { SupplementService } from './supplement.service';

@Component({
  selector: 'app-supplement-table',
  imports: [TableModule, ButtonModule, ToolbarModule, DatePipe, RouterLink],
  templateUrl: './supplement-table.html',
  styleUrl: './supplement-table.scss',
})
export class SupplementTable implements OnInit {
  private readonly supplementService = inject(SupplementService);

  protected readonly days = signal<string[]>([]);
  protected readonly rows = signal<SupplementRow[]>([]);

  /** AG-UI agent (Manfred Steyer lib) talking to the backend SSE endpoint. */
  protected readonly chat = agUiResource({
    url: 'http://localhost:8080/api/agui/analyze',
    tools: [],
  });

  ngOnInit(): void {
    this.supplementService.getTracking().subscribe((tracking) => {
      this.days.set(tracking.days);
      this.rows.set(tracking.rows);
    });
  }

  protected analyze(): void {
    this.chat.sendMessage({ role: 'user', content: this.buildPrompt() });
  }

  /** Turns the current table into a readable prompt for the model. */
  private buildPrompt(): string {
    const days = this.days();
    const lines = this.rows().map((row) => {
      const marks = row.taken
        .map((taken, i) => `${days[i]}: ${taken ? 'genommen' : 'nicht genommen'}`)
        .join('; ');
      return `- ${row.supplement}: ${marks}`;
    });
    return `Analysiere meine Supplement-Einnahme der letzten ${days.length} Tage.\n${lines.join('\n')}`;
  }
}

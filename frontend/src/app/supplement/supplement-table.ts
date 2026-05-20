import { DatePipe } from '@angular/common';
import { Component, OnInit, inject, signal } from '@angular/core';
import { TableModule } from 'primeng/table';
import { SupplementRow } from './supplement.model';
import { SupplementService } from './supplement.service';

@Component({
  selector: 'app-supplement-table',
  imports: [TableModule, DatePipe],
  templateUrl: './supplement-table.html',
  styleUrl: './supplement-table.scss',
})
export class SupplementTable implements OnInit {
  private readonly supplementService = inject(SupplementService);

  protected readonly days = signal<string[]>([]);
  protected readonly rows = signal<SupplementRow[]>([]);

  ngOnInit(): void {
    this.supplementService.getTracking().subscribe((tracking) => {
      this.days.set(tracking.days);
      this.rows.set(tracking.rows);
    });
  }
}

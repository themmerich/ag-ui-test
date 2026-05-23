import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { DatePickerModule } from 'primeng/datepicker';
import { SupplementIntake } from './supplement.model';
import { SupplementService } from './supplement.service';

/** Form page to record/update the supplement intake for a chosen day. */
@Component({
  selector: 'app-supplement-form',
  imports: [FormsModule, DatePickerModule, CheckboxModule, ButtonModule, RouterLink],
  templateUrl: './supplement-form.html',
  styleUrl: './supplement-form.scss',
})
export class SupplementForm implements OnInit {
  private readonly service = inject(SupplementService);
  private readonly router = inject(Router);

  protected readonly date = signal<Date>(new Date());
  protected readonly intakes = signal<SupplementIntake[]>([]);
  protected readonly existing = signal(false);
  protected readonly saving = signal(false);

  ngOnInit(): void {
    this.load(this.date());
  }

  protected onDate(date: Date | null): void {
    if (!date) {
      return;
    }
    this.date.set(date);
    this.load(date);
  }

  protected setTaken(supplement: string, taken: boolean): void {
    this.intakes.update((list) =>
      list.map((entry) => (entry.supplement === supplement ? { ...entry, taken } : entry)),
    );
  }

  protected save(): void {
    this.saving.set(true);
    this.service.saveDay(this.iso(this.date()), this.intakes()).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => this.saving.set(false),
    });
  }

  private load(date: Date): void {
    this.service.getDay(this.iso(date)).subscribe((day) => {
      this.intakes.set(day.supplements);
      this.existing.set(day.existing);
    });
  }

  /** Local yyyy-MM-dd (avoids the UTC shift of toISOString). */
  private iso(date: Date): string {
    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.padStart(2, '0');
    const day = `${date.getDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}

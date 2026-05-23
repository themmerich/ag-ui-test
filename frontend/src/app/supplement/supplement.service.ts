import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SupplementDay, SupplementIntake, SupplementTracking } from './supplement.model';

/** Reads and edits supplement tracking data via the backend REST API. */
@Injectable({ providedIn: 'root' })
export class SupplementService {
  // Absolute dev URL; relies on the backend CORS config. Move to environment files for prod.
  private readonly baseUrl = 'http://localhost:8080/api/supplements';

  private readonly http = inject(HttpClient);

  getTracking(): Observable<SupplementTracking> {
    return this.http.get<SupplementTracking>(`${this.baseUrl}/tracking`);
  }

  /** Intake for a single day (ISO date, e.g. "2026-05-23"). */
  getDay(date: string): Observable<SupplementDay> {
    return this.http.get<SupplementDay>(`${this.baseUrl}/day/${date}`);
  }

  /** Creates or updates the intake for a single day. */
  saveDay(date: string, supplements: SupplementIntake[]): Observable<SupplementDay> {
    return this.http.put<SupplementDay>(`${this.baseUrl}/day/${date}`, { supplements });
  }
}

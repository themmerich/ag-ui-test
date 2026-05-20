import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SupplementTracking } from './supplement.model';

/** Reads supplement tracking data from the backend REST API. */
@Injectable({ providedIn: 'root' })
export class SupplementService {
  // Absolute dev URL; relies on the backend CORS config. Move to environment files for prod.
  private readonly baseUrl = 'http://localhost:8080/api/supplements';

  private readonly http = inject(HttpClient);

  getTracking(): Observable<SupplementTracking> {
    return this.http.get<SupplementTracking>(`${this.baseUrl}/tracking`);
  }
}

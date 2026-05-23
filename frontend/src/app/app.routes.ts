import { Routes } from '@angular/router';
import { SupplementForm } from './supplement/supplement-form';
import { SupplementTable } from './supplement/supplement-table';

export const routes: Routes = [
  { path: '', component: SupplementTable },
  { path: 'supplements/new', component: SupplementForm },
];

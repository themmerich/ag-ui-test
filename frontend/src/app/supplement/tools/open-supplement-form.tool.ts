import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { defineAgUiTool } from '@internal/ag-ui-client';
import { z } from 'zod';

/**
 * Client-side AG-UI tool: lets the agent open the supplement entry form for a given date.
 * Runs in the lib's injection context, so `inject(Router)` works inside `execute`.
 */
export const openSupplementFormTool = defineAgUiTool({
  name: 'openSupplementForm',
  description:
    'Öffnet das Formular zum Erfassen/Bearbeiten der Supplement-Einnahme für einen bestimmten Tag. ' +
    'Aufrufen, wenn der Nutzer Einnahmedaten für ein Datum eintragen oder ändern möchte.',
  schema: z.object({
    date: z.string().describe('Datum im Format yyyy-MM-dd, z. B. 2026-05-15'),
  }),
  followUpAfterExecution: false,
  execute: ({ date }) => {
    void inject(Router).navigate(['/supplements/new'], { queryParams: { date } });
  },
});

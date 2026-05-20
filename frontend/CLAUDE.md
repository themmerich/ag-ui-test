# CLAUDE.md — Frontend

Angular-Frontend mit PrimeNG, Dev-Server auf **:4200**.

## Stack
- **Angular 21** (Standalone-Komponenten, Signals, neue Control-Flow-Syntax `@if`/`@for`)
- **PrimeNG 21** mit **`@primeuix/themes`** (Preset **Aura**) + `primeicons`
  - ⚠️ `@primeng/themes` ist **deprecated** — Theme-Presets kommen aus `@primeuix/themes`
    (`import Aura from '@primeuix/themes/aura'`). PrimeNG-Setup in `src/app/app.config.ts`
    (`providePrimeNG` + `provideAnimationsAsync`).
- HttpClient aktiv via `provideHttpClient()`

## Befehle (im `frontend/`-Verzeichnis)
```powershell
npm start            # Dev-Server (ng serve) auf :4200
npm run build        # Production-Build
npm test             # Unit-Tests (Vitest via @angular/build) – watch-Modus
npx ng test --watch=false   # Tests einmalig
npm run lint         # ESLint
npm run format       # Prettier --write (src/)
npm run format:check # Prettier --check
```

## Backend-Anbindung
- Services rufen das Backend per absoluter Dev-URL `http://localhost:8080/api/...` auf
  (siehe `src/app/supplement/supplement.service.ts`). Nutzt das Backend-CORS; kein Dev-Proxy.
- Für die Anzeige muss das Backend (`:8080`) laufen.

## Lint & Format
- **ESLint**: Flat-Config `eslint.config.js` (`typescript-eslint` + `angular-eslint`).
  `eslint-config-prettier` steht **als letzter Eintrag** → keine Formatierungs-Konflikte mit Prettier.
- **Prettier**: Config in `package.json` (`printWidth: 100`, `singleQuote: true`, HTML-Parser für
  Templates). Nach Code-Änderungen `npm run lint` und `npm run format:check` grün halten.

## Konventionen
- Dateinamen/Klassen **ohne** `Component`-Suffix (z. B. `app.ts` → `App`,
  `supplement-table.ts` → `SupplementTable`). Templates/Styles als separate `.html`/`.scss`.
- Dependency Injection via `inject()`; State als `signal()`.
- Ein Feature je Ordner unter `src/app/<feature>/` (Model + Service + Komponente),
  Beispiel `src/app/supplement/`.
- PrimeNG-Module pro Komponente in `imports` einbinden (z. B. `TableModule` aus `primeng/table`).
- Bei großen PrimeNG-Modulen kann das Initial-Bundle-Budget in `angular.json` angepasst werden müssen.

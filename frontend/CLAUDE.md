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

## Seiten / Routing
- `app.routes.ts`: `''` → `supplement/supplement-table` (Übersicht: Tabelle + „Analysieren"),
  `supplements/new` → `supplement/supplement-form` (Tag erfassen: Datepicker + Checkbox je Supplement).
  Shell (`app.html`) = `h1` + `<router-outlet/>` + `<app-chat-drawer/>`.
- Daten-Service `supplement/supplement.service.ts`: `getTracking()`, `getDay(date)`, `saveDay(date, …)`.
- Genutzte PrimeNG-Komponenten: `p-table`, `p-toolbar`, `p-button`, `p-drawer` (Chat),
  `p-datepicker` + `p-checkbox` (Formular).

## AG-UI (KI-Features)
- **Vendored Lib** `libs/ag-ui-client` (aus Manfred Steyers Artikelserie) — kapselt `@ag-ui/client`
  (`HttpAgent`). Import über den tsconfig-Pfad **`@internal/ag-ui-client`** (`tsconfig.json` → `paths`,
  `baseUrl: "."`). Die Lib wird **unverändert** gepflegt → siehe „Lint & Format".
- Nutzung: `chat = agUiResource({ url, tools: [] })`, dann `chat.sendMessage({ role:'user', content })`;
  `chat.value()` (Signal) liefert die Messages, `chat.isLoading()` den Status. Zwei Stellen:
  „Analysieren"-Button (`supplement/supplement-table.ts` → `/api/agui/analyze`) und Chat-Sidebar
  (`chat/chat-drawer.ts` → `/api/agui/chat`, PrimeNG `p-drawer`).
- Assistant-Text wird als **Markdown** gerendert (`provideMarkdown()` in `app.config.ts`).
  `message.toolCalls` wird im Chat als ausgegraute „Tool Call: …"-Zeile angezeigt.
- Zusatz-Deps der Lib: `@ag-ui/client`, `ngx-markdown`, `@modelcontextprotocol/sdk` + `ext-apps`,
  `zod`, `marked`. Die Markdown-/MCP-Teile werden für die Text-Analyse nur kompiliert, nicht genutzt
  (für spätere Artikel-Features drin).
- **`.npmrc` `legacy-peer-deps=true`** ist nötig: Angular CLI 21 bringt MCP-SDK 1.26 mit, `ext-apps`
  will `^1.29` → ohne den Eintrag scheitert `npm install` am Peer-Konflikt.

## Lint & Format
- **ESLint**: Flat-Config `eslint.config.js` (`typescript-eslint` + `angular-eslint`).
  `eslint-config-prettier` steht **als letzter Eintrag** → keine Formatierungs-Konflikte mit Prettier.
- **Prettier**: Config in `package.json` (`printWidth: 100`, `singleQuote: true`, HTML-Parser für
  Templates). Nach Code-Änderungen `npm run lint` und `npm run format:check` grün halten.
- Das vendored **`libs/**`** ist von ESLint (`eslint.config.js` → `ignores`) und Prettier
  (`.prettierignore`) **ausgenommen** — nicht linten/formatieren.

## Konventionen
- Dateinamen/Klassen **ohne** `Component`-Suffix (z. B. `app.ts` → `App`,
  `supplement-table.ts` → `SupplementTable`). Templates/Styles als separate `.html`/`.scss`.
- Dependency Injection via `inject()`; State als `signal()`.
- Ein Feature je Ordner unter `src/app/<feature>/` (Model + Service + Komponente),
  Beispiel `src/app/supplement/`.
- PrimeNG-Module pro Komponente in `imports` einbinden (z. B. `TableModule` aus `primeng/table`).
- Bei großen PrimeNG-Modulen kann das Initial-Bundle-Budget in `angular.json` angepasst werden müssen.

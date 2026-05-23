# CLAUDE.md — ag-ui-test (Monorepo)

Monorepo mit einem **Angular-Frontend** (`frontend/`) und einem **Spring-Boot-Backend** (`backend/`).
Subprojekt-spezifische Hinweise stehen in `frontend/CLAUDE.md` und `backend/CLAUDE.md`.

## Struktur
```
ag-ui-test/
├── backend/    # Spring Boot 4 (Gradle, Kotlin DSL), läuft auf :8080  → siehe backend/CLAUDE.md
└── frontend/   # Angular 21 + PrimeNG, Dev-Server auf :4200           → siehe frontend/CLAUDE.md
```
Es gibt **keinen** Build im Repo-Root. Der Gradle-Projekt-Root ist `backend/`; IntelliJ muss
`backend/build.gradle.kts` als Gradle-Projekt verknüpft bekommen (Repo-Root allein wird nicht als
Spring-Boot-App erkannt).

## Frontend ↔ Backend
- Das Frontend (`:4200`) ruft das Backend (`:8080`) per absoluter URL auf (kein Dev-Proxy).
- CORS ist backend-seitig konfiguriert (`backend/.../config/WebConfig.java`, Property
  `app.cors.allowed-origins`).
- Für End-to-End müssen **beide** Server laufen.

## KI-Features (AG-UI)
KI läuft über den **AG-UI**-Standard: Das Backend streamt AG-UI-Events als SSE (Spring AI Anthropic),
das Frontend konsumiert sie über die vendored Lib `frontend/libs/ag-ui-client` (`agUiResource`).
Endpoints: `POST /api/agui/analyze` (einmalige Tabellen-Analyse, „Analysieren"-Button) und
`POST /api/agui/chat` (Chat-Sidebar, Multi-Turn). Der Chat-Agent hat ein Server-Tool, mit dem er die
Einnahmedaten nachschlägt; Tool-Aufrufe werden im Chat angezeigt. Details in den Subprojekt-Docs.
Braucht `ANTHROPIC_API_KEY` zur Laufzeit.

## Git-Workflow
- `main` ist der Haupt-Branch. Pro Aufgabe ein `feature/<name>`-Branch von `main`.
- Ablauf: Branch → committen → pushen → **PR gegen `main`**. Der Nutzer reviewt und merged selbst
  (von Hand). **Keine** Commits/PRs ohne explizite Ansage des Nutzers; nichts selbst mergen.
- Commit-/PR-Beschreibungen sinnvoll und prägnant halten.

## Konventionen
- `.gitignore` ist dreistufig (Root + `backend/` + `frontend/`). Root ignoriert OS-/IDE-Dateien,
  `.idea/` und `.claude/`.
- Zeilenenden sind via Root-`.gitattributes` auf **LF** normalisiert (`* text=auto eol=lf`,
  `*.bat`/`*.cmd` → CRLF). Das verhindert CRLF/LF-Churn auf Windows unabhängig von `core.autocrlf`.

# CLAUDE.md — Backend

Spring-Boot-Backend, läuft als Servlet/MVC-App auf **:8080**.

## Stack
- **Spring Boot 4.0.6**, **Java 25**, **Gradle** (Kotlin DSL, `build.gradle.kts`)
- Basis-Package `de.primeux.demo`
- Dependencies: `spring-boot-starter-webmvc` **und** `-webflux` (bei beiden gewinnt MVC/Servlet),
  `-data-jpa`, PostgreSQL-Treiber, `spring-ai-starter-model-anthropic`, Lombok, DevTools

## Befehle (im `backend/`-Verzeichnis)
```powershell
.\gradlew.bat bootRun          # App starten (:8080)
.\gradlew.bat test             # Tests
.\gradlew.bat build            # Build inkl. Tests
.\gradlew.bat compileJava      # nur kompilieren
```
In IntelliJ: `DemoApplication.main()` per grünem ▶ starten (Spring-Boot-Run-Config gibt es nur in
Ultimate; in Community läuft die `main()` als normale Java-App).

## Wichtig: keine Datenbank konfiguriert
JPA + PostgreSQL sind im Classpath, aber es ist **keine Datasource** eingerichtet. Damit die App (und
`@SpringBootTest`) booten, ist die DB-Auto-Config in `application.properties` deaktiviert:
```properties
spring.autoconfigure.exclude=org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration
```
(Spring Boot 4 hat diese Auto-Config-Klassen in eigene Module/Packages verschoben.) Sobald eine echte
DB angebunden wird, diesen Exclude entfernen und Datasource-Properties ergänzen.

## REST-API
**Supplements** (`de.primeux.demo.supplement`)
- `GET /api/supplements/tracking?days=N` → Tracking-Matrix der letzten N Tage (Default 7, clamp 1–30).
- `GET /api/supplements/day/{date}` → Einnahme eines Tages (`existing=false` + alle `taken=false`, wenn
  noch nichts gespeichert).
- `PUT /api/supplements/day/{date}` → Tag anlegen/aktualisieren.
- Daten in einem **mutierbaren In-Memory-Store** (`SupplementService`: beim Start mit 30 Tagen
  deterministisch geseedet; PUT überschreibt einen Tag; resettet bei Neustart, da keine DB).

**AG-UI** (`de.primeux.demo.agui`, alle `text/event-stream`)
- `POST /api/agui/analyze` → einmalige Analyse der Tabellendaten.
- `POST /api/agui/chat` → Multi-Turn-Chat (`ChatService` mappt den vollen Verlauf auf Spring-AI-Messages).
- Events (`RUN_STARTED` → `TEXT_MESSAGE_*` → `RUN_FINISHED`, sonst `RUN_ERROR`) werden „von Hand" gebaut
  (`AgUiEvents`/`AgUiStreamer`), keine AG-UI-Java-Lib. `RunAgentInput` ignoriert unbekannte Felder.
- **Tool-Calling**: `SupplementQueryService` (`@Tool getSupplementIntake(days)`) ist im Chat registriert.
  Spring AI verdeckt interne Tool-Calls im Stream → `EmittingToolCallback` sendet `TOOL_CALL_*`-Events
  über einen Seitenkanal (mit dem Text-Stream gemerged), damit der Client sie anzeigen kann.

CORS: `config/WebConfig.java` (global `/**`), Origins via `app.cors.allowed-origins` (Default `:4200`).

## KI / Anthropic-Key
- KI über `spring-ai-starter-model-anthropic` (`ChatClient.Builder` auto-konfiguriert).
- `application.properties`: `spring.ai.anthropic.api-key=${ANTHROPIC_API_KEY:}`. Die App **bootet
  ohne** Key; der Key wird erst beim `/api/agui/analyze`-Call gebraucht (sonst `RUN_ERROR`/401).
  Key als **Env-Var** setzen (IntelliJ-Run-Config), nicht committen.

## Konventionen
- DTOs als **Java `record`s**, Konstruktor-Injection (kein Field-Injection).
- REST-Endpunkte unter `/api/**`. Feature-Packages unter `de.primeux.demo.<feature>`
  (z. B. `supplement/` mit Controller + Service + Records).
- In-Memory-Daten in einem `@Service` halten (mutierbarer Store, beim Start deterministisch geseedet — nicht random).

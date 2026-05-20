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
- `GET /api/supplements/tracking` → Supplement-Tracking-Testdaten (in-memory, deterministisch).
  Lesend, keine Datenmanipulation.
- CORS: `config/WebConfig.java` (global, Mapping `/**`), erlaubte Origins via
  `app.cors.allowed-origins` (Default `http://localhost:4200`).

## Konventionen
- DTOs als **Java `record`s**, Konstruktor-Injection (kein Field-Injection).
- REST-Endpunkte unter `/api/**`. Feature-Packages unter `de.primeux.demo.<feature>`
  (z. B. `supplement/` mit Controller + Service + Records).
- In-Memory-Testdaten in einem `@Service` halten; deterministisch, nicht random.

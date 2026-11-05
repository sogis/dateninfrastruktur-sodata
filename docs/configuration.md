# Sodata-Konfiguration

Der kanonische Stack bindet Sodata an die explizit vorbereitete
Managementdatenbank. Für einen eigenen kontrollierten Container benötigt
die Anwendung dieselben drei Einstellungen:

| Umgebungsvariable | Bedeutung |
|---|---|
| `DATENINFRASTRUKTUR_CATALOG_DATABASE_JDBCURL` | JDBC-URL der Managementdatenbank |
| `DATENINFRASTRUKTUR_CATALOG_DATABASE_USERNAME` | Dedizierte Rolle `di_public` |
| `DATENINFRASTRUKTUR_CATALOG_DATABASE_PASSWORD` | Privates Runtime-Secret der Rolle |

Beim Start und für Readiness werden Rolle, Berechtigungsgrenze und
`api_public` geprüft. Ohne passenden DB-Vertrag schlägt der Start fehl.
`/actuator/health`, `/actuator/health/liveness`,
`/actuator/health/readiness` und `/actuator/info` geben keine Datenbankdetails
aus. Fehlermeldungen werden durch `spring.web.error.include-stacktrace=never`
und die übrigen Spring-Fehleroptionen geschützt.

`X-Content-Type-Options`, `Referrer-Policy`, `X-Frame-Options`,
`Permissions-Policy` und `Content-Security-Policy` gelten auch für
Fehlerseiten. Der Browser verwendet standardmässig nur dieselbe Origin
für Verbindungen. Zusätzliche Verbindungsziele benötigen einen expliziten
Eintrag unter `dateninfrastruktur.security.csp.connect-src`.

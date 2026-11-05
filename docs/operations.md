# Sodata betreiben

Sodata wird als Dienst im [kanonischen Stack](../../dateninfrastruktur-dev-stack/docs/operations.md)
gebaut und gestartet. Das Gateway veröffentlicht den Katalog; der Sodata-
Container hat keinen eigenen Hostport. Readiness prüft SQL-Sichten und die
Berechtigung der Rolle `di_public`. Bei Störungen zuerst den PostgreSQL-
Dienst, Readiness und die Stack-Logs prüfen.

Für Tests ohne Docker: Java 25, `./gradlew test`. Echte DB- und Browsertests
nutzen explizit private Testkontexte, siehe `scripts/test_database_services.py`
und den G5-Integrationslauf im Stack. Konfigurationswerte stehen unter
[Konfiguration](configuration.md).

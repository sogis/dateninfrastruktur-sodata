# Sodata – öffentlicher Katalog

Sodata rendert Suche, Facetten, Datensatzdetails, Serien und Ausgaben mit
Spring Boot, JTE und HTMX. Die Anwendung liest die freigegebenen PostgreSQL-
Sichten mit der Rolle `di_public`. Downloads laufen über das Gateway; Anträge
und Freigaben bearbeitet das Admin-Backend.

Installation und Normalstart sind im [Compose-Betrieb](../dateninfrastruktur-dev-stack/docs/operations.md)
beschrieben. Die [Konfiguration](docs/configuration.md) und der
[DB-Katalog](docs/database-catalog.md) erklären die SQL-Schnittstelle und
Benutzerfunktionen. Hinweise zum [Containerbuild](docs/container-deployment.md)
und zu [Metadatentexten](docs/metadata-text.md) ergänzen sie. Frühere
Spezifikationen, UI-Entwürfe und Explore-Unterlagen dokumentieren ihre damalige
Version. Dateien unter `spec/fixtures/` dienen nur als historische Quellen
und gelangen nicht in das Produktimage.

Mit Java 25: `./gradlew test`. Die echte DB-/Browser-Integration läuft in
den dafür vorgesehenen privaten Testkontexten. Der übergreifende Nachweis
liegt bei [MA-G](../dateninfrastruktur-dev-stack/docs/implementation/storage-and-delivery/WORK_QUEUE.md).

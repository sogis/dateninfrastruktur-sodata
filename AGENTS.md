# AGENTS.md – dateninfrastruktur-sodata

## Vor Beginn lesen

Lies ausdrücklich `../AGENTS.md` und `../dateninfrastruktur-dev-stack/docs/implementation/grundinstallation/AUFTRAG_GRUNDINSTALLATION.md` sowie den zur aktuellen Rolle gehörenden Startprompt im selben Dokumentationsordner. Fehlt `../AGENTS.md`, lies ersatzweise `../dateninfrastruktur-dev-stack/docs/implementation/grundinstallation/templates/AGENTS.workspace.md`. Verlasse dich nicht auf automatische Vererbung über Git-Grenzen. Weitere betroffene Schwesterrepos haben eigene AGENTS-Dateien.

Diese Regeln gelten für Implementierung **und** Review. Ein Reviewauftrag erlaubt keine Änderungen an Produktdateien, Tests, Anforderungen oder AGENTS-Dateien; Befunde melden, nicht selbst reparieren. Builds/Tests nur mit eigenen Laufzeitdaten. Quellrepos, fremde Änderungen und laufende fremde Instanzen erhalten; keine Secrets in Git oder Berichten.

## Verantwortung

Übernehme das vorhandene Sodata-Portal aus dem festgelegten Quellstand. Suche, Datensatzseiten, Katalogsnapshot, Manifestimport und Readiness bleiben funktional erhalten.

## Besondere Regeln

- Nur Änderungen für den eigenständigen lauffähigen Aufbau; kein neuer Login, keine neue Portal-Datenbank, keine Anträge/Freigaben oder ClickHouse-Anwendungsabfragen in dieser Etappe.
- Bestehendes Dockerfile-Target `jvm-runtime` verwenden. Java-/Node-Anforderungen am übernommenen Build prüfen. Keine Neuimplementierung des Frontends oder zusätzliche Native-Image-Arbeit.
- `DATENINFRASTRUKTUR_EXPLORE_ENABLED=false` im Stack; das vorhandene SQL-/R-Lab wird hier nicht erweitert.
- Interne Package-/Konfigurations-/Artefaktnamen dürfen gemäss Legacy-Liste erhalten bleiben. Dockerfile-COPY-Pfade und Gradle-Artefaktname müssen zusammenpassen.
- Manifest, Katalogartefakte und Reloadziele auf die lokale eigene Umgebung beziehen; keine bestehenden Dienste neu laden.

## Nachweise

Bestehende relevante Tests erhalten und ausführen. Lokal erzeugte gültige Veröffentlichung laden; Readiness und Webseiten über das Gateway prüfen. Den verwendeten Buildweg und tatsächlich ausgeführte Tests dokumentieren. Repoübergreifende Phasen- und Reviewberichte gehören in den Dev-Stack, nicht in duplizierte Auftragsordner dieses Repos.

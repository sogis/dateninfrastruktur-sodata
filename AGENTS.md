# AGENTS.md – dateninfrastruktur-sodata

## Vor Beginn lesen

Lies ausdrücklich `../AGENTS.md` sowie den aktuellen Gesamtauftrag unter `../dateninfrastruktur-dev-stack/docs/implementation/management-and-access/EXECUTION_PLAN.md`, die dort referenzierten SPEC-Dateien, TEST_PLAN und den rollenbezogenen Start-/Reviewprompt. Die nachfolgenden Grundinstallationsregeln und Nachweise sind historisch auf ihren damaligen Auftrag begrenzt; sie verbieten den ausdrücklich beauftragten Ausbau MA-0 bis MA-E nicht. Weitere betroffene Schwesterrepos haben eigene AGENTS-Dateien.

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

## Aktuelle Management-/Access-Etappe

Sodata liest ausschliesslich die freigegebenen SQL-Views mit read-only-Konto. Schreibende Selbstbedienung liegt im Adminbackend; kein File-Streaming, keine Autorisierung aus Snapshot/Lucene. Im DB-Modus kein stiller XTF-Fallback und keine ungefilterten historischen Katalogartefakte. Bestehende Darstellung gezielt erhalten.

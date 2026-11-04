# Öffentlicher PostgreSQL-Katalog – MA-D

Der explizite Modus `DATENINFRASTRUKTUR_CATALOG_SOURCE_TYPE=database` liest
`api_public` in der Management-DB mit **di_public**. Die Anwendung schreibt keine
Anträge, Grants, Metadaten oder Auditdaten. Selbstbedienung gehört zum Admin-
Backend unter `/access/*`. Der unabhängige Phasenstatus steht im Schwesterrepo
STACK unter `docs/implementation/management-and-access/WORK_QUEUE.md`.

## Start im eigenen lokalen Managementruntime

Java25, Docker und die Geschwisterrepos ADMIN/STACK/PUBLIC erforderlich. Ein
bereits vorhandenes eigenes Runtime wird erhalten; keine Nutzer-.env verwenden.

```sh
# ADMIN: explizites lokales DB-Setup, kein Reset
python3 scripts/setup.py
# PUBLIC: vorhandener JVM-Buildweg
./gradlew bootJar
# ADMIN: Adminjar bauen, beide Anwendungen über eigenes Gateway starten
./gradlew bootJar
python3 scripts/identity_runtime.py start --portal-jar=/absoluter/pfad/dateninfrastruktur-sodata/build/libs/dateninfrastruktur-sodata-0.1.0-SNAPSHOT.jar
```

Die Gatewaybasis steht nur in ADMIN/.runtime/identity/state.json. Dort führen
`/` und `/datasets` ins Portal, `/management` in die Verwaltung, `/access/me` zur
Selbstbedienung. Keycloak übernimmt die Anmeldung; der Portal-Antragslink führt
zum Adminformular mit der **Root-Dataset-UUID**, auch bei einer Serienausgabe.
Die eigene Testtopologie verwendet Loopbackports für Gateway/Keycloak und
gezielte Backendtests, die Publicinstanz hat keinen Hostport. Private Downloads
und die endgültige Mehrinstanzentopologie folgen hinter MA-E.

Ein produktseitig unabhängiger JVM-Prozess kann mit diesen Properties starten:

- `dateninfrastruktur.catalog.source-type=database`
- `dateninfrastruktur.catalog.database.jdbc-url`
- `dateninfrastruktur.catalog.database.username=di_public`
- `dateninfrastruktur.catalog.database.password` aus sicherer Runtimekonfiguration
- `dateninfrastruktur.explore.enabled=false`

Keine XTF-, `current.json`-, DuckDB-, Jenkins- oder Reload-URL ist dafür nötig.
Der bestehende `jvm-runtime`-Dockerfiletarget bleibt der Containerbuildweg; es
gibt keine neue Nativezusage. Spring Boot ist genau4.1.2, Java25.

## Konsistenz, Suche und Darstellung

`JdbcCatalogRepository` verwendet ausschliesslich öffentliche Views. Ein HTTP-
Request beginnt frisch; Ergebnis/Facetten gehören zu einer kurzen read-only
Repeatable-Read-Transaktion. Kein persistenter Gesamtsnapshot, keine Lucene-/
Rechtecachewahrheit und kein Fallback bei DBfehler. Bereits laufende zwei
Portalinstanzen sehen die nächste committete Adminänderung ohne Reload.

Suche: literale, leerzeichengetrennte **AND**-Begriffe in Titel, Beschreibung und
Keywords; Gross-/Kleinschreibung wird ignoriert. Lucene-Operatoren, Wildcards,
Fuzzy-/Phrasenoperatoren werden im DB-Modus nicht interpretiert. Symbole sind
literal. Filter für Thema/Amt/Änderungszeit/Ressourcentyp, vorhandene Sortierungen
und begrenzte Pagination sind im DB-Modus umgesetzt; Identifier ist stabiler Tie-Breaker.
Die Facetten zählen nur öffentliche Rootdatensätze. Keine versteckten Entwürfe
oder Ausgaben über Details, Facetten oder Suche.

Vorhandene JTE/HTMX-VMs und sicherer eingeschränkter Markdownrenderer bleiben
bestehen. Ein Rootdatensatz wird genau einmal gelistet; die interne Default-
Ausgabe eines Einzelangebots erscheint nicht separat. Serien dürfen ohne
publizierte Ausgabe dargestellt werden, ohne eine Ausgabe zu erfinden.
Aktuelle Ausgaben/Versionen werden über die freigegebenen Zeiger beziehungsweise
neueste verfügbare sichtbare Einträge abgebildet. Zusätzliche echte Ausgaben-
Erläuterungen bleiben sichtbar. Schema/Attributname, Typ, Beschreibung, Einheit
und Reihenfolge werden übernommen. Unbekannte Prüf-/Lieferwerte bleiben unbekannt.

Im DB-Modus antworten `/catalog/**` und die alten Explore-Kontexte/-Seiten mit
**410**. Es gibt keinen aktuellen vollständigen XTF-/DuckDB-Export. Alte
Explore-Links werden ausgeblendet; bestehende Datei-/Fixturemodi bleiben als
Vergleich erhalten. Der Importer übernimmt keine alte Download-URL als
öffentliche Dateiverfügbarkeit. Die Dateifunktion folgt erst mit dem privaten
Origin hinter MA-E.

Readiness prüft Publicrolle und Views; DBverlust ergibt503 statt Snapshotdaten.
Liveness bleibt Prozesszustand. Start mit falscher Rolle/fehlenden Views stoppt.
Publicrolle hat weder Mutationsrecht noch Zugriff auf Basis-/Storage-/Grant-/
Audit-/Legacyherkunftstabellen. Das Portal führt kein DDL aus.

## Reproduzierbare Tests

Aus ADMIN `python3 scripts/test_catalog_all.py`, alternativ STACK:
`./scripts/management/test.sh --phase MA-D`. Der Runtimebereich muss frei sein;
bestehende eigene Daten werden nicht implizit ersetzt. Der Test richtet nur
eigene gelabelte Ressourcen ein und entfernt sie im finally.

M21–M25: echte PG-Views, Suche/Filter/Sort/Pagination und Attribute; tatsächliche
HTTP-/Browseransichten; zwei JVM-Instanzen, echte Cayenne-Metadatenmutation und
503 bei entzogener DBverbindung ohne Fallback; echte Publicrollendenials; Legacy-
Dry-run/Apply/UUID-Idempotenz/Schutz späterer Adminänderungen. Zusätzlich echter
Portal→Gateway→Keycloak→Admin-Browserweg und gezielte C-MINOR-Regressionsproben.
Rohbelege/gesicherte JUnitberichte: ADMIN/.runtime/catalog-d-evidence/.

Nach Review MA-D-REV-01: HTTP page/size werden explizit in die DB-Abfrage
übernommen. Standard10, maximal100 Einträge; echte Vor-/Zurücknavigation
erhält Suche/Filter/Sortierung und Seitengrösse. HTMX und vollständige Seiten
verwenden dieselbe Darstellung. Ausgabenexpansion erhält das aktuelle Fenster;
überhohe Seiten innerhalb der erlaubten Eingabegrenze werden auf die letzte
Ergebnisseite begrenzt. Der bisherige Snapshotmodus bleibt unpaged. Ein echter
13-Root-HTTP-/Browser-Reproducer prüft die vollständige Erreichbarkeit.

MA-E: Registrierte DB-Distributionen tragen eine reine Darstellungsmarkierung
`gatewayProtected`. Auch bei restricted bleiben die kanonischen Downloadlinks
anklickbar; APISIX prüft den aktuellen Zugang bei jedem Abruf. Das Portal behauptet
damit kein persönliches Recht und speichert keines. Legacy-URL-Links behalten
ihr bisheriges Verhalten. Private Origins/Storagefelder erscheinen nicht im DTO.

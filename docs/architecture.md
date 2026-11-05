# Sodata-Architektur

APISIX leitet Katalogrouten zu Sodata. `DatabaseCatalogController` nutzt
`CatalogQueryService` und `JdbcCatalogRepository` für die freigegebenen
PostgreSQL-Sichten. Die Ergebnisse werden in die bestehenden Seitenmodelle
übersetzt und mit JTE und HTMX dargestellt. Admin bearbeitet Schreibvorgänge.
Downloads passieren separat den Gateway-Check bei Admin und den privaten
Datei-Origin. Eine PostgreSQL-Änderung wird bei der nächsten Kataloganfrage
sichtbar. Sie benötigt keinen Hintergrundimport und keine Veröffentlichung
eines Gesamtkatalogartefakts.

SQL-, Filter- und Darstellungsverhalten stehen im [DB-Katalog](database-catalog.md).

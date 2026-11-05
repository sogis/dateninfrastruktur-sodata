# Öffentlicher PostgreSQL-Katalog

Sodata liest `api_public` mit der dedizierten Rolle `di_public`.
Die kurze read-only Repeatable-Read-Transaktion liefert Treffer und Facetten
aus demselben Stand. Die nächste Anfrage sieht neue committete Metadaten und
Versionen. Ein Datenbankausfall ergibt 503 für betroffene Seiten; Liveness
bezieht sich auf den Prozess. Die Rolle darf Basis-, Storage-, Grant- und
Audittabellen weder lesen noch verändern.

- `/` und `/datasets`: Suche, Facetten für Thema/Amt/Änderungszeit/Ressourcentyp,
  Sortierung, Karten-/Listenansicht und Pagination.
- `/datasets/{identifier}`: Detail, Kontakt, Struktur und Nutzung.
- `/series/{identifier}`: Reihe mit ausgewählten Ausgaben.
  `/issues/current` und der konkrete Ausgabeschlüssel wählen die Ausgabe.
- Die öffentlichen Sichten stellen verfügbare Downloadlinks bereit; das
  Gateway autorisiert jeden Abruf. Die Selbstbedienung unter `/access/me`
  und Anträge für die Root-Dataset-UUID führen zum Admin-Backend.

Suchbegriffe werden ohne Beachtung der Gross-/Kleinschreibung als literale
AND-Bedingungen angewandt. Titel, Beschreibung und Keywords sind suchbar.
Die Standardseite umfasst 10, eine Seite höchstens 100 Einträge. Vor- und
Zurücknavigation erhält Such- und Filterzustand. Entwürfe und verborgene
Ausgaben sind nicht öffentlich. Metadatentexte verwenden den eingeschränkten
Markdownrenderer.

Die [Betriebsanleitung](operations.md) verweist auf Setup und Integration.

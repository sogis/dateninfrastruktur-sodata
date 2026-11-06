# dateninfrastruktur-sodata

Im gemeinsamen Dateninfrastruktur-Workspace vor repoübergreifender Arbeit
`../AGENTS.md` ausdrücklich lesen und die Repo-Grenzen verifizieren. Bei einem
Einzelclone gelten die lokalen Regeln und Bedienhinweise im README. Für
repoübergreifende Aufträge zuerst den verifizierten Workspace bereitstellen.

Sodata zeigt den öffentlichen Katalog. Es verwendet das Lesekonto
`di_public` und die dafür freigegebenen Sichten in `api_public`.
Datenbankzugriffe bleiben read-only mit konsistentem Snapshot. Fachliche
Freigaben und Schreibvorgänge liegen beim Admin. Downloads laufen über
Gateway, Autorisierung und Origin.

README.md und docs/database-catalog.md beschreiben Datenbankgrenze und
Build. Den gepflegten Dockerfile-Target `jvm-runtime` und die
vorhandenen Rollen- und Katalogtests verwenden.

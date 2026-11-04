package ch.so.agi.datenportal.catalog.database;
public final class CatalogDatabaseUnavailableException extends RuntimeException {
    public CatalogDatabaseUnavailableException(Throwable cause){super("Der Katalog ist vorübergehend nicht verfügbar.",cause);}
}

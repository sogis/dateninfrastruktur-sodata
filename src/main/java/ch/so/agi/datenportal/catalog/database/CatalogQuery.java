package ch.so.agi.datenportal.catalog.database;
import ch.so.agi.datenportal.search.*;
/** Explicitly bounded SQL query. PostgreSQL search uses literal AND tokens, no Lucene operators. */
public record CatalogQuery(SearchQuery search){
    public CatalogQuery {
        if(search==null || search.q().length()>500 || search.filters().themes().size()>30 || search.filters().offices().size()>50 || search.pageRequest().page()>100000)throw new IllegalArgumentException("Ungültige Katalogabfrage.");
        int size=search.pageRequest().size()>0?Math.min(100,search.pageRequest().size()):10;
        search=new SearchQuery(search.q(),search.filters(),search.sortMode(),PageRequest.of(Math.max(1,search.pageRequest().page()),size));
    }
}

package ch.so.agi.datenportal.catalog.database;
import java.time.Clock;
import java.util.*;
import org.junit.jupiter.api.Test;
import ch.so.agi.datenportal.catalog.domain.*;
import ch.so.agi.datenportal.search.*;
import static org.junit.jupiter.api.Assertions.*;
class JdbcCatalogRepositoryIT {
    @Test void realPublicViewsSearchFacetsPaginationStandaloneSeriesAndSchema(){
        var data=DatabaseFixture.data();try(var ds=DatabaseFixture.source("di_public","DI_PUBLIC_TEST_PASSWORD")){
            var service=new CatalogQueryService(new JdbcCatalogRepository(ds,Clock.systemUTC()));
            var filter=new SearchFilters(Set.of("Geografie"),Set.of(data.get("officeIdentifier")),Optional.empty(),Set.of());
            var query=new CatalogQuery(new SearchQuery("MA-D",filter,SortMode.TITLE_ASC,PageRequest.of(1,1)));var page=service.search(query);
            assertEquals(2,page.results().totalElements());assertEquals(1,page.results().entries().size());assertEquals(2,page.results().totalPages());
            var next=service.search(new CatalogQuery(new SearchQuery("MA-D",filter,SortMode.TITLE_ASC,PageRequest.of(2,1))));assertNotEquals(page.results().entries().getFirst().identifier(),next.results().entries().getFirst().identifier());
            assertTrue(service.findDataset(data.get("draftIdentifier")).isEmpty());assertTrue(page.facets().themes().stream().noneMatch(f->"Gesundheit".equals(f.value())));
            var dataset=service.findDataset(data.get("openIdentifier")).orElseThrow();assertInstanceOf(DatasetEntry.class,dataset.entry());assertEquals("gemeindename",dataset.entry().metadata().attributes().getFirst().name());assertEquals("Testmodell",dataset.entry().metadata().model().orElseThrow());
            var series=service.findDataset(data.get("seriesIdentifier")).orElseThrow();assertInstanceOf(DatasetSeriesEntry.class,series.entry());assertEquals(2,((DatasetSeriesEntry)series.entry()).issueCount());assertEquals("2026",service.findCurrentIssue(data.get("seriesIdentifier")).orElseThrow().issue().identifier());assertTrue(service.findIssue(data.get("seriesIdentifier"),"hidden").isEmpty());
            assertTrue(service.findIssue(data.get("seriesIdentifier"),"2025").orElseThrow().issue().description().contains("Ausgabehinweis erhalten"));
            var empty=service.findDataset(data.get("emptySeriesIdentifier")).orElseThrow();assertEquals(0,((DatasetSeriesEntry)empty.entry()).issueCount());assertTrue(service.findCurrentIssue(data.get("emptySeriesIdentifier")).isEmpty());
            assertEquals(0,service.search(new CatalogQuery(new SearchQuery("' OR true --",filter,SortMode.TITLE_ASC,PageRequest.of(1,20)))).results().totalElements());
        }
    }
}

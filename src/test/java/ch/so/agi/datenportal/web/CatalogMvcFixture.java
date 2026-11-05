package ch.so.agi.datenportal.web;

import ch.so.agi.datenportal.catalog.database.CatalogQuery;
import ch.so.agi.datenportal.catalog.database.CatalogQueryService;
import ch.so.agi.datenportal.catalog.database.JdbcCatalogRepository;
import ch.so.agi.datenportal.search.Facets;
import ch.so.agi.datenportal.search.SearchResult;
import java.util.List;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
class CatalogMvcFixture {
    @Bean @Primary JdbcCatalogRepository availableCatalogContract() {
        var repository = mock(JdbcCatalogRepository.class);
        when(repository.contractReady()).thenReturn(true);
        return repository;
    }

    @Bean @Primary
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(name="di.test.empty-catalog", havingValue="true", matchIfMissing=true)
    CatalogQueryService emptyCatalog() {
        var service = mock(CatalogQueryService.class);
        when(service.search(any())).thenAnswer(call -> {
            CatalogQuery query = call.getArgument(0);
            return new CatalogQueryService.CatalogPage(
                    new SearchResult(List.of(), 0, query.search()), new Facets(List.of(), List.of(), List.of()));
        });
        return service;
    }
}

package ch.so.agi.datenportal;

import static org.assertj.core.api.Assertions.assertThat;

import ch.so.agi.datenportal.catalog.service.CatalogService;
import ch.so.agi.datenportal.catalog.domain.CatalogSnapshot;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DatenportalApplicationTests {

    @Autowired
    private CatalogService catalogService;

    @Value("${gg.jte.development-mode}")
    private boolean jteDevelopmentMode;

    @Test
    void contextLoadsAndStartupCatalogIsAvailable() {
        String sourceDescription = catalogService.withSnapshot(CatalogSnapshot::sourceDescription);
        assertThat(sourceDescription).isEqualTo("classpath:published_catalog_full_62_entries.xtf");
        assertThat(catalogService.visibleEntries()).hasSize(62);
        assertThat(catalogService.findVisibleEntry("ch.so.bauinventar")).isPresent();
        assertThat(catalogService.findAnyEntry("ch.so.abstimmungsresultate_2026")).isPresent();
        int indexedDocuments = catalogService.withSnapshot(snapshot -> snapshot.searchIndex().documentCount());
        assertThat(indexedDocuments).isEqualTo(62);
        assertThat(jteDevelopmentMode).isFalse();
    }
}

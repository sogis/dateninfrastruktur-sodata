package ch.so.agi.datenportal.catalog.database;
import org.junit.jupiter.api.Test;
import com.microsoft.playwright.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
class CatalogDisclosureIT {
    @Test void actualHttpAndBrowserExposeOnlyPublicMetadataAndCorrectSelfServiceLinks()throws Exception{
        var data=DatabaseFixture.data();try(var context=DatabaseFixture.start();var playwright=Playwright.create(new Playwright.CreateOptions().setEnv(Map.of("PLAYWRIGHT_SKIP_BROWSER_GC","1")));var browser=playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(true))){
            String base=DatabaseFixture.base(context);
            for(String path:new String[]{"/catalog/published-catalog.xtf","/catalog/catalog.duckdb","/datasets/"+data.get("openIdentifier")+"/explore","/explore/context/anything"})assertEquals(410,DatabaseFixture.get(base,path).statusCode(),path);
            assertEquals(404,DatabaseFixture.get(base,"/datasets/"+data.get("draftIdentifier")).statusCode());assertEquals(404,DatabaseFixture.get(base,"/series/"+data.get("seriesIdentifier")+"/issues/hidden").statusCode());
            assertEquals(200,DatabaseFixture.get(base,"/series/"+data.get("emptySeriesIdentifier")).statusCode());assertEquals(404,DatabaseFixture.get(base,"/series/"+data.get("emptySeriesIdentifier")+"/issues/current").statusCode());
            var page=browser.newPage();page.navigate(base+"/series/"+data.get("seriesIdentifier"));assertEquals("/access/requests/new?dataset="+data.get("seriesId"),page.locator("a",new Page.LocatorOptions().setHasText("Datenzugang anfragen")).getAttribute("href"));
            page.navigate(base+"/series/"+data.get("seriesIdentifier")+"/issues/current");assertEquals("/access/requests/new?dataset="+data.get("seriesId"),page.locator("a",new Page.LocatorOptions().setHasText("Datenzugang anfragen")).getAttribute("href"));assertTrue(page.locator("a[href='/access/me']").count()>0);
            assertFalse(page.content().contains("DRAFT_ONLY_SECRET"));assertFalse(page.content().contains("storageProfile"));assertFalse(page.content().contains("objectkey"));assertEquals(0,page.locator("script:not([src])").count(),"Untrusted MTEXT script is escaped/sanitized");
            page.navigate(base+"/datasets/"+data.get("openIdentifier")+"/structure-quality-origin");assertTrue(page.locator("main").innerText().contains("gemeindename"));
            page.screenshot(new Page.ScreenshotOptions().setPath(java.nio.file.Path.of(".runtime/database-catalog-browser.png")).setFullPage(true));
        }
    }
}

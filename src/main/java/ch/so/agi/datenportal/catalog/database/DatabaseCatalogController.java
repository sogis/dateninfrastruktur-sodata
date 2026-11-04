package ch.so.agi.datenportal.catalog.database;
import java.util.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import ch.so.agi.datenportal.web.*;
import ch.so.agi.datenportal.catalog.domain.*;

@Controller @ConditionalOnProperty(name="dateninfrastruktur.catalog.source-type",havingValue="database")
public final class DatabaseCatalogController {
    private final CatalogQueryService query;private final HomePageVmFactory home;private final DetailPageVmFactory detail;private final FilterVmFactory filters;
    public DatabaseCatalogController(CatalogQueryService query,HomePageVmFactory home,DetailPageVmFactory detail,FilterVmFactory filters){this.query=query;this.home=home;this.detail=detail;this.filters=filters;}
    @ModelAttribute("databaseMode") boolean databaseMode(){return true;}
    @GetMapping({"/","/datasets"})String index(@ModelAttribute CatalogQueryParams params,HttpServletRequest request,Model model){
        var normalized=params.normalized();var result=query.search(new CatalogQuery(normalized.toSearchQuery()));var page=home.database(result.results(),normalized,result.facets());model.addAttribute("page",page);
        if(HtmxRequest.targetsResults(request)){model.addAttribute("results",page.results());model.addAttribute("filterPanel",page.filterPanel());model.addAttribute("queryParams",page.queryParams());return "fragments/catalogResults";}return "pages/catalog";
    }
    private CatalogQueryService.DatasetDetail find(String identifier){return query.findDataset(identifier).orElseThrow(()->new CatalogNotFoundException("Katalogeintrag nicht gefunden"));}
    private void access(Model model,UUID dataset,CatalogEntry entry){model.addAttribute("requestAccessUrl",entry.isOpenData()?null:"/access/requests/new?dataset="+dataset);}
    @GetMapping({"/datasets/{identifier}","/datasets/{identifier}/structure-quality-origin","/datasets/{identifier}/usage"})String dataset(@PathVariable String identifier,HttpServletRequest request,Model model){
        var found=find(identifier);if(!(found.entry() instanceof DatasetEntry entry))throw new CatalogNotFoundException("Einzelangebot nicht gefunden");access(model,found.datasetId(),entry);
        if(request.getRequestURI().endsWith("/structure-quality-origin")){model.addAttribute("page",detail.datasetStructureQualityOrigin(entry));return "pages/structureQualityOrigin";}
        if(request.getRequestURI().endsWith("/usage")){model.addAttribute("page",detail.datasetUsage(entry));return "pages/usage";}
        model.addAttribute("page",detail.dataset(entry));return "pages/entryDetail";
    }
    @GetMapping("/series/{identifier}")String series(@PathVariable String identifier,Model model){var found=find(identifier);if(!(found.entry() instanceof DatasetSeriesEntry entry))throw new CatalogNotFoundException("Serie nicht gefunden");access(model,found.datasetId(),entry);model.addAttribute("page",detail.series(entry));return "pages/seriesDetail";}
    @GetMapping({"/series/{identifier}/issues/{issueKey}","/series/{identifier}/issues/{issueKey}/structure-quality-origin","/series/{identifier}/issues/{issueKey}/usage"})String issue(@PathVariable String identifier,@PathVariable String issueKey,HttpServletRequest request,Model model){
        var found=("current".equals(issueKey)?query.findCurrentIssue(identifier):query.findIssue(identifier,issueKey)).orElseThrow(()->new CatalogNotFoundException("Ausgabe nicht gefunden"));access(model,found.datasetId(),found.issue());
        if(request.getRequestURI().endsWith("/structure-quality-origin")){model.addAttribute("page",detail.issueStructureQualityOrigin(found.series(),found.issue()));return "pages/structureQualityOrigin";}
        if(request.getRequestURI().endsWith("/usage")){model.addAttribute("page",detail.issueUsage(found.series(),found.issue()));return "pages/usage";}
        model.addAttribute("page",detail.issue(found.series(),found.issue()));return "pages/entryDetail";
    }
    @GetMapping("/datasets/filter-popover")String popover(@RequestParam("filter")String id,@ModelAttribute CatalogQueryParams params,Model model){var normalized=params.normalized();var panel=filters.create(normalized,query.facets(new CatalogQuery(normalized.toSearchQuery())));var group=panel.groups().stream().filter(g->g.id().equals(id)).findFirst().orElseThrow(()->new CatalogNotFoundException("Filter nicht gefunden"));model.addAttribute("group",group);model.addAttribute("queryParams",normalized);return "fragments/filterPopover";}
    @GetMapping("/datasets/mobile-filters")String mobile(@ModelAttribute CatalogQueryParams params,Model model){var normalized=params.normalized();model.addAttribute("filterPanel",filters.create(normalized,query.facets(new CatalogQuery(normalized.toSearchQuery()))));model.addAttribute("queryParams",normalized);return "fragments/mobileFilters";}
    @GetMapping({"/catalog/**","/explore/**","/datasets/{identifier}/explore","/series/{identifier}/issues/{issueKey}/explore"})ResponseEntity<Void> unavailable(){return ResponseEntity.status(410).cacheControl(CacheControl.noStore()).build();}
}

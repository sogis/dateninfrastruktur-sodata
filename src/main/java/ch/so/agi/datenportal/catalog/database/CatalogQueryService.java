package ch.so.agi.datenportal.catalog.database;
import java.util.*;
import ch.so.agi.datenportal.catalog.domain.*;
import ch.so.agi.datenportal.search.*;
/** A page and its facets share one short read-only repeatable-read transaction, never a retained snapshot. */
public final class CatalogQueryService {
    public record CatalogPage(SearchResult results,Facets facets){}
    public record DatasetDetail(UUID datasetId,CatalogEntry entry){}
    public record IssueDetail(UUID datasetId,DatasetSeriesEntry series,DatasetIssueEntry issue){}
    private final JdbcCatalogRepository repository;
    public CatalogQueryService(JdbcCatalogRepository repository){this.repository=repository;}
    public CatalogPage search(CatalogQuery query){return repository.read(c->new CatalogPage(repository.search(c,query),repository.facets(c)));}
    public Facets facets(CatalogQuery query){return repository.read(repository::facets);}
    public Optional<DatasetDetail> findDataset(String identifier){return repository.read(c->repository.findDataset(c,identifier));}
    public Optional<IssueDetail> findIssue(String identifier,String issueKey){return findDataset(identifier).filter(d->d.entry() instanceof DatasetSeriesEntry).flatMap(d->{var s=(DatasetSeriesEntry)d.entry();return s.issues().stream().filter(i->i.identifier().equals(issueKey)).findFirst().map(i->new IssueDetail(d.datasetId(),s,i));});}
    public Optional<IssueDetail> findCurrentIssue(String identifier){return findDataset(identifier).filter(d->d.entry() instanceof DatasetSeriesEntry).flatMap(d->{var s=(DatasetSeriesEntry)d.entry();return s.currentIssue().map(i->new IssueDetail(d.datasetId(),s,i));});}
}

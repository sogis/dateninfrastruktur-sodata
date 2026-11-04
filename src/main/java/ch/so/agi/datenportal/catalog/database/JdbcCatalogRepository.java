package ch.so.agi.datenportal.catalog.database;
import java.sql.*;
import java.net.URI;
import java.time.*;
import java.util.*;
import javax.sql.DataSource;
import ch.so.agi.datenportal.catalog.domain.*;
import ch.so.agi.datenportal.search.*;

/** Read-only primary-DB adapter. SQL mentions only explicit api_public views. */
public final class JdbcCatalogRepository {
    private final DataSource source;private final Clock clock;
    public JdbcCatalogRepository(DataSource source,Clock clock){this.source=source;this.clock=clock;}
    @FunctionalInterface public interface Read<T>{T run(Connection connection)throws SQLException;}
    public <T>T read(Read<T> work){
        try(var connection=source.getConnection()){
            connection.setReadOnly(true);connection.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);connection.setAutoCommit(false);
            try{T value=work.run(connection);connection.commit();return value;}
            catch(Exception error){connection.rollback();if(error instanceof SQLException sql)throw sql;if(error instanceof RuntimeException runtime)throw runtime;throw new SQLException(error);}
        }catch(SQLException error){throw new CatalogDatabaseUnavailableException(error);}
    }
    private List<Map<String,Object>> rows(Connection c,String sql,Object... values)throws SQLException{
        try(var statement=c.prepareStatement(sql)){statement.setQueryTimeout(3);for(int i=0;i<values.length;i++)statement.setObject(i+1,values[i]);
            try(var result=statement.executeQuery()){var list=new ArrayList<Map<String,Object>>();var md=result.getMetaData();while(result.next()){var row=new HashMap<String,Object>();for(int i=1;i<=md.getColumnCount();i++)row.put(md.getColumnLabel(i),result.getObject(i));list.add(row);}return list;}}
    }
    private String string(Map<String,Object> r,String key){return r.get(key)==null?null:r.get(key).toString();}
    private UUID uuid(Map<String,Object> r,String key){return r.get(key)==null?null:UUID.fromString(r.get(key).toString());}
    private LocalDate date(Map<String,Object> r,String key){Object v=r.get(key);return v==null?null:v instanceof java.sql.Date d?d.toLocalDate():LocalDate.parse(v.toString());}
    private Optional<String> text(Map<String,Object> r,String key){return Optional.ofNullable(string(r,key)).filter(s->!s.isBlank());}
    private Optional<URI> uri(Map<String,Object> r,String key){return text(r,key).map(URI::create);}
    private Office office(Connection c,UUID id)throws SQLException{
        var r=rows(c,"SELECT identifier,name,abbreviation,email,website FROM api_public.offices WHERE id=?",id).getFirst();
        return new Office(string(r,"identifier"),string(r,"name"),text(r,"abbreviation"),uri(r,"email"),uri(r,"website"));
    }
    public Optional<CatalogQueryService.DatasetDetail> findDataset(Connection c,String identifier)throws SQLException{
        var result=rows(c,"SELECT * FROM api_public.datasets WHERE identifier=?",identifier);
        return result.isEmpty()?Optional.empty():Optional.of(new CatalogQueryService.DatasetDetail(uuid(result.getFirst(),"id"),entry(c,result.getFirst())));
    }
    private CatalogEntry entry(Connection c,Map<String,Object> root)throws SQLException{
        UUID id=uuid(root,"id");Office owner=office(c,uuid(root,"owner_office")),publisher=office(c,uuid(root,"publisher_office"));
        var themes=rows(c,"SELECT theme FROM api_public.themes WHERE dataset_id=? ORDER BY theme",id).stream().map(r->Theme.fromModelValue(string(r,"theme"))).toList();
        var keywords=rows(c,"SELECT keyword FROM api_public.keywords WHERE dataset_id=? ORDER BY keyword",id).stream().map(r->string(r,"keyword")).toList();
        var issues=new ArrayList<DatasetIssueEntry>();
        for(var issue:rows(c,"SELECT * FROM api_public.issues WHERE dataset_id=? ORDER BY modified DESC,issue_key",id)){
            var version=currentVersion(c,issue);var metadata=metadata(c,root,issue,version);var links=distributions(c,version);
            issues.add(new DatasetIssueEntry(string(issue,"issue_key"),string(root,"title"),string(root,"description")+(text(issue,"note").isPresent()?"\n\nZur Ausgabe:\n"+string(issue,"note"):""),publisher,owner,themes,keywords,date(issue,"modified"),AccessLevel.fromModelValue(string(root,"access_level")),metadata,links,string(issue,"label"),Objects.equals(uuid(root,"current_issue"),uuid(issue,"id"))));
        }
        if("series".equals(string(root,"kind")))return new DatasetSeriesEntry(string(root,"identifier"),string(root,"title"),string(root,"description"),publisher,owner,themes,keywords,AccessLevel.fromModelValue(string(root,"access_level")),metadata(c,root,null,null),issues,Optional.of(date(root,"modified")));
        // Internal default issue is never a second top-level catalogue object.
        var issueRows=rows(c,"SELECT * FROM api_public.issues WHERE dataset_id=? AND issue_key='default'",id);
        var issue=issueRows.isEmpty()?null:issueRows.getFirst();var version=issue==null?null:currentVersion(c,issue);
        return new DatasetEntry(string(root,"identifier"),string(root,"title"),string(root,"description"),publisher,owner,themes,keywords,date(root,"modified"),AccessLevel.fromModelValue(string(root,"access_level")),metadata(c,root,issue,version),distributions(c,version));
    }
    private Map<String,Object> currentVersion(Connection c,Map<String,Object> issue)throws SQLException{
        // Explicit current pointer, otherwise newest available confirmed version; no prepared/withdrawn row can enter the view.
        var list=rows(c,"SELECT * FROM api_public.versions WHERE issue_id=? ORDER BY CASE WHEN id=? THEN 0 ELSE 1 END,sequence DESC,id LIMIT 1",uuid(issue,"id"),uuid(issue,"current_version"));return list.isEmpty()?null:list.getFirst();
    }
    private List<DistributionLink> distributions(Connection c,Map<String,Object> version)throws SQLException{
        if(version==null)return List.of();var links=new ArrayList<DistributionLink>();
        for(var r:rows(c,"SELECT format,download_path FROM api_public.distributions WHERE version_id=? ORDER BY format,id",uuid(version,"id"))){URI path=URI.create(string(r,"download_path"));links.add(new DistributionLink(path,path,DistributionFormat.fromModelValue(string(r,"format"))));}return List.copyOf(links);
    }
    private CatalogEntryMetadata metadata(Connection c,Map<String,Object> root,Map<String,Object> issue,Map<String,Object> version)throws SQLException{
        var attributes=new ArrayList<DatasetAttribute>();Optional<String> model=Optional.empty();Optional<StructureSummary> structure=Optional.empty();
        if(version!=null){
            UUID schema=uuid(version,"schema_id");if(schema!=null){
                var schemas=rows(c,"SELECT model_name FROM api_public.schemas WHERE id=?",schema);if(!schemas.isEmpty())model=text(schemas.getFirst(),"model_name");
                for(var r:rows(c,"SELECT name,data_type,description,unit,mandatory FROM api_public.attributes WHERE schema_id=? ORDER BY ordinal,id",schema))attributes.add(new DatasetAttribute(string(r,"name"),string(r,"data_type"),text(r,"description"),text(r,"unit"),Boolean.TRUE.equals(r.get("mandatory"))));
            }
            if(version.get("object_count") instanceof Number objects && version.get("attribute_count") instanceof Number count && objects.longValue()<=Integer.MAX_VALUE && count.longValue()<=Integer.MAX_VALUE)structure=Optional.of(new StructureSummary(objects.intValue(),count.intValue()));
        }
        Optional<TemporalCoverage> coverage=issue==null?Optional.empty():Optional.of(new TemporalCoverage(Optional.ofNullable(date(issue,"start_date")),Optional.ofNullable(date(issue,"end_date")),Optional.ofNullable(date(issue,"reference_date"))));
        var contact=new ContactPoint(text(root,"contact_name").orElse("Fachkontakt"),text(root,"contact_unit"),uri(root,"contact_email"),text(root,"contact_phone"),uri(root,"contact_url"));
        return new CatalogEntryMetadata(Optional.empty(),Optional.ofNullable(date(root,"issued")),uri(root,"license_uri"),Optional.of(contact),text(root,"frequency"),Optional.of("published"),text(root,"origin"),coverage,attributes,model,text(root,"survey_method"),text(root,"data_available_from"),text(root,"further_uses"),text(root,"auxiliary_data"),Optional.empty(),structure);
    }
    private String dateClause(String expression,ModifiedDateRange range,List<Object> values){
        LocalDate today=LocalDate.now(clock),year=today.withDayOfYear(1),last=year.minusYears(1);
        return switch(range){
            case LAST_30_DAYS->{values.add(today.minusDays(30));values.add(today);yield expression+" BETWEEN ? AND ?";}
            case LAST_6_MONTHS->{values.add(today.minusMonths(6));values.add(today);yield expression+" BETWEEN ? AND ?";}
            case THIS_YEAR->{values.add(year);values.add(today);yield expression+" BETWEEN ? AND ?";}
            case LAST_YEAR->{values.add(last);values.add(year);yield expression+">=? AND "+expression+"<?";}
            case OLDER->{values.add(last);yield expression+"<?";}
        };
    }
    private String placeholders(Collection<?> values){return String.join(",",Collections.nCopies(values.size(),"?"));}
    private String filter(SearchQuery query,List<Object> values){
        var clauses=new ArrayList<String>();clauses.add("true");
        // Literal whitespace-separated AND terms. All symbols are literal; no query-language interpolation.
        for(String token:query.q().toLowerCase(Locale.ROOT).split("\\s+"))if(!token.isBlank()){
            clauses.add("(position(? in lower(d.title||' '||d.description))>0 OR EXISTS(SELECT 1 FROM api_public.keywords k WHERE k.dataset_id=d.id AND position(? in lower(k.keyword))>0))");values.add(token);values.add(token);
        }
        var filters=query.filters();
        if(!filters.themes().isEmpty()){clauses.add("EXISTS(SELECT 1 FROM api_public.themes t WHERE t.dataset_id=d.id AND t.theme IN ("+placeholders(filters.themes())+"))");values.addAll(new TreeSet<>(filters.themes()));}
        if(!filters.offices().isEmpty()){clauses.add("EXISTS(SELECT 1 FROM api_public.offices o WHERE o.id=d.owner_office AND o.identifier IN ("+placeholders(filters.offices())+"))");values.addAll(new TreeSet<>(filters.offices()));}
        if(filters.modifiedRange().isPresent())clauses.add("("+dateClause("d.modified",filters.modifiedRange().orElseThrow(),values)+")");
        if(!filters.resourceTypes().isEmpty()){
            var kinds=filters.resourceTypes().stream().map(type->switch(type){case DATASET->"standalone";case DATASET_SERIES->"series";case DATASET_ISSUE->"__not_a_root";}).sorted().toList();
            clauses.add("d.kind IN ("+placeholders(kinds)+")");values.addAll(kinds);
        }
        return String.join(" AND ",clauses);
    }
    public SearchResult search(Connection c,CatalogQuery input)throws SQLException{
        var query=input.search();var values=new ArrayList<Object>();String where=filter(query,values);
        int total=((Number)rows(c,"SELECT count(*) AS total FROM api_public.datasets d WHERE "+where,values.toArray()).getFirst().get("total")).intValue();
        String order=query.sortMode()==SortMode.TITLE_ASC?"lower(d.title),d.modified DESC,d.identifier":"d.modified DESC,lower(d.title),d.identifier";
        int totalPages=(total+query.pageRequest().size()-1)/query.pageRequest().size();
        int page=Math.min(query.pageRequest().page(),Math.max(1,totalPages));
        values.add(query.pageRequest().size());values.add((page-1)*query.pageRequest().size());
        var entries=new ArrayList<CatalogEntry>();for(var row:rows(c,"SELECT d.* FROM api_public.datasets d WHERE "+where+" ORDER BY "+order+" LIMIT ? OFFSET ?",values.toArray()))entries.add(entry(c,row));
        var hits=entries.stream().map(e->SearchHit.withoutHighlight(e.identifier(),0.0f)).toList();
        return new SearchResult(entries,hits,total,page,query.pageRequest().size(),totalPages,new SearchQuery(query.q(),query.filters(),query.sortMode(),PageRequest.of(page,query.pageRequest().size())));
    }
    public Facets facets(Connection c)throws SQLException{
        var themes=rows(c,"SELECT theme,count(DISTINCT dataset_id) AS total FROM api_public.themes GROUP BY theme ORDER BY theme").stream().map(r->new FacetValue(string(r,"theme"),Theme.fromModelValue(string(r,"theme")).displayName(),((Number)r.get("total")).longValue())).toList();
        var offices=rows(c,"SELECT o.identifier,o.name,count(*) AS total FROM api_public.datasets d JOIN api_public.offices o ON o.id=d.owner_office GROUP BY o.identifier,o.name ORDER BY lower(o.name),o.identifier").stream().map(r->new FacetValue(string(r,"identifier"),string(r,"name"),((Number)r.get("total")).longValue())).toList();
        var dates=new ArrayList<FacetValue>();for(var range:ModifiedDateRange.values()){var args=new ArrayList<Object>();String predicate=dateClause("modified",range,args);long count=((Number)rows(c,"SELECT count(*) AS total FROM api_public.datasets WHERE "+predicate,args.toArray()).getFirst().get("total")).longValue();dates.add(new FacetValue(range.parameterValue(),range.label(),count));}
        return new Facets(themes,offices,dates);
    }
    public boolean contractReady(){try{return read(c->{var r=rows(c,"SELECT current_user AS role,(SELECT rolsuper OR rolbypassrls OR rolcreaterole FROM pg_roles WHERE rolname=current_user) AS unsafe,(SELECT count(*) FROM api_public.datasets) AS visible").getFirst();return "di_public".equals(r.get("role")) && Boolean.FALSE.equals(r.get("unsafe"));});}catch(RuntimeException error){return false;}}
}

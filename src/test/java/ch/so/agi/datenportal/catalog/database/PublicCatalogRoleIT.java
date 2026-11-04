package ch.so.agi.datenportal.catalog.database;
import org.junit.jupiter.api.Test;
import java.sql.*;
import static org.junit.jupiter.api.Assertions.*;
class PublicCatalogRoleIT {
    @Test void databaseRoleAllowsOnlyViewsAndNeverWriterOrSensitiveBaseTables()throws Exception{
        try(var ds=DatabaseFixture.source("di_public","DI_PUBLIC_TEST_PASSWORD");var c=ds.getConnection();var s=c.createStatement()){
            c.setReadOnly(false);try(var r=s.executeQuery("SELECT title FROM api_public.datasets")){assertTrue(r.next(),"Positive control must prove real public data");}
            try(var r=s.executeQuery("SELECT has_table_privilege('api_public.datasets','UPDATE')")){assertTrue(r.next());assertFalse(r.getBoolean(1));}
            for(String sql:new String[]{"SELECT * FROM di_management.dataset LIMIT 1","SELECT * FROM di_management.accessgrant LIMIT 1","SELECT * FROM audit.row_change LIMIT 1","SELECT original_xml FROM di_ops.legacy_origin LIMIT 1","SELECT objectkey FROM di_management.distribution LIMIT 1","UPDATE di_management.dataset SET title='forbidden'","CREATE TABLE api_public.forbidden(id integer)"}){
                var error=assertThrows(SQLException.class,()->s.execute(sql));assertEquals("42501",error.getSQLState(),sql);
            }
        }
    }
}

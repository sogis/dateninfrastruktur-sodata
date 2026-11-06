package ch.so.agi.datenportal.catalog.database;
import javax.xml.parsers.DocumentBuilderFactory;
import java.nio.file.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class DatabaseCatalogModeIT {
    @Test void twoRealInstancesReadCommittedAdminChangesWithoutSnapshotAndFailClosedOnDatabaseOutage()throws Exception{
        var data=DatabaseFixture.data();try(var first=DatabaseFixture.start();var second=DatabaseFixture.start();var admin=DatabaseFixture.source("postgres","DI_PUBLIC_TEST_ADMIN_PASSWORD")){
            String a=DatabaseFixture.base(first),b=DatabaseFixture.base(second),path="/datasets/"+data.get("openIdentifier");
            assertEquals(200,DatabaseFixture.get(a,path).statusCode());assertEquals(200,DatabaseFixture.get(b,"/actuator/health/readiness").statusCode());
            assertAdminMutation();
            assertTrue(DatabaseFixture.get(a,path).body().contains("Katalog aktualisiert ohne Reload"));
            assertTrue(DatabaseFixture.get(b,path).body().contains("Katalog aktualisiert ohne Reload"));
            try(var c=admin.getConnection();var s=c.createStatement()){
                // This is a dedicated role in this owned database/container, never the user's stack.
                s.execute("ALTER ROLE di_public NOLOGIN");s.execute("SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE usename='di_public' AND datname=current_database() AND pid<>pg_backend_pid()");
            }
            try{
                assertEquals(503,DatabaseFixture.get(a,path).statusCode());assertEquals(503,DatabaseFixture.get(b,path).statusCode());assertEquals(503,DatabaseFixture.get(a,"/actuator/health/readiness").statusCode());assertEquals(200,DatabaseFixture.get(a,"/actuator/health/liveness").statusCode());
            }finally{try(var c=admin.getConnection();var s=c.createStatement()){s.execute("ALTER ROLE di_public LOGIN");}}
        }
    }

    private static void assertAdminMutation() throws Exception {
        var source = Path.of(System.getenv("DI_ADMIN_SOURCE")).toRealPath();
        var fixture = Path.of(System.getenv("DI_PORTAL_FIXTURE")).toRealPath();
        var command = new ProcessBuilder("./gradlew", "integrationTest", "--tests", "*DatabaseCatalogMutationIT", "--console=plain")
            .directory(source.toFile()).redirectErrorStream(true);
        command.environment().put("DI_PORTAL_FIXTURE", fixture.toString());
        command.environment().put("DI_TEST_URL", System.getenv("DI_PUBLIC_TEST_URL"));
        var process = command.start();
        var output = process.getInputStream().readAllBytes();
        assertEquals(0, process.waitFor(), "Admin Cayenne mutation failed in the isolated Gradle test");
        assertTrue(output.length > 0, "Expected output from the Admin test process");
        var result = source.resolve("build/test-results/integrationTest/TEST-ch.so.agi.dateninfrastruktur.admin.DatabaseCatalogMutationIT.xml");
        var suite = DocumentBuilderFactory.newDefaultInstance().newDocumentBuilder().parse(result.toFile()).getDocumentElement();
        assertEquals("1", suite.getAttribute("tests"));
        assertEquals("0", suite.getAttribute("skipped"));
        assertEquals("0", suite.getAttribute("failures"));
        assertEquals("0", suite.getAttribute("errors"));
    }
}

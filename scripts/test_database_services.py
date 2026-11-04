#!/usr/bin/env python3
"""Real portal tests against the exact explicitly owned ADMIN harness, never user configuration."""
import importlib.util,json,os,pathlib,subprocess,sys
R=pathlib.Path(__file__).resolve().parents[1];admin=R.parent/'dateninfrastruktur-admin'
sys.path.insert(0,str(admin/'scripts'))
from management_db import state,D
st=state();roles=json.loads((D/'roles.json').read_text());env=os.environ.copy()
env.update(DI_PUBLIC_TEST_URL='jdbc:postgresql://127.0.0.1:'+st['port']+'/dateninfrastruktur_management?socketTimeout=3&connectTimeout=2',DI_PUBLIC_TEST_PASSWORD=roles['di_public'],DI_PUBLIC_TEST_ADMIN_PASSWORD=st['password'],DI_PORTAL_FIXTURE=str(admin/'.runtime/portal-fixture.json'),DI_ADMIN_SOURCE=str(admin),PLAYWRIGHT_SKIP_BROWSER_GC='1')
args=[str(R/'gradlew'),'--no-daemon','managementDatabaseTest']
for pattern in sys.argv[1:] or ['*JdbcCatalogRepositoryIT','*PublicCatalogRoleIT']:args+=['--tests',pattern]
p=subprocess.run(args,cwd=R,env=env,capture_output=True,text=True);log=p.stdout+p.stderr
for secret in [st['password'],*roles.values()]:log=log.replace(secret,'[REDACTED]')
(R/'.runtime/database-services.log').write_text(log);print(log[-8000:]);sys.exit(p.returncode)

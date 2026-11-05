# Sodata-Containerbuild

Dockerfile-Ziel `jvm-runtime` ist das im gemeinsamen Compose-Projekt
verwendete Image. Java 25 und vorkompilierte JTE-Templates sind enthalten.
Das vorhandene Standardziel baut das GraalVM-Native-Image. Beide Builds
benötigen keine Node- oder Browserruntime. Laufzeitparameter stehen in
[Konfiguration](configuration.md); Inbetriebnahme und Integration erfolgen
über den [Stack-Betrieb](../../dateninfrastruktur-dev-stack/docs/operations.md).

```sh
docker build --target jvm-runtime -t dateninfrastruktur-sodata-jvm:local .
```

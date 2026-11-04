package ch.so.agi.datenportal.config;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
public final class SnapshotModeCondition implements Condition {
    public boolean matches(ConditionContext context,AnnotatedTypeMetadata metadata){return !"database".equalsIgnoreCase(context.getEnvironment().getProperty("dateninfrastruktur.catalog.source-type",""));}
}

package ch.so.agi.datenportal.config;
import java.lang.annotation.*;
import org.springframework.context.annotation.Conditional;
/** Legacy import/reload components exist only outside the explicit primary-DB mode. */
@Target({ElementType.TYPE,ElementType.METHOD}) @Retention(RetentionPolicy.RUNTIME)
@Conditional(SnapshotModeCondition.class)
public @interface SnapshotMode {}

package ch.so.agi.datenportal.config;

import java.io.IOException;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;

public final class JteRuntimeHints implements RuntimeHintsRegistrar {

    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // JTE loads precompiled page classes by name and their static content as resources.
        hints.resources().registerPattern("gg/jte/generated/precompiled/**/*.bin");
        var resources = new PathMatchingResourcePatternResolver(classLoader);
        var metadata = new CachingMetadataReaderFactory(classLoader);
        try {
            for (Resource template : resources.getResources("classpath*:gg/jte/generated/precompiled/**/*.class")) {
                String className = metadata.getMetadataReader(template).getClassMetadata().getClassName();
                hints.reflection().registerTypeIfPresent(classLoader, className,
                        MemberCategory.INVOKE_DECLARED_CONSTRUCTORS,
                        MemberCategory.INVOKE_PUBLIC_METHODS,
                        MemberCategory.ACCESS_PUBLIC_FIELDS);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to register precompiled JTE templates for native image", exception);
        }
    }
}

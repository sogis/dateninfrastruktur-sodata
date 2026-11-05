package ch.so.agi.datenportal.config;

import java.util.List;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityHeadersConfigurationTest {
    @Test void doesNotEnableBrowserRuntimeEvaluation() {
        var config = new SecurityHeadersConfiguration(new SecurityCspProperties(null));
        assertThat(config.csp()).contains("script-src 'self';", "connect-src 'self';", "frame-ancestors 'none'")
                .doesNotContain("unsafe-eval", "blob:");
    }

    @Test void allowsExplicitConnectOriginsWithoutDirectiveInjection() {
        var config = new SecurityHeadersConfiguration(new SecurityCspProperties(List.of("'self'", "https://example.org")));
        assertThat(config.csp()).contains("connect-src 'self' https://example.org;");
        assertThatThrownBy(() -> new SecurityCspProperties(List.of("https://example.org; script-src *")))
                .isInstanceOf(IllegalArgumentException.class);
    }
}

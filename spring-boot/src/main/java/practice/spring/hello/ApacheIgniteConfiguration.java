package practice.spring.hello;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApacheIgniteConfiguration {
    @Bean
    public IgniteConfiguration igniteConfiguration() {
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName("test-ignite");
        return cfg;
    }

    @Bean
    Ignite ignite(IgniteConfiguration igniteConfiguration) {
        return Ignition.start(igniteConfiguration);
    }

    @Bean
    IgniteCache<Integer, String> userCache(Ignite ignite) {
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("userCache");
        // Dummy "users"
        cache.put(1, "User One");
        cache.put(2, "User Two");
        return cache;
    }
}

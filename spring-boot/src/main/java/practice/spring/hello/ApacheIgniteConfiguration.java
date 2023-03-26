package practice.spring.hello;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialize a simple in-memory Apache Ignite cache.
 */
@Configuration
public class ApacheIgniteConfiguration {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    IgniteConfiguration igniteConfiguration() {
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
        logger.info("Dummy cache loaded");
        return cache;
    }
}

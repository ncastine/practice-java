package practice.spring.hello;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import practice.map_struct.Employee;

/**
 * Initialize a simple in-memory Apache Ignite cache.
 */
@Configuration
@Profile("!no-ignite")
public class ApacheIgniteConfiguration {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Bean
    IgniteConfiguration igniteConfiguration() {
        // Just local for now
        IgniteConfiguration cfg = new IgniteConfiguration();
        cfg.setIgniteInstanceName("test-ignite");
        return cfg;
    }

    @Bean
    Ignite ignite(IgniteConfiguration igniteConfiguration) {
        return Ignition.start(igniteConfiguration);
    }

    @Bean
    IgniteCache<String, Employee> userCache(Ignite ignite) {
        IgniteCache<String, Employee> cache = ignite.getOrCreateCache("userCache");
        if (cache.size() == 0) {
            // Dummy "users"
            Employee employee1 = new Employee();
            employee1.id = "1";
            employee1.name = "User One";
            employee1.department = "Dummy";
            cache.put(employee1.id, employee1);
            Employee employee2 = new Employee();
            employee2.id = "2";
            employee2.name = "User Two";
            employee2.department = employee1.department;
            cache.put(employee2.id, employee2);
            logger.info("Dummy cache loaded");
        }
        return cache;
    }
}

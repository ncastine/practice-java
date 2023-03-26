package practice.spring.hello;

import java.util.Arrays;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
public class Application {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @RequestScope
    Callable<String> personalMessage(HttpServletRequest request) {
        logger.debug("Request scope called {}", request);
        final String user = request.getRemoteUser();
        return () -> user == null ? null : "I see you are " + user;
    }

    /**
     * Registering a command line runner Bean just so it gets run after
     * everything is wired.
     */
    @Bean
    CommandLineRunner debugListBeans(ApplicationContext context) {
        return args -> {
            // Note if any command arguments provided
            logger.debug("Command line arguments: {}", Arrays.asList(args));
            // Print all Beans at debug level.
            // DO NOT evaluate context until command line runs.
            logger.debug("All registered Beans...");
            Arrays.stream(context.getBeanDefinitionNames()).sorted()
                    .forEach(b -> logger.debug("{}", b));
        };
    }
}

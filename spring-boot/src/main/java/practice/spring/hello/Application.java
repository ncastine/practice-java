package practice.spring.hello;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.context.annotation.RequestScope;

import javax.servlet.http.HttpServletRequest;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    @RequestScope
    public Callable<String> personalMessage(HttpServletRequest request) {
        System.out.println("Request scope called " + request);
        return () -> "Test " + request.getRemoteUser();
    }

    /**
     * Registering a command line runner Bean just so it gets run after
     * everything is wired.
     */
    @Bean
    public CommandLineRunner debugListBeans(ApplicationContext context, Environment environment) {
        return args -> {
            // Only print Beans if debug profile is set.
            // Do not evaluate context until command line runs.
            if (Arrays.stream(environment.getActiveProfiles()).collect(Collectors.toSet()).contains("debug")) {
                if (args != null && args.length > 0) {
                    System.out.println("Arguments: " +  String.join(", ", Arrays.asList(args)));
                }
                System.out.println("All registered Beans...");
                String[] beanNames = context.getBeanDefinitionNames();
                Arrays.sort(beanNames);
                for (String beanName : beanNames) {
                    System.out.println(beanName);
                }
            }
        };
    }
}

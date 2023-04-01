package practice.spring.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dirt simple web security configuration with form login.
 */
@Configuration
public class SecurityConfiguration {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Name of users file to load on startup.
     */
    private static final String USERS_FILE = "users.properties";

    /**
     * Prioritized list of paths to search for users file to load.
     */
    private static final List<String> USERS_FILE_PATHS = List.of(
            System.getProperty("user.home") + "/.config/spring-playground/"
                    + USERS_FILE, "target/" + USERS_FILE, USERS_FILE);

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth ->
                        auth.antMatchers("/", "/home").permitAll()
                                .anyRequest().authenticated())
                .formLogin()
                .loginPage("/login")
                .permitAll()
                .and()
                .logout()
                .permitAll();
        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        // Disable security on endpoints where it is not needed
        return web -> web.ignoring().antMatchers("/user-cache");
    }

    @SuppressWarnings("deprecation")
    @Bean
    UserDetailsService userDetailsService() {
        final List<UserDetails> users = readUsers();
        if (users.isEmpty()) {
            logger.info("Creating default user");
            return new InMemoryUserDetailsManager(
                    User.withDefaultPasswordEncoder()
                            .username("user1")
                            .password("password")
                            .roles("USER").build());
        }
        return new InMemoryUserDetailsManager(users);
    }

    /**
     * Read users from file.
     */
    private List<UserDetails> readUsers() {
        logger.debug("Searching for users file...");
        for (String path : USERS_FILE_PATHS) {
            if (Files.exists(Path.of(path))) {
                logger.info("Loading {} ...", path);
                try (InputStream stream = new FileInputStream(path)) {
                    final Properties properties = new Properties();
                    properties.load(stream);
                    final List<UserDetails> users = readUsers(properties);
                    if (!users.isEmpty()) {
                        logger.info("Read {} users", users.size());
                        return users;
                    }
                } catch (IOException e) {
                    logger.error("Failed to read file: {}", path, e);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Read users from properties.
     * <p>Properties format is:
     * <pre>{@code
     * user1.password=something
     * user1.roles=USER,ADMIN
     * user2.password=something
     * user2.roles=USER
     * }</pre>
     */
    @SuppressWarnings("deprecation")
    private List<UserDetails> readUsers(Properties properties) {
        // First pass just to get unique usernames
        final Set<String> names = properties.keySet().stream()
                .filter(Objects::nonNull).map(Object::toString)
                .map(s -> s.split("\\.")[0])
                .collect(Collectors.toSet());
        // Only consider users if they have a roles
        return names.stream().map(n -> {
            final String roles = properties.getProperty(n + ".roles");
            if (roles != null && ! roles.trim().isEmpty()) {
                final String password = properties.getProperty(n + ".password");
                final User.UserBuilder builder = User.withDefaultPasswordEncoder()
                        .username(n).roles(Arrays.stream(roles.split(","))
                                .map(String::trim).toArray(String[]::new));
                if (password != null && !password.isEmpty()) {
                    builder.password(password);
                }
                return builder.build();
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }
}

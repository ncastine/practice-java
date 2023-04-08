package practice.spring.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

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

import static java.util.Collections.singletonList;

/**
 * DO NOT use this in production applications! Loads users from a
 * prioritized list of potential file paths on disk.
 */
public class FileLoadUserDetailsService extends InMemoryUserDetailsManager {
    private static final Logger LOG = LoggerFactory.getLogger(FileLoadUserDetailsService.class);

    /**
     * Construct a user details service that loads from potential file paths.
     * @param paths prioritized list of paths to search for users file to load
     */
    public FileLoadUserDetailsService(List<String> paths) {
        super(readOrCreateUsers(paths));
    }

    /**
     * Attempt to read users from file. Generate default user if none found.
     */
    @SuppressWarnings("deprecation")
    private static List<UserDetails> readOrCreateUsers(List<String> paths) {
        final List<UserDetails> users = readUsers(paths);
        if (users.isEmpty()) {
            LOG.info("Creating default user");
            return singletonList(User.withDefaultPasswordEncoder()
                    .username("user1")
                    .password("password")
                    .roles("USER").build());
        }
        return users;
    }

    /**
     * Read users from file.
     */
    private static List<UserDetails> readUsers(List<String> paths) {
        LOG.debug("Searching for users file...");
        for (String path : paths) {
            if (Files.exists(Path.of(path))) {
                LOG.info("Loading {} ...", path);
                try (InputStream stream = new FileInputStream(path)) {
                    final Properties properties = new Properties();
                    properties.load(stream);
                    final List<UserDetails> users = readUsers(properties);
                    if (!users.isEmpty()) {
                        LOG.info("Read {} users", users.size());
                        return users;
                    }
                } catch (IOException e) {
                    LOG.error("Failed to read file: {}", path, e);
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
    private static List<UserDetails> readUsers(Properties properties) {
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

package practice.spring.hello;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Collections.singletonList;

/**
 * DO NOT use this in production applications! Loads users from a
 * prioritized list of potential file paths on disk.
 */
public class FileLoadUserDetailsService extends InMemoryUserDetailsManager {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Prioritized list of paths to search for users file to load. Must
     * include filename within each.
     */
    private final List<String> userFilePaths;

    /**
     * Construct a user details service that can load users from potential
     * file paths.
     * @param userFilePaths prioritized list of paths to search for users
     * file to load. Must include filename within each.
     */
    public FileLoadUserDetailsService(List<String> userFilePaths) {
        this.userFilePaths = userFilePaths;
    }

    /**
     * Attempt to load users from the prioritized list of search paths.
     * Stops on first successful file read.
     * @return number of users read
     */
    public int loadUsers() throws IOException {
        final List<NonHashedUserDetails> users = loadRawUsers();
        // Add all for in-memory usage
        users.forEach(this::createUser);
        return users.size();
    }

    /**
     * Generate some users. Writes them to first non-existent file in the
     * prioritized list, where the parent path is writable.
     */
    public void generateUsers(int count) throws IOException {
        logger.info("Generating {} users", count);
        List<NonHashedUserDetails> generatedUsers = new ArrayList<>(count);
        for (int i = 1; i <= count; i++) {
            // First user gets admin role
            final List<String> roles = i == 1 ? Arrays.asList("USER", "ADMIN") : singletonList("USER");
            // Randomized password. Just first section of a UUID
            generatedUsers.add(new NonHashedUserDetails("user" + i, UUID.randomUUID().toString().split("-")[0], roles));
        }
        // Add all for in-memory usage
        generatedUsers.forEach(this::createUser);
        // Write to non-existent file in writable path
        if (writeUsers(generatedUsers)) {
            logger.info("Wrote generated users");
        } else {
            logger.warn("Failed to write generated users");
        }
    }

    /**
     * Add the given user to the in-memory manager.
     */
    private void createUser(NonHashedUserDetails user) {
        // Call method in parent class. Convert format.
        createUser(toUserDetails(user));
    }

    /**
     * Attempt to read users from prioritized file paths.
     */
    private List<NonHashedUserDetails> loadRawUsers() throws IOException {
        logger.debug("Searching for users file...");
        final ObjectMapper mapper = new ObjectMapper();
        for (String path : userFilePaths) {
            final Path filePath = Path.of(path);
            if (Files.isReadable(filePath)) {
                logger.info("Loading {} ...", path);
                final List<NonHashedUserDetails> users = loadUsers(filePath, mapper);
                if (!users.isEmpty()) {
                    logger.info("Read {} users", users.size());
                    return users;
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Read users from given JSON file.
     */
    private List<NonHashedUserDetails> loadUsers(Path filePath, ObjectMapper mapper) throws IOException {
        return mapper.readValue(filePath.toFile(), new TypeReference<>(){});
    }

    /**
     * Write generated users to first writable path.
     * @return true if a file is written successfully
     */
    private boolean writeUsers(List<NonHashedUserDetails> users) throws IOException {
        for (String path : userFilePaths) {
            final Path fullPath = Path.of(path);
            // Ensure the directory exists and is writable
            final Path directory = fullPath.getParent();
            logger.debug("Checking directory: {}", directory);
            if (Files.notExists(fullPath) && Files.isWritable(directory)) {
                writeUsers(fullPath, users);
                return true;
            }
        }
        return false;
    }

    /**
     * Write generated users to a JSON file.
     */
    private void writeUsers(Path filePath, List<NonHashedUserDetails> users) throws IOException {
        new ObjectMapper().writeValue(filePath.toFile(), users);
    }

    /**
     * Convert to user format needed by Spring in-memory.
     */
    @SuppressWarnings("deprecation")
    private UserDetails toUserDetails(NonHashedUserDetails user) {
        return User.withDefaultPasswordEncoder()
                .username(user.getName())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(new String[0])).build();
    }
}

package practice.spring.hello;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import practice.mapStruct.Employee;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Intentionally avoiding REST testing here since that would complicate the configuration.
 * This is still an integration test since it uses a real (in-memory) Ignite instance.
 */
@SpringBootTest(classes = { UserCacheController.class, ApacheIgniteConfiguration.class } )
public class UserCacheControllerIT {

    @Autowired
    private UserCacheController controller;

    @Test
    public void index() {
        Map<String, Employee> response = controller.index();
        assertNotNull(response);
        // Dummy data is installed
        assertFalse(response.isEmpty());
    }
}

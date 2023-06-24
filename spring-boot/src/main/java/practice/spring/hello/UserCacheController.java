package practice.spring.hello;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.map_struct.Employee;

import javax.cache.Cache.Entry;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Testing out Ignite caching.
 */
@RestController
@Profile("!no-ignite")
public class UserCacheController {
    private final IgniteCache<String, Employee> userCache;

    @Autowired
    UserCacheController(IgniteCache<String, Employee> userCache) {
        this.userCache = userCache;
    }

    @RequestMapping("/user-cache")
    public Map<String, Employee> index() {
        try (QueryCursor<Entry<String, Employee>> cursor = userCache.query(new ScanQuery<>())) {
            return cursor.getAll().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        }
    }
}

package practice.spring.hello;

import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.ScanQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.cache.Cache.Entry;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Testing out Ignite caching
 */
@RestController
public class IgniteUserCacheController {
    private final IgniteCache<Integer, String> userCache;

    @Autowired
    IgniteUserCacheController(IgniteCache<Integer, String> userCache) {
        this.userCache = userCache;
    }

    @RequestMapping("/user-cache")
    public Map<Integer, String> index() {
        try (QueryCursor<Entry<Integer, String>> cursor = userCache.query(new ScanQuery<>())) {
            return cursor.getAll().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        }
    }
}

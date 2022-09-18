package practice.spring.hello;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Confirm {@link Optional} does not fool cache expiration.
 */
public class GuavaCacheOptionalTest {
	/**
	 * Dirt simple cache loader that. Will spy on it to see when it gets called.
	 */
	private static class DummyCacheLoader {
		Optional<Object> load(String key) {
			if ("invalid".equals(key)) {
				return Optional.empty();
			}
			return Optional.of(new Object());
		}
	}

	/**
	 * Cache instance used as test subject.
	 */
	private Cache<String, Optional<Object>> underTest;

	/**
	 * Will be spied on during test.
	 */
	private DummyCacheLoader cacheLoader;

	/**
	 * Calls get on the test cache with loader callback.
	 */
	private Optional<Object> cacheLoaderGet(String key) {
		try {
			return underTest.get(key, () -> cacheLoader.load(key));
		} catch (ExecutionException e) {
			// Should not happen
			throw new RuntimeException(e);
		}
	}

	@BeforeEach
	public void setUp() {
		underTest = CacheBuilder.newBuilder().expireAfterWrite(2, TimeUnit.SECONDS).build();
		cacheLoader = Mockito.spy(new DummyCacheLoader());
	}

	@Test
	public void testCachedHappyResult() {
		final String key = "key" + ThreadLocalRandom.current().nextInt();
		Optional<Object> result = cacheLoaderGet(key);
		assertNotNull(result);
		assertTrue(result.isPresent());
		Optional<Object> retryResult = cacheLoaderGet(key);
		assertNotNull(retryResult);
		assertSame(result, retryResult);
		assertTrue(retryResult.isPresent());
		retryResult = cacheLoaderGet(key);
		assertNotNull(retryResult);
		assertSame(result, retryResult);
		assertTrue(retryResult.isPresent());
		verify(cacheLoader, times(1)).load(eq(key));
		verifyNoMoreInteractions(cacheLoader);
	}

	@Test
	public void testCacheReloadOptionalAfterExpire() throws InterruptedException {
		final String key = "key" + ThreadLocalRandom.current().nextInt();
		Optional<Object> result = cacheLoaderGet(key);
		assertNotNull(result);
		assertTrue(result.isPresent());
		// Wait until expiration
		Thread.sleep(3_000);
		Optional<Object> retryResult = cacheLoaderGet(key);
		assertNotNull(retryResult);
		assertNotSame(result, retryResult);
		assertTrue(retryResult.isPresent());
		verify(cacheLoader, times(2)).load(eq(key));
		verifyNoMoreInteractions(cacheLoader);
	}

	@Test
	public void testCachedAbsentResult() {
		final String key = "invalid";
		Optional<Object> result = cacheLoaderGet(key);
		assertNotNull(result);
		assertFalse(result.isPresent());
		Optional<Object> retryResult = cacheLoaderGet(key);
		assertNotNull(retryResult);
		assertSame(result, retryResult);
		assertFalse(retryResult.isPresent());
		retryResult = cacheLoaderGet(key);
		assertNotNull(retryResult);
		assertSame(result, retryResult);
		assertFalse(retryResult.isPresent());
		verify(cacheLoader, times(1)).load(eq(key));
		verifyNoMoreInteractions(cacheLoader);
	}

	@Test
	public void testCacheReloadOptionalAbsentAfterExpire() throws InterruptedException {
		final String key = "invalid";
		Optional<Object> result = cacheLoaderGet(key);
		assertNotNull(result);
		assertFalse(result.isPresent());
		// Wait until expiration
		Thread.sleep(3_000);
		Optional<Object> retryResult = cacheLoaderGet(key);
		assertNotNull(retryResult);
		assertFalse(retryResult.isPresent());
		verify(cacheLoader, times(2)).load(eq(key));
		verifyNoMoreInteractions(cacheLoader);
	}
}

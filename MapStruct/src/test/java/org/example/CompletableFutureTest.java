package org.example;

import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class CompletableFutureTest {
	@Test
	public void test() throws Exception {
		CompletableFuture<String> initial = CompletableFuture.supplyAsync(() -> "supplied");

		assertEquals("suppliedAppended", initial.thenApplyAsync(s -> s + "Appended").get());
	}

	@Test
	public void testExceptionThrown() throws Exception {
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> { throw new RuntimeException("Test exception in background"); });

		final AtomicBoolean secondStageRan = new AtomicBoolean();

		future.whenCompleteAsync((r, e) -> secondStageRan.set(true));

		try {
			future.get();
			fail("Should have thrown exception");
		} catch (ExecutionException e) {
			assertTrue(e.getCause() instanceof RuntimeException);
			assertEquals("Test exception in background", e.getCause().getMessage());
		}

		assertTrue(secondStageRan.get(), "Second stage should have run");
	}
}

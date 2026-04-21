package com.zxy.examples.concurrent.jdk21;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Jdk21ConcurrencyCaseTest {

    @Test
    public void testJdk21CaseWithFallback() {
        Jdk21ConcurrencyCase example = new Jdk21ConcurrencyCase();
        Jdk21ConcurrencyCase.ExecutionSummary summary = example.processBatch(Arrays.asList(10, 20, 30));

        assertEquals(3, summary.getResult().size());
        assertTrue(summary.getResult().contains("jdk21-task-10"));
        if (Jdk21ConcurrencyCase.isVirtualThreadSupported()) {
            assertEquals("virtual-thread", summary.getMode());
        } else {
            assertEquals("platform-thread", summary.getMode());
        }
    }
}


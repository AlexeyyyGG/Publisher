package com.cloud.publishing.backend;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class FailingTest {
    @Test
    void intentionallyFailingTest() {
        assertTrue(false);
    }
}

package com.cloud.publishing.itest.context;

import java.util.HashMap;
import java.util.Map;

public class TestContext {
    private static final TestContext CONTEXT = new TestContext();
    private final Map<String, Object> values = new HashMap<>();

    private TestContext() {
    }

    public static TestContext getContext() {
        return CONTEXT;
    }

    public <T> void put(String key, T value) {
        values.put(key, value);
    }

    public <T> T get(String key, Class<T> type) {
        return type.cast(values.get(key));
    }

    public void clear() {
        values.clear();
    }
}
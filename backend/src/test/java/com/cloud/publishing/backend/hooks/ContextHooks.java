package com.cloud.publishing.backend.hooks;

import com.cloud.publishing.backend.context.TestContext;
import io.cucumber.java.Before;

public class ContextHooks {
    @Before
    public void clearContext() {
        TestContext.getContext().clear();
    }
}
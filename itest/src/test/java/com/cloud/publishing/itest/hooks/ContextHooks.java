package com.cloud.publishing.itest.hooks;

import com.cloud.publishing.itest.context.TestContext;
import io.cucumber.java.Before;

public class ContextHooks {
    @Before
    public void clearContext() {
        TestContext.getContext().clear();
    }
}
package com.cloud.publishing.itest;

import org.junit.platform.console.ConsoleLauncher;

public class Main {
    public static void main(String[] args) {
        ConsoleLauncher.main(
                "execute",
                "--select-class",
                "com.cloud.publishing.itest.runners.RunTest"
        );
    }
}
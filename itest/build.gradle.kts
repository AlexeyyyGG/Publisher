plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks.jar {
    enabled = false
}

dependencies {
    implementation(project(":common"))
    implementation(libs.jackson.databind)
    implementation(libs.mysql.connector)
    implementation("org.springframework.security:spring-security-crypto:7.1.0")
    implementation("org.junit.platform:junit-platform-console-standalone:1.13.4")
    implementation(libs.cucumber.java)
    implementation(libs.cucumber.junit)
    implementation(libs.rest.assured)
    implementation(libs.assertj.core)
}

tasks.shadowJar {
    archiveClassifier.set("")
    from(sourceSets.test.get().output)
    mergeServiceFiles()
    manifest {
        attributes("Main-Class" to "com.cloud.publishing.itest.Main")
    }
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.test {
    enabled = false
}
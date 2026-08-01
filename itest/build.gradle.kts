import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":backend"))
    implementation(project(":common"))
    implementation(libs.mysql.connector)
    implementation("org.springframework.security:spring-security-crypto:7.1.0")
    implementation("org.junit.platform:junit-platform-console-standalone:1.13.4")
    implementation(libs.cucumber.java)
    implementation(libs.cucumber.junit)
    implementation(libs.rest.assured)
    implementation(libs.assertj.core)
}


tasks.named<JavaCompile>("compileJava") {
    dependsOn(":backend:shadowJar")
}

tasks.register<ShadowJar>("itestJar") {
    archiveFileName.set("itest.jar")
    from(sourceSets.main.get().output)
    from(sourceSets.test.get().output)
    configurations = listOf(project.configurations.runtimeClasspath.get())
    mergeServiceFiles()
    isZip64 = true
    manifest {
        attributes("Main-Class" to "com.cloud.publishing.itest.Main")
    }
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
}
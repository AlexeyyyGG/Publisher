plugins {
    id("java")
}

dependencies {
    implementation(project(":backend"))
    implementation(project(":common"))
    testImplementation(libs.rest.assured)
    testImplementation(libs.cucumber.java)
    testImplementation(libs.cucumber.junit)
    testImplementation(libs.junit.suite)
    testImplementation(libs.assertj.core)
}

tasks.test {
    useJUnitPlatform()
}
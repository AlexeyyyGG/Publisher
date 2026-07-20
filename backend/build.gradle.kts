plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "com.cloud.publishing.backend.Application"
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":model"))
    implementation(libs.spring.context)
    implementation(libs.spring.web)
    implementation(libs.spring.webmvc)
    implementation(libs.spring.jdbc)
    implementation(libs.springSecurity.web)
    implementation(libs.springSecurity.config)
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.mapstruct)
    implementation(libs.hibernate.validator)
    implementation(libs.jackson.databind)
    compileOnly(libs.servlet.api)
    implementation(libs.tomcat.jasper)
    implementation("com.mysql:mysql-connector-j:8.3.0")
    implementation("com.zaxxer:HikariCP:7.0.2")
    implementation("ch.qos.logback:logback-classic:1.5.6")
}

tasks.test {
    useJUnitPlatform()
}
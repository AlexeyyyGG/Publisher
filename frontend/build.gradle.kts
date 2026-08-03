plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

tasks.jar {
    enabled = false
}

dependencies {
    implementation(project(":common"))
    implementation(project(":model"))
    implementation(libs.spring.context)
    implementation(libs.spring.web)
    implementation(libs.spring.webmvc)
    implementation(libs.springSecurity.web)
    implementation(libs.springSecurity.config)
    annotationProcessor(libs.mapstruct.processor)
    implementation(libs.mapstruct)
    implementation(libs.jackson.databind)
    implementation(libs.hibernate.validator)
    implementation(libs.servlet.api)
    implementation(libs.tomcat.jasper)
    implementation(libs.tomcat.el)
    implementation("org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1")
    implementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0")
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.eclipse.jdt:ecj:3.33.0")
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes["Main-Class"] = "com.cloud.publishing.frontend.Application"
    }
    mergeServiceFiles()
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}
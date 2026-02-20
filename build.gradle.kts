import org.gradle.api.plugins.quality.CheckstyleExtension;
plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("checkstyle")
    id("jacoco")
    id("org.sonarqube") version "7.2.0.6526"
}

configure<CheckstyleExtension> {
    toolVersion = "10.12.4"
    isIgnoreFailures = false
}

sonarqube {
	properties {
        property("sonar.projectKey", "advprog-2026-A18-project_MySawit-Manajemen_Kebun_Sawit")
        property("sonar.organization", "advprog-2026-a18-project")
        property("sonar.host.url", "https://sonarcloud.io")
	}
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.named("jacocoTestReport"))
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
    }
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"
description = "MySawit-Manajemen_Kebun_Sawit"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:9.22.0")
    implementation("org.postgresql:postgresql:42.7.3")
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-thymeleaf-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Ensure test task uses JUnit Platform

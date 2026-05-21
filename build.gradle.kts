import org.gradle.api.plugins.quality.CheckstyleExtension

val jjwtVersion = "0.12.6"
val dotenvVersion = "4.0.0"
val grpcVersion = "1.73.0"
val protobufVersion = "4.31.1"

plugins {
    java
    id("org.springframework.boot") version "4.0.2"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.google.protobuf") version "0.9.5"
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
        property("sonar.exclusions", "**/application-prod.yml,**/application-local.yml,**/application.properties")
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

// Enable dependency locking for all configurations
dependencyLocking {
    lockAllConfigurations()
}

repositories {
    mavenCentral()
}

dependencies {
    // implementation
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("me.paulschwarz:spring-dotenv:$dotenvVersion")
    implementation("io.jsonwebtoken:jjwt-api:$jjwtVersion")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core:9.22.0")
    implementation("org.postgresql:postgresql:42.7.3")
    implementation("com.h2database:h2")
    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-netty-shaded:$grpcVersion")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")

    // compileOnly
    compileOnly("org.projectlombok:lombok")
    compileOnly("org.apache.tomcat:annotations-api:6.0.53")

    // runtimeOnly
    runtimeOnly("io.jsonwebtoken:jjwt-impl:$jjwtVersion")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:$jjwtVersion")

    // annotationProcessor
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // developmentOnly
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // testImplementation
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testImplementation("org.junit.jupiter:junit-jupiter-params")

    // testRuntimeOnly
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:$protobufVersion"
    }
    plugins {
        create("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                create("grpc")
            }
        }
    }
}

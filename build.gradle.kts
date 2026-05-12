import org.gradle.api.JavaVersion
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

plugins {
    base
    jacoco
    id("com.diffplug.spotless") version "7.2.1"
    id("org.sonarqube") version "6.3.1.5724"
    id("io.spring.dependency-management") version "1.1.7"
}

val springBootVersion = "3.5.0"
val testcontainersVersion = "1.21.1"

allprojects {
    group = "com.travelcx.recovery"
    version = "0.1.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    if (path in setOf(":apps", ":libs")) {
        return@subprojects
    }

    apply(plugin = "java")
    apply(plugin = "jacoco")
    apply(plugin = "checkstyle")
    apply(plugin = "io.spring.dependency-management")

    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:$springBootVersion")
            mavenBom("org.testcontainers:testcontainers-bom:$testcontainersVersion")
        }
    }

    dependencies {
        add("testImplementation", "org.junit.jupiter:junit-jupiter")
        add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher")
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
    }

    extensions.configure<CheckstyleExtension> {
        toolVersion = "10.26.1"
        configFile = rootProject.file("config/checkstyle/checkstyle.xml")
    }

}

tasks.register("qualityCheck") {
    dependsOn(subprojects.map { it.tasks.named("check") })
}

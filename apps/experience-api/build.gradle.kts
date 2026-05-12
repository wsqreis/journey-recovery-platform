plugins {
    id("org.springframework.boot") version "3.5.0"
}

dependencies {
    implementation(project(":libs:domain"))
    implementation(project(":libs:contracts"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

plugins {
    id("org.springframework.boot") version "3.5.0"
}

dependencies {
    implementation(project(":libs:domain"))
    implementation(project(":libs:contracts"))
    testImplementation(project(":libs:testing-support"))

    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.kafka:spring-kafka")
    implementation("io.lettuce:lettuce-core")
    implementation("io.micrometer:micrometer-tracing-bridge-otel")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.kafka:spring-kafka-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:postgresql")
}

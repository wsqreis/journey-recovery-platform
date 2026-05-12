dependencies {
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("jakarta.validation:jakarta.validation-api")
    implementation("org.springframework:spring-core")

    testImplementation("org.assertj:assertj-core")
    testImplementation("com.fasterxml.jackson.core:jackson-databind")
    testImplementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
}

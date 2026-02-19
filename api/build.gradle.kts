plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":core"))
    implementation(project(":data"))

    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("io.ktor:ktor-server-auth:2.3.5")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.5")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
    // ✅ Swagger / OpenAPI
    implementation("io.ktor:ktor-server-swagger:2.3.5")
    implementation("io.ktor:ktor-server-openapi:2.3.5")

    // ✅ Koin для DI (ОБЯЗАТЕЛЬНО!)
    implementation("io.insert-koin:koin-ktor:3.5.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.0")

    // Тесты
    testImplementation("io.ktor:ktor-server-tests:2.3.5")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:postgresql:1.19.3")
    testImplementation("io.mockk:mockk:1.13.8")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("com.zaxxer:HikariCP:5.0.1")
    testImplementation("org.flywaydb:flyway-core:9.22.3")
    testImplementation("org.jetbrains.exposed:exposed-core:0.44.0")
    testImplementation("org.jetbrains.exposed:exposed-dao:0.44.0")
    testImplementation("org.jetbrains.exposed:exposed-jdbc:0.44.0")
    testImplementation("org.jetbrains.exposed:exposed-java-time:0.44.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("com.typesafe:config:1.4.2")
    testImplementation("io.ktor:ktor-server-test-host:2.3.5")
}

tasks.shadowJar {
    archiveClassifier.set("all")
    manifest {
        attributes["Main-Class"] = "com.example.api.ApplicationKt"
    }
}
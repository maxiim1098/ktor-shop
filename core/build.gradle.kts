dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))

    implementation("io.insert-koin:koin-ktor:3.5.0")
    implementation("io.insert-koin:koin-logger-slf4j:3.5.0")
    implementation("io.ktor:ktor-server-core:2.3.5")
    implementation("io.ktor:ktor-server-auth:2.3.5")
    implementation("io.ktor:ktor-server-auth-jwt:2.3.5")
    implementation("io.ktor:ktor-server-netty:2.3.5")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.5")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.5")
    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.15.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    // ✅ Добавляем Redis
    implementation("org.redisson:redisson:3.23.4")
    // ✅ Добавляем RabbitMQ
    implementation("com.rabbitmq:amqp-client:5.20.0")
    // Для работы с корутинами в RabbitMQ
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}
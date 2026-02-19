dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("com.auth0:java-jwt:4.4.0")   // ✅ уже есть
    // Добавь для валидации (опционально)
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
}
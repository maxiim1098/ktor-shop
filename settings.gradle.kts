plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "ktor-shop"
include("domain")
include("api")
include("data")
include("core")

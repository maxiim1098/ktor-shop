# diagnose_build.ps1
$outFile = "full_diagnostic_with_build.log"
if (Test-Path $outFile) { Remove-Item $outFile }

function Write-Section($title) {
    "`n" + ("="*80) | Out-File $outFile -Append
    "=== $title ===" | Out-File $outFile -Append
    ("="*80) | Out-File $outFile -Append
}

Write-Section "ENVIRONMENT"
Get-ComputerInfo | Out-File $outFile -Append
java -version 2>&1 | Out-File $outFile -Append
./gradlew --version 2>&1 | Out-File $outFile -Append

Write-Section "DOCKERFILE"
if (Test-Path "Dockerfile") {
    Get-Content Dockerfile | Out-File $outFile -Append
} else {
    "Dockerfile not found" | Out-File $outFile -Append
}

Write-Section "DOCKER-COMPOSE.YML"
if (Test-Path "docker-compose.yml") {
    Get-Content docker-compose.yml | Out-File $outFile -Append
} else {
    "docker-compose.yml not found" | Out-File $outFile -Append
}

Write-Section "API BUILD.GRADLE.KTS"
if (Test-Path "api/build.gradle.kts") {
    Get-Content api/build.gradle.kts | Out-File $outFile -Append
} else {
    "api/build.gradle.kts not found" | Out-File $outFile -Append
}

Write-Section "CORE BUILD.GRADLE.KTS"
if (Test-Path "core/build.gradle.kts") {
    Get-Content core/build.gradle.kts | Out-File $outFile -Append
} else {
    "core/build.gradle.kts not found" | Out-File $outFile -Append
}

Write-Section "DATA BUILD.GRADLE.KTS"
if (Test-Path "data/build.gradle.kts") {
    Get-Content data/build.gradle.kts | Out-File $outFile -Append
} else {
    "data/build.gradle.kts not found" | Out-File $outFile -Append
}

Write-Section "DOMAIN BUILD.GRADLE.KTS"
if (Test-Path "domain/build.gradle.kts") {
    Get-Content domain/build.gradle.kts | Out-File $outFile -Append
} else {
    "domain/build.gradle.kts not found" | Out-File $outFile -Append
}

Write-Section "SETTINGS.GRADLE.KTS"
if (Test-Path "settings.gradle.kts") {
    Get-Content settings.gradle.kts | Out-File $outFile -Append
} else {
    "settings.gradle.kts not found" | Out-File $outFile -Append
}

Write-Section "GRADLE.PROPERTIES"
if (Test-Path "gradle.properties") {
    Get-Content gradle.properties | Out-File $outFile -Append
} else {
    "gradle.properties not found" | Out-File $outFile -Append
}

Write-Section "APPLICATION.CONF (CORE)"
if (Test-Path "core/src/main/resources/application.conf") {
    Get-Content core/src/main/resources/application.conf | Out-File $outFile -Append
} else {
    "core/src/main/resources/application.conf not found" | Out-File $outFile -Append
}

Write-Section "CURRENT LIBS BEFORE BUILD"
if (Test-Path "api/build/libs") {
    Get-ChildItem api/build/libs | Out-File $outFile -Append
} else {
    "api/build/libs does not exist yet" | Out-File $outFile -Append
}

Write-Section "RUNNING GRADLE SHADOWJAR WITH INFO"
Write-Host "Running gradle :api:shadowJar --info --no-daemon (this may take a while)..." -ForegroundColor Yellow
$gradleOutput = .\gradlew :api:shadowJar --info --no-daemon 2>&1
$gradleOutput | Out-File $outFile -Append

Write-Section "LIBS AFTER BUILD"
if (Test-Path "api/build/libs") {
    Get-ChildItem api/build/libs | Out-File $outFile -Append
} else {
    "api/build/libs still does not exist" | Out-File $outFile -Append
}

Write-Section "END"
Write-Host "Diagnostic completed. Please send file '$outFile' to chat." -ForegroundColor Green
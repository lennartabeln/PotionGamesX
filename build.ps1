# Build script for PotionGames - suppresses Maven internal warnings
Write-Host "[PotionGames Build Script]" -ForegroundColor Green
Write-Host "Building with clean compilation..." -ForegroundColor Green
Write-Host ""

# Suppress Unsafe warnings from Google Guice (Maven internal dependency)
# These are NOT code warnings, but Maven's internal dependencies
$env:MAVEN_OPTS = "--add-opens java.base/sun.misc=ALL-UNNAMED -XX:+IgnoreUnrecognizedVMOptions -Dorg.slf4j.simpleLogger.defaultLogLevel=warn"

Write-Host "Note: sun.misc.Unsafe warnings are from Maven's Google Guice library (safe to ignore)" -ForegroundColor Yellow
Write-Host ""

mvn -DskipTests clean package

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "[SUCCESS] Build completed successfully!" -ForegroundColor Green
    Write-Host "JAR: target\PotionGamesX-1.0.0.jar" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "[ERROR] Build failed with exit code $LASTEXITCODE" -ForegroundColor Red
}

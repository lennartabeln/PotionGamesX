# Build script for PotionGames - suppresses Maven internal warnings
Write-Host "[PotionGames Build Script]" -ForegroundColor Green
Write-Host "Building with clean compilation..." -ForegroundColor Green
Write-Host ""

$env:MAVEN_OPTS = "-XX:+IgnoreUnrecognizedVMOptions --add-opens=java.base/sun.misc=ALL-UNNAMED"

mvn -DskipTests clean package

if ($LASTEXITCODE -eq 0) {
    Write-Host ""
    Write-Host "[SUCCESS] Build completed successfully!" -ForegroundColor Green
    Write-Host "JAR: target\PotionGamesX-1.0.0.jar" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "[ERROR] Build failed with exit code $LASTEXITCODE" -ForegroundColor Red
}

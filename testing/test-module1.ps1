# Module 1 Testing Script
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "TESTING MODULE 1: Cuentas & Preferencias" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

$baseUrl = "http://localhost:8080/api/v1"

# Test 1: Register a new user (US-01)
Write-Host "`n[TEST 1] POST /auth/register - Create new user..." -ForegroundColor Yellow
$registerBody = @{
    email = "testuser@example.com"
    password = "Test123!"
    nombre = "Test User"
} | ConvertTo-Json

try {
    $registerResponse = Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method POST `
        -ContentType "application/json" -Body $registerBody
    Write-Host "✅ User registered successfully" -ForegroundColor Green
    Write-Host "Response: $($registerResponse | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "❌ Registration failed: $($_.Exception.Message)" -ForegroundColor Red
    $_ | Format-List -Force
}

# Test 2: Login (US-02)
Write-Host "`n[TEST 2] POST /auth/login - User login..." -ForegroundColor Yellow
$loginBody = @{
    email = "testuser@example.com"
    password = "Test123!"
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST `
        -ContentType "application/json" -Body $loginBody
    Write-Host "✅ Login successful" -ForegroundColor Green
    Write-Host "Token: $($loginResponse.token.Substring(0,20))..."
    $token = $loginResponse.token
} catch {
    Write-Host "❌ Login failed: $($_.Exception.Message)" -ForegroundColor Red
    $_ | Format-List -Force
}

# Test 3: Get Profile (US-04) - WITHOUT auth since security is disabled
Write-Host "`n[TEST 3] GET /app/profile - Get user profile..." -ForegroundColor Yellow
try {
    $profileResponse = Invoke-RestMethod -Uri "$baseUrl/app/profile" -Method GET
    Write-Host "✅ Profile retrieved successfully" -ForegroundColor Green
    Write-Host "Profile: $($profileResponse | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "❌ Get profile failed: $($_.Exception.Message)" -ForegroundColor Red
    $_ | Format-List -Force
}

# Test 4: Update Profile (US-03, US-04)
Write-Host "`n[TEST 4] PUT /app/profile - Update user profile..." -ForegroundColor Yellow
$updateBody = @{
    nombre = "Updated Test User"
    unidadesMedida = "LBS"
    objetivoActual = "BAJAR_PESO"
    nivelActividadActual = "MODERADO"
    etiquetasSaludIds = @(1, 2)  # Assuming these IDs exist from seed data
} | ConvertTo-Json

try {
    $updateResponse = Invoke-RestMethod -Uri "$baseUrl/app/profile" -Method PUT `
        -ContentType "application/json" -Body $updateBody
    Write-Host "✅ Profile updated successfully" -ForegroundColor Green
    Write-Host "Updated Profile: $($updateResponse | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "❌ Update profile failed: $($_.Exception.Message)" -ForegroundColor Red
    $_ | Format-List -Force
}

# Test 5: Delete Account (US-05)
Write-Host "`n[TEST 5] DELETE /app/profile - Soft delete account..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/app/profile" -Method DELETE
    Write-Host "✅ Account deleted (soft delete) successfully" -ForegroundColor Green
} catch {
    Write-Host "❌ Delete account failed: $($_.Exception.Message)" -ForegroundColor Red
    $_ | Format-List -Force
}

# Test 6: Verify user is inactive after deletion
Write-Host "`n[TEST 6] POST /auth/login - Verify inactive user cannot login..." -ForegroundColor Yellow
try {
    Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method POST `
        -ContentType "application/json" -Body $loginBody
    Write-Host "❌ UNEXPECTED: Inactive user was able to login!" -ForegroundColor Red
} catch {
    Write-Host "✅ Correctly rejected: Inactive user cannot login" -ForegroundColor Green
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "MODULE 1 TESTING COMPLETED" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan

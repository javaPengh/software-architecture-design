Param(
  [switch]$BuildFrontend
)

$ErrorActionPreference = 'Stop'

function Test-PortUsed($port) {
  try {
    $c = Get-NetTCPConnection -LocalPort $port -ErrorAction SilentlyContinue
    return $null -ne $c
  } catch { return $false }
}

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

if (Test-PortUsed 8081) {
  Write-Host 'membership_service already running on :8081' -ForegroundColor Yellow
} else {
  Write-Host 'Starting membership_service on :8081 ...' -ForegroundColor Green
  Start-Job -Name membership_service -ScriptBlock {
    Set-Location "$using:root"
    mvn -q -DskipTests -pl membership_service spring-boot:run
  } | Out-Null
}

if (Test-PortUsed 8082) {
  Write-Host 'old_project already running on :8082' -ForegroundColor Yellow
} else {
  Write-Host 'Starting old_project on :8082 ...' -ForegroundColor Green
  Start-Job -Name old_project -ScriptBlock {
    Set-Location "$using:root"
    mvn -q -DskipTests -pl old_project spring-boot:run
  } | Out-Null
}

if ($BuildFrontend) {
  Write-Host 'Installing frontend deps...' -ForegroundColor Green
  Set-Location (Join-Path $root 'online-booking-system')
  npm install
}

Write-Host 'Starting Vite dev server on :5173 with proxy...' -ForegroundColor Green
Start-Job -Name frontend -ScriptBlock {
  Set-Location (Join-Path $using:root 'online-booking-system')
  npm run dev
} | Out-Null

Write-Host 'All jobs started.' -ForegroundColor Green
Write-Host 'Membership: http://localhost:8081/membership/checkPing'
Write-Host 'Old Project: http://localhost:8082/user/checkPing'
Write-Host 'Frontend: http://localhost:5173/'


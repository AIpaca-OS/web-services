# Ejecutar en Windows PowerShell 5.1/PowerShell 7.
# Requiere Spring Boot local con APP_PROFILES_DEMO_ENABLED=true.
param([string]$BaseUrl = 'http://localhost:8080')
$ErrorActionPreference = 'Stop'
$base = $BaseUrl.TrimEnd('/') + '/api/v1/routes'
$routeId = $null
try {
    Write-Host "Verificando $base ..."
    $api = $BaseUrl.TrimEnd('/') + '/api/v1'
    $driver = Invoke-RestMethod -Uri "$api/profiles/by-account/10002" -Method Get
    $all = Invoke-RestMethod -Uri "${base}?driverProfileId=$($driver.id)" -Method Get
    Write-Host '[OK] GET /api/v1/routes'
    $suffix = (Get-Date -Format 'yyyyMMddHHmmss')
    $input = @{
      driverProfileId = $driver.id
      name = "Ruta temporal UPC $suffix"
      shift = 'MORNING'
      stops = @(@{ address = 'Av. Primavera 123, Surco' })
      schedule = @(@{ dayOfWeek = 'MONDAY'; departureTime = '07:00' })
      scheduleConfirmed = $true
    }
    $body = ConvertTo-Json -InputObject $input -Depth 20
    $created = Invoke-RestMethod -Uri $base -Method Post -ContentType 'application/json' -Body $body
    $routeId = $created.id
    if (-not $routeId) { throw 'POST devolvio respuesta sin ID' }
    Write-Host "[OK] POST /api/v1/routes : $routeId"
    $read = Invoke-RestMethod -Uri "$base/$routeId" -Method Get
    if ($read.name -ne $input.name) { throw 'GET /{id} devolvio nombre diferente' }
    Write-Host '[OK] GET /api/v1/routes/{id}'
    # Preservar ID de la parada al actualizar (identidad usada por asignaciones)
    $input.Remove('driverProfileId')
    $input.name = "Ruta actualizada UPC $suffix"
    $input.stops = @(@{ id = $read.stops[0].id; address = 'Av. Primavera 123, Surco' })
    $body = ConvertTo-Json -InputObject $input -Depth 20
    $updated = Invoke-RestMethod -Uri "$base/$routeId" -Method Put -ContentType 'application/json' -Body $body
    if ($updated.name -ne $input.name) { throw 'PUT no modifico el nombre' }
    Write-Host '[OK] PUT /api/v1/routes/{id}'
    $schedule = @{ schedule = @(@{ dayOfWeek = 'TUESDAY'; departureTime='07:15' }); confirm=$true }
    $body = ConvertTo-Json -InputObject $schedule -Depth 20
    $updated = Invoke-RestMethod -Uri "$base/$routeId/schedule" -Method Put -ContentType 'application/json' -Body $body
    if ($updated.schedule[0].dayOfWeek -ne 'TUESDAY') { throw 'Horario no persistido' }
    Write-Host '[OK] PUT /api/v1/routes/{id}/schedule'
    Write-Host 'No se prueba publicar/asignar: requiere un vehiculo y estudiantes registrados.'
} finally {
    if ($routeId) {
        try { Invoke-RestMethod -Uri "$base/$routeId" -Method Delete | Out-Null; Write-Host '[OK] DELETE /api/v1/routes/{id}' }
        catch { Write-Warning "Revisar ruta de prueba $routeId : $($_.Exception.Message)" }
    }
}

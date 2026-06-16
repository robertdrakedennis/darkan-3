<#
.SYNOPSIS
    Launch the 948-5 NXT client against the local Darkan server (Windows analog of run-client.sh).

.DESCRIPTION
    Mirrors run-client.sh's setup:
      - Sets working directory to ~/.darkan3 (forces cache/user folders there via preferences.cfg)
      - Resolves DARKAN_RSA_MODULUS / DARKAN_JS5_RSA_MODULUS / DARKAN_HTTP_PORT from .env or
        from the EnvVars.kt defaults
      - Invokes darkan_injector.exe to cross-process LoadLibrary the patcher DLL into rs2client.exe

    Pre-reqs to actually log in:
      - JDK 17+ on PATH (project compiles to JDK 25; IntelliJ-managed JDKs at
        ~\.jdks\openjdk-25.0.1 work — set JAVA_HOME before running gradle)
      - MongoDB reachable at mongodb://localhost:27017 (lobby uses it for
        account persistence; override via MONGO_URI in .env)
      - Lobby running: ./gradlew :lobby:run     (serves jav_config.ws at http://127.0.0.1:8829)
      - JS5 server up (lobby starts it)
      - patcher-win artifacts built: client/launcher/patcher-win/build.ps1

.PARAMETER ConfigUri
    The --configURI handed to rs2client.exe. Defaults to the local lobby's HTTP config endpoint.

.PARAMETER ClientBinary
    Path to rs2client.exe. Defaults to <repo>/client/rs2client.exe.

.PARAMETER Injector
    Path to darkan_injector.exe. Defaults to the release build under patcher-win/target.
#>

[CmdletBinding()]
param(
    [string]$ConfigUri = "http://127.0.0.1:8829/jav_config.ws",
    [string]$ClientBinary,
    [string]$Injector
)

$ErrorActionPreference = 'Stop'

# --- Paths --------------------------------------------------------------------
$ProjectDir = $PSScriptRoot
if (-not $ClientBinary) { $ClientBinary = Join-Path $ProjectDir "client\rs2client.exe" }
if (-not $Injector)     { $Injector     = Join-Path $ProjectDir "client\launcher\patcher-win\target\release\darkan_injector.exe" }
$DarkanDir = Join-Path $env:USERPROFILE ".darkan3"

if (-not (Test-Path $ClientBinary)) {
    throw "Client binary not found at $ClientBinary"
}
if (-not (Test-Path $Injector)) {
    throw "Injector not found at $Injector — build via client/launcher/patcher-win/build.ps1"
}

# --- .env loading (parser mirrors run-client.sh's `export "$line"` pattern) ---
$EnvFile = Join-Path $ProjectDir ".env"
$envMap = @{}
if (Test-Path $EnvFile) {
    Write-Host "[run-client] loading $EnvFile"
    foreach ($line in Get-Content $EnvFile) {
        $t = $line.Trim()
        if ($t -eq "" -or $t.StartsWith("#")) { continue }
        $eq = $t.IndexOf("=")
        if ($eq -lt 1) { continue }
        $envMap[$t.Substring(0, $eq).Trim()] = $t.Substring($eq + 1).Trim()
    }
} else {
    Write-Host "[run-client] no .env at $EnvFile — using EnvVars.kt defaults"
}

# --- Defaults mirror core/src/main/kotlin/org/darkan/core/EnvVars.kt ----------
# If these defaults drift from EnvVars.kt the lobby and the patcher will hold
# DIFFERENT RSA keys, login will silently fail with bad decrypt. Keep in sync.
$DefaultLoginDec = "111451331001887558258925086433184522962620313279302629231414920307603218477441933165186052978804326289285676150340879072290510255271162395882754221854449961272233234909169893946939423821658569017156126420112007996521313178880507779240372698073063645756262828071730223699075458202798234856769948825293382469811"
$DefaultJs5Dec   = "607965588982248330233902754760888879244171161418961826903960418513412609766664967574451942600964209987808041308255722791500320489124153698940516919995471220002139601799785952158680888820166280067292720345233736731634993120775356851006057824891699866786027346773480858371704388164471753794304200128393573466002773067682508117814040767925677620462514901062196110607534068452908174613686563526508450752942067149657220307868117668884777486999733376497895071133451664509996931083306160454074974443686914884748192238672150072195674547942839469313907534033197375444776196145933319579262233136997562488850188874395834545929991334208906108182369743279293033798709279624769646397338646881066433314080604090555935009157445196380152335080031142782622168575293272850922070163635389108696373624243025153138996865315048565734441425749040313780033218000924617324891126545204145866635724377015424668843559454127185566450652722973164916810697337987323202282941562751439572720529752776985897334000998969994008572387377023861597284919891061469589489128290776090498500279846599015507628119581019718447034066717290620579835638341917462965858784957333271481729655108715179885916521695451903242151351621566668972400218027343367139044105431340870051476517853"

$LoginDec = if ($envMap.ContainsKey("RSA_LOGIN_MODULUS")) { $envMap["RSA_LOGIN_MODULUS"] } else { $DefaultLoginDec }
$Js5Dec   = if ($envMap.ContainsKey("RSA_JS5_MODULUS"))   { $envMap["RSA_JS5_MODULUS"]   } else { $DefaultJs5Dec }
$HttpPort = if ($envMap.ContainsKey("CONFIG_HTTP_PORT"))  { $envMap["CONFIG_HTTP_PORT"]  } else { "8829" }

# BigInteger.ToString("x") returns lowercase hex; positive numbers may carry a
# leading '0' sign byte. The patcher's pad_modulus_hex left-pads to the target
# length, so trimming the leading zeros here is safe + length-stable.
$LoginHex = [System.Numerics.BigInteger]::Parse($LoginDec).ToString("x").ToLowerInvariant().TrimStart('0')
$Js5Hex   = [System.Numerics.BigInteger]::Parse($Js5Dec).ToString("x").ToLowerInvariant().TrimStart('0')

Write-Host ("[run-client] modulus hex lengths: login={0} (<=256), js5={1} (<=1024)" -f $LoginHex.Length, $Js5Hex.Length)

# --- Darkan working dir + preferences.cfg -------------------------------------
if (-not (Test-Path $DarkanDir)) {
    New-Item -ItemType Directory -Path $DarkanDir | Out-Null
    Write-Host "[run-client] created $DarkanDir"
}
$PrefsPath = Join-Path $DarkanDir "preferences.cfg"
$DarkanDirForPrefs = $DarkanDir -replace '\\', '/'
$PrefsContent = "cache_folder=$DarkanDirForPrefs`nLanguage=0`nuser_folder=$DarkanDirForPrefs`n"
Set-Content -Path $PrefsPath -Value $PrefsContent -Encoding ascii -NoNewline
Write-Host "[run-client] wrote $PrefsPath"

# --- Patcher env vars ---------------------------------------------------------
$env:DARKAN_RSA_MODULUS     = $LoginHex
$env:DARKAN_JS5_RSA_MODULUS = $Js5Hex
$env:DARKAN_HTTP_PORT       = $HttpPort

Write-Host "[run-client] launching"
Write-Host "[run-client]   injector  : $Injector"
Write-Host "[run-client]   target    : $ClientBinary"
Write-Host "[run-client]   configURI : $ConfigUri"
Write-Host "[run-client]   cwd       : $DarkanDir"
Write-Host "[run-client]   port      : $HttpPort"

# --- Launch -------------------------------------------------------------------
Push-Location $DarkanDir
try {
    & $Injector $ClientBinary "--configURI" $ConfigUri
    $exit = $LASTEXITCODE
} finally {
    Pop-Location
}

Write-Host ""
Write-Host "[run-client] injector exited with $exit"
Write-Host "[run-client] patcher log: $env:TEMP\darkan-patcher.log"
exit $exit

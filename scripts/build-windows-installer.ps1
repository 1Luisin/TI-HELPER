param(
    [string]$JavaHome = "C:\Program Files\Eclipse Adoptium\jdk-21.0.11.10-hotspot"
)

$ErrorActionPreference = "Stop"

$repoRoot = Resolve-Path (Join-Path $PSScriptRoot "..")
$appName = "TI Helper - SCMJF"
$appVersion = "0.2.4"
$artifactName = "ti-helper-scmjf-0.2.4-SNAPSHOT.jar"
$iconPath = Join-Path $repoRoot "src\main\resources\br\com\scmjf\tihelper\assets\TIHELPER.ico"
$installerRoot = Join-Path $repoRoot "target\installer"
$inputDir = Join-Path $installerRoot "input"
$appImageRoot = Join-Path $installerRoot "app-image"
$issPath = Join-Path $repoRoot "installer\ti-helper-scmjf.iss"

if (-not (Test-Path (Join-Path $JavaHome "bin\jpackage.exe"))) {
    throw "JDK 21 com jpackage nao encontrado em: $JavaHome"
}

$env:JAVA_HOME = $JavaHome
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Push-Location $repoRoot
try {
    Write-Host "Compilando projeto e copiando dependencias..."
    mvn -q -DskipTests package dependency:copy-dependencies

    if (Test-Path $installerRoot) {
        Remove-Item -LiteralPath $installerRoot -Recurse -Force
    }
    New-Item -ItemType Directory -Path $inputDir | Out-Null
    New-Item -ItemType Directory -Path $appImageRoot | Out-Null

    Copy-Item -LiteralPath (Join-Path $repoRoot "target\$artifactName") -Destination $inputDir
    Copy-Item -Path (Join-Path $repoRoot "target\dependency\*.jar") -Destination $inputDir

    Write-Host "Gerando app-image com runtime Java embutido..."
    jpackage `
        --type app-image `
        --name $appName `
        --app-version $appVersion `
        --vendor "SCMJF" `
        --dest $appImageRoot `
        --icon $iconPath `
        --input $inputDir `
        --main-jar $artifactName `
        --main-class "br.com.scmjf.tihelper.Launcher"

    $isccCommand = Get-Command iscc.exe -ErrorAction SilentlyContinue
    $isccPath = if ($isccCommand) { $isccCommand.Source } else { $null }
    if (-not $isccPath) {
        $localInno = Join-Path $env:LOCALAPPDATA "Programs\Inno Setup 6\ISCC.exe"
        $programFilesInno = "C:\Program Files (x86)\Inno Setup 6\ISCC.exe"
        if (Test-Path $localInno) {
            $isccPath = $localInno
        } elseif (Test-Path $programFilesInno) {
            $isccPath = $programFilesInno
        } else {
            throw "Inno Setup 6 nao encontrado. Instale com: winget install --id JRSoftware.InnoSetup"
        }
    }

    Write-Host "Gerando instalador Setup.exe com Inno Setup..."
    & $isccPath $issPath

    Write-Host ""
    Write-Host "Instalador gerado em:"
    Write-Host (Join-Path $installerRoot "TI Helper - SCMJF Setup.exe")
}
finally {
    Pop-Location
}

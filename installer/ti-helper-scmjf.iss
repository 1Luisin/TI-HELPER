#define AppName "TI Helper - SCMJF"
#define AppVersion "0.2.7"
#define AppPublisher "SCMJF"
#define AppExeName "TI Helper - SCMJF.exe"

[Setup]
AppId={{7F32188D-52F9-45CE-9B17-0B7C224E1E62}
AppName={#AppName}
AppVersion={#AppVersion}
AppPublisher={#AppPublisher}
DefaultDirName={localappdata}\Programs\{#AppName}
DefaultGroupName={#AppName}
DisableProgramGroupPage=yes
OutputDir=..\target\installer
OutputBaseFilename=TI Helper - SCMJF Setup
SetupIconFile=..\src\main\resources\br\com\scmjf\tihelper\assets\TIHELPER.ico
UninstallDisplayIcon={app}\{#AppExeName}
Compression=lzma2
SolidCompression=yes
WizardStyle=modern
PrivilegesRequired=lowest

[Languages]
Name: "brazilianportuguese"; MessagesFile: "compiler:Languages\BrazilianPortuguese.isl"

[Tasks]
Name: "desktopicon"; Description: "Criar atalho na area de trabalho"; GroupDescription: "Atalhos:"; Flags: checkedonce

[Files]
Source: "..\target\installer\app-image\TI Helper - SCMJF\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\{#AppName}"; Filename: "{app}\{#AppExeName}"
Name: "{autodesktop}\{#AppName}"; Filename: "{app}\{#AppExeName}"; Tasks: desktopicon

[Run]
Filename: "{app}\{#AppExeName}"; Description: "Abrir {#AppName}"; Flags: nowait postinstall skipifsilent

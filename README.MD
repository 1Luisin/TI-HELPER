# TI Helper - SCMJF

Protótipo visual desktop em Java 21, Maven e JavaFX/FXML.

## Escopo desta versão

- Login simulado com perfis `ADMIN`, `OPERADOR_TI` e `CONSULTA`.
- Navegação visual por Dashboard, Servidores, Painéis, Serviços, Queries, Scripts, Histórico, Configurações e Sobre.
- Dados mockados com persistência local JSON.
- Logs locais para inicialização, login, logout, erros e fechamento.
- Preferência local para fechar na bandeja ou encerrar totalmente.
- Histórico compartilhado entre ações simuladas de serviços, queries, scripts e cadastros.
- Sem Oracle, PowerShell, BAT, CMD, PS1, servidores reais ou integrações externas.

## Dados locais

A versão `0.3.4-SNAPSHOT` salva dados mockados em:

```text
%LOCALAPPDATA%\TI Helper - SCMJF\data
```

Os logs ficam em:

```text
%LOCALAPPDATA%\TI Helper - SCMJF\logs\app.log
```

## Credenciais mockadas

- `admin` / `admin`
- `ti` / `ti`
- `consulta` / `consulta`

## Executar

```bash
mvn clean javafx:run
```

## Gerar instalador Windows

Pré-requisitos na máquina que gera o instalador:

- JDK 21 com `jpackage`.
- Maven.
- Inno Setup 6.

Instalar Inno Setup 6:

```powershell
winget install --id JRSoftware.InnoSetup
```

Gerar o instalador:

```powershell
.\scripts\build-windows-installer.ps1
```

Saída esperada:

```text
target\installer\TI Helper - SCMJF Setup.exe
```

O instalador gerado leva o runtime Java junto, então o computador de teste não precisa ter Java ou Maven instalados.

## Estrutura

- `controller`: controllers JavaFX das telas.
- `model`: objetos de domínio simples para tabelas e ações.
- `persistence`: persistência local JSON e backup.
- `service`: autenticação, dados e ações simuladas.
- `util`: contexto de sessão, navegação, paths, logs e alertas.
- `resources/view`: telas FXML.
- `resources/styles`: CSS da aplicação.

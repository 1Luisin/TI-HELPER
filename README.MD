# TI Helper - SCMJF

Prototipo visual desktop em Java 21, Maven e JavaFX/FXML.

## Escopo desta versao

- Login simulado com perfis `ADMIN`, `OPERADOR_TI` e `CONSULTA`.
- Navegacao visual por Dashboard, Servidores, Paineis, Servicos, Queries, Scripts, Historico, Configuracoes e Sobre.
- Dados mockados com persistencia local JSON.
- Logs locais para inicializacao, login, logout, erros e fechamento.
- Preferencia local para fechar na bandeja ou encerrar totalmente.
- Historico compartilhado entre acoes simuladas de servicos, queries, scripts e cadastros.
- Sem Oracle, PowerShell, BAT, CMD, PS1, servidores reais ou integracoes externas.

## Dados locais

A versao `0.3.1-SNAPSHOT` salva dados mockados em:

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

Prerequisitos na maquina que gera o instalador:

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

Saida esperada:

```text
target\installer\TI Helper - SCMJF Setup.exe
```

O instalador gerado leva o runtime Java junto, entao o computador de teste nao precisa ter Java ou Maven instalados.

## Estrutura

- `controller`: controllers JavaFX das telas.
- `model`: objetos de dominio simples para tabelas e acoes.
- `persistence`: persistencia local JSON e backup.
- `service`: autenticacao, dados e acoes simuladas.
- `util`: contexto de sessao, navegacao, paths, logs e alertas.
- `resources/view`: telas FXML.
- `resources/styles`: CSS da aplicacao.

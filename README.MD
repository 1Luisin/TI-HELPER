# TI Helper - SCMJF

Protótipo visual desktop em Java 21, Maven e JavaFX/FXML.

## Escopo desta versão

- Login simulado com perfis `ADMIN`, `OPERADOR_TI` e `CONSULTA`.
- Navegação visual por Dashboard, Servidores, Serviços, Queries, Scripts, Histórico e Configurações.
- Dados mockados em memória, sem Oracle, PowerShell, BAT, servidores reais ou integrações externas.
- Histórico compartilhado entre ações simuladas de serviços, queries e scripts.

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
- `model`: objetos de domínio simples para tabelas e ações.
- `service`: autenticação, dados e ações simuladas.
- `util`: contexto de sessão, navegação e alertas.
- `resources/view`: telas FXML.
- `resources/styles`: CSS da aplicação.

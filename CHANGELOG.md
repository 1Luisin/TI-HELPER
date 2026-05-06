# Changelog

## 0.2.3-SNAPSHOT

- Adiciona preferencia local para decidir se o X deixa o app na bandeja ou encerra totalmente.
- Adiciona pergunta inicial sobre comportamento de fechamento e salva a resposta em `config.json`.
- Substitui o menu nativo da bandeja por um pop-up JavaFX estilizado com mais opcoes.

## 0.2.2-SNAPSHOT

- Oculta menus e botoes indisponiveis para o perfil logado em vez de exibi-los desabilitados.
- Remove do pop-up de perfil a opcao de abrir Configuracoes quando o usuario nao tem acesso.
- Oculta atalhos de acao no Dashboard conforme permissao do perfil.

## 0.2.1-SNAPSHOT

- Move informacoes do prototipo de Configuracoes para Sobre.
- Renomeia o menu Diagnostico para Sobre.

## 0.2.0-SNAPSHOT

- Adiciona persistencia local JSON em `AppData/Local/TI Helper - SCMJF/data`.
- Adiciona logs locais em `AppData/Local/TI Helper - SCMJF/logs/app.log`.
- Adiciona tela de Sobre com caminhos locais, ambiente, versao, SO e Java.
- Amplia cadastros administrativos com editar, excluir, limpar campos, backup, importacao e restauracao dos mocks.
- Amplia historico com filtros por tipo, status, usuario e periodo, detalhes do registro e exportacao CSV.
- Mantem todas as acoes como simuladas, sem banco, backend ou execucao remota.

## 0.1.0

- Cria o primeiro prototipo visual JavaFX com login mockado.
- Adiciona dashboard, servidores, servicos, queries, scripts, historico, configuracoes e paineis.
- Adiciona services mockados, permissao por perfil e historico em memoria.
- Adiciona area nao cliente customizada, bandeja do Windows e instalador Windows.

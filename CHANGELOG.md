# Changelog

## 0.3.4-SNAPSHOT

- Atualiza o endereço do busy de produção para `http://172.18.3.109:4005`.
- Abre o busy de produção diretamente no navegador padrão ao clicar no botão da tela de servidores.
- Exibe uma mensagem amigável e registra o erro localmente se o endereço não puder ser aberto.

## 0.3.3-SNAPSHOT

- Moderniza os filtros de data do histórico com campo e botão de calendário integrados.
- Padroniza as barras de rolagem da aplicação com um estilo mais discreto e estados de interação.
- Refina a aparência do calendário aberto, incluindo seleção, data atual e efeitos de foco.

## 0.3.2-SNAPSHOT

- Corrige acentuação e textos em português em telas, menus, tabelas, alertas, histórico, dados mockados e documentação.
- Corrige textos com mojibake em queries simuladas.

## 0.3.1-SNAPSHOT

- Adiciona ao cadastro de serviços a opção de permitir ou bloquear reinício simulado.
- Exibe na tela de Serviços se o reinício está permitido ou bloqueado para cada serviço.
- Exige motivo para executar queries simuladas e registra esse motivo no histórico.

## 0.3.0-SNAPSHOT

- Amplia o cadastro de usuários com nome, e-mail, setor e módulos de acesso.
- Aplica permissão por módulo em conjunto com o perfil do usuário.
- Restringe Histórico e dados de histórico apenas a usuários ADMIN.
- Adiciona na tela Sobre o crédito da equipe de TI Santa Casa com o ano atual.

## 0.2.7-SNAPSHOT

- Corrige o fechamento pelo X para respeitar a preferência salva localmente.
- Mantém o pop-up de escolha apenas quando a configuração estiver como "Perguntar ao fechar".

## 0.2.6-SNAPSHOT

- Aumenta a largura do diálogo de fechamento para evitar textos truncados.
- Define largura mínima para os botões Cancelar, Fechar totalmente e Ficar em segundo plano.

## 0.2.5-SNAPSHOT

- Corrige o botão X da barra customizada para abrir diretamente a pergunta de fechamento.
- Registra o handler de fechamento mesmo quando a bandeja do sistema não puder ser instalada.
- Evita ocultar a janela quando a opção de bandeja for escolhida sem bandeja disponível.

## 0.2.4-SNAPSHOT

- Faz o clique no X sempre abrir a pergunta de fechamento como diálogo central da aplicação.
- Simplifica o menu da bandeja para apenas Abrir janela, Sobre e Sair.
- Remove o pop-up JavaFX customizado da bandeja e volta ao menu nativo do sistema.

## 0.2.3-SNAPSHOT

- Adiciona preferência local para decidir se o X deixa o app na bandeja ou encerra totalmente.
- Adiciona pergunta inicial sobre comportamento de fechamento e salva a resposta em `config.json`.
- Substitui o menu nativo da bandeja por um pop-up JavaFX estilizado com mais opções.

## 0.2.2-SNAPSHOT

- Oculta menus e botões indisponíveis para o perfil logado em vez de exibi-los desabilitados.
- Remove do pop-up de perfil a opção de abrir Configurações quando o usuário não tem acesso.
- Oculta atalhos de ação no Dashboard conforme permissão do perfil.

## 0.2.1-SNAPSHOT

- Move informações do protótipo de Configurações para Sobre.
- Renomeia o menu Diagnóstico para Sobre.

## 0.2.0-SNAPSHOT

- Adiciona persistência local JSON em `AppData/Local/TI Helper - SCMJF/data`.
- Adiciona logs locais em `AppData/Local/TI Helper - SCMJF/logs/app.log`.
- Adiciona tela de Sobre com caminhos locais, ambiente, versão, SO e Java.
- Amplia cadastros administrativos com editar, excluir, limpar campos, backup, importação e restauração dos mocks.
- Amplia histórico com filtros por tipo, status, usuário e período, detalhes do registro e exportação CSV.
- Mantém todas as ações como simuladas, sem banco, backend ou execução remota.

## 0.1.0

- Cria o primeiro protótipo visual JavaFX com login mockado.
- Adiciona dashboard, servidores, serviços, queries, scripts, histórico, configurações e painéis.
- Adiciona services mockados, permissão por perfil e histórico em memória.
- Adiciona área não cliente customizada, bandeja do Windows e instalador Windows.

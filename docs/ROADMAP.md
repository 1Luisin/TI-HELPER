# Roadmap

## Fase 1 - Persistencia local

- Consolidar JSON local para testes em multiplas maquinas.
- Validar backup, importacao e restauracao de dados mockados.
- Melhorar diagnostico local e logs de erro.

## Fase 2 - Contratos API

- Definir DTOs e contratos para substituir services mockados.
- Documentar endpoints previstos para autenticacao, servidores, servicos, queries, scripts, paineis e historico.
- Manter a UI dependendo de interfaces.

## Fase 3 - Backend Spring Boot

- Criar API Spring Boot separada.
- Implementar autenticacao, autorizacao e auditoria.
- Expor endpoints para as telas JavaFX.

## Fase 4 - Oracle

- Planejar conexoes Oracle no backend, nunca direto na UI.
- Criar queries parametrizadas e controladas por perfil.
- Registrar auditoria de consultas.

## Fase 5 - Execucao remota

- Definir mecanismo seguro para acoes remotas.
- Evitar execucao direta de scripts pela aplicacao desktop.
- Registrar solicitacao, aprovacao, resultado e logs.

## Fase 6 - Piloto em producao

- Instalar para grupo pequeno da TI.
- Validar permissao, logs, suporte e rollback.
- Fechar criterios para versao 1.0.0.

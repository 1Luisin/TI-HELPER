package br.com.scmjf.tihelper.model;

public enum TipoAcao {
    LOGIN("Login"),
    LOGOUT("Logout"),
    TESTAR_CONEXAO("Testar conexão"),
    REINICIAR_SERVICO("Reiniciar serviço"),
    EXECUTAR_QUERY("Executar query"),
    EXECUTAR_SCRIPT("Executar script"),
    CADASTRAR_SERVIDOR("Cadastrar servidor"),
    ALTERAR_SERVIDOR("Alterar servidor"),
    EXCLUIR_SERVIDOR("Excluir servidor"),
    CADASTRAR_SERVICO("Cadastrar serviço"),
    ALTERAR_SERVICO("Alterar serviço"),
    EXCLUIR_SERVICO("Excluir serviço"),
    CADASTRAR_QUERY("Cadastrar query"),
    ALTERAR_QUERY("Alterar query"),
    EXCLUIR_QUERY("Excluir query"),
    CADASTRAR_SCRIPT("Cadastrar script"),
    ALTERAR_SCRIPT("Alterar script"),
    EXCLUIR_SCRIPT("Excluir script"),
    CADASTRAR_PAINEL("Cadastrar painel"),
    ALTERAR_PAINEL("Alterar painel"),
    EXCLUIR_PAINEL("Excluir painel"),
    CADASTRAR_USUARIO("Cadastrar usuário"),
    ALTERAR_USUARIO("Alterar usuário"),
    EXCLUIR_USUARIO("Excluir usuário"),
    RESTAURAR_DADOS("Restaurar dados"),
    EXPORTAR_BACKUP("Exportar backup"),
    IMPORTAR_BACKUP("Importar backup");

    private final String displayName;

    TipoAcao(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

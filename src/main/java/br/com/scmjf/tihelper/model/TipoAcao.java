package br.com.scmjf.tihelper.model;

public enum TipoAcao {
    LOGIN("Login"),
    LOGOUT("Logout"),
    TESTAR_CONEXAO("Testar conexao"),
    REINICIAR_SERVICO("Reiniciar servico"),
    EXECUTAR_QUERY("Executar query"),
    EXECUTAR_SCRIPT("Executar script"),
    CADASTRAR_SERVIDOR("Cadastrar servidor"),
    ALTERAR_SERVIDOR("Alterar servidor"),
    EXCLUIR_SERVIDOR("Excluir servidor"),
    CADASTRAR_SERVICO("Cadastrar servico"),
    ALTERAR_SERVICO("Alterar servico"),
    EXCLUIR_SERVICO("Excluir servico"),
    CADASTRAR_QUERY("Cadastrar query"),
    ALTERAR_QUERY("Alterar query"),
    EXCLUIR_QUERY("Excluir query"),
    CADASTRAR_SCRIPT("Cadastrar script"),
    ALTERAR_SCRIPT("Alterar script"),
    EXCLUIR_SCRIPT("Excluir script"),
    CADASTRAR_PAINEL("Cadastrar painel"),
    ALTERAR_PAINEL("Alterar painel"),
    EXCLUIR_PAINEL("Excluir painel"),
    CADASTRAR_USUARIO("Cadastrar usuario"),
    ALTERAR_USUARIO("Alterar usuario"),
    EXCLUIR_USUARIO("Excluir usuario"),
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

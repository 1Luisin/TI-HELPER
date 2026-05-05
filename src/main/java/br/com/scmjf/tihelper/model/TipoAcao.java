package br.com.scmjf.tihelper.model;

public enum TipoAcao {
    LOGIN("Login"),
    LOGOUT("Logout"),
    TESTAR_CONEXAO("Testar conexão"),
    REINICIAR_SERVICO("Reiniciar serviço"),
    EXECUTAR_QUERY("Executar query"),
    EXECUTAR_SCRIPT("Executar script"),
    CADASTRAR_SERVIDOR("Cadastrar servidor"),
    CADASTRAR_SERVICO("Cadastrar serviço"),
    CADASTRAR_QUERY("Cadastrar query"),
    CADASTRAR_SCRIPT("Cadastrar script"),
    CADASTRAR_PAINEL("Cadastrar painel"),
    ALTERAR_USUARIO("Alterar usuário");

    private final String displayName;

    TipoAcao(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

package br.com.scmjf.tihelper.model;

public enum StatusExecucao {
    SUCESSO("Sucesso"),
    ERRO("Erro"),
    SIMULADO("Simulado"),
    NEGADO("Negado"),
    CANCELADO("Cancelado");

    private final String displayName;

    StatusExecucao(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

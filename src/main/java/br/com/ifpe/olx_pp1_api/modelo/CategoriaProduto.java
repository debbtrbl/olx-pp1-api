package br.com.ifpe.olx_pp1_api.modelo;

public enum CategoriaProduto {
    
    CELULAR_TELEFONIA("Celular e Telefonia"),
    ELETRODOMESTICOS("Eletrodomésticos"),
    CASA_DECORACAO_UTENSILIOS("Casa, Decoração e Utensílios"),
    MODA("Moda");

    private final String descricao;

    CategoriaProduto(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
package model;

public class Equipamento {
    private String nome;
    private String tipo;
    private int quantidade;
    private long valor;
    private int quantidadeDisponivel;

    public enum Condicao{
        DISPONIVEL,
        RESERVADO,
        MANUTENCAO,
        EMUSO
    }
    private Condicao condicao;
    public Equipamento(String nome, String tipo, int quantidade, long valor, Condicao condicao, int quantidadeDisponivel) {
        this.nome = nome;
        this.tipo = tipo;
        this.quantidade = quantidade;
        this.valor = valor;
        this.quantidadeDisponivel = quantidade;
        this.condicao = Condicao.DISPONIVEL;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(long valor) {
        this.valor = valor;
    }

    public Condicao getCondicao() {
        return condicao;
    }

    public void setCondicao(Condicao condicao) {
        this.condicao = condicao;
    }

    public int getQuantidadeDisponivel() {
        return quantidadeDisponivel;
    }
    public void setQuantidadeDisponivel(int quantidadeDisponivel) {
        this.quantidadeDisponivel = quantidadeDisponivel;
    }
}

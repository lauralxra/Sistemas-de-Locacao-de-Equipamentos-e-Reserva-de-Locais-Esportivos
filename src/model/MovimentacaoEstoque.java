package model;

import java.time.LocalDateTime;

public class MovimentacaoEstoque {

    public enum Tipo {
        SAIDA_RESERVA,
        ENTRADA_DEVOLUCAO
    }

    private Equipamento equipamento;
    private int quantidade;
    private Tipo tipo;
    private LocalDateTime data;

    public MovimentacaoEstoque(Equipamento equipamento, int quantidade, Tipo tipo) {
        this.equipamento = equipamento;
        this.quantidade = quantidade;
        this.tipo = tipo;
        this.data = LocalDateTime.now();
    }

    public Equipamento getEquipamento() { return equipamento; }
    public int getQuantidade() { return quantidade; }
    public Tipo getTipo() { return tipo; }
    public LocalDateTime getData() { return data; }
}

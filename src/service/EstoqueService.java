package service;

import exceptions.EstoqueInsuficiente;
import model.Equipamento;
import model.MovimentacaoEstoque;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EstoqueService {

    private List<MovimentacaoEstoque> historico = new ArrayList<>();

    // Saída de equipamento para uma reserva
    public void reservar(Map<Equipamento, Integer> itens) throws EstoqueInsuficiente {

        for (Map.Entry<Equipamento, Integer> entry : itens.entrySet()) {
            Equipamento eq = entry.getKey();
            int quantidadeSolicitada = entry.getValue();

            if (eq.getQuantidadeDisponivel() < quantidadeSolicitada) {
                throw new EstoqueInsuficiente(
                        "Estoque insuficiente para " + eq.getNome() +
                                ". Disponível: " + eq.getQuantidadeDisponivel()
                );
            }

            eq.setQuantidadeDisponivel(eq.getQuantidadeDisponivel() - quantidadeSolicitada);

            historico.add(
                    new MovimentacaoEstoque(eq, quantidadeSolicitada, MovimentacaoEstoque.Tipo.SAIDA_RESERVA)
            );
        }
    }

    // Entrada de estoque na devolução
    public void devolver(Map<Equipamento, Integer> itens) {

        for (Map.Entry<Equipamento, Integer> entry : itens.entrySet()) {
            Equipamento eq = entry.getKey();
            int quantidade = entry.getValue();

            eq.setQuantidadeDisponivel(eq.getQuantidadeDisponivel() + quantidade);

            historico.add(
                    new MovimentacaoEstoque(eq, quantidade, MovimentacaoEstoque.Tipo.ENTRADA_DEVOLUCAO)
            );
        }
    }

    public List<MovimentacaoEstoque> getHistorico() {
        return historico;
    }
}

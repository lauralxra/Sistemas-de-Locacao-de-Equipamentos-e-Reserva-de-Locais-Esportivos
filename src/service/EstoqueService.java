package service;

import exceptions.EquipamentoManutencao;
import exceptions.EstoqueInsuficiente;
import model.Equipamento;
import model.MovimentacaoEstoque;
import repository.EquipamentoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EstoqueService {

    private List<MovimentacaoEstoque> historico = new ArrayList<>();

    private EquipamentoRepository equipamentoRepository;

    public EstoqueService() {
        this.equipamentoRepository = new EquipamentoRepository();
    }

    public void reservar(Map<Equipamento, Integer> itens) throws EstoqueInsuficiente, EquipamentoManutencao {
        for (Map.Entry<Equipamento, Integer> entry : itens.entrySet()) {

            Equipamento eqNoBanco = equipamentoRepository.buscarPorNome(entry.getKey().getNome())
                    .orElseThrow(() -> new EstoqueInsuficiente("Equipamento não cadastrado: " + entry.getKey().getNome()));

            int quantidadeSolicitada = entry.getValue();

            if (eqNoBanco.getQuantidadeDisponivel() < quantidadeSolicitada) {
                throw new EstoqueInsuficiente(
                        "Estoque insuficiente para " + eqNoBanco.getNome() +
                                ". Disponível: " + eqNoBanco.getQuantidadeDisponivel()
                );
            }
            if (eqNoBanco.getCondicao() == Equipamento.Condicao.MANUTENCAO) {
                throw new EquipamentoManutencao("Equipamento " + eqNoBanco.getNome() + " em manutenção");
            }


            eqNoBanco.setQuantidadeDisponivel(eqNoBanco.getQuantidadeDisponivel() - quantidadeSolicitada);


            historico.add(new MovimentacaoEstoque(eqNoBanco, quantidadeSolicitada, MovimentacaoEstoque.Tipo.SAIDA_RESERVA));
        }
    }

    public void devolver(Map<Equipamento, Integer> itens) {
        for (Map.Entry<Equipamento, Integer> entry : itens.entrySet()) {


            equipamentoRepository.buscarPorNome(entry.getKey().getNome()).ifPresent(eqNoBanco -> {
                int quantidade = entry.getValue();
                eqNoBanco.setQuantidadeDisponivel(eqNoBanco.getQuantidadeDisponivel() + quantidade);

                historico.add(new MovimentacaoEstoque(eqNoBanco, quantidade, MovimentacaoEstoque.Tipo.ENTRADA_DEVOLUCAO));
            });
        }
    }

    public List<MovimentacaoEstoque> getHistorico() {
        return historico;
    }

    public List<Equipamento> listarEquipamentos() {
        return equipamentoRepository.listarTodos();
    }
}
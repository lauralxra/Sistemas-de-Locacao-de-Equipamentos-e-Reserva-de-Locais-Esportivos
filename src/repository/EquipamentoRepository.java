package repository;

import model.Equipamento;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EquipamentoRepository {

    private static List<Equipamento> bancoDeDadosEquipamentos = new ArrayList<>();

    public EquipamentoRepository() {
        if (bancoDeDadosEquipamentos.isEmpty()) {
            // Dados iniciais
            salvar(new Equipamento("Bola de Futebol", "Esporte", 20, 50, Equipamento.Condicao.DISPONIVEL, 20));
            salvar(new Equipamento("Raquete de Tênis", "Esporte", 10, 150, Equipamento.Condicao.DISPONIVEL, 10));
        }
    }

    public void salvar(Equipamento equipamento) {
        bancoDeDadosEquipamentos.add(equipamento);
    }

    public List<Equipamento> listarTodos() {
        return new ArrayList<>(bancoDeDadosEquipamentos);
    }

    public Optional<Equipamento> buscarPorNome(String nome) {
        return bancoDeDadosEquipamentos.stream()
                .filter(e -> e.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }

    public boolean existeEstoque(String nomeEquipamento, int quantidadeNecessaria) {
        Optional<Equipamento> eq = buscarPorNome(nomeEquipamento);
        return eq.isPresent() && eq.get().getQuantidadeDisponivel() >= quantidadeNecessaria;
    }
}
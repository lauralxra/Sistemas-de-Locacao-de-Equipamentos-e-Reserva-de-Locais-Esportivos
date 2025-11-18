package service;

import model.Equipamento;
import model.Locacao;
import exceptions.EstoqueInsuficiente;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LocacaoService {

    private List<Locacao> locacoes = new ArrayList<>();
    private EstoqueService estoqueService = new EstoqueService();

    // Criar locação já valida estoque e registra saída
    public Locacao criarLocacao(Locacao novaLocacao)
            throws EstoqueInsuficiente {

        // 1. verifica estoque e reserva
        estoqueService.reservar(novaLocacao.getEquipamentos());

        // 2. salva locação
        locacoes.add(novaLocacao);

        System.out.println("Locação criada com sucesso!");
        return novaLocacao;
    }


    // Cálculo do valor total da locação
    public long valorLocacao(Locacao locacao) {

        Duration duracao = Duration.between(locacao.getInicio(), locacao.getFim());
        long horas = duracao.toHours();

        long total = 0;

        for (Map.Entry<Equipamento, Integer> entry : locacao.getEquipamentos().entrySet()) {
            Equipamento eq = entry.getKey();
            int quantidade = entry.getValue();

            long valorPorEquipamento = (long) (eq.getValor() * quantidade * horas);

            total += valorPorEquipamento;
        }

        return total;
    }

    // Devolução dos equipamentos
    public void devolver(Locacao locacao) {
        estoqueService.devolver(locacao.getEquipamentos());
        locacao.setDevolvido(true);
    }
    public List<Locacao> getLocacoes() {
        return locacoes;
    }


}

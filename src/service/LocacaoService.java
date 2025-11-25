package service;

import exceptions.EquipamentoManutencao;
import exceptions.EstoqueInsuficiente;
import model.Equipamento;
import model.Locacao;
import repository.LocacaoRepository;

import java.time.Duration;
import java.util.List;
import java.util.Map;

public class LocacaoService {

    private EstoqueService estoqueService;
    private LocacaoRepository locacaoRepository;

    public LocacaoService() {
        this.estoqueService = new EstoqueService();
        this.locacaoRepository = new LocacaoRepository();
    }

    // Criar locação: Valida estoque -> Registra saída -> Salva no Repositório
    public Locacao criarLocacao(Locacao novaLocacao) throws EstoqueInsuficiente, EquipamentoManutencao {

        // 1. Delega a baixa de estoque para o serviço responsável
        estoqueService.reservar(novaLocacao.getEquipamentos());

        // 2. Salva a locação no repositório
        locacaoRepository.salvar(novaLocacao);

        System.out.println("Locação criada e salva no repositório!");
        return novaLocacao;
    }

    public long valorLocacao(Locacao locacao) {
        Duration duracao = Duration.between(locacao.getInicio(), locacao.getFim());
        long horas = duracao.toHours();
        if (horas == 0) horas = 1; // Cobrar no mínimo 1 hora

        long total = 0;
        for (Map.Entry<Equipamento, Integer> entry : locacao.getEquipamentos().entrySet()) {
            Equipamento eq = entry.getKey();
            int quantidade = entry.getValue();
            long valorPorEquipamento = (long) (eq.getValor() * quantidade * horas);
            total += valorPorEquipamento;
        }
        return total;
    }

    public void devolver(Locacao locacao) {
        estoqueService.devolver(locacao.getEquipamentos());
        locacao.setDevolvido(true);
        // Em um banco de dados real, aqui chamaríamos locacaoRepository.atualizar(locacao);
    }

    public List<Locacao> getLocacoes() {
        return locacaoRepository.listarTodas();
    }
}
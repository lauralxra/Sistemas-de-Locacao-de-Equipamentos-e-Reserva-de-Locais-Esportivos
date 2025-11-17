package service;
import model.*;

import java.time.Duration;
import java.util.*;
import java.time.LocalDateTime;

public class LocacaoService {
    private List<Locacao> locacoes;
    public Locacao criarLocacao(Locacao novaLocacao){
        locacoes.add(novaLocacao);
        System.out.println("Locacao criada com sucesso");
        return novaLocacao;
    }
    public long valorLocacao(Locacao novaLocacao){
        for(Map.Entry<Equipamento, Integer> entry : novaLocacao.getEquipamentos().entrySet() ){
            Duration duracao = Duration.between(novaLocacao.getInicio(), novaLocacao.getFim());
            long horas = duracao.toHours();
            Equipamento equipamento = entry.getKey();
            long quantidade = entry.getValue();
            long valor = horas * quantidade;
        }
        return valorLocacao(novaLocacao);
    }

}

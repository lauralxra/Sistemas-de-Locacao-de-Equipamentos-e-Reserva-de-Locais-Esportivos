package repository;

import model.Locacao;
import java.util.ArrayList;
import java.util.List;

public class LocacaoRepository {
    private static List<Locacao> bancoLocacoes = new ArrayList<>();

    public void salvar(Locacao locacao) {
        bancoLocacoes.add(locacao);
    }

    public List<Locacao> listarTodas() {
        return new ArrayList<>(bancoLocacoes);
    }
}
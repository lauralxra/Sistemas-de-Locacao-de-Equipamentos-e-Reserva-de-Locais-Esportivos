package repository;

import model.LocalEsportivo;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LocalEsportivoRepository {

    private static List<LocalEsportivo> bancoDeDadosLocais = new ArrayList<>();

    public LocalEsportivoRepository() {
        if (bancoDeDadosLocais.isEmpty()) {
            // Como LocalEsportivo pede uma lista de equipamentos no construtor, passamos null ou lista vazia por enquanto
            salvar(new LocalEsportivo("Quadra Poliesportiva", "Quadra", 20, 100.0, LocalEsportivo.Condicao.DISPONIVEL, new ArrayList<>()));
            salvar(new LocalEsportivo("Campo Society", "Campo", 14, 250.0, LocalEsportivo.Condicao.DISPONIVEL, new ArrayList<>()));
        }
    }

    public void salvar(LocalEsportivo local) {
        bancoDeDadosLocais.add(local);
    }

    public List<LocalEsportivo> listarTodos() {
        return new ArrayList<>(bancoDeDadosLocais);
    }

    public Optional<LocalEsportivo> buscarPorNome(String nome) {
        return bancoDeDadosLocais.stream()
                .filter(l -> l.getNome().equalsIgnoreCase(nome))
                .findFirst();
    }
}
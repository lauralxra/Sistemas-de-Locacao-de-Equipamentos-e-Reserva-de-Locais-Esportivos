package repository;

import model.Cliente;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ClienteRepository {

    // Simula o banco de dados em memória
    private static List<Cliente> bancoDeDadosClientes = new ArrayList<>();

    // Inicializa com alguns dados dummy para teste (opcional)
    public ClienteRepository() {
        if (bancoDeDadosClientes.isEmpty()) {
            salvar(new Cliente("João Silva", "111.222.333-44", "joao@email.com", "9999-0000"));
            salvar(new Cliente("Maria Oliveira", "555.666.777-88", "maria@email.com", "8888-1111"));
        }
    }

    public void salvar(Cliente cliente) {
        // Em um banco real, aqui faríamos um INSERT ou UPDATE
        if (!bancoDeDadosClientes.contains(cliente)) {
            bancoDeDadosClientes.add(cliente);
        }
    }

    public List<Cliente> listarTodos() {
        // Retorna uma cópia para evitar modificação direta da lista original fora do repo
        return new ArrayList<>(bancoDeDadosClientes);
    }

    public Optional<Cliente> buscarPorId(long id) {
        return bancoDeDadosClientes.stream()
                .filter(c -> c.getId() == id)
                .findFirst();
    }

    public List<Cliente> buscarPorNome(String nome) {
        return bancoDeDadosClientes.stream()
                .filter(c -> c.getNome().toLowerCase().contains(nome.toLowerCase()))
                .collect(Collectors.toList());
    }

    public void remover(Cliente cliente) {
        bancoDeDadosClientes.remove(cliente);
    }
}
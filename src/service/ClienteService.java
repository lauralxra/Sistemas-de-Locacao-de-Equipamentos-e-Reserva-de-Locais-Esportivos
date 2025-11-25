package service;

import model.Cliente;
import repository.ClienteRepository;
import java.util.List;

public class ClienteService {

    // Injeção do repositório
    private ClienteRepository repository;

    public ClienteService() {
        this.repository = new ClienteRepository();
    }

    public List<Cliente> getAllClientes() {
        return repository.listarTodos();
    }

    public void adicionarCliente(Cliente cliente) {
        // Aqui você pode adicionar validações de negócio antes de salvar
        if (cliente.getNome() == null || cliente.getNome().isEmpty()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        repository.salvar(cliente);
    }

    public void removerCliente(Cliente cliente) {
        repository.remover(cliente);
    }
}
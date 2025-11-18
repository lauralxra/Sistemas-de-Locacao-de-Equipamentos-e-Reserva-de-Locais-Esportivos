package service;

import model.*;
import service.ReservaService;
import service.LocacaoService;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RelatorioService {
    private ReservaService reservaService;
    private LocacaoService locacaoService;
    public RelatorioService(ReservaService reservaService, LocacaoService locacaoService) {
        this.reservaService = reservaService;
        this.locacaoService = locacaoService;
    }

    public List<Reserva> listarReservas() {
        List<Reserva> reservas = reservaService.getReservas();
        System.out.println("Reservas listado");
        if(reservas.isEmpty()) {
            System.out.println("Nenhuma reserva encontrada");
        } else {
        for(Reserva reserva : reservas) {
            Duration duracao = Duration.between(reserva.getInicio(),  reserva.getFim());
            System.out.println("\n");
            System.out.println("Cliente da reserva: " + reserva.getCliente().getDocumento());

            System.out.println("id da reserva: " + reserva.getIdReserva());
            System.out.println("local da reserva: " + reserva.getLocal().getNome());
            System.out.println("condicao do local da reserva :" + reserva.getLocal().getCondicao());
            System.out.println("duracao da reserva: " + duracao);
            System.out.printf("taxa de ocupacao: %d / %d\n", reserva.getConvidados(), reserva.getLocal().getCapacidade());
            System.out.println("Status da reserva:" + reserva.getStatus());

        }
        }

        return reservas;
    }
    private void imprimirLocacao(Locacao l) {

        System.out.println("\n-------------------------------");
        System.out.println("Funcionario: " +
                (l.getFuncionario() != null ? l.getFuncionario().getCpf() : "N/A"));

        System.out.println("Início: " + l.getInicio());
        System.out.println("Fim: " + l.getFim());
        System.out.println("Devolvido: " + (l.isDevolvido() ? "Sim" : "Não"));

        System.out.println("Equipamentos:");
        l.getEquipamentos().forEach((equip, qtd) ->
                System.out.println("  - " + equip.getNome() + " (Qtd: " + qtd + ")")
        );
    }

    public List<Reserva> listarReservasPorCliente(String documentoCliente) {

        List<Reserva> reservasFiltradas = reservaService.getReservas().stream()
                .filter(r -> r.getCliente() != null &&
                        r.getCliente().getDocumento().equals(documentoCliente)) // ← CORREÇÃO AQUI
                .toList();

        if (reservasFiltradas.isEmpty()) {
            System.out.println("\nNenhuma reserva encontrada para o cliente: " + documentoCliente);
            return reservasFiltradas;
        }

        System.out.println("\n===== RESERVAS DO CLIENTE: " + documentoCliente + " =====");

        reservasFiltradas.forEach(r -> {
            System.out.println("\n----------------------------------");
            System.out.println("ID: " + r.getIdReserva());
            System.out.println("Local: " + r.getLocal().getNome());
            System.out.println("Convidados: " + r.getConvidados());
            System.out.println("Início: " + r.getInicio());
            System.out.println("Fim: " + r.getFim());
            System.out.println("Status: " + r.getStatus());
        });

        return reservasFiltradas;
    }

    public List<Locacao> listarLocacoesPorCliente(String documentoCliente) {

        List<Locacao> locacoes = locacaoService.getLocacoes().stream()
                .filter(l -> l.getCliente() != null &&
                        l.getCliente().getDocumento().equals(documentoCliente))
                .toList();

        if (locacoes.isEmpty()) {
            System.out.println("\nNenhuma locação encontrada para o cliente: " + documentoCliente);
            return locacoes;
        }

        System.out.println("\n===== LOCAÇÕES DO CLIENTE: " + documentoCliente + " =====");
        locacoes.forEach(this::imprimirLocacao);

        return locacoes;
    }



}

package service;

import model.Reserva;
import service.ReservaService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class RelatorioService {
    private ReservaService reservaService;

    public RelatorioService(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    public List<Reserva> listarReservas() {
        List<Reserva> reservas = reservaService.getReservas();
        System.out.println("Reservas listado");
        if(reservas.isEmpty()) {
            System.out.println("Nenhuma reserva encontrada");
        } else {
        for(Reserva reserva : reservas) {
            Duration duracao = Duration.between(reserva.getInicio(),  reserva.getFim());
            System.out.println("id da reserva: " + reserva.getIdReserva());
            System.out.println("local da reserva: " + reserva.getLocal().getNome());
            System.out.println("condicao do local da reserva :" + reserva.getLocal().getCondicao());
            System.out.println("duracao da reserva: " + duracao);
            System.out.printf("taxa de ocupacao: %d / %d\n", reserva.getConvidados(), reserva.getLocal().getCapacidade());
            System.out.println("equipamentos alugados:" + reserva.getNomeEquipamentos());
            System.out.println("Status da reserva:" + reserva.getStatus());
        }
        }

        return reservas;

    }
}

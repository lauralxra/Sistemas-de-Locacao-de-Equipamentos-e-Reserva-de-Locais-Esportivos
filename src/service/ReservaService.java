package service;

import exceptions.LocalJaReservado; // Assumindo que você criou essa exception
import model.Reserva;
import repository.LocalEsportivoRepository;
import repository.ReservaRepository;

import java.util.List;

public class ReservaService {

    private ReservaRepository reservaRepository;
    private LocalEsportivoRepository localRepository;

    public ReservaService() {
        this.reservaRepository = new ReservaRepository();
        this.localRepository = new LocalEsportivoRepository();
    }

    public void criarReserva(Reserva novaReserva) throws LocalJaReservado {
        // 1. Validação: Verifica se o local já está ocupado no horário (Simplificado)
        List<Reserva> reservasExistentes = reservaRepository.buscarPorLocal(novaReserva.getLocal().getNome());

        for (Reserva r : reservasExistentes) {
            if (r.estaAtiva() && horariosConflitam(r, novaReserva)) {
                throw new LocalJaReservado("O local " + novaReserva.getLocal().getNome() + " já está reservado neste horário.");
            }
        }

        // 2. Salva no repositório
        reservaRepository.salvar(novaReserva);
        System.out.println("Reserva criada com sucesso!");
    }

    public List<Reserva> listarReservas() {
        return reservaRepository.listarTodas();
    }

    // Lógica auxiliar para verificar colisão de horários
    private boolean horariosConflitam(Reserva r1, Reserva r2) {
        return r1.getInicio().isBefore(r2.getFim()) && r1.getFim().isAfter(r2.getInicio());
    }

    public void cancelarReserva(Reserva reserva) {
        reserva.cancelar();
        // Em banco real: reservaRepository.atualizar(reserva);
    }
}
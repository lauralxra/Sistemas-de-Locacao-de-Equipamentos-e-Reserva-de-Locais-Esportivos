package repository;

import model.Reserva;
import java.util.ArrayList;
import java.util.List;

public class ReservaRepository {
    private static List<Reserva> bancoReservas = new ArrayList<>();

    public void salvar(Reserva reserva) {
        bancoReservas.add(reserva);
    }

    public List<Reserva> listarTodas() {
        return new ArrayList<>(bancoReservas);
    }


    public List<Reserva> buscarPorLocal(String nomeLocal) {
        List<Reserva> resultado = new ArrayList<>();
        for (Reserva r : bancoReservas) {
            if (r.getLocal().getNome().equalsIgnoreCase(nomeLocal)) {
                resultado.add(r);
            }
        }
        return resultado;
    }
}
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.*;
import service.*;


public class Main {
    public static void main(String[] args) {

        Equipamento bola = new Equipamento("bola", "bola", 3, 10, Equipamento.Condicao.DISPONIVEL);

        Funcionario funcionario1 = new Funcionario("carlinhos", "22342", Funcionario.Autorizacao.AUTORIZADO);
        Funcionario funcionario2 = new Funcionario("jonas", "575837", Funcionario.Autorizacao.NAOAUTORIZADO);
        Equipamento raquete = new Equipamento("raquete", "equipamento", 5, 10, Equipamento.Condicao.DISPONIVEL);
        Cliente cliente = new Cliente("Ana", "123.456.789-00", "ana@gmail.com", "1234556");
        Cliente cliente2 = new Cliente("carlos", "4567", "caarlos@gmail.com", "45967");
        LocalEsportivo quadra = new LocalEsportivo("Quadra A", "Futebol", 20, 150.0, LocalEsportivo.Condicao.DISPONIVEL, null);
        LocalEsportivo campo = new LocalEsportivo("campinho", "volei", 30, 200.0, LocalEsportivo.Condicao.DISPONIVEL, null);
        List<Reserva> reservas = new ArrayList<>();
        Map<Equipamento, Integer> equipamentosReserva1 = new HashMap<>();
        equipamentosReserva1.put(bola, 2);     // Reservando 2 bolas
        // Reservando 1 raquete

        Map<Equipamento, Integer> equipamentosReserva2 = new HashMap<>();
        equipamentosReserva2.put(bola, 2);     // Tentando reservar 9 bolas (funciona)
        equipamentosReserva2.put(raquete, 5);  // Tentando reservar 6 raquetes (erro: só há 5 disponíveis)

        ReservaService service = new ReservaService();
        RelatorioService relatorioService = new RelatorioService(service);

        Reserva reserva1 = new Reserva(1,20, cliente, Reserva.StatusReserva.ATIVA, quadra, LocalDateTime.now(), LocalDateTime.now().plusDays(1), funcionario1);
        Reserva reserva2 = new Reserva(2,30, cliente2, Reserva.StatusReserva.ATIVA, campo, LocalDateTime.now(), LocalDateTime.now().plusDays(2), funcionario1);
        service.criarReserva(reserva2);
        Reserva reserva3 = new Reserva(4, 40, cliente2, Reserva.StatusReserva.ATIVA, campo, LocalDateTime.now(), LocalDateTime.now().plusDays(4), null);
        service.criarReserva(reserva3);
        relatorioService.listarReservas();
        //até aqui eu testei a criação de reservas e o cancelamento
        //implementei o requerimento de reservas de equipamentos dando erro se não tiver quantidade
        //implementei a reserva de locais sem equipamentos
        //comecei a implementar os relatorios
    }
}

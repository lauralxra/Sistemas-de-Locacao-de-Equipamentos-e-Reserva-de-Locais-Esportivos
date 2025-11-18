import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import exceptions.EquipamentoManutencao;
import exceptions.EstoqueInsuficiente;
import model.*;
import service.*;


public class Main {
    public static void main(String[] args) {
        Document document = new Document();
        Equipamento bola = new Equipamento("bola", "bola", 3, 10, Equipamento.Condicao.DISPONIVEL, 3);
        Equipamento chuteira = new Equipamento("chuteira", "sapato", 4, 5, Equipamento.Condicao.DISPONIVEL, 4);
        Funcionario funcionario1 = new Funcionario("carlinhos", "22342", Funcionario.Autorizacao.AUTORIZADO);
        Funcionario funcionario2 = new Funcionario("jonas", "575837", Funcionario.Autorizacao.NAOAUTORIZADO);
        Equipamento raquete = new Equipamento("raquete", "equipamento", 5, 10, Equipamento.Condicao.DISPONIVEL, 5);
        Cliente cliente = new Cliente("Ana", "123.456.789-00", "ana@gmail.com", "1234556");
        Cliente cliente2 = new Cliente("carlos", "4567", "caarlos@gmail.com", "45967");
        LocalEsportivo quadra = new LocalEsportivo("Quadra A", "Futebol", 20, 150.0, LocalEsportivo.Condicao.DISPONIVEL, null);
        LocalEsportivo campo = new LocalEsportivo("campinho", "volei", 30, 200.0, LocalEsportivo.Condicao.DISPONIVEL, null);
        List<Reserva> reservas = new ArrayList<>();

        Map<Equipamento, Integer> equipamentosLocacao = new HashMap<>();
        equipamentosLocacao.put(chuteira, 4);

        Map<Equipamento, Integer> equipamentosReserva1 = new HashMap<>();
        equipamentosReserva1.put(bola, 2);     // Reservando 2 bolas
        // Reservando 1 raquete

        Map<Equipamento, Integer> equipamentosReserva2 = new HashMap<>();
        equipamentosReserva2.put(bola, 2);     // Tentando reservar 9 bolas (funciona)
        equipamentosReserva2.put(raquete, 5);  // Tentando reservar 6 raquetes (erro: só há 5 disponíveis)

        ReservaService service = new ReservaService();
        LocacaoService locacaoService = new LocacaoService();
        RelatorioService relatorioService = new RelatorioService(service, locacaoService);

        Reserva reserva1 = new Reserva(1,20, cliente, Reserva.StatusReserva.ATIVA, quadra, LocalDateTime.now(), LocalDateTime.now().plusDays(1), funcionario1);
        service.criarReserva(reserva1);
        Reserva reserva2 = new Reserva(2,30, cliente2, Reserva.StatusReserva.ATIVA, campo, LocalDateTime.now(), LocalDateTime.now().plusDays(2), funcionario1);
        service.criarReserva(reserva2);
        Reserva reserva3 = new Reserva(4, 20, cliente2, Reserva.StatusReserva.ATIVA, campo, LocalDateTime.now().plusDays(3), LocalDateTime.now().plusDays(4), funcionario1);
        service.criarReserva(reserva3);
        System.out.println("\n");
        relatorioService.listarReservas();
        System.out.println("\n");
        Locacao Locacao1 = new Locacao(cliente, LocalDateTime.now(), LocalDateTime.now().plusDays(2), funcionario1,equipamentosLocacao, Locacao.StatusLocacao.ATIVO);


        try {
            locacaoService.criarLocacao(Locacao1);
            long valor = locacaoService.valorLocacao(Locacao1);
        } catch (EstoqueInsuficiente | EquipamentoManutencao e) {
            System.out.println("Erro ao criar locação: " + e.getMessage());
        }

        relatorioService.gerarPdfHistoricoCliente("123.456.789-00", "historico cliente: .pdf");
        //até aqui eu testei a criação de reservas e o cancelamento
        //implementei o requerimento de reservas de equipamentos dando erro se não tiver quantidade
        //implementei a reserva de locais sem equipamentos
        //comecei a implementar os relatorios
        equipamentosLocacao.forEach((equip, qtd) -> {
            System.out.println("Equipamento: " + equip.getNome());
            System.out.println("Condição: " + equip.getCondicao());
        });
        relatorioService.gerarPdfEquipamentosMaisUsados(LocalDateTime.now(), LocalDateTime.now().plusDays(5), "Equipamentos mais usados.pdf");
        relatorioService.gerarPdfClientesMaisAtivos(LocalDateTime.now(), LocalDateTime.now().plusDays(5), "Clientes mais ativos.pdf");




        relatorioService.gerarPdfLocaisMaisUsados(LocalDateTime.now(), LocalDateTime.now().plusDays(6), "relatorio.pdf");
        relatorioService.gerarPdfHistoricoCliente("123.456.789-00", "Historico_Cliente_123.pdf");
    }


}

package service;

import com.lowagie.text.*;
import model.*;
import service.ReservaService;
import service.LocacaoService;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;
import exceptions.EquipamentoManutencao;
import exceptions.EstoqueInsuficiente;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void relatorioEquipamentosMaisUsados(LocalDateTime inicio, LocalDateTime fim) {

        System.out.println("\n===== RELATÓRIO DE EQUIPAMENTOS USADOS =====");
        System.out.println("Período: " + inicio + " até " + fim);

        // 1. Filtrar locações no período
        List<Locacao> locacoesPeriodo = locacaoService.getLocacoes().stream()
                .filter(l -> !l.getInicio().isAfter(fim) && !l.getFim().isBefore(inicio))
                .toList();

        if (locacoesPeriodo.isEmpty()) {
            System.out.println("Nenhuma locação encontrada no período.");
            return;
        }

        // 2. Map para acumular quantidade total por equipamento
        Map<Equipamento, Integer> usoEquipamentos = new HashMap<>();

        for (Locacao loc : locacoesPeriodo) {
            for (Map.Entry<Equipamento, Integer> entry : loc.getEquipamentos().entrySet()) {
                Equipamento eq = entry.getKey();
                int qtd = entry.getValue();

                usoEquipamentos.put(eq, usoEquipamentos.getOrDefault(eq, 0) + qtd);
            }
        }

        // 3. Ordenar por quantidade utilizada (decrescente)
        List<Map.Entry<Equipamento, Integer>> ranking = usoEquipamentos.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .toList();

        // 4. Imprimir resultado
        System.out.println("\nEquipamentos mais usados no período:\n");

        int pos = 1;
        for (Map.Entry<Equipamento, Integer> entry : ranking) {
            Equipamento eq = entry.getKey();
            int qtd = entry.getValue();
            long locacoesComEq = locacoesPeriodo.stream()
                    .filter(l -> l.getEquipamentos().containsKey(eq))
                    .count();

            System.out.printf("%d) %s — %d unidades — %d locações\n",
                    pos++, eq.getNome(), qtd, locacoesComEq);
        }
    }
    private boolean intersecta(LocalDateTime aInicio, LocalDateTime aFim,
                               LocalDateTime bInicio, LocalDateTime bFim) {
        return !aInicio.isAfter(bFim) && !aFim.isBefore(bInicio);
    }

    public void gerarRelatorioClientesMaisAtivos(LocalDateTime inicio, LocalDateTime fim) {

        Map<String, Integer> contadorClientes = new HashMap<>();

        // ---- RESERVAS ----
        for (Reserva reserva : reservaService.getReservas()) {
            if (intersecta(reserva.getInicio(), reserva.getFim(), inicio, fim)) {

                String cpf = reserva.getCliente().getDocumento();
                contadorClientes.put(cpf, contadorClientes.getOrDefault(cpf, 0) + 1);
            }
        }

        // ---- LOCAÇÕES ----
        for (Locacao locacao : locacaoService.getLocacoes()) {
            if (intersecta(locacao.getInicio(), locacao.getFim(), inicio, fim)) {

                String cpf = locacao.getCliente().getDocumento();
                contadorClientes.put(cpf, contadorClientes.getOrDefault(cpf, 0) + 1);
            }
        }

        if (contadorClientes.isEmpty()) {
            System.out.println("Nenhuma atividade encontrada no período.");
            return;
        }

        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(contadorClientes.entrySet());
        ranking.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        System.out.println("\n===== CLIENTES MAIS ATIVOS =====");
        for (Map.Entry<String, Integer> entry : ranking) {
            System.out.println("Cliente: " + entry.getKey() + " → " + entry.getValue() + " atividades");
        }
    }


// Importar suas classes de modelo (Reserva, Local, etc.)

    public void gerarPdfLocaisMaisUsados(LocalDateTime inicio, LocalDateTime fim, String nomeArquivo) {

        // 1. Definições Iniciais do PDF
        Document document = new Document();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            // --- INÍCIO: Conteúdo do PDF ---

            // Configuração de Fontes (Opcional, mas recomendado para estilo)
            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Font.UNDERLINE);
            Font fontCabecalho = FontFactory.getFont(FontFactory.HELVETICA, 12, Font.BOLD);
            Font fontCorpo = FontFactory.getFont(FontFactory.HELVETICA, 10);


            // --- TITULO E PERÍODO ---
            Paragraph titulo = new Paragraph("RELATÓRIO DE LOCAIS MAIS USADOS", fontTitulo);
            titulo.setAlignment(Paragraph.ALIGN_CENTER);
            titulo.setSpacingAfter(15f);
            document.add(titulo);

            Paragraph periodo = new Paragraph("Período: " + inicio.toLocalDate() + " até " + fim.toLocalDate(), fontCabecalho);
            periodo.setSpacingAfter(10f);
            document.add(periodo);

            // --- LÓGICA DO RELATÓRIO (A mesma que você já tinha) ---

            Map<String, Integer> usoLocais = new HashMap<>();

            List<Reserva> reservasPeriodo = reservaService.getReservas().stream()
                    .filter(r -> r.getLocal() != null && intersecta(r.getInicio(), r.getFim(), inicio, fim))
                    .toList();

            if (reservasPeriodo.isEmpty()) {
                document.add(new Paragraph("Nenhuma reserva encontrada no período.", fontCorpo));
                System.out.println("PDF gerado, mas sem reservas encontradas no período.");
                return;
            }

            for (Reserva reserva : reservasPeriodo) {
                String nomeLocal = reserva.getLocal().getNome();
                usoLocais.put(nomeLocal, usoLocais.getOrDefault(nomeLocal, 0) + 1);
            }

            List<Map.Entry<String, Integer>> ranking = usoLocais.entrySet()
                    .stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .toList();

            // --- GERAÇÃO DO RANKING NO PDF ---

            Paragraph subtitulo = new Paragraph("Ranking de Locais mais reservados:", fontCabecalho);
            subtitulo.setSpacingBefore(10f);
            document.add(subtitulo);

            int pos = 1;
            for (Map.Entry<String, Integer> entry : ranking) {
                String nomeLocal = entry.getKey();
                int numReservas = entry.getValue();

                String linha = String.format("%d) %s — %d reservas", pos++, nomeLocal, numReservas);

                // Adiciona a linha como um Parágrafo simples
                document.add(new Paragraph(linha, fontCorpo));
            }

            // --- FIM: Conteúdo do PDF ---

        } catch (DocumentException e) {
            System.err.println("Erro ao gerar PDF (DocumentException): " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro de IO (arquivo): " + e.getMessage());
        } finally {
            if (document.isOpen()) {
                document.close();
                System.out.println("✅ Relatório PDF gerado com sucesso: " + nomeArquivo);
            }
        }
    }







}

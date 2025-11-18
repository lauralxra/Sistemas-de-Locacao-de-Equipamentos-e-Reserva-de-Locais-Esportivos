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
                        r.getCliente().getDocumento().equals(documentoCliente))
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


    public void gerarPdfHistoricoCliente(String documentoCliente, String nomeArquivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fontSubtitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, Font.UNDERLINE);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("HISTÓRICO DO CLIENTE: " + documentoCliente, fontTitulo));
            document.add(new Paragraph(" ")); // Espaço em branco

            // --- SEÇÃO DE RESERVAS ---
            document.add(new Paragraph("RESERVAS DE ESPAÇO", fontSubtitulo));

            List<Reserva> reservas = reservaService.getReservas().stream()
                    .filter(r -> r.getCliente() != null && r.getCliente().getDocumento().equals(documentoCliente))
                    .toList();

            if (reservas.isEmpty()) {
                document.add(new Paragraph("Nenhuma reserva registrada.", fontNormal));
            } else {
                for (Reserva r : reservas) {
                    String texto = String.format("Local: %s | Data: %s | Status: %s",
                            r.getLocal().getNome(), r.getInicio().toLocalDate(), r.getStatus());
                    document.add(new Paragraph(texto, fontNormal));
                }
            }

            document.add(new Paragraph(" ")); // Espaço

            // --- SEÇÃO DE LOCAÇÕES ---
            document.add(new Paragraph("LOCAÇÕES DE EQUIPAMENTOS", fontSubtitulo));

            List<Locacao> locacoes = locacaoService.getLocacoes().stream()
                    .filter(l -> l.getCliente() != null && l.getCliente().getDocumento().equals(documentoCliente))
                    .toList();

            if (locacoes.isEmpty()) {
                document.add(new Paragraph("Nenhuma locação registrada.", fontNormal));
            } else {
                for (Locacao l : locacoes) {
                    document.add(new Paragraph("De: " + l.getInicio() + " Até: " + l.getFim(), fontNormal));
                    document.add(new Paragraph("Itens:", fontNormal));

                    // Lista os equipamentos indentados
                    l.getEquipamentos().forEach((equip, qtd) -> {
                        Paragraph item = new Paragraph("   • " + equip.getNome() + " (Qtd: " + qtd + ")", fontNormal);
                        try { document.add(item); } catch (DocumentException e) {}
                    });
                    document.add(new Paragraph("-----------------------"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document.isOpen()) document.close();
            System.out.println("PDF do Cliente gerado: " + nomeArquivo);
        }
    }
    public void gerarPdfEquipamentosMaisUsados(LocalDateTime inicio, LocalDateTime fim, String nomeArquivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("EQUIPAMENTOS MAIS UTILIZADOS", fontTitulo));
            document.add(new Paragraph("Período: " + inicio.toLocalDate() + " a " + fim.toLocalDate(), fontNormal));
            document.add(new Paragraph(" "));


            List<Locacao> locacoesPeriodo = locacaoService.getLocacoes().stream()
                    .filter(l -> !l.getInicio().isAfter(fim) && !l.getFim().isBefore(inicio))
                    .toList();

            if (locacoesPeriodo.isEmpty()) {
                document.add(new Paragraph("Nenhum dado no período.", fontNormal));
                return;
            }

            Map<Equipamento, Integer> usoEquipamentos = new HashMap<>();
            for (Locacao loc : locacoesPeriodo) {
                for (Map.Entry<Equipamento, Integer> entry : loc.getEquipamentos().entrySet()) {
                    usoEquipamentos.put(entry.getKey(), usoEquipamentos.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }

            List<Map.Entry<Equipamento, Integer>> ranking = usoEquipamentos.entrySet()
                    .stream()
                    .sorted((a, b) -> b.getValue() - a.getValue())
                    .toList();

            // Imprimir no PDF
            int pos = 1;
            for (Map.Entry<Equipamento, Integer> entry : ranking) {
                String linha = String.format("%dº LUGAR: %s", pos++, entry.getKey().getNome());
                String detalhe = String.format("    Total Unidades: %d", entry.getValue());

                document.add(new Paragraph(linha, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                document.add(new Paragraph(detalhe, fontNormal));
                document.add(new Paragraph(" "));
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document.isOpen()) document.close();
            System.out.println("PDF Equipamentos gerado: " + nomeArquivo);
        }
    }
    private boolean intersecta(LocalDateTime aInicio, LocalDateTime aFim,
                               LocalDateTime bInicio, LocalDateTime bFim) {
        return !aInicio.isAfter(bFim) && !aFim.isBefore(bInicio);
    }
    public void gerarPdfClientesMaisAtivos(LocalDateTime inicio, LocalDateTime fim, String nomeArquivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Font fontDestaque = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font fontNormal = FontFactory.getFont(FontFactory.HELVETICA, 12);

            document.add(new Paragraph("TOP CLIENTES (MAIS ATIVOS)", fontTitulo));
            document.add(new Paragraph("Considerando Reservas e Locações", fontNormal));
            document.add(new Paragraph("Período: " + inicio.toLocalDate() + " a " + fim.toLocalDate()));
            document.add(new Paragraph(" "));


            Map<String, Integer> contadorClientes = new HashMap<>();

            for (Reserva reserva : reservaService.getReservas()) {
                if (intersecta(reserva.getInicio(), reserva.getFim(), inicio, fim)) {
                    String cpf = reserva.getCliente().getDocumento();
                    contadorClientes.put(cpf, contadorClientes.getOrDefault(cpf, 0) + 1);
                }
            }
            for (Locacao locacao : locacaoService.getLocacoes()) {
                if (intersecta(locacao.getInicio(), locacao.getFim(), inicio, fim)) {
                    String cpf = locacao.getCliente().getDocumento();
                    contadorClientes.put(cpf, contadorClientes.getOrDefault(cpf, 0) + 1);
                }
            }

            if (contadorClientes.isEmpty()) {
                document.add(new Paragraph("Nenhuma atividade registrada no período.", fontNormal));
            } else {
                List<Map.Entry<String, Integer>> ranking = new ArrayList<>(contadorClientes.entrySet());
                ranking.sort((a, b) -> b.getValue().compareTo(a.getValue()));

                int pos = 1;
                for (Map.Entry<String, Integer> entry : ranking) {
                    String texto = String.format("#%d - Cliente CPF: %s", pos++, entry.getKey());
                    String atividades = "      Total de Atividades: " + entry.getValue();

                    document.add(new Paragraph(texto, fontDestaque));
                    document.add(new Paragraph(atividades, fontNormal));
                    document.add(new Paragraph("-----------------------"));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document.isOpen()) document.close();
            System.out.println("PDF Clientes Ativos gerado: " + nomeArquivo);
        }
    }
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

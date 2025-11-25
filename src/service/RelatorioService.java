package service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import model.Cliente;
import model.Equipamento;
import model.Locacao;
import model.Reserva;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RelatorioService {

    private final ReservaService reservaService;
    private final LocacaoService locacaoService;


    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public RelatorioService(ReservaService reservaService, LocacaoService locacaoService) {
        this.reservaService = reservaService;
        this.locacaoService = locacaoService;
    }


    public void gerarPdfListaReservas(String nomeArquivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            adicionarCabecalho(document, "Relatório Geral de Reservas");

            List<Reserva> reservas = reservaService.listarReservas();

            if (reservas.isEmpty()) {
                document.add(new Paragraph("Nenhuma reserva cadastrada no sistema."));
            } else {
                // Cria tabela com 6 colunas
                PdfPTable table = new PdfPTable(6);
                table.setWidthPercentage(100);

                // Cabeçalhos da Tabela
                adicionarCelulaCabecalho(table, "ID");
                adicionarCelulaCabecalho(table, "Cliente");
                adicionarCelulaCabecalho(table, "Local");
                adicionarCelulaCabecalho(table, "Início");
                adicionarCelulaCabecalho(table, "Fim");
                adicionarCelulaCabecalho(table, "Status");

                for (Reserva r : reservas) {
                    table.addCell(String.valueOf(r.getIdReserva()));
                    table.addCell(r.getCliente() != null ? r.getCliente().getNome() : "N/A");
                    table.addCell(r.getLocal().getNome());
                    table.addCell(r.getInicio().format(dtf));
                    table.addCell(r.getFim().format(dtf));
                    table.addCell(r.getStatus().toString());
                }
                document.add(table);
            }

            System.out.println("📄 PDF '" + nomeArquivo + "' gerado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document.isOpen()) document.close();
        }
    }


    public void gerarPdfHistoricoCliente(String documentoCliente, String nomeArquivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            adicionarCabecalho(document, "Histórico do Cliente: " + documentoCliente);

            // --- Parte 1: Reservas ---
            Paragraph subtituloReserva = new Paragraph("Histórico de Reservas", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloReserva.setSpacingAfter(10);
            subtituloReserva.setSpacingBefore(10);
            document.add(subtituloReserva);

            List<Reserva> reservas = reservaService.listarReservas().stream()
                    .filter(r -> r.getCliente() != null && r.getCliente().getDocumento().equals(documentoCliente))
                    .toList();

            if (reservas.isEmpty()) {
                document.add(new Paragraph("Nenhuma reserva encontrada para este cliente."));
            } else {
                PdfPTable table = new PdfPTable(4);
                table.setWidthPercentage(100);
                adicionarCelulaCabecalho(table, "Local");
                adicionarCelulaCabecalho(table, "Data");
                adicionarCelulaCabecalho(table, "Convidados");
                adicionarCelulaCabecalho(table, "Status");

                for (Reserva r : reservas) {
                    table.addCell(r.getLocal().getNome());
                    table.addCell(r.getInicio().format(dtf));
                    table.addCell(String.valueOf(r.getConvidados()));
                    table.addCell(r.getStatus().toString());
                }
                document.add(table);
            }

            // --- Parte 2: Locações ---
            Paragraph subtituloLocacao = new Paragraph("Histórico de Locações", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14));
            subtituloLocacao.setSpacingAfter(10);
            subtituloLocacao.setSpacingBefore(20); // Espaço maior antes do novo bloco
            document.add(subtituloLocacao);

            List<Locacao> locacoes = locacaoService.getLocacoes().stream()
                    .filter(l -> l.getCliente() != null && l.getCliente().getDocumento().equals(documentoCliente))
                    .toList();

            if (locacoes.isEmpty()) {
                document.add(new Paragraph("Nenhuma locação encontrada para este cliente."));
            } else {
                for (Locacao l : locacoes) {
                    Paragraph p = new Paragraph("• Locação em " + l.getInicio().format(dtf) + " - Devolvido: " + (l.isDevolvido() ? "Sim" : "Não"));
                    document.add(p);

                    com.lowagie.text.List listaEquip = new com.lowagie.text.List(false, 20);
                    l.getEquipamentos().forEach((eq, qtd) -> {
                        listaEquip.add(new ListItem(qtd + "x " + eq.getNome()));
                    });
                    document.add(listaEquip);
                    document.add(new Paragraph(" ")); // Linha em branco
                }
            }

            System.out.println("📄 PDF '" + nomeArquivo + "' gerado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document.isOpen()) document.close();
        }
    }


    public void gerarPdfEquipamentosMaisUsados(LocalDateTime inicio, LocalDateTime fim, String nomeArquivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            adicionarCabecalho(document, "Equipamentos Mais Usados");
            document.add(new Paragraph("Período: " + inicio.format(dtf) + " até " + fim.format(dtf) + "\n\n"));

            // Lógica de cálculo (reaproveitada)
            Map<Equipamento, Integer> contagem = new HashMap<>();
            List<Locacao> locacoes = locacaoService.getLocacoes().stream()
                    .filter(l -> intersecta(l.getInicio(), l.getFim(), inicio, fim))
                    .toList();

            for (Locacao loc : locacoes) {
                for (Map.Entry<Equipamento, Integer> entry : loc.getEquipamentos().entrySet()) {
                    contagem.put(entry.getKey(), contagem.getOrDefault(entry.getKey(), 0) + entry.getValue());
                }
            }

            if (contagem.isEmpty()) {
                document.add(new Paragraph("Nenhuma locação no período."));
            } else {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(80);
                table.setHorizontalAlignment(Element.ALIGN_LEFT);
                adicionarCelulaCabecalho(table, "Equipamento");
                adicionarCelulaCabecalho(table, "Qtd. Total Locada");

                contagem.entrySet().stream()
                        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                        .forEach(entry -> {
                            table.addCell(entry.getKey().getNome());
                            table.addCell(String.valueOf(entry.getValue()));
                        });
                document.add(table);
            }
            System.out.println("📄 PDF '" + nomeArquivo + "' gerado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document.isOpen()) document.close();
        }
    }


    public void gerarPdfClientesMaisAtivos(LocalDateTime inicio, LocalDateTime fim, String nomeArquivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            adicionarCabecalho(document, "Ranking de Clientes Mais Ativos");
            document.add(new Paragraph("Período: " + inicio.format(dtf) + " até " + fim.format(dtf) + "\n\n"));

            Map<String, Integer> atividade = new HashMap<>();

            // Contar Reservas
            for (Reserva r : reservaService.listarReservas()) {
                if (intersecta(r.getInicio(), r.getFim(), inicio, fim) && r.getCliente() != null) {
                    atividade.put(r.getCliente().getNome(), atividade.getOrDefault(r.getCliente().getNome(), 0) + 1);
                }
            }
            // Contar Locações
            for (Locacao l : locacaoService.getLocacoes()) {
                if (intersecta(l.getInicio(), l.getFim(), inicio, fim) && l.getCliente() != null) {
                    atividade.put(l.getCliente().getNome(), atividade.getOrDefault(l.getCliente().getNome(), 0) + 1);
                }
            }

            if (atividade.isEmpty()) {
                document.add(new Paragraph("Nenhuma atividade encontrada no período."));
            } else {
                PdfPTable table = new PdfPTable(3); // Posicao, Nome, Atividades
                table.setWidthPercentage(100);
                adicionarCelulaCabecalho(table, "#");
                adicionarCelulaCabecalho(table, "Cliente");
                adicionarCelulaCabecalho(table, "Total de Atividades");

                List<Map.Entry<String, Integer>> ranking = atividade.entrySet().stream()
                        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                        .toList();

                int pos = 1;
                for (Map.Entry<String, Integer> entry : ranking) {
                    table.addCell(String.valueOf(pos++) + "º");
                    table.addCell(entry.getKey());
                    table.addCell(String.valueOf(entry.getValue()));
                }
                document.add(table);
            }
            System.out.println("📄 PDF '" + nomeArquivo + "' gerado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document.isOpen()) document.close();
        }
    }


    public void gerarPdfLocaisMaisUsados(LocalDateTime inicio, LocalDateTime fim, String nomeArquivo) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(nomeArquivo));
            document.open();

            adicionarCabecalho(document, "Locais Mais Reservados");
            document.add(new Paragraph("Período: " + inicio.format(dtf) + " até " + fim.format(dtf) + "\n\n"));

            Map<String, Integer> usoLocais = new HashMap<>();
            reservaService.listarReservas().stream()
                    .filter(r -> intersecta(r.getInicio(), r.getFim(), inicio, fim))
                    .forEach(r -> {
                        String nome = r.getLocal().getNome();
                        usoLocais.put(nome, usoLocais.getOrDefault(nome, 0) + 1);
                    });

            if (usoLocais.isEmpty()) {
                document.add(new Paragraph("Nenhuma reserva encontrada."));
            } else {
                PdfPTable table = new PdfPTable(2);
                table.setWidthPercentage(80);
                table.setHorizontalAlignment(Element.ALIGN_LEFT);
                adicionarCelulaCabecalho(table, "Local Esportivo");
                adicionarCelulaCabecalho(table, "Reservas Feitas");

                usoLocais.entrySet().stream()
                        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                        .forEach(entry -> {
                            table.addCell(entry.getKey());
                            table.addCell(String.valueOf(entry.getValue()));
                        });
                document.add(table);
            }
            System.out.println("📄 PDF '" + nomeArquivo + "' gerado com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(document.isOpen()) document.close();
        }
    }



    private boolean intersecta(LocalDateTime aInicio, LocalDateTime aFim, LocalDateTime bInicio, LocalDateTime bFim) {
        return !aInicio.isAfter(bFim) && !aFim.isBefore(bInicio);
    }

    // Adiciona um título padrão ao documento
    private void adicionarCabecalho(Document document, String tituloTexto) throws DocumentException {
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.BLACK);
        Paragraph titulo = new Paragraph(tituloTexto, fontTitulo);
        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(20);
        document.add(titulo);
    }

    // Adiciona uma célula de cabeçalho estilizada na tabela
    private void adicionarCelulaCabecalho(PdfPTable table, String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
        cell.setBackgroundColor(Color.LIGHT_GRAY);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }
}
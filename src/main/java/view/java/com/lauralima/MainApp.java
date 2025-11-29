package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import model.Funcionario;
import service.ClienteService;
import service.LocacaoService;
import service.RelatorioService;
import service.ReservaService;

public class MainApp extends Application {

    // Instância única dos serviços para toda a aplicação
    private final ClienteService clienteService = new ClienteService();
    private final ReservaService reservaService = new ReservaService();
    private final LocacaoService locacaoService = new LocacaoService();
    private final RelatorioService relatorioService = new RelatorioService(reservaService, locacaoService);
    
    // Funcionário "Logado" (Mock)
    private final Funcionario funcionarioLogado = new Funcionario("Admin", "0000", Funcionario.Autorizacao.AUTORIZADO);

    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema de Gestão Esportiva");

        // Layout Principal
        rootLayout = new BorderPane();
        
        // Menu Lateral
        VBox menu = criarMenuLateral();
        rootLayout.setLeft(menu);
        
        // Tela Inicial (Bem-vindo)
        mostrarTelaInicial();

        Scene scene = new Scene(rootLayout, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    private void mostrarTelaLocacaoEquipamento() {
        TelaLocacaoEquipamentos view = new TelaLocacaoEquipamentos(locacaoService, clienteService, funcionarioLogado);
        rootLayout.setCenter(view.getView());
    }

    private VBox criarMenuLateral() {
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(15));
        menu.setStyle("-fx-background-color: #2c3e50;");
        menu.setPrefWidth(200);

        Label lblMenu = new Label("MENU");
        lblMenu.setTextFill(javafx.scene.paint.Color.WHITE);
        lblMenu.setFont(new Font("Arial Bold", 18));

        Separator sep = new Separator();

        // Estilo dos botões
        String buttonStyle = "-fx-background-color: transparent; -fx-text-fill: white; -fx-alignment: CENTER-LEFT; -fx-font-size: 14px; -fx-cursor: hand;";
        
        Button btnInicio = new Button("🏠 Início");
        btnInicio.setStyle(buttonStyle);
        btnInicio.setMaxWidth(Double.MAX_VALUE);
        btnInicio.setOnAction(e -> mostrarTelaInicial());

        Button btnClientes = new Button("👥 Clientes");
        btnClientes.setStyle(buttonStyle);
        btnClientes.setMaxWidth(Double.MAX_VALUE);
        btnClientes.setOnAction(e -> mostrarTelaClientes());

        Button btnReservas = new Button("📅 Reservas");
        btnReservas.setStyle(buttonStyle);
        btnReservas.setMaxWidth(Double.MAX_VALUE);
        btnReservas.setOnAction(e -> mostrarTelaReservas());

        Button btnRelatorios = new Button("📄 Relatórios PDF");
        btnRelatorios.setStyle(buttonStyle);
        btnRelatorios.setMaxWidth(Double.MAX_VALUE);
        btnRelatorios.setOnAction(e -> mostrarTelaRelatorios());
     // ... Botões anteriores

        Button btnLocacaoEquip = new Button("⚽ Locar Equipamentos");
        btnLocacaoEquip.setStyle(buttonStyle);
        btnLocacaoEquip.setMaxWidth(Double.MAX_VALUE);
        btnLocacaoEquip.setOnAction(e -> mostrarTelaLocacaoEquipamento());

       menu.getChildren().addAll(lblMenu, sep, btnInicio, btnClientes, btnReservas, btnLocacaoEquip, btnRelatorios);
       return menu;
    }

    // --- MÉTODOS DE NAVEGAÇÃO ---

    private void mostrarTelaInicial() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-alignment: center;");
        Label welcome = new Label("Bem-vindo ao Sistema Esportivo");
        welcome.setFont(new Font(24));
        content.getChildren().add(welcome);
        rootLayout.setCenter(content);
    }

    private void mostrarTelaClientes() {
        TelaClientes view = new TelaClientes(clienteService);
        rootLayout.setCenter(view.getView());
    }

    private void mostrarTelaReservas() {
        TelaReservas view = new TelaReservas(reservaService, clienteService, funcionarioLogado);
        rootLayout.setCenter(view.getView());
    }
    
    private void mostrarTelaRelatorios() {
        TelaRelatorios view = new TelaRelatorios(relatorioService);
        rootLayout.setCenter(view.getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
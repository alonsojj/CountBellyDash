package br.com.countbellydash.ui;

import br.com.countbellydash.model.DashboardData;
import br.com.countbellydash.model.SessionDto;
import br.com.countbellydash.service.DashboardService;
import br.com.countbellydash.service.SessionService;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos; 
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.List;
import java.util.Comparator;
import java.util.stream.Collectors;
import br.com.countbellydash.service.BlacklistWhitelistService;
import javafx.scene.image.Image;

public class Main extends Application {

    private final DashboardService service = new DashboardService();
    private Stage primaryStage; 
    private BorderPane rootDashboard;
    private double zoomScale = 1.0; 
        private static final String API_URL = dotenv.get("API_URL");
    private static final String USERNAME = dotenv.get("BOT_USERNAME");
    private static final String PASSWORD = dotenv.get("BOT_PASSWORD");
    
    private static final double ZOOM_STEP = 0.1;
    private static final double MAX_ZOOM = 1.5;
    private static final double MIN_ZOOM = 0.8;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        stage.setTitle("Countbelly Dashboard - Login");
        try {
            Image icon = new Image(getClass().getResourceAsStream("/logo.png"));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.err.println("Error loading application icon: " + e.getMessage());
        }
        stage.setFullScreen(false); 
        showLoginScreen(stage);
    }
    
    // --- LÓGICA DE LOGIN ---

    private void showLoginScreen(Stage stage) {
        Label title = createTitleLabel("Acesso ao Dash do CountBelly");
        TextField usernameField = new TextField();
        usernameField.setPromptText("Usuário");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Senha");
        Button loginButton = new Button("Acessar");
        Label messageLabel = new Label("");
        
        usernameField.setMaxWidth(250);
        passwordField.setMaxWidth(250);
        loginButton.getStyleClass().add("login-button");
        messageLabel.getStyleClass().add("metric-danger");

        VBox loginLayout = new VBox(20);
        loginLayout.setAlignment(Pos.CENTER);
        loginLayout.setPadding(new Insets(50));
        loginLayout.getChildren().addAll(
            title, usernameField, passwordField, loginButton, messageLabel
        );
        loginLayout.getStyleClass().add("login-panel");
        
        loginButton.setOnAction(e -> {
            String user = usernameField.getText();
            String pass = passwordField.getText();
            
            if (USERNAME.equals(user) && PASSWORD.equals(pass)) {
                showDashboard(stage);
            } else {
                messageLabel.setText("Usuário ou senha incorretos.");
            }
        });

        Scene loginScene = new Scene(loginLayout, 500, 350);
        try {
            String cssPath = getClass().getResource("/dashboard.css").toExternalForm();
            loginScene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("cade esse arquivo?");
        }
        
        stage.setScene(loginScene); 
        stage.setMinHeight(350);
        stage.setMinWidth(500);
        stage.show();
    }
    

    // --- LÓGICA DO DASHBOARD ---

    private void showDashboard(Stage stage) {
        
        rootDashboard = new BorderPane();
        rootDashboard.setPadding(new Insets(20));
        
        Scene scene = new Scene(rootDashboard, 1200, 800); 
        try {
            String cssPath = getClass().getResource("/dashboard.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (NullPointerException e) {
            System.err.println("AVISO: Não foi possível carregar o arquivo 'dashboard.css'. O dashboard pode aparecer sem estilos.");
        }

        stage.setTitle("Countbelly Dashboard");
        stage.setScene(scene);
        
        stage.setFullScreen(false); 
        stage.setMaximized(true); 
        
        refreshDashboardContent(); 
        
        stage.show();
    }
    
    /**
     * Método que refaz a busca de dados e reconstrói o conteúdo do dashboard.
     */
    private void refreshDashboardContent() {
        // Puxa todos os dados
        DashboardData data = SessionService.buildDashboardData();
        // List<DashboardService.AgendaItem> fullAgenda = service.fetchAgendaDetails();

        // MOCK DE DADOS PARA A AGENDA (PARA EVITAR ERRO DE BANCO POR ENQUANTO)
        List<DashboardService.AgendaItem> fullAgenda = new java.util.ArrayList<>();
        fullAgenda.add(new DashboardService.AgendaItem("Cliente Mock 1", "2025-12-05 10:00", "PF", "Agendada"));
        fullAgenda.add(new DashboardService.AgendaItem("Cliente Mock 2", "2025-12-01 14:30", "PJ", "Realizada"));
        fullAgenda.add(new DashboardService.AgendaItem("Cliente Mock 3", "2025-12-10 11:00", "PF", "Agendada"));
        fullAgenda.add(new DashboardService.AgendaItem("Cliente Mock 4", "2025-11-28 09:00", "PJ", "Realizada"));
        fullAgenda.add(new DashboardService.AgendaItem("Cliente Mock 5", "2025-12-03 16:00", "PF", "Agendada"));
        fullAgenda.add(new DashboardService.AgendaItem("Cliente Mock 6", "2025-11-25 11:00", "PF", "Realizada"));
        fullAgenda.add(new DashboardService.AgendaItem("Cliente Mock 7", "2025-12-06 13:00", "PJ", "Agendada"));
        fullAgenda.add(new DashboardService.AgendaItem("Cliente Mock 8", "2025-11-30 10:00", "PF", "Cancelada"));
        // FIM DO MOCK 
        
        // --- 1. Lógica de Filtragem e Ordenação da Agenda ---
        List<DashboardService.AgendaItem> futureAgenda = service.getFutureAgenda(fullAgenda);
        List<DashboardService.AgendaItem> pastAgenda = service.getPastAgenda(fullAgenda);


        // --- 2. HEADER: Título e Ações ---
        
        Button refreshButton = new Button("🔄 Atualizar Dados");
        refreshButton.getStyleClass().addAll("action-button", "refresh-button");
        refreshButton.setOnAction(e -> refreshDashboardContent()); 
        
        Button zoomInButton = new Button("Zoom +");
        zoomInButton.getStyleClass().addAll("action-button", "zoom-button");
        zoomInButton.setOnAction(e -> zoomIn());

        Button zoomOutButton = new Button("Zoom -");
        zoomOutButton.getStyleClass().addAll("action-button", "zoom-button");
        zoomOutButton.setOnAction(e -> zoomOut());

        Button logoutButton = new Button("Sair 🚪");
        logoutButton.getStyleClass().addAll("action-button", "logout-button"); 
        logoutButton.setOnAction(e -> showLoginScreen(primaryStage)); 
        
        HBox zoomControls = new HBox(10); 
        zoomControls.getChildren().addAll(zoomOutButton, zoomInButton);
        zoomControls.setAlignment(Pos.CENTER); 
        
        HBox actionControlsRight = new HBox(20); 
        actionControlsRight.setAlignment(Pos.CENTER); 
        actionControlsRight.getChildren().addAll(refreshButton, zoomControls, logoutButton); 

        Label titleLabel = createTitleLabel("📊 Dashboard - Countbelly Chatbot");
        
        Label statusLabel = createStatusLabel("Status da Fonte: " + data.getStatusConexao());
        if (data.getStatusConexao().contains("ERRO")) {
            statusLabel.getStyleClass().add("metric-danger");
        }
        
        VBox titleAndStatus = new VBox(5);
        titleAndStatus.getChildren().addAll(
            titleLabel,
            statusLabel
        );
        titleAndStatus.setAlignment(Pos.CENTER_LEFT); 

        HBox topHeader = new HBox(30);
        topHeader.setAlignment(Pos.CENTER_LEFT); 
        
        topHeader.getChildren().add(titleAndStatus); 
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topHeader.getChildren().add(spacer);
        
        topHeader.getChildren().add(actionControlsRight); 
        
        rootDashboard.setTop(topHeader);
        
        // --- 3. LAYOUT CENTRAL (Cards, Gráficos e Agenda) ---
        
VBox centralContentVBox = new VBox(25); 
        VBox.setVgrow(centralContentVBox, Priority.ALWAYS); 
        
        // 3A. LINHA DE CARDS DE MÉTRICA (TOPO)
        HBox topMetrics = createTopMetrics(data); 
        VBox.setVgrow(topMetrics, Priority.NEVER); 
        centralContentVBox.getChildren().add(topMetrics);


        // 3B. GRID PRINCIPAL: Gráficos (Col. 0) vs Agenda (Col. 1)
        
        GridPane mainGrid = new GridPane(); 
        mainGrid.setHgap(30);
        mainGrid.setVgap(25);
        VBox.setVgrow(mainGrid, Priority.ALWAYS); 
        
        // Restrições de Coluna: 60% (Gráficos) e 40% (Agenda)
        ColumnConstraints gridCol1 = new ColumnConstraints(); 
        gridCol1.setHgrow(Priority.ALWAYS); 
        gridCol1.setPercentWidth(60); 
        
        ColumnConstraints gridCol2 = new ColumnConstraints(); 
        gridCol2.setHgrow(Priority.ALWAYS);
        gridCol2.setPercentWidth(40); 
        mainGrid.getColumnConstraints().addAll(gridCol1, gridCol2); 
        
        RowConstraints gridRow1 = new RowConstraints();
        gridRow1.setVgrow(Priority.ALWAYS); 
        mainGrid.getRowConstraints().add(gridRow1);


        // PAINEL DE GRÁFICOS (COLUNA 0: 60%)
        VBox chartVBoxStack = new VBox(25);
        VBox.setVgrow(chartVBoxStack, Priority.ALWAYS); 

        PieChart pieChart = createSegmentationChart(data.getClientesPF(), data.getClientesPJ());
        BarChart<String, Number> barChart = createDailyVolumeChart(service.fetchDailyMessageVolume());

        VBox.setVgrow(pieChart, Priority.ALWAYS);
        VBox.setVgrow(barChart, Priority.ALWAYS);
        
        chartVBoxStack.getChildren().addAll(pieChart, barChart);
        GridPane.setValignment(chartVBoxStack, VPos.TOP); 
        mainGrid.add(chartVBoxStack, 0, 0);


        // PAINEL DA AGENDA E SESSÕES ATIVAS (COLUNA 1: 40%)
        TabPane rightHandTabPane = new TabPane();
        rightHandTabPane.getStyleClass().add("custom-tab-pane"); 
        rightHandTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE); // Desabilita o fechamento de abas
        
        // Tab da Agenda
        VBox agendaPanel = createAgendaDetailPanel(futureAgenda, pastAgenda, "📅 Agenda (Próximo e Histórico)");
        Tab agendaTab = new Tab("Agenda", agendaPanel);
        agendaTab.getStyleClass().add("custom-tab"); 
        agendaTab.setGraphic(new Label("📅")); // Ícone
        
        // Tab de Sessões Ativas
        VBox activeSessionsPanel = createActiveSessionsPanel(data); // Passa o DashboardData para acessar as sessões
        Tab sessionsTab = new Tab("Sessões Ativas", activeSessionsPanel);
        sessionsTab.getStyleClass().add("custom-tab"); 
        sessionsTab.setGraphic(new Label("💬")); // Ícone
        
        // Nova Tab: Blacklist/Whitelist (Mockup)
        VBox blacklistWhitelistPanel = createBlacklistWhitelistPanel();
        Tab blacklistWhitelistTab = new Tab("Blacklist/Whitelist", blacklistWhitelistPanel);
        blacklistWhitelistTab.getStyleClass().add("custom-tab");
        blacklistWhitelistTab.setGraphic(new Label("🚫")); // Ícone para a nova aba

        rightHandTabPane.getTabs().addAll(agendaTab, sessionsTab, blacklistWhitelistTab);
        
        VBox.setVgrow(rightHandTabPane, Priority.ALWAYS); 
        GridPane.setValignment(rightHandTabPane, VPos.TOP); 
        mainGrid.add(rightHandTabPane, 1, 0);


        // Adiciona o Grid Principal ao VBox Central
        centralContentVBox.getChildren().add(mainGrid);
        
        // APLICA O ZOOM
        centralContentVBox.setScaleX(zoomScale);
        centralContentVBox.setScaleY(zoomScale);
        
        rootDashboard.setCenter(centralContentVBox); 
        BorderPane.setMargin(centralContentVBox, new Insets(20, 0, 0, 0)); 
    }
    
    // --- MÉTODOS DE ZOOM ---

    /**
     * Cria o painel para exibir as sessões ativas individualmente.
     * Assume-se que 'ativas' são as sessões que não estão em estado 13 (aguardando humano) ou 14 (atendimento humano)
     * e cujos detalhes estão no DashboardData.
     * @param data Os dados completos do Dashboard.
     * @return Um VBox contendo a lista de sessões ativas.
     */
    private VBox createActiveSessionsPanel(DashboardData data) {
        VBox sessionsPanel = new VBox(10);
        sessionsPanel.getStyleClass().add("detail-panel"); // Reutiliza estilo do painel da agenda
        
        Label title = new Label("💬 Sessões Ativas");
        title.getStyleClass().add("detail-title");
        sessionsPanel.getChildren().add(title);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        VBox sessionItemsBox = new VBox(10);
        sessionItemsBox.setMaxWidth(Double.MAX_VALUE);
        sessionItemsBox.setPadding(new Insets(5, 5, 5, 5));


        List<SessionDto> activeSessions = data.getSessionsMap().values().stream()
                                               .collect(Collectors.toList());

        System.out.println("DEBUG: Total active sessions found: " + activeSessions.size());

        if (activeSessions.isEmpty()) {
            Label noData = new Label("Nenhuma sessão ativa no momento.");
            noData.getStyleClass().add("agenda-item");
            noData.setMaxWidth(Double.MAX_VALUE);
            sessionItemsBox.getChildren().add(noData);
        } else {
            for (SessionDto session : activeSessions) {
                System.out.println("DEBUG: Processing active session for userId: " + session.getUserId());
                VBox sessionCard = new VBox(5);
                sessionCard.getStyleClass().add("session-card");
                sessionCard.setMaxWidth(Double.MAX_VALUE);

                Label clientInfo = new Label(
                    String.format("Cliente: %s (%s)\nID da Sessão: %s\nEstado: %d",
                        session.getClient() != null ? session.getClient().getName() : "Desconhecido",
                        session.getClient() != null && session.getClient().getDocumentType() != null ? session.getClient().getDocumentType() : "N/A",
                        session.getUserId(),
                        session.getState()
                    )
                );
                clientInfo.setWrapText(true);
                clientInfo.getStyleClass().add("session-client-info");
                sessionCard.getChildren().add(clientInfo);

                Button endButton = new Button("Encerrar 🔴");
                endButton.getStyleClass().add("end-session-button");
                endButton.setOnAction(e -> {
                    System.out.println("DEBUG: Attempting to end session for userId: " + session.getUserId());
                    SessionService.deleteSession(session.getUserId());
                    refreshDashboardContent();
                });
                HBox buttonBox = new HBox(endButton);
                buttonBox.setAlignment(Pos.CENTER_RIGHT);
                sessionCard.getChildren().add(buttonBox);

                sessionItemsBox.getChildren().add(sessionCard);
            }
        }
        
        scrollPane.setContent(sessionItemsBox);
        sessionsPanel.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        VBox.setVgrow(sessionsPanel, Priority.ALWAYS);

        return sessionsPanel;
    }

    /**
     * Cria um painel de mockup para a funcionalidade de Blacklist/Whitelist.
     * @return Um VBox contendo elementos de UI de mockup.
     */
    private VBox createBlacklistWhitelistPanel() {
        VBox panel = new VBox(15); // Increased spacing
        panel.getStyleClass().add("detail-panel");
        panel.setPadding(new Insets(15));
        panel.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("🚫 Blacklist & Whitelist");
        title.getStyleClass().add("detail-title");
        panel.getChildren().add(title);

        // --- Mode Selection ---
        Label modeSelectionTitle = new Label("Modo de Operação:");
        modeSelectionTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        
        ComboBox<String> modeComboBox = new ComboBox<>();
        modeComboBox.getItems().addAll("BLACKLIST", "WHITELIST", "DISABLE");
        modeComboBox.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(modeComboBox, Priority.ALWAYS);

        HBox modeBox = new HBox(10, modeSelectionTitle, modeComboBox);
        modeBox.setAlignment(Pos.CENTER_LEFT);
        panel.getChildren().add(modeBox);

        // --- Fetch and set current mode ---
        final String currentMode = BlacklistWhitelistService.getMode();
        if (currentMode != null && modeComboBox.getItems().contains(currentMode)) {
            modeComboBox.setValue(currentMode);
        } else {
            System.err.println("Não foi possível buscar o modo ou modo inválido. Usando padrão 'DISABLE'.");
            modeComboBox.setValue("DISABLE"); // Fallback
        }

        // --- Text Areas ---
        Label blacklistLabel = new Label("Números na Blacklist (um por linha):");
        blacklistLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TextArea blacklistTextArea = new TextArea();
        blacklistTextArea.setPrefRowCount(5);
        blacklistTextArea.setPromptText("Digite os números da blacklist...");

        Label whitelistLabel = new Label("Números na Whitelist (um por linha):");
        whitelistLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px;");
        TextArea whitelistTextArea = new TextArea();
        whitelistTextArea.setPrefRowCount(5);
        whitelistTextArea.setPromptText("Digite os números da whitelist...");

        panel.getChildren().addAll(blacklistLabel, blacklistTextArea, whitelistLabel, whitelistTextArea);

        // --- UI Logic ---
        Runnable updateTextAreaVisibility = () -> {
            String selected = modeComboBox.getValue();
            boolean isBlacklist = "BLACKLIST".equals(selected);
            boolean isWhitelist = "WHITELIST".equals(selected);

            blacklistLabel.setVisible(isBlacklist);
            blacklistLabel.setManaged(isBlacklist);
            blacklistTextArea.setVisible(isBlacklist);
            blacklistTextArea.setManaged(isBlacklist);

            whitelistLabel.setVisible(isWhitelist);
            whitelistLabel.setManaged(isWhitelist);
            whitelistTextArea.setVisible(isWhitelist);
            whitelistTextArea.setManaged(isWhitelist);
        };

        Runnable loadNumbers = () -> {
            blacklistTextArea.setText(String.join("\n", BlacklistWhitelistService.getBlacklist()));
            whitelistTextArea.setText(String.join("\n", BlacklistWhitelistService.getWhitelist()));
            updateTextAreaVisibility.run();
        };

        loadNumbers.run();
        modeComboBox.valueProperty().addListener((obs, o, n) -> updateTextAreaVisibility.run());

        // --- Save Button ---
        Button saveButton = new Button("Salvar Configurações");
        saveButton.getStyleClass().add("action-button");
        saveButton.setOnAction(e -> {
            String selectedMode = modeComboBox.getValue();
            
            // 1. Save mode if changed
            if (currentMode != null && !currentMode.equals(selectedMode)) {
                System.out.println("DEBUG: Salvando modo alterado de '" + currentMode + "' para '" + selectedMode + "'");
                BlacklistWhitelistService.postMode(selectedMode);
            }

            // 2. Save lists
            if ("BLACKLIST".equals(selectedMode)) {
                List<String> newItems = List.of(blacklistTextArea.getText().split("\n")).stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
                List<String> oldItems = BlacklistWhitelistService.getBlacklist();
                List<String> toAdd = newItems.stream().filter(i -> !oldItems.contains(i)).collect(Collectors.toList());
                List<String> toRemove = oldItems.stream().filter(i -> !newItems.contains(i)).collect(Collectors.toList());
                if (!toAdd.isEmpty()) BlacklistWhitelistService.postBlacklist(toAdd);
                if (!toRemove.isEmpty()) BlacklistWhitelistService.deleteBlacklist(toRemove);
            } else if ("WHITELIST".equals(selectedMode)) {
                List<String> newItems = List.of(whitelistTextArea.getText().split("\n")).stream().map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
                List<String> oldItems = BlacklistWhitelistService.getWhitelist();
                List<String> toAdd = newItems.stream().filter(i -> !oldItems.contains(i)).collect(Collectors.toList());
                List<String> toRemove = oldItems.stream().filter(i -> !newItems.contains(i)).collect(Collectors.toList());
                if (!toAdd.isEmpty()) BlacklistWhitelistService.postWhitelist(toAdd);
                if (!toRemove.isEmpty()) BlacklistWhitelistService.deleteWhitelist(toRemove);
            }
            
            // 3. Refresh dashboard to reflect all changes
            refreshDashboardContent();
        });
        panel.getChildren().add(saveButton);

        VBox.setVgrow(panel, Priority.ALWAYS);
        return panel;
    }

    private void zoomIn() {
        if (zoomScale < MAX_ZOOM) {
            zoomScale = Math.min(zoomScale + ZOOM_STEP, MAX_ZOOM);
            applyZoom();
        }
    }
    
    private void zoomOut() {
        if (zoomScale > MIN_ZOOM) {
            zoomScale = Math.max(zoomScale - ZOOM_STEP, MIN_ZOOM);
            applyZoom();
        }
    }

    private void applyZoom() {
        if (rootDashboard != null) {
            refreshDashboardContent();
        }
    }
    
    // --- MÉTODOS AUXILIARES ---

    private VBox createAgendaDetailPanel(List<DashboardService.AgendaItem> futureAgenda, 
                                          List<DashboardService.AgendaItem> pastAgenda, 
                                          String panelTitle) {
        
        VBox panel = new VBox(10);
        panel.getStyleClass().add("detail-panel");
        
        Label title = new Label(panelTitle);
        title.getStyleClass().add("detail-title");
        panel.getChildren().add(title);

        GridPane agendaLayoutGrid = new GridPane();
        agendaLayoutGrid.setVgap(15);
        GridPane.setVgrow(agendaLayoutGrid, Priority.ALWAYS);

        RowConstraints row50 = new RowConstraints();
        row50.setVgrow(Priority.ALWAYS);
        row50.setPercentHeight(50);
        agendaLayoutGrid.getRowConstraints().addAll(row50, row50);
        
        
        // --- SEÇÃO 1: PRÓXIMAS REUNIÕES (Metade Superior) ---
        VBox futureSection = createAgendaSection(futureAgenda, "Próximas Reuniões (Futuras):", false);
        GridPane.setValignment(futureSection, VPos.TOP);
        agendaLayoutGrid.add(futureSection, 0, 0);
        
        // --- SEÇÃO 2: HISTÓRICO RECENTE (Metade Inferior) ---
        VBox pastSection = createAgendaSection(pastAgenda, "Histórico Recente (Passadas):", true);
        GridPane.setValignment(pastSection, VPos.TOP);
        agendaLayoutGrid.add(pastSection, 0, 1);
        
        panel.getChildren().add(agendaLayoutGrid);
        
        panel.setMaxWidth(Double.MAX_VALUE);
        panel.setMaxHeight(Double.MAX_VALUE); 
        VBox.setVgrow(panel, Priority.ALWAYS); 

        return panel;
    }

    /**
     * MÉTODO CORRIGIDO para garantir que o Label se expanda horizontalmente
     */
    private VBox createAgendaSection(List<DashboardService.AgendaItem> agenda, String subTitle, boolean isPastSection) {
        VBox section = new VBox(5);
        VBox.setVgrow(section, Priority.ALWAYS); 
        
        Label title = new Label(subTitle);
        title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #555555;");
        section.getChildren().add(title);
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true); // Faz o conteúdo interno usar a largura total do ScrollPane
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");
        
        VBox agendaItemsBox = new VBox(10);
        // Garante que o VBox interno use toda a largura
        agendaItemsBox.setMaxWidth(Double.MAX_VALUE); 
        agendaItemsBox.setPadding(new Insets(5, 5, 5, 5));
        
        if (agenda.isEmpty()) {
            Label noData = new Label(isPastSection ? "Sem histórico recente." : "Nenhuma reunião futura.");
            noData.getStyleClass().add("agenda-item");
            noData.setMaxWidth(Double.MAX_VALUE); 
            agendaItemsBox.getChildren().add(noData);
        } else {
            for (DashboardService.AgendaItem item : agenda) {
                
                String statusDisplay = item.status.equals("Agendada") ? "🟢" : "⚫";
                
                Label detail = new Label(
                    String.format("%s %s (%s)\nCliente: %s\nData/Hora: %s", 
                        statusDisplay,
                        item.status.toUpperCase(), 
                        item.clienteType, item.nomeCliente, item.dataHora)
                );
                detail.setWrapText(true); 
                detail.getStyleClass().add("agenda-item");
                // Garante que o Label de detalhe se estenda por toda a largura
                detail.setMaxWidth(Double.MAX_VALUE); 
                
                if (isPastSection) {
                    detail.getStyleClass().add("agenda-item-history");
                }
                
                // Adiciona o Label diretamente ao VBox (o setMaxWidth resolve o problema)
                agendaItemsBox.getChildren().add(detail);
            }
        }
        
        scrollPane.setContent(agendaItemsBox);
        section.getChildren().add(scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS); 
        GridPane.setVgrow(section, Priority.ALWAYS); 

        return section;
    }
    
    private HBox createTopMetrics(DashboardData data) {
        HBox hbox = new HBox(20);
        hbox.getChildren().addAll(
            createMetricCard("Total de Sessões", String.valueOf(data.getClientesAtendidosTotal()), "metric-value"),
            createMetricCard("Em Atendimento (Bot)", String.valueOf(data.getClientesAguardando()), "metric-value"), 
            createMetricCard("Atend. Humano", String.valueOf(data.getAtendimentoHumano()), "metric-danger"),
            createMetricCard("Reuniões Agendadas", String.valueOf(data.getReunioesAgendadas()), "metric-value")
        );
        HBox.setHgrow(hbox, Priority.ALWAYS); 
        return hbox;
    }

    private VBox createMetricCard(String title, String value, String valueStyleClass) {
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("metric-title");

        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().addAll("metric-value", valueStyleClass);

        VBox card = new VBox(5);
        card.getStyleClass().add("metric-card");
        card.getChildren().addAll(titleLabel, valueLabel);
        
        VBox.setVgrow(card, Priority.NEVER); 
        card.setMaxHeight(Region.USE_PREF_SIZE); 
        HBox.setHgrow(card, Priority.ALWAYS); 
        
        return card;
    }

    private PieChart createSegmentationChart(int pfCount, int pjCount) {
        ObservableList<Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Clientes Pessoa Física (PF) (" + pfCount + ")", pfCount), 
            new PieChart.Data("Clientes Pessoa Jurídica (PJ) (" + pjCount + ")", pjCount) 
        );
        final PieChart chart = new PieChart(pieChartData);
        chart.setTitle("Segmentação PF vs PJ");
        chart.setLegendSide(javafx.geometry.Side.BOTTOM);
        return chart;
    }
    
    private BarChart<String, Number> createDailyVolumeChart(Map<String, Integer> volumeData) {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        
        chart.setTitle("Mensagens Recebidas por Hora (Hoje)");
        xAxis.setLabel("Hora do Dia");
        yAxis.setLabel("Volume de Mensagens");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Mensagens");
        
        for (Map.Entry<String, Integer> entry : volumeData.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        chart.getData().add(series);
        chart.setLegendVisible(false);
        return chart;
    }

    private Label createTitleLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("titulo-principal");
        return label;
    }

    private Label createStatusLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("metric-title");
        return label;
    }

    public static void main(String[] args) {
        launch(args);
    }
}

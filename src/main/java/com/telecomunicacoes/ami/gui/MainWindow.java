package com.telecomunicacoes.ami.gui;

import com.telecomunicacoes.ami.codec.AMIPseudoternary;
import com.telecomunicacoes.ami.codec.BinaryConverter;
import com.telecomunicacoes.ami.codec.Encryption;
import com.telecomunicacoes.ami.model.Message;
import com.telecomunicacoes.ami.network.Client;
import com.telecomunicacoes.ami.network.Server;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Janela principal da aplicação
 * Integra todas as funcionalidades: criptografia, codificação AMI, rede e visualização
 */
public class MainWindow {

    // Componentes principais
    private Encryption encryption;
    private AMIPseudoternary ami;
    private Server server;
    private Client client;

    // Interface gráfica - Transmissão
    private TextArea txtOriginalTx;
    private TextArea txtEncryptedTx;
    private TextArea txtBinaryTx;
    private TextArea txtEncodedTx;
    private WaveformChart chartTx;

    // Interface gráfica - Recepção
    private TextArea txtEncodedRx;
    private TextArea txtBinaryRx;
    private TextArea txtEncryptedRx;
    private TextArea txtOriginalRx;
    private WaveformChart chartRx;

    // Controles
    private TextField txtServerIP;
    private TextField txtServerPort;
    private Button btnStartServer;
    private Button btnStopServer;
    private Button btnSend;
    private TextArea txtLog;

    private Stage primaryStage;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Inicializa componentes
        encryption = new Encryption();
        ami = new AMIPseudoternary();
        server = new Server(5555);
        client = new Client();

        // Configura handlers
        setupHandlers();

        // Cria interface
        Scene scene = createScene();

        // Configura janela
        primaryStage.setTitle("AMI Pseudoternário - Comunicação de Dados");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1400);
        primaryStage.setMinHeight(900);
        primaryStage.setOnCloseRequest(e -> cleanup());
        primaryStage.show();

        // Log inicial
        log("✓ Aplicação iniciada");
        log("Algoritmo: AMI Pseudoternário (Bit 1=0V, Bit 0=±V alternado)");
        log("Criptografia: XOR com chave");
    }

    /**
     * Cria a cena principal
     */
    private Scene createScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Título
        Label title = new Label("Sistema de Codificação AMI Pseudoternário");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        title.setTextFill(Color.DARKBLUE);

        VBox topBox = new VBox(10);
        topBox.getChildren().addAll(title, createControlPanel());
        topBox.setAlignment(Pos.CENTER);
        root.setTop(topBox);

        // Painéis de transmissão e recepção
        HBox mainPanel = new HBox(5);
        mainPanel.getChildren().addAll(
                createTransmissionPanel(),
                createReceptionPanel()
        );
        VBox.setVgrow(mainPanel, Priority.ALWAYS);  // Permite crescer
        root.setCenter(mainPanel);

        // Log
        root.setBottom(createLogPanel());

        return new Scene(root, 1400, 900);
    }

    /**
     * Cria painel de controle (conexão e envio)
     */
    private VBox createControlPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc; -fx-border-width: 1;");

        // Configuração de servidor
        HBox serverConfig = new HBox(10);
        serverConfig.setAlignment(Pos.CENTER);

        txtServerPort = new TextField("5555");
        txtServerPort.setPrefWidth(80);
        btnStartServer = new Button("▶ Iniciar Servidor");
        btnStopServer = new Button("⏹ Parar Servidor");
        btnStopServer.setDisable(true);

        btnStartServer.setOnAction(e -> startServer());
        btnStopServer.setOnAction(e -> stopServer());

        serverConfig.getChildren().addAll(
                new Label("Porta:"), txtServerPort,
                btnStartServer, btnStopServer
        );

        // Configuração de cliente
        HBox clientConfig = new HBox(10);
        clientConfig.setAlignment(Pos.CENTER);

        txtServerIP = new TextField("localhost");
        txtServerIP.setPrefWidth(150);
        btnSend = new Button("📤 Enviar Mensagem");
        btnSend.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSend.setOnAction(e -> sendMessage());

        clientConfig.getChildren().addAll(
                new Label("Servidor Destino:"), txtServerIP,
                new Label("Porta:"), new TextField("5555") {{ setPrefWidth(80); txtServerPort.textProperty().bindBidirectional(textProperty()); }},
                btnSend
        );

        panel.getChildren().addAll(
                new Label(),
                serverConfig,
                new Separator(),
                clientConfig
        );

        return panel;
    }

    /**
     * Cria painel de transmissão (lado esquerdo)
     */
    private VBox createTransmissionPanel() {
        VBox panel = new VBox(10);
        panel.setPrefWidth(680);

        Label title = new Label("📤 TRANSMISSÃO (Host A)");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        txtOriginalTx = createTextArea("Digite a mensagem aqui...", 3);
        txtEncryptedTx = createTextArea("Texto criptografado aparecerá aqui", 2);
        txtBinaryTx = createTextArea("Representação binária aparecerá aqui", 3);
        txtEncodedTx = createTextArea("Sinal codificado AMI aparecerá aqui", 2);

        chartTx = new WaveformChart(660, 200);
        chartTx.setTitle("Forma de Onda - Transmissão");

        Button btnProcess = new Button("⚙ Processar Mensagem");
        btnProcess.setOnAction(e -> processTransmission());

        panel.getChildren().addAll(
                title,
                new Label("1. Mensagem Original:"), txtOriginalTx,
                new Label("2. Mensagem Criptografada:"), txtEncryptedTx,
                new Label("3. Representação Binária:"), txtBinaryTx,
                new Label("4. Sinal Codificado AMI:"), txtEncodedTx,
                btnProcess,
                new Label("5. Forma de Onda:"), chartTx
        );

        return panel;
    }

    /**
     * Cria painel de recepção (lado direito)
     */
    private VBox createReceptionPanel() {
        VBox panel = new VBox(5);
        panel.setPrefWidth(680);

        Label title = new Label("📥 RECEPÇÃO (Host B)");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        txtEncodedRx = createTextArea("Sinal recebido aparecerá aqui", 2);
        txtBinaryRx = createTextArea("Binário decodificado aparecerá aqui", 3);
        txtEncryptedRx = createTextArea("Texto criptografado aparecerá aqui", 2);
        txtOriginalRx = createTextArea("Mensagem final aparecerá aqui", 3);

        chartRx = new WaveformChart(660, 200);
        chartRx.setTitle("Forma de Onda - Recepção");

        panel.getChildren().addAll(
                title,
                new Label("1. Sinal Recebido AMI:"), txtEncodedRx,
                new Label("2. Forma de Onda:"), chartRx,
                new Label("3. Binário Decodificado:"), txtBinaryRx,
                new Label("4. Texto Criptografado:"), txtEncryptedRx,
                new Label("5. Mensagem Original:"), txtOriginalRx
        );

        return panel;
    }

    /**
     * Cria painel de log
     */
    private VBox createLogPanel() {
        VBox panel = new VBox(5);
        panel.setPadding(new Insets(10, 0, 0, 0));

        Label title = new Label("📋 LOG DE EVENTOS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        txtLog = new TextArea();
        txtLog.setEditable(false);
        txtLog.setPrefHeight(120);
        txtLog.setStyle("-fx-font-family: monospace; -fx-font-size: 11;");

        panel.getChildren().addAll(title, txtLog);
        return panel;
    }

    /**
     * Cria área de texto configurada
     */
    private TextArea createTextArea(String prompt, int rows) {
        TextArea ta = new TextArea();
        ta.setPromptText(prompt);
        ta.setPrefRowCount(rows);
        ta.setWrapText(true);
        return ta;
    }

    /**
     * Processa a transmissão (etapas 1-5)
     */
    private void processTransmission() {
        try {
            String original = txtOriginalTx.getText().trim();

            if (original.isEmpty()) {
                showAlert("Erro", "Digite uma mensagem para processar!");
                return;
            }

            log("▶ Processando transmissão...");

            // 1. Criptografia
            String encrypted = encryption.encrypt(original);
            txtEncryptedTx.setText(encrypted);
            log("  1. Criptografia aplicada");

            // 2. Conversão para binário
            String binary = BinaryConverter.textToBinary(encrypted);
            txtBinaryTx.setText(BinaryConverter.formatBinary(binary));
            log("  2. Convertido para binário (" + binary.length() + " bits)");

            // 3. Codificação AMI
            int[] signal = ami.encode(binary);
            txtEncodedTx.setText(AMIPseudoternary.signalToString(signal) + "\n\n" +
                    AMIPseudoternary.getSignalStatistics(signal));
            log("  3. Codificado em AMI Pseudoternário");

            // 4. Visualização
            chartTx.setSignal(signal);
            log("  4. Forma de onda gerada");

            log("✓ Processamento concluído! Pronto para enviar.");

        } catch (Exception e) {
            log("✗ Erro no processamento: " + e.getMessage());
            e.printStackTrace();
            showAlert("Erro", "Erro ao processar: " + e.getMessage());
        }
    }

    /**
     * Envia mensagem pela rede
     */
    private void sendMessage() {
        try {
            int[] signal = chartTx.getSignal();
            if (signal == null || signal.length == 0) {
                showAlert("Erro", "Processe uma mensagem primeiro!");
                return;
            }

            // Verifica se o servidor está rodando
            if (!server.isRunning()) {
                showAlert("Servidor Não Iniciado",
                    "O servidor precisa estar rodando para receber mensagens.\n\n" +
                    "Clique em '▶ Iniciar Servidor' primeiro!");
                log("⚠ Tentativa de envio com servidor parado");
                return;
            }

            // Cria mensagem
            Message message = new Message();
            message.setOriginalText(txtOriginalTx.getText());
            message.setEncryptedText(txtEncryptedTx.getText());
            message.setBinaryString(txtBinaryTx.getText().replaceAll("\\s", ""));
            message.setEncodedSignal(signal);

            // Configura cliente
            client.setServerAddress(txtServerIP.getText());
            client.setServerPort(Integer.parseInt(txtServerPort.getText()));

            // Envia
            log("📤 Enviando mensagem...");
            client.sendMessage(
                    message,
                    () -> Platform.runLater(() -> log("✓ Mensagem enviada com sucesso!")),
                    error -> Platform.runLater(() -> log("✗ " + error))
            );

        } catch (Exception e) {
            log("✗ Erro ao enviar: " + e.getMessage());
            showAlert("Erro", "Erro ao enviar: " + e.getMessage());
        }
    }

    /**
     * Processa recepção de mensagem
     */
    private void processReception(Message message) {
        Platform.runLater(() -> {
            try {
                log("▶ Processando recepção...");

                // 1. Sinal recebido
                int[] signal = message.getEncodedSignal();
                txtEncodedRx.setText(AMIPseudoternary.signalToString(signal) + "\n\n" +
                        AMIPseudoternary.getSignalStatistics(signal));
                chartRx.setSignal(signal);
                log("  1. Sinal AMI recebido");

                // 2. Decodificação AMI
                String binary = ami.decode(signal);
                txtBinaryRx.setText(BinaryConverter.formatBinary(binary));
                log("  2. Decodificado de AMI para binário");

                // 3. Conversão binário para texto
                String encrypted = BinaryConverter.binaryToText(binary);
                txtEncryptedRx.setText(encrypted);
                log("  3. Convertido de binário para texto");

                // 4. Descriptografia
                String original = encryption.decrypt(encrypted);
                txtOriginalRx.setText(original);
                log("  4. Descriptografia aplicada");

                log("✓ Mensagem recebida: \"" + original + "\"");

            } catch (Exception e) {
                log("✗ Erro na recepção: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Inicia o servidor
     */
    private void startServer() {
        try {
            int port = Integer.parseInt(txtServerPort.getText());
            server.setPort(port);
            server.start();

            btnStartServer.setDisable(true);
            btnStopServer.setDisable(false);
            txtServerPort.setDisable(true);

        } catch (NumberFormatException e) {
            showAlert("Erro", "Porta inválida!");
        }
    }

    /**
     * Para o servidor
     */
    private void stopServer() {
        server.stop();
        btnStartServer.setDisable(false);
        btnStopServer.setDisable(true);
        txtServerPort.setDisable(false);
    }

    /**
     * Configura handlers de eventos
     */
    private void setupHandlers() {
        server.setMessageHandler(this::processReception);
        server.setStatusHandler(this::log);
        client.setStatusHandler(this::log);
    }

    /**
     * Adiciona mensagem ao log
     */
    private void log(String message) {
        Platform.runLater(() -> {
            String timestamp = java.time.LocalTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")
            );
            txtLog.appendText("[" + timestamp + "] " + message + "\n");
        });
    }

    /**
     * Mostra alerta
     */
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    /**
     * Limpeza ao fechar
     */
    private void cleanup() {
        if (server.isRunning()) {
            server.stop();
        }
        log("✓ Aplicação encerrada");
    }
}
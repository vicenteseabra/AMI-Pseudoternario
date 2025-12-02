package com.telecomunicacoes.ami.controller;

import com.telecomunicacoes.ami.codec.*;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class MainController {

    @FXML private TextField inputField;
    @FXML private TextField signalField;
    @FXML private TextArea logArea;
    @FXML private Canvas waveformCanvas;
    @FXML private CheckBox encryptCheckBox;

    private int[] currentSignal;
    private final Encryption encryption = new Encryption();
    private final AMIPseudoternary amiCodec = new AMIPseudoternary();

    @FXML
    public void initialize() {
        // Verificar se componentes foram injetados
        if (logArea == null) {
            System.err.println("ERRO: logArea não foi injetado!");
        }
        if (waveformCanvas == null) {
            System.err.println("ERRO: waveformCanvas não foi injetado!");
        }

        // Redimensionamento dinâmico do canvas
        waveformCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            if (currentSignal != null) {
                drawAMISignal(currentSignal);
            }
        });

        waveformCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            if (currentSignal != null) {
                drawAMISignal(currentSignal);
            }
        });

        addLog("Sistema iniciado com sucesso");
    }

    @FXML
    private void handleEncode() {
        try {
            String input = inputField.getText();
            if (input == null || input.isEmpty()) {
                showAlert("Erro", "Digite uma mensagem para codificar");
                return;
            }

            addLog("Iniciando codificação: \"" + input + "\"");

            // Criptografar se checkbox estiver marcado
            String toEncode = input;
            if (encryptCheckBox.isSelected()) {
                toEncode = encryption.encrypt(input);
                addLog("Mensagem criptografada: " + toEncode.substring(0, Math.min(toEncode.length(), 30)) + "...");
            }

            // Converter para binário
            String binary = BinaryConverter.textToBinary(toEncode);
            addLog("Convertido para binário: " + binary.length() + " bits");

            // Codificar em AMI
            currentSignal = amiCodec.encode(binary);
            addLog("Sinal AMI gerado: " + currentSignal.length + " símbolos");

            // Exibir resultado
            signalField.setText(formatSignal(currentSignal));
            drawAMISignal(currentSignal);

            addLog("Codificação concluída com sucesso");

        } catch (Exception e) {
            addLog("ERRO: " + e.getMessage());
            showAlert("Erro na Codificação", e.getMessage());
        }
    }

    @FXML
    private void handleDecode() {
        try {
            if (currentSignal == null) {
                showAlert("Erro", "Nenhum sinal para decodificar");
                return;
            }

            addLog("Iniciando decodificação...");

            // Decodificar AMI
            String binary = amiCodec.decode(currentSignal);
            addLog("Binário recuperado: " + binary.length() + " bits");

            // Converter para texto
            String decoded = BinaryConverter.binaryToText(binary);

            // Descriptografar se necessário
            if (encryptCheckBox.isSelected()) {
                decoded = encryption.decrypt(decoded);
                addLog("Mensagem descriptografada");
            }

            addLog("Mensagem decodificada: \"" + decoded + "\"");
            showAlert("Decodificação", "Mensagem: " + decoded);

        } catch (Exception e) {
            addLog("ERRO: " + e.getMessage());
            showAlert("Erro na Decodificação", e.getMessage());
        }
    }

    @FXML
    private void handleClearLogs() {
        logArea.clear();
        addLog("Logs limpos");
    }

    private void drawAMISignal(int[] signal) {
        GraphicsContext gc = waveformCanvas.getGraphicsContext2D();

        double width = waveformCanvas.getWidth();
        double height = waveformCanvas.getHeight();

        // Limpar canvas
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        // Configurações de desenho
        double midY = height / 2;
        double amplitude = Math.min(midY - 30, 120);
        double bitWidth = Math.min(width / Math.max(signal.length, 1), 100);

        // Desenhar grade de fundo
        gc.setStroke(Color.LIGHTGRAY);
        gc.setLineWidth(0.5);
        for (int i = 0; i <= signal.length; i++) {
            double x = i * bitWidth;
            gc.strokeLine(x, 0, x, height);
        }

        // Desenhar eixo central
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);
        gc.strokeLine(0, midY, width, midY);

        // Desenhar níveis de referência
        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeLine(0, midY - amplitude, width, midY - amplitude);
        gc.strokeLine(0, midY + amplitude, width, midY + amplitude);

        // Desenhar forma de onda
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(3);

        for (int i = 0; i < signal.length; i++) {
            double x1 = i * bitWidth;
            double x2 = x1 + bitWidth;
            double y = midY - (signal[i] * amplitude);

            // Transição vertical se necessário
            if (i > 0) {
                double prevY = midY - (signal[i - 1] * amplitude);
                if (prevY != y) {
                    gc.strokeLine(x1, prevY, x1, y);
                }
            }

            // Linha horizontal do bit atual
            gc.strokeLine(x1, y, x2, y);
        }

        // Desenhar labels dos valores
        gc.setFill(Color.BLACK);
        gc.fillText("+1", 5, midY - amplitude - 5);
        gc.fillText("0", 5, midY + 5);
        gc.fillText("-1", 5, midY + amplitude + 15);
    }

    private String formatSignal(int[] signal) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < Math.min(signal.length, 50); i++) {
            if (i > 0) sb.append(", ");
            sb.append(signal[i] > 0 ? "+" : "").append(signal[i]);
        }
        if (signal.length > 50) {
            sb.append(", ... (").append(signal.length).append(" total)");
        }
        sb.append("]");
        return sb.toString();
    }

    private void addLog(String message) {
        if (logArea != null) {
            String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String logEntry = String.format("[%s] %s\n", timestamp, message);
            logArea.appendText(logEntry);
            logArea.setScrollTop(Double.MAX_VALUE);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

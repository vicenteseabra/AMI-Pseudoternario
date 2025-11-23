package com.telecomunicacoes.ami.network;

import com.google.gson.Gson;
import com.telecomunicacoes.ami.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;

/**
 * Cliente TCP para enviar mensagens codificadas em AMI
 * Implementa envio assíncrono para não bloquear a interface
 */
public class Client {

    private static final int DEFAULT_PORT = 5555;
    private static final int CONNECTION_TIMEOUT = 5000; // 5 segundos

    private String serverAddress;
    private int serverPort;
    private Consumer<String> statusHandler;
    private Gson gson;

    public Client() {
        this("localhost", DEFAULT_PORT);
    }

    public Client(String serverAddress, int serverPort) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.gson = new Gson();
    }

    /**
     * Define o handler para atualizar status
     * @param handler Função que processa mensagens de status
     */
    public void setStatusHandler(Consumer<String> handler) {
        this.statusHandler = handler;
    }

    /**
     * Envia mensagem de forma assíncrona
     * @param message Mensagem a ser enviada
     * @param onSuccess Callback de sucesso
     * @param onError Callback de erro
     */
    public void sendMessage(Message message, Runnable onSuccess, Consumer<String> onError) {
        new Thread(() -> {
            try {
                sendMessageSync(message);
                if (onSuccess != null) {
                    onSuccess.run();
                }
            } catch (Exception e) {
                String errorMsg = "Erro ao enviar: " + e.getMessage();
                updateStatus("✗ " + errorMsg);
                if (onError != null) {
                    onError.accept(errorMsg);
                }
            }
        }).start();
    }

    /**
     * Envia mensagem de forma síncrona
     * @param message Mensagem a ser enviada
     * @throws IOException Se houver erro na conexão
     */
    private void sendMessageSync(Message message) throws IOException {
        updateStatus("Conectando ao servidor " + serverAddress + ":" + serverPort + "...");

        try (Socket socket = new Socket(serverAddress, serverPort)) {
            socket.setSoTimeout(CONNECTION_TIMEOUT);

            updateStatus("✓ Conectado ao servidor!");

            // Streams de entrada e saída
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Serializa a mensagem para JSON
            String json = gson.toJson(message);

            updateStatus("Enviando mensagem (" + json.length() + " bytes)...");

            // Envia a mensagem
            out.println(json);
            out.flush();

            // Aguarda confirmação
            try {
                String response = in.readLine();
                if ("ACK".equals(response)) {
                    updateStatus("✓ Mensagem enviada e confirmada!");
                } else {
                    updateStatus("✗ Resposta inesperada: " + response);
                }
            } catch (SocketTimeoutException e) {
                updateStatus("⚠ Timeout aguardando confirmação (mas mensagem foi enviada)");
            }

        } catch (IOException e) {
            updateStatus("✗ Erro de conexão: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Testa conexão com o servidor
     * @return true se conseguir conectar
     */
    public boolean testConnection() {
        try {
            updateStatus("Testando conexão com " + serverAddress + ":" + serverPort + "...");

            try (Socket socket = new Socket(serverAddress, serverPort)) {
                socket.setSoTimeout(2000);
                updateStatus("✓ Servidor respondeu!");
                return true;
            }

        } catch (IOException e) {
            updateStatus("✗ Servidor não está acessível: " + e.getMessage());
            return false;
        }
    }

    /**
     * Atualiza status via handler
     * @param status Mensagem de status
     */
    private void updateStatus(String status) {
        System.out.println("[Client] " + status);
        if (statusHandler != null) {
            statusHandler.accept(status);
        }
    }

    /**
     * Define endereço do servidor
     * @param address Endereço IP ou hostname
     */
    public void setServerAddress(String address) {
        this.serverAddress = address;
    }

    /**
     * Define porta do servidor
     * @param port Número da porta
     */
    public void setServerPort(int port) {
        this.serverPort = port;
    }

    /**
     * Retorna endereço do servidor
     * @return Endereço configurado
     */
    public String getServerAddress() {
        return serverAddress;
    }

    /**
     * Retorna porta do servidor
     * @return Porta configurada
     */
    public int getServerPort() {
        return serverPort;
    }
}
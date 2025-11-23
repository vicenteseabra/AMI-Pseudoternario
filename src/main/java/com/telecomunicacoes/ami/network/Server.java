package com.telecomunicacoes.ami.network;

import com.google.gson.Gson;
import com.telecomunicacoes.ami.model.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.function.Consumer;

/**
 * Servidor TCP para receber mensagens codificadas em AMI
 * Implementa thread separada para não bloquear a interface gráfica
 */
public class Server {

    private static final int DEFAULT_PORT = 5555;
    private static final int SOCKET_TIMEOUT = 1000; // 1 segundo

    private ServerSocket serverSocket;
    private Thread serverThread;
    private boolean running;
    private int port;
    private Consumer<Message> messageHandler;
    private Consumer<String> statusHandler;
    private final Gson gson;

    public Server() {
        this(DEFAULT_PORT);
    }

    public Server(int port) {
        this.port = port;
        this.running = false;
        this.gson = new Gson();
    }

    /**
     * Define o handler para processar mensagens recebidas
     * @param handler Função que processa a mensagem
     */
    public void setMessageHandler(Consumer<Message> handler) {
        this.messageHandler = handler;
    }

    /**
     * Define o handler para atualizar status do servidor
     * @param handler Função que processa mensagens de status
     */
    public void setStatusHandler(Consumer<String> handler) {
        this.statusHandler = handler;
    }

    /**
     * Inicia o servidor em uma thread separada
     */
    public void start() {
        if (running) {
            updateStatus("Servidor já está rodando!");
            return;
        }

        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                serverSocket.setSoTimeout(SOCKET_TIMEOUT);
                running = true;

                updateStatus("✓ Servidor iniciado na porta " + port);
                updateStatus("Aguardando conexões...");

                while (running) {
                    try {
                        Socket clientSocket = serverSocket.accept();
                        updateStatus("✓ Cliente conectado: " + clientSocket.getInetAddress().getHostAddress());

                        // Processa a mensagem em uma thread separada
                        handleClient(clientSocket);

                    } catch (SocketTimeoutException e) {
                        // Timeout normal, continua o loop
                        continue;
                    }
                }

            } catch (IOException e) {
                if (running) {
                    updateStatus("✗ Erro no servidor: " + e.getMessage());
                    e.printStackTrace();
                }
            } finally {
                cleanup();
            }
        });

        serverThread.setDaemon(true);
        serverThread.start();
    }

    /**
     * Processa a conexão com um cliente
     * @param clientSocket Socket do cliente
     */
    private void handleClient(Socket clientSocket) {
        new Thread(() -> {
            try (
                    BufferedReader in = new BufferedReader(
                            new InputStreamReader(clientSocket.getInputStream())
                    );
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                // Lê a mensagem JSON
                StringBuilder jsonBuilder = new StringBuilder();
                String line;

                while ((line = in.readLine()) != null) {
                    jsonBuilder.append(line);
                    if (line.trim().endsWith("}")) {
                        break;
                    }
                }

                String json = jsonBuilder.toString();

                if (!json.isEmpty()) {
                    // Desserializa a mensagem
                    Message message = gson.fromJson(json, Message.class);

                    updateStatus("✓ Mensagem recebida (" + message.getEncodedSignal().length + " elementos)");

                    // Processa a mensagem via handler
                    if (messageHandler != null) {
                        messageHandler.accept(message);
                    }

                    // Envia confirmação
                    out.println("ACK");
                } else {
                    updateStatus("✗ Mensagem vazia recebida");
                }

            } catch (IOException e) {
                updateStatus("✗ Erro ao processar cliente: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Para o servidor
     */
    public void stop() {
        if (!running) {
            return;
        }

        running = false;
        updateStatus("Parando servidor...");

        cleanup();

        if (serverThread != null) {
            try {
                serverThread.join(2000); // Aguarda até 2 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        updateStatus("✓ Servidor parado");
    }

    /**
     * Limpa recursos
     */
    private void cleanup() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Atualiza status via handler
     * @param status Mensagem de status
     */
    private void updateStatus(String status) {
        System.out.println("[Server] " + status);
        if (statusHandler != null) {
            statusHandler.accept(status);
        }
    }

    /**
     * Verifica se o servidor está rodando
     * @return true se estiver rodando
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Retorna a porta do servidor
     * @return Número da porta
     */
    public int getPort() {
        return port;
    }

    /**
     * Define uma nova porta (só funciona se servidor estiver parado)
     * @param port Nova porta
     */
    public void setPort(int port) {
        if (running) {
            throw new IllegalStateException("Não pode mudar porta com servidor rodando");
        }
        this.port = port;
    }
}
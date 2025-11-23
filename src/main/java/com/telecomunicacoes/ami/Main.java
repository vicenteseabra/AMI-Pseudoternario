package com.telecomunicacoes.ami;

import com.telecomunicacoes.ami.gui.MainWindow;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Classe principal da aplicação AMI Pseudoternário
 * Sistema de Comunicação de Dados com Codificação de Linha
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            MainWindow mainWindow = new MainWindow();
            mainWindow.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao iniciar aplicação: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package com.telecomunicacoes.ami.gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Componente para desenhar a forma de onda do sinal AMI Pseudoternário
 * Mostra os níveis +V, 0V, -V ao longo do tempo
 */
public class WaveformChart extends Canvas {

    private static final double PADDING = 40;
    private static final double GRID_COLOR_ALPHA = 0.2;

    private int[] signal;
    private String title;

    public WaveformChart(double width, double height) {
        super(width, height);
        this.title = "Forma de Onda AMI Pseudoternário";
        drawEmpty();
    }

    /**
     * Atualiza o sinal a ser exibido
     * @param signal Array com níveis de tensão (-1, 0, +1)
     */
    public void setSignal(int[] signal) {
        this.signal = signal;
        draw();
    }

    /**
     * Define o título do gráfico
     * @param title Título
     */
    public void setTitle(String title) {
        this.title = title;
        if (signal != null) {
            draw();
        } else {
            drawEmpty();
        }
    }

    /**
     * Desenha o gráfico vazio
     */
    private void drawEmpty() {
        GraphicsContext gc = getGraphicsContext2D();

        // Limpa o canvas
        gc.clearRect(0, 0, getWidth(), getHeight());

        // Fundo
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, getWidth(), getHeight());

        // Borda
        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeRect(0, 0, getWidth(), getHeight());

        // Mensagem
        gc.setFill(Color.GRAY);
        gc.fillText("Aguardando sinal...", getWidth() / 2 - 60, getHeight() / 2);
    }

    /**
     * Desenha o gráfico com o sinal
     */
    private void draw() {
        if (signal == null || signal.length == 0) {
            drawEmpty();
            return;
        }

        GraphicsContext gc = getGraphicsContext2D();
        double width = getWidth();
        double height = getHeight();

        // Limpa o canvas
        gc.clearRect(0, 0, width, height);

        // Fundo branco
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, width, height);

        // Área de desenho
        double chartWidth = width - 2 * PADDING;
        double chartHeight = height - 2 * PADDING;
        double centerY = PADDING + chartHeight / 2;

        // Desenha grid
        drawGrid(gc, PADDING, PADDING, chartWidth, chartHeight);

        // Desenha eixos
        drawAxes(gc, PADDING, centerY, chartWidth, chartHeight);

        // Desenha forma de onda
        drawWaveform(gc, PADDING, centerY, chartWidth, chartHeight);

        // Desenha título
        gc.setFill(Color.BLACK);
        gc.fillText(title, 10, 15);

        // Informações
        String info = String.format("Elementos: %d | Níveis: +V, 0V, -V", signal.length);
        gc.setFill(Color.DARKGRAY);
        gc.fillText(info, 10, height - 10);
    }

    /**
     * Desenha o grid de fundo
     */
    private void drawGrid(GraphicsContext gc, double x, double y, double w, double h) {
        gc.setStroke(Color.gray(0.8, GRID_COLOR_ALPHA));
        gc.setLineWidth(0.5);

        // Linhas horizontais
        for (int i = 0; i <= 4; i++) {
            double lineY = y + (h / 4) * i;
            gc.strokeLine(x, lineY, x + w, lineY);
        }

        // Linhas verticais (a cada 10 elementos)
        int step = Math.max(1, signal.length / 20);
        for (int i = 0; i <= signal.length; i += step) {
            double lineX = x + (w / signal.length) * i;
            gc.strokeLine(lineX, y, lineX, y + h);
        }
    }

    /**
     * Desenha os eixos principais
     */
    private void drawAxes(GraphicsContext gc, double x, double centerY, double w, double h) {
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2);

        // Eixo horizontal (tempo)
        gc.strokeLine(x, centerY, x + w, centerY);

        // Eixo vertical (tensão)
        gc.strokeLine(x, centerY - h/2, x, centerY + h/2);

        // Labels
        gc.setFill(Color.BLACK);
        gc.fillText("+V", x - 25, centerY - h/4);
        gc.fillText("0V", x - 25, centerY + 5);
        gc.fillText("-V", x - 25, centerY + h/4);

        gc.fillText("Tempo →", x + w - 60, centerY + h/2 + 30);
    }

    /**
     * Desenha a forma de onda do sinal
     */
    private void drawWaveform(GraphicsContext gc, double x, double centerY, double w, double h) {
        if (signal.length == 0) return;

        gc.setStroke(Color.BLUE);
        gc.setLineWidth(2);

        double stepX = w / signal.length;
        double amplitude = h / 4; // Espaço para cada nível

        // Desenha linha conectando os pontos
        for (int i = 0; i < signal.length; i++) {
            double x1 = x + i * stepX;
            double x2 = x + (i + 1) * stepX;

            // Converte nível (-1, 0, +1) para coordenada Y
            double y1 = centerY - (signal[i] * amplitude);
            double y2 = (i < signal.length - 1) ?
                    centerY - (signal[i + 1] * amplitude) : y1;

            // Linha horizontal no nível atual
            gc.strokeLine(x1, y1, x2, y1);

            // Linha vertical para transição
            if (i < signal.length - 1 && signal[i] != signal[i + 1]) {
                gc.strokeLine(x2, y1, x2, y2);
            }
        }

        // Desenha pontos nos níveis
        gc.setFill(Color.RED);
        for (int i = 0; i < signal.length; i++) {
            double pointX = x + i * stepX;
            double pointY = centerY - (signal[i] * amplitude);
            gc.fillOval(pointX - 3, pointY - 3, 6, 6);
        }

        // Desenha rótulos dos bits (se não for muito pequeno)
        if (stepX > 15 && signal.length <= 50) {
            gc.setFill(Color.DARKBLUE);
            for (int i = 0; i < signal.length; i++) {
                String label = signal[i] == 0 ? "1" : "0";
                double labelX = x + i * stepX + stepX / 2 - 3;
                double labelY = centerY + h / 2 + 15;
                gc.fillText(label, labelX, labelY);
            }
        }
    }

    /**
     * Limpa o gráfico
     */
    public void clear() {
        this.signal = null;
        drawEmpty();
    }

    /**
     * Retorna o sinal atual
     * @return Array de níveis
     */
    public int[] getSignal() {
        return signal;
    }
}
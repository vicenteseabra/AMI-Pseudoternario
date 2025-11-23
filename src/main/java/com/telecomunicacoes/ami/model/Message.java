package com.telecomunicacoes.ami.model;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Modelo de dados que representa uma mensagem no sistema
 * Contém todas as etapas de transformação da mensagem
 */
public class Message implements Serializable {
    private static final long serialVersionUID = 1L;

    private String originalText;           // Texto original
    private String encryptedText;          // Texto criptografado
    private String binaryString;           // Representação binária
    private int[] encodedSignal;           // Sinal codificado AMI (-1, 0, +1)
    private long timestamp;                // Timestamp do envio

    public Message() {
        this.timestamp = System.currentTimeMillis();
    }

    public Message(String originalText) {
        this();
        this.originalText = originalText;
    }

    // Getters e Setters
    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public String getEncryptedText() {
        return encryptedText;
    }

    public void setEncryptedText(String encryptedText) {
        this.encryptedText = encryptedText;
    }

    public String getBinaryString() {
        return binaryString;
    }

    public void setBinaryString(String binaryString) {
        this.binaryString = binaryString;
    }

    public int[] getEncodedSignal() {
        return encodedSignal;
    }

    public void setEncodedSignal(int[] encodedSignal) {
        this.encodedSignal = encodedSignal;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "originalText='" + originalText + '\'' +
                ", encryptedText='" + encryptedText + '\'' +
                ", binaryLength=" + (binaryString != null ? binaryString.length() : 0) +
                ", signalLength=" + (encodedSignal != null ? encodedSignal.length : 0) +
                ", timestamp=" + timestamp +
                '}';
    }
}
package com.telecomunicacoes.ami.codec;

import java.nio.charset.StandardCharsets;

/**
 * Classe responsável pela conversão entre texto e binário
 * Suporta ASCII estendido para caracteres especiais e acentuados
 */
public class BinaryConverter {

    /**
     * Converte texto para representação binária usando ASCII estendido
     * @param text Texto a ser convertido
     * @return String binária (8 bits por caractere)
     */
    public static String textToBinary(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        StringBuilder binary = new StringBuilder();
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);

        for (byte b : bytes) {
            // Converte cada byte para 8 bits
            String byteBinary = String.format("%8s", Integer.toBinaryString(b & 0xFF))
                    .replace(' ', '0');
            binary.append(byteBinary);
        }

        return binary.toString();
    }

    /**
     * Converte representação binária de volta para texto
     * @param binary String binária (múltiplo de 8 bits)
     * @return Texto original
     */
    public static String binaryToText(String binary) {
        if (binary == null || binary.isEmpty() || binary.length() % 8 != 0) {
            throw new IllegalArgumentException("Binário inválido: deve ter múltiplo de 8 bits");
        }

        int numBytes = binary.length() / 8;
        byte[] bytes = new byte[numBytes];

        for (int i = 0; i < numBytes; i++) {
            String byteBinary = binary.substring(i * 8, (i + 1) * 8);
            bytes[i] = (byte) Integer.parseInt(byteBinary, 2);
        }

        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Valida se uma string é binária válida
     * @param binary String a ser validada
     * @return true se for binária válida
     */
    public static boolean isValidBinary(String binary) {
        if (binary == null || binary.isEmpty()) {
            return false;
        }

        // Verifica se contém apenas 0s e 1s
        if (!binary.matches("[01]+")) {
            return false;
        }

        // Verifica se é múltiplo de 8
        return binary.length() % 8 == 0;
    }

    /**
     * Formata string binária em grupos de 8 bits para melhor visualização
     * @param binary String binária
     * @return String formatada com espaços
     */
    public static String formatBinary(String binary) {
        if (binary == null || binary.isEmpty()) {
            return "";
        }

        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < binary.length(); i++) {
            if (i > 0 && i % 8 == 0) {
                formatted.append(" ");
            }
            formatted.append(binary.charAt(i));
        }
        return formatted.toString();
    }

    /**
     * Retorna informações sobre a conversão
     * @param text Texto original
     * @return String com estatísticas
     */
    public static String getConversionInfo(String text) {
        if (text == null) {
            return "Texto nulo";
        }

        String binary = textToBinary(text);
        return String.format(
                "Caracteres: %d | Bytes: %d | Bits: %d",
                text.length(),
                text.getBytes(StandardCharsets.UTF_8).length,
                binary.length()
        );
    }
}
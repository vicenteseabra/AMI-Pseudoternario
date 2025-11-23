package com.telecomunicacoes.ami.codec;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Sistema de Criptografia usando XOR com chave expandida
 * Método simples mas eficaz para o projeto acadêmico
 *
 * EXPLICAÇÃO DO MÉTODO:
 * 1. XOR (Exclusive OR) é uma operação bit a bit
 * 2. A ⊕ B ⊕ B = A (propriedade de reversibilidade)
 * 3. Cada byte do texto é XOR com byte correspondente da chave
 * 4. A chave é expandida/repetida para cobrir todo o texto
 * 5. Base64 é usado para garantir caracteres imprimíveis
 */
public class Encryption {

    // Chave padrão (pode ser alterada)
    private static final String DEFAULT_KEY = "UTFPR-COMUNICACAO-DE-DADOS";
    private String key;

    public Encryption() {
        this.key = DEFAULT_KEY;
    }

    public Encryption(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Chave não pode ser vazia");
        }
        this.key = key;
    }

    /**
     * Criptografa o texto usando XOR com a chave
     * @param plainText Texto original
     * @return Texto criptografado em Base64
     */
    public String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return "";
        }

        byte[] textBytes = plainText.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] encrypted = new byte[textBytes.length];

        // XOR cada byte do texto com byte correspondente da chave (expandida)
        for (int i = 0; i < textBytes.length; i++) {
            encrypted[i] = (byte) (textBytes[i] ^ keyBytes[i % keyBytes.length]);
        }

        // Codifica em Base64 para garantir caracteres imprimíveis
        return Base64.getEncoder().encodeToString(encrypted);
    }

    /**
     * Descriptografa o texto usando XOR com a chave
     * @param encryptedText Texto criptografado em Base64
     * @return Texto original
     */
    public String decrypt(String encryptedText) {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return "";
        }

        try {
            // Decodifica de Base64
            byte[] encrypted = Base64.getDecoder().decode(encryptedText);
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            byte[] decrypted = new byte[encrypted.length];

            // XOR novamente para descriptografar (propriedade de reversibilidade)
            for (int i = 0; i < encrypted.length; i++) {
                decrypted[i] = (byte) (encrypted[i] ^ keyBytes[i % keyBytes.length]);
            }

            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Erro ao descriptografar: Base64 inválido", e);
        }
    }

    /**
     * Altera a chave de criptografia
     * @param newKey Nova chave
     */
    public void setKey(String newKey) {
        if (newKey == null || newKey.isEmpty()) {
            throw new IllegalArgumentException("Chave não pode ser vazia");
        }
        this.key = newKey;
    }

    /**
     * Retorna a chave atual (mascarada por segurança)
     * @return Chave mascarada
     */
    public String getMaskedKey() {
        if (key.length() <= 4) {
            return "****";
        }
        return key.substring(0, 2) + "****" + key.substring(key.length() - 2);
    }

    /**
     * Testa a criptografia com texto de exemplo
     * @return true se o teste passou
     */
    public static boolean selfTest() {
        try {
            Encryption enc = new Encryption();
            String original = "Teste de Criptografia com Acentos: áéíóú ãõ çÇ";
            String encrypted = enc.encrypt(original);
            String decrypted = enc.decrypt(encrypted);

            boolean success = original.equals(decrypted);

            if (!success) {
                System.err.println("FALHA no teste de criptografia!");
                System.err.println("Original:  " + original);
                System.err.println("Decrypted: " + decrypted);
            }

            return success;
        } catch (Exception e) {
            System.err.println("ERRO no teste de criptografia: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retorna explicação do método de criptografia
     * @return String explicativa
     */
    public static String getMethodExplanation() {
        return """
            MÉTODO DE CRIPTOGRAFIA: XOR com Chave Expandida
            
            Funcionamento:
            1. Operação XOR (⊕) bit a bit entre texto e chave
            2. Chave é repetida para cobrir todo o texto
            3. Propriedade: A ⊕ B ⊕ B = A (reversível)
            4. Base64 garante caracteres imprimíveis
            
            Exemplo:
            Texto:  'A' = 01000001
            Chave:  'K' = 01001011
            XOR:        = 00001010 = '\n' (não imprimível)
            Base64:     = 'Cg==' (imprimível)
            
            Segurança: Adequada para fins acadêmicos.
            Nota: Para produção, usar AES ou similar.
            """;
    }
}
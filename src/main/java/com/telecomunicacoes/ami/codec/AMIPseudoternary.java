package com.telecomunicacoes.ami.codec;

/**
 * Implementação do algoritmo AMI Pseudoternário
 *
 * REGRAS DO AMI PSEUDOTERNÁRIO:
 * - Bit 1 é codificado como tensão ZERO (0)
 * - Bit 0 é codificado alternando entre +V e -V
 *
 * Diferença do AMI tradicional:
 * - AMI tradicional: 0=0V, 1 alterna entre +V/-V
 * - AMI Pseudoternário: INVERSO - 1=0V, 0 alterna entre +V/-V
 *
 * Níveis de sinal:
 * +1 = Tensão positiva (+V)
 *  0 = Tensão zero (0V)
 * -1 = Tensão negativa (-V)
 */
public class AMIPseudoternary {

    // Estado interno para alternância de polaridade
    private int lastNonZeroLevel;

    public AMIPseudoternary() {
        this.lastNonZeroLevel = -1; // Inicia com -1 para primeiro ser +1
    }

    /**
     * Codifica string binária em sinal AMI Pseudoternário
     * @param binaryString String contendo apenas 0s e 1s
     * @return Array de inteiros representando níveis de tensão (-1, 0, +1)
     */
    public int[] encode(String binaryString) {
        if (binaryString == null || binaryString.isEmpty()) {
            return new int[0];
        }

        // Valida entrada
        if (!binaryString.matches("[01]+")) {
            throw new IllegalArgumentException("String deve conter apenas 0s e 1s");
        }

        int[] signal = new int[binaryString.length()];
        lastNonZeroLevel = -1; // Reset do estado

        for (int i = 0; i < binaryString.length(); i++) {
            char bit = binaryString.charAt(i);

            if (bit == '1') {
                // Bit 1 = tensão zero
                signal[i] = 0;

            } else { // bit == '0'
                // Bit 0 = alterna entre +V e -V
                lastNonZeroLevel = -lastNonZeroLevel;
                signal[i] = lastNonZeroLevel;
            }
        }

        return signal;
    }

    /**
     * Decodifica sinal AMI Pseudoternário de volta para binário
     * @param signal Array de níveis de tensão (-1, 0, +1)
     * @return String binária original
     */
    public String decode(int[] signal) {
        if (signal == null || signal.length == 0) {
            return "";
        }

        StringBuilder binary = new StringBuilder();

        for (int level : signal) {
            if (level == 0) {
                // Tensão zero = bit 1
                binary.append('1');

            } else if (level == 1 || level == -1) {
                // Tensão +V ou -V = bit 0
                binary.append('0');

            } else {
                throw new IllegalArgumentException(
                        "Sinal inválido: nível " + level + " não é -1, 0 ou +1"
                );
            }
        }

        return binary.toString();
    }

    /**
     * Valida se um sinal segue as regras AMI Pseudoternário
     * @param signal Array de níveis de tensão
     * @return true se válido
     */
    public boolean validateSignal(int[] signal) {
        if (signal == null || signal.length == 0) {
            return false;
        }

        int lastPolarity = 0;

        for (int level : signal) {
            // Verifica se está nos níveis permitidos
            if (level != -1 && level != 0 && level != 1) {
                return false;
            }

            // Verifica alternância de polaridade para bits 0
            if (level != 0) {
                if (lastPolarity != 0 && level == lastPolarity) {
                    // Dois níveis não-zero consecutivos devem ter polaridades opostas
                    return false;
                }
                lastPolarity = level;
            }
        }

        return true;
    }

    /**
     * Converte sinal para string legível
     * @param signal Array de níveis
     * @return String formatada
     */
    public static String signalToString(int[] signal) {
        if (signal == null || signal.length == 0) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < signal.length; i++) {
            if (i > 0) sb.append(", ");

            switch (signal[i]) {
                case 1:  sb.append("+V"); break;
                case 0:  sb.append(" 0"); break;
                case -1: sb.append("-V"); break;
                default: sb.append(" ?"); break;
            }
        }
        sb.append("]");

        return sb.toString();
    }

    /**
     * Calcula estatísticas do sinal
     * @param signal Array de níveis
     * @return String com estatísticas
     */
    public static String getSignalStatistics(int[] signal) {
        if (signal == null || signal.length == 0) {
            return "Sinal vazio";
        }

        int countPositive = 0;
        int countZero = 0;
        int countNegative = 0;

        for (int level : signal) {
            if (level > 0) countPositive++;
            else if (level == 0) countZero++;
            else countNegative++;
        }

        return String.format(
                "Total: %d | +V: %d (%.1f%%) | 0V: %d (%.1f%%) | -V: %d (%.1f%%)",
                signal.length,
                countPositive, (countPositive * 100.0 / signal.length),
                countZero, (countZero * 100.0 / signal.length),
                countNegative, (countNegative * 100.0 / signal.length)
        );
    }

    /**
     * Teste de auto-verificação do algoritmo
     * @return true se todos os testes passaram
     */
    public static boolean selfTest() {
        AMIPseudoternary ami = new AMIPseudoternary();

        // Teste 1: Sequência simples
        String test1 = "10110001";
        int[] encoded1 = ami.encode(test1);
        String decoded1 = ami.decode(encoded1);

        if (!test1.equals(decoded1)) {
            System.err.println("FALHA Teste 1: " + test1 + " != " + decoded1);
            return false;
        }

        // Teste 2: Todos 1s
        String test2 = "11111111";
        int[] encoded2 = ami.encode(test2);
        String decoded2 = ami.decode(encoded2);

        if (!test2.equals(decoded2)) {
            System.err.println("FALHA Teste 2: " + test2 + " != " + decoded2);
            return false;
        }

        // Teste 3: Todos 0s (deve alternar)
        String test3 = "00000000";
        int[] encoded3 = ami.encode(test3);
        String decoded3 = ami.decode(encoded3);

        if (!test3.equals(decoded3)) {
            System.err.println("FALHA Teste 3: " + test3 + " != " + decoded3);
            return false;
        }

        // Verifica alternância em teste 3
        for (int i = 1; i < encoded3.length; i++) {
            if (encoded3[i] == encoded3[i-1]) {
                System.err.println("FALHA: Não houve alternância em sequência de 0s");
                return false;
            }
        }

        System.out.println("✓ Todos os testes AMI Pseudoternário passaram!");
        return true;
    }

    /**
     * Retorna explicação do algoritmo
     * @return String explicativa
     */
    public static String getAlgorithmExplanation() {
        return """
            ALGORITMO: AMI Pseudoternário (Alternate Mark Inversion - Pseudo-ternary)
            
            Características:
            • Usa 3 níveis de tensão: +V, 0V, -V
            • Bit 1 → 0V (tensão zero)
            • Bit 0 → Alterna entre +V e -V
            
            Vantagens:
            ✓ Elimina componente DC (tensões positivas e negativas se equilibram)
            ✓ Facilita sincronização
            ✓ Detecta erros de transmissão (violações de alternância)
            ✓ Melhor uso de banda que NRZ
            
            Exemplo:
            Binário:  1  0  1  1  0  0  0  1
            Sinal:    0 +V  0  0 -V +V -V  0
            
            Observação: É o INVERSO do AMI tradicional
            """;
    }
}
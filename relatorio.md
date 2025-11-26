# Documentação Completa do Projeto AMI Pseudoternário

## 1. Visão Geral do Projeto

Este projeto implementa um **codificador e decodificador AMI (Alternate Mark Inversion) Pseudoternário**, uma técnica fundamental em telecomunicações para transmissão de sinais digitais em linhas de comunicação.

---

## 2. Estrutura e Configuração do Projeto

### 2.1 Identificação Maven (`pom.xml`)

```
Grupo: com.telecomunicacoes
Artefato: ami-pseudoternario
Versão: 1.0-SNAPSHOT
```

### 2.2 Tecnologias Utilizadas

| Tecnologia | Versão | Propósito |
|------------|--------|-----------|
| Java | 17 | Linguagem de programação |
| JavaFX | 21 | Interface gráfica do usuário |
| Gson | 2.10.1 | Manipulação de dados JSON |
| Maven | 4.0.0 | Gerenciamento de dependências |

### 2.3 Classe Principal

**Classe de entrada:** `com.telecomunicacoes.ami.Main`

---

## 3. Algoritmo AMI Pseudoternário - Explicação Detalhada

### 3.1 Conceito Fundamental

O **AMI (Alternate Mark Inversion)** é uma técnica de codificação de linha que transforma sinais binários (níveis lógicos 0 e 1) em **três níveis de tensão**:

- **+V** (tensão positiva)
- **0** (tensão zero/nula)
- **-V** (tensão negativa)

### 3.2 Regras de Codificação

#### Regra 1: Codificação do Bit 0
- Todo bit **0** é sempre representado por **tensão zero** (0V)
- Não há alternância para zeros

#### Regra 2: Codificação do Bit 1
- Os bits **1** alternam entre **+V** e **-V**
- A alternância ocorre sequencialmente
- Cada bit 1 deve ter polaridade oposta ao bit 1 anterior

### 3.3 Processo de Codificação Passo a Passo

**Estado inicial:** Próximo bit 1 será +V

```
Bit de entrada: 1
Ação: Codifica como +V
Próximo estado: Próximo bit 1 será -V

Bit de entrada: 0
Ação: Codifica como 0
Estado: Mantém (próximo bit 1 continua sendo -V)

Bit de entrada: 1
Ação: Codifica como -V
Próximo estado: Próximo bit 1 será +V
```

### 3.4 Exemplo Completo de Codificação

```
Sequência binária:    1    0    1    1    0    1    0    0    1    1
                      ↓    ↓    ↓    ↓    ↓    ↓    ↓    ↓    ↓    ↓
Sinal AMI:           +V    0   -V   +V    0   -V    0    0   +V   -V
```

**Análise:**
- 1º bit (1): +V (primeiro 1)
- 2º bit (0): 0 (sempre zero)
- 3º bit (1): -V (alterna de +V)
- 4º bit (1): +V (alterna de -V)
- 5º bit (0): 0 (sempre zero)
- 6º bit (1): -V (alterna de +V)
- Bits 7-8 (0,0): 0, 0 (sempre zero)
- 9º bit (1): +V (alterna de -V)
- 10º bit (1): -V (alterna de +V)

### 3.5 Algoritmo de Decodificação

**Regra de decodificação:**
- **0V** → bit 0
- **+V ou -V** → bit 1

```
Sinal AMI:        +V    0   -V   +V    0   -V
                   ↓    ↓    ↓    ↓    ↓    ↓
Binário:           1    0    1    1    0    1
```

---

## 4. Propriedades e Características

### 4.1 Vantagens do AMI

1. **Eliminação de Componente DC**
    - Tensão média = 0V devido à alternância +V/-V
    - Reduz interferência em circuitos acoplados por AC

2. **Detecção de Erros**
    - Violação da regra de alternância indica erro
    - Exemplo: +V → +V (sem -V no meio) = erro detectado

3. **Sincronização**
    - Transições de sinal facilitam recuperação do relógio
    - Receptor pode sincronizar timing facilmente

4. **Eficiência Espectral**
    - Energia concentrada em frequências baixas
    - Melhor aproveitamento da largura de banda

5. **Imunidade a Ruído**
    - Três níveis oferecem melhor margem de ruído
    - Facilita detecção de thresholds

### 4.2 Desvantagens e Limitações

1. **Longas Sequências de Zeros**
    - Problema: "00000000" → perda de sincronização
    - Solução: B8ZS, HDB3, ou técnicas de scrambling

2. **Complexidade do Receptor**
    - Requer detecção de três níveis (não apenas dois)
    - Circuitos mais sofisticados

3. **Maior Largura de Banda**
    - Requer aproximadamente 2× a largura de banda do NRZ

---

## 5. Aplicações Práticas

### 5.1 Telecomunicações Digitais

- **Linhas T1** (América do Norte): 1.544 Mbps
- **Linhas E1** (Europa): 2.048 Mbps
- **ISDN** (Integrated Services Digital Network)

### 5.2 Transmissão de Dados

- Redes de longa distância
- Sistemas de comunicação ponto-a-ponto
- Barramentos de dados industriais

---

## 6. Estrutura Esperada da Implementação

### 6.1 Camada de Interface (JavaFX)

**Componentes esperados:**
- Campo de entrada para sequência binária
- Visualização gráfica do sinal AMI
- Botões: Codificar, Decodificar, Limpar
- Display de resultado

### 6.2 Camada Lógica

**Classes principais:**

```java
AMIEncoder
- encode(String binary): int[]
- validateInput(String binary): boolean

AMIDecoder
- decode(int[] amiSignal): String
- detectViolations(int[] amiSignal): boolean

AMISignal
- levels: int[] // +1, 0, -1
- toBinary(): String
- toWaveform(): GraphData
```

### 6.3 Camada de Persistência (Gson)

**Funcionalidades:**
- Salvar configurações em JSON
- Exportar resultados de codificação
- Importar sequências de teste

---

## 7. Como Executar o Projeto

### 7.1 Compilar e Executar

```bash
mvn clean javafx:run
```

### 7.2 Gerar Artefato

```bash
mvn clean package
```

---

## 8. Exemplo de Uso Completo

**Entrada:**
```
Binário: 11010110
```

**Processo:**
```
Posição:  0  1  2  3  4  5  6  7
Bit:      1  1  0  1  0  1  1  0
AMI:     +1 -1  0 +1  0 -1 +1  0
```

**Saída:**
```
Sinal AMI: [+1, -1, 0, +1, 0, -1, +1, 0]
```

---

## 9. Referências e Padrões

- **ITU-T G.703**: Características físicas e elétricas
- **ANSI T1.403**: Padrão de interface de rede T1
- **IEEE 802.3**: Variantes para Ethernet

---

## 10. Conclusão

O algoritmo AMI Pseudoternário é uma técnica essencial em telecomunicações que oferece **balance DC**, **detecção de erros** e **sincronização** eficiente. Este projeto fornece uma implementação educacional e funcional desta técnica fundamental.
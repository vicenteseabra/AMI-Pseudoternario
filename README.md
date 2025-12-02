# Sistema de Comunicação com Codificação AMI Pseudoternário

Este projeto é uma aplicação desktop desenvolvida em Java com JavaFX que simula um sistema de comunicação de dados. Ele demonstra o processo completo de transmissão e recepção de uma mensagem, aplicando criptografia, conversão para binário e, principalmente, a codificação de linha **AMI Pseudoternário**.

## Características Principais

*   **Interface Gráfica Intuitiva:** Construída com JavaFX, a interface é dividida em painéis de Transmissão (Host A) e Recepção (Host B), facilitando a visualização de cada etapa do processo.
*   **Simulação Completa:** Demonstra o fluxo de ponta a ponta:
    1.  Criação da mensagem.
    2.  Criptografia.
    3.  Conversão para binário.
    4.  Codificação de linha.
    5.  Transmissão em rede.
    6.  Decodificação.
    7.  Descriptografia.
*   **Codificação AMI Pseudoternário:** Implementa o algoritmo de codificação onde o bit `1` é representado por 0V e o bit `0` é representado por níveis de tensão positivo e negativo (+V e -V) alternadamente.
*   **Visualização de Sinal:** Gera e exibe um gráfico da forma de onda do sinal codificado tanto na transmissão quanto na recepção.
*   **Comunicação em Rede:** Utiliza um modelo cliente-servidor simples para simular o envio dos dados através de uma rede local (localhost).
*   **Log de Eventos:** Um painel de log detalha cada ação realizada pela aplicação, desde a criptografia até a recepção da mensagem, auxiliando no entendimento do processo.

## Como Funciona

A aplicação simula a comunicação entre dois hosts (A e B) na mesma máquina.

### Painel de Transmissão (Host A)

1.  **Entrada de Dados:** O usuário pode inserir uma mensagem de texto no campo "Mensagem Original" ou uma sequência binária diretamente no campo "Representação Binária".
2.  **Processamento:** Ao clicar em "Processar Mensagem", a aplicação executa as seguintes etapas:
    *   **Criptografia:** A mensagem de texto é criptografada usando um algoritmo XOR simples.
    *   **Conversão Binária:** O texto criptografado é convertido para sua representação em bits (0s e 1s).
    *   **Codificação AMI:** A sequência binária é codificada para o sinal AMI Pseudoternário.
    *   **Visualização:** A forma de onda do sinal resultante é desenhada no gráfico.
3.  **Envio:**
    *   Primeiro, o servidor deve ser iniciado no painel de controle ("▶ Iniciar Servidor").
    *   Ao clicar em "📤 Enviar Mensagem", o sinal codificado é encapsulado e enviado para o servidor local.

### Painel de Recepção (Host B)

1.  **Recepção de Dados:** O servidor, que está escutando na porta configurada, recebe a mensagem do cliente.
2.  **Processamento Reverso:** Automaticamente, a aplicação inicia o processo de decodificação no painel de recepção:
    *   **Exibição do Sinal:** O sinal AMI recebido e sua respectiva forma de onda são exibidos.
    *   **Decodificação AMI:** O sinal é decodificado de volta para sua sequência binária original.
    *   **Conversão para Texto:** A sequência binária é convertida para texto (que ainda está criptografado).
    *   **Descriptografia:** O texto é descriptografado para revelar a mensagem original enviada pelo Host A.

## Como Executar o Projeto

Este é um projeto Maven. Para executá-lo, você precisará do JDK (Java Development Kit) e do Maven instalados em seu sistema.

1.  Clone o repositório para sua máquina local.
2.  Abra o projeto em sua IDE de preferência (como IntelliJ IDEA ou Eclipse).
3.  A IDE deve reconhecer o `pom.xml` e baixar as dependências necessárias (como o JavaFX).
4.  Localize e execute a classe principal que contém o método `main`.


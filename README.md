# 🚀 AMI Pseudoternário - Guia de Uso

## 📋 Índice

1. [Como Executar](#como-executar)
2. [Configurar Firewall](#configurar-firewall)
3. [Usar a Aplicação](#usar-a-aplicação)
4. [Solução de Problemas](#solução-de-problemas)

---

## 🎯 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.6 ou superior

### Executar a aplicação

```bash
mvn javafx:run
```

Ou compile e execute o JAR:

```bash
mvn clean package
java -jar target/ami-pseudoternario-1.0-SNAPSHOT.jar
```

---

## 🔥 Configurar Firewall

### ⚡ Método Rápido (Recomendado)

**Execute o script automatizado:**

1. Clique com botão direito em `configurar-firewall.ps1`
2. Selecione **"Executar com PowerShell"**
3. Clique em **"Sim"** quando pedir permissão de administrador
4. Pronto! ✅

### 📝 Método Manual

#### Windows:

**Abra PowerShell como Administrador e execute:**

```powershell
# Criar regras de firewall
New-NetFirewallRule -DisplayName "AMI-5555-IN" -Direction Inbound -Protocol TCP -LocalPort 5555 -Action Allow -Profile Any
New-NetFirewallRule -DisplayName "AMI-5555-OUT" -Direction Outbound -Protocol TCP -LocalPort 5555 -Action Allow -Profile Any
```

#### Linux/Ubuntu:

**Execute no terminal:**

```bash
# Liberar porta 5555
sudo ufw allow 5555/tcp

# Verificar
sudo ufw status

# Descobrir seu IP
hostname -I
```

**Ou use o script automático:**
```bash
chmod +x configurar-firewall-ubuntu.sh
sudo bash configurar-firewall-ubuntu.sh
```

#### Ou via Interface Gráfica (Windows):

1. Abra: `wf.msc` (Windows + R)
2. Regras de Entrada → Nova Regra
3. Porta TCP: 5555
4. Permitir a conexão
5. Aplicar a todos os perfis
6. Nomear: "AMI Pseudoternario"

---

## 🌐 Problema com Hotspot do Celular?

### Por que não funciona no hotspot?

O Windows trata o hotspot como **Rede Pública** e bloqueia conexões por segurança.

### ✅ Solução:

**Opção 1 - Mudar para Rede Privada (MAIS SIMPLES):**

1. Conecte ao hotspot do celular
2. Abra: **Configurações** → **Rede e Internet** → **WiFi**
3. Clique no nome do hotspot
4. Em **"Perfil de rede"**, selecione: **"Privado"**
5. Pronto! ✅

**Opção 2 - Liberar na Rede Pública:**

Execute o script `configurar-firewall.ps1` (ele já configura para todas as redes)

---

## 🎮 Usar a Aplicação

### 1️⃣ Testar Localmente (Mesma Máquina)

```
┌─────────────────────────────────────────────┐
│  PASSO 1: Iniciar Servidor                  │
│  ➤ Clique em "▶ Iniciar Servidor"          │
│  ✓ Aguarde: "Servidor iniciado..."         │
└─────────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────────┐
│  PASSO 2: Processar Mensagem                │
│  ➤ Digite uma mensagem                      │
│  ➤ Clique em "⚙ Processar Mensagem"        │
│  ✓ Forma de onda será gerada               │
└─────────────────────────────────────────────┘
            ↓
┌─────────────────────────────────────────────┐
│  PASSO 3: Enviar                            │
│  ➤ Clique em "📤 Enviar Mensagem"          │
│  ✓ Mensagem aparece no painel de recepção  │
└─────────────────────────────────────────────┘
```

### 2️⃣ Enviar Entre Dispositivos

#### Cenário 1: Windows → Windows

**No Computador RECEPTOR (Windows):**

1. Inicie o servidor
2. Descubra seu IP:
   ```powershell
   ipconfig
   ```
   Anote o IP (ex: `192.168.43.123`)

**No Computador EMISSOR (Windows):**

1. No campo **"Servidor Destino"**, digite o IP do receptor
2. Porta: `5555`
3. Processe sua mensagem
4. Envie! 📤

#### Cenário 2: Windows → Ubuntu Linux

**No Ubuntu RECEPTOR:**

1. Libere o firewall:
   ```bash
   sudo ufw allow 5555/tcp
   ```

2. Descubra seu IP:
   ```bash
   hostname -I
   ```
   Anote o IP (ex: `192.168.1.100`)

3. Execute a aplicação:
   ```bash
   mvn javafx:run
   ```

4. Inicie o servidor na interface

**No Windows EMISSOR:**

1. Configure:
   - **Servidor Destino:** `192.168.1.100` (IP do Ubuntu)
   - **Porta:** `5555`
2. Processe e envie!

#### Cenário 3: Ubuntu → Windows

**No Windows RECEPTOR:**

1. Configure firewall (execute `configurar-firewall.ps1`)
2. Descubra seu IP: `ipconfig`
3. Inicie servidor no aplicativo

**No Ubuntu EMISSOR:**

1. Configure servidor destino com IP do Windows
2. Porta: `5555`
3. Envie!
---

## 🔧 Comandos Úteis

### Verificar se o Java está instalado:
```bash
java -version
```

### Verificar se o Maven está instalado:
```bash
mvn -version
```

### Limpar e recompilar:
```bash
mvn clean compile
```

### Ver logs detalhados:
```bash
mvn javafx:run -X
```

### Comandos específicos por sistema:

#### Windows:
```powershell
# Testar conectividade
Test-NetConnection -ComputerName localhost -Port 5555

# Ver conexões ativas
netstat -ano | findstr :5555

# Descobrir IP
ipconfig
```

#### Linux/Ubuntu:
```bash
# Testar se porta está aberta
sudo ss -tlnp | grep :5555

# Ver conexões ativas
sudo ss -tnp | grep :5555

# Descobrir IP
hostname -I

# Testar conectividade
telnet localhost 5555

# Monitorar conexões em tempo real
watch -n 1 'sudo ss -tnp | grep :5555'
```



---

## 📚 Documentação Adicional

### Geral:
- **Algoritmo AMI:** `relatorio.md`
- **Guia de Uso:** `README.md` (este arquivo)
- **Solução de Erros:** `SOLUCAO_ERRO_CONEXAO.md`

### Windows:
- **Configurar Firewall Windows:** `CONFIGURAR_FIREWALL.md`
- **Script Automático:** `configurar-firewall.ps1`
- **Remover Regras:** `remover-regras-firewall.ps1`

### Linux/Ubuntu:
- **Configurar Firewall Ubuntu:** `CONFIGURAR_FIREWALL_UBUNTU.md`
- **Script Automático:** `configurar-firewall-ubuntu.sh`
- **Remover Regras:** `remover-regras-ubuntu.sh`

---

## 🎓 Exemplo de Uso Completo

```
1. Executar: mvn javafx:run
2. Clicar em: "▶ Iniciar Servidor"
3. Digitar: "Ola Mundo"
4. Clicar em: "⚙ Processar Mensagem"
5. Ver a forma de onda sendo gerada
6. Clicar em: "📤 Enviar Mensagem"
7. Ver a mensagem sendo recebida e decodificada
```

**Resultado esperado:**
```
[12:30:45] ✓ Servidor iniciado na porta 5555
[12:30:50] ▶ Processando transmissão...
[12:30:50]   1. Criptografia aplicada
[12:30:50]   2. Convertido para binário (256 bits)
[12:30:50]   3. Codificado em AMI Pseudoternário
[12:30:50]   4. Forma de onda gerada
[12:30:50] ✓ Processamento concluído!
[12:30:52] 📤 Enviando mensagem...
[12:30:52] ✓ Mensagem enviada com sucesso!
[12:30:52] ▶ Processando recepção...
[12:30:52] ✓ Mensagem recebida: "Ola Mundo"
```

---

## 🆘 Precisa de Ajuda?

1. Verifique os logs na parte inferior da interface
2. Consulte `SOLUCAO_ERRO_CONEXAO.md` para erros comuns
3. Execute `configurar-firewall.ps1` se houver problemas de conexão
4. Verifique se ambos os dispositivos estão na mesma rede

---

## 📄 Licença

Este projeto foi desenvolvido para fins educacionais.
UTFPR - Universidade Tecnológica Federal do Paraná
Disciplina: Comunicação de Dados


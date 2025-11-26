# 🐧 Configurar Firewall no Ubuntu/Linux

## 📋 Visão Geral

Para receber mensagens do projeto AMI no Ubuntu, você precisa liberar a **porta 5555** no firewall.

---

## 🎯 Método 1: Script Automático (RECOMENDADO)

### Passo a Passo:

1. **Transfira o script para o Ubuntu:**

   ```bash
   # Se os dois computadores estão na mesma rede:
   # No Windows, compartilhe a pasta ou use pendrive
   
   # Ou copie o conteúdo e crie o arquivo manualmente:
   nano configurar-firewall-ubuntu.sh
   ```

2. **Dê permissão de execução:**

   ```bash
   chmod +x configurar-firewall-ubuntu.sh
   ```

3. **Execute como root:**

   ```bash
   sudo bash configurar-firewall-ubuntu.sh
   ```

4. **Pronto!** ✅ A porta 5555 está liberada.

---

## 🛠️ Método 2: Comandos Manuais

### Para Ubuntu com UFW (mais comum):

```bash
# 1. Habilitar UFW (se ainda não estiver)
sudo ufw enable

# 2. Liberar porta 5555
sudo ufw allow 5555/tcp comment 'AMI Pseudoternario'

# 3. Verificar regras
sudo ufw status numbered

# 4. Recarregar
sudo ufw reload
```

**Saída esperada:**
```
Status: active

To                         Action      From
--                         ------      ----
5555/tcp                   ALLOW       Anywhere                  # AMI Pseudoternario
5555/tcp (v6)              ALLOW       Anywhere (v6)             # AMI Pseudoternario
```

---

### Para Fedora/RHEL com Firewalld:

```bash
# 1. Iniciar Firewalld
sudo systemctl start firewalld
sudo systemctl enable firewalld

# 2. Liberar porta 5555
sudo firewall-cmd --permanent --add-port=5555/tcp

# 3. Recarregar
sudo firewall-cmd --reload

# 4. Verificar
sudo firewall-cmd --list-ports
```

**Saída esperada:**
```
5555/tcp
```

---

## 🔍 Verificar se a Porta Está Aberta

### Método 1: Verificar Firewall

```bash
# UFW
sudo ufw status | grep 5555

# Firewalld
sudo firewall-cmd --list-ports | grep 5555
```

### Método 2: Verificar Conexões

```bash
# Ver se algo está escutando na porta 5555
sudo netstat -tlnp | grep :5555

# Ou com ss (mais moderno)
sudo ss -tlnp | grep :5555
```

**Se estiver funcionando, verá:**
```
tcp   LISTEN   0   50   0.0.0.0:5555   0.0.0.0:*   users:(("java",pid=12345,fd=10))
```

### Método 3: Testar de Outro Computador

**No Windows (emissor), teste a conexão:**

```powershell
Test-NetConnection -ComputerName 192.168.1.100 -Port 5555
```

Substitua `192.168.1.100` pelo IP do Ubuntu.

**Resultado esperado:**
```
TcpTestSucceeded : True
```

---

## 🌐 Descobrir o IP do Ubuntu

### Método 1: Comando ip

```bash
ip addr show | grep "inet " | grep -v 127.0.0.1
```

### Método 2: Comando hostname

```bash
hostname -I
```

### Método 3: Interface gráfica

```bash
# Ver todas as interfaces e IPs
ifconfig
```

**Exemplo de saída:**
```
192.168.1.100    ← Use este IP no Windows
```

---

## 🎮 Fluxo Completo de Uso

### No Ubuntu (Receptor):

```bash
# 1. Liberar firewall
sudo ufw allow 5555/tcp

# 2. Descobrir IP
hostname -I

# 3. Executar aplicação
cd /caminho/para/AMI-Pseudoternario
mvn javafx:run

# 4. Iniciar servidor na interface
#    (clicar no botão "▶ Iniciar Servidor")
```

### No Windows (Emissor):

```
1. Configurar servidor destino: 192.168.1.100 (IP do Ubuntu)
2. Porta: 5555
3. Processar mensagem
4. Enviar! 📤
```

---

## 🔐 Segurança: Limitar Acesso por IP

Se quiser permitir apenas do Windows específico:

### UFW:

```bash
# Permitir apenas do IP 192.168.1.50 (Windows)
sudo ufw allow from 192.168.1.50 to any port 5555 proto tcp

# Bloquear acesso geral
sudo ufw deny 5555/tcp
```

### Firewalld:

```bash
# Criar regra específica
sudo firewall-cmd --permanent --add-rich-rule='
  rule family="ipv4" 
  source address="192.168.1.50" 
  port protocol="tcp" port="5555" 
  accept'

sudo firewall-cmd --reload
```

---

## 🐛 Solução de Problemas

### ❌ "Connection refused" ao testar do Windows

**Causa:** Servidor não está rodando no Ubuntu

**Solução:**
```bash
# Verificar se o Java está rodando
ps aux | grep java

# Verificar se está escutando na porta
sudo ss -tlnp | grep :5555
```

---

### ❌ "No route to host"

**Causa:** Firewall bloqueando ou rede diferente

**Solução:**
```bash
# 1. Verificar firewall
sudo ufw status

# 2. Temporariamente desativar para teste
sudo ufw disable

# 3. Testar conexão
# (se funcionar, o problema é firewall)

# 4. Reativar e configurar corretamente
sudo ufw enable
sudo ufw allow 5555/tcp
```

---

### ❌ "Connection timed out"

**Causa:** Firewall ou roteador bloqueando

**Solução:**
```bash
# 1. Verificar se porta está aberta
sudo ufw status | grep 5555

# 2. Verificar se servidor está escutando em todas as interfaces
sudo ss -tlnp | grep :5555
# Deve mostrar: 0.0.0.0:5555 (não 127.0.0.1:5555)

# 3. Testar localmente primeiro
telnet localhost 5555
```

---

### ❌ Servidor só aceita conexões locais (127.0.0.1)

**Causa:** Java bind apenas no localhost

**Solução:** Verificar código do servidor

No arquivo `Server.java`, certifique-se que está usando:
```java
ServerSocket serverSocket = new ServerSocket(5555, 50, null);
// null = aceita de qualquer interface
```

Ou especificamente:
```java
ServerSocket serverSocket = new ServerSocket(5555, 50, InetAddress.getByName("0.0.0.0"));
```

---

## 📊 Comandos Úteis para Diagnóstico

### Ver todas as portas abertas:

```bash
sudo netstat -tlnp
```

### Ver processos usando rede:

```bash
sudo lsof -i -P -n | grep LISTEN
```

### Testar porta localmente:

```bash
# Instalar telnet se necessário
sudo apt install telnet

# Testar conexão
telnet localhost 5555
```

### Ver logs do firewall:

```bash
# UFW
sudo less /var/log/ufw.log

# Firewalld
sudo journalctl -u firewalld
```

### Ping entre máquinas:

```bash
# Do Ubuntu para Windows
ping 192.168.1.50

# Do Windows para Ubuntu
ping 192.168.1.100
```

---

## 🗑️ Remover Regras

### UFW:

```bash
# Listar regras numeradas
sudo ufw status numbered

# Remover por número
sudo ufw delete [número]

# Ou remover por especificação
sudo ufw delete allow 5555/tcp
```

### Firewalld:

```bash
# Remover porta
sudo firewall-cmd --permanent --remove-port=5555/tcp
sudo firewall-cmd --reload
```

---

## 🔄 Configuração Completa Passo a Passo

### 1️⃣ Preparar o Ubuntu:

```bash
# Atualizar sistema
sudo apt update && sudo apt upgrade -y

# Instalar dependências
sudo apt install -y openjdk-17-jdk maven ufw

# Clonar ou copiar projeto
cd ~
# [copie o projeto aqui]

# Entrar na pasta
cd AMI-Pseudoternario
```

### 2️⃣ Configurar Firewall:

```bash
# Habilitar UFW
sudo ufw enable

# Liberar porta 5555
sudo ufw allow 5555/tcp

# Verificar
sudo ufw status
```

### 3️⃣ Executar Aplicação:

```bash
# Compilar
mvn clean compile

# Executar
mvn javafx:run
```

### 4️⃣ Descobrir IP:

```bash
# Ver IP
hostname -I
# Exemplo: 192.168.1.100
```

### 5️⃣ No Windows:

```
Servidor Destino: 192.168.1.100
Porta: 5555
```

### 6️⃣ Testar:

```bash
# No Ubuntu, verificar conexões
watch -n 1 'sudo ss -tnp | grep :5555'
```

---

## 🎓 Exemplo Completo de Sessão

```bash
# Ubuntu - Terminal 1
$ sudo ufw allow 5555/tcp
Rule added
Rule added (v6)

$ hostname -I
192.168.1.100

$ cd AMI-Pseudoternario
$ mvn javafx:run
[INFO] Building ami-pseudoternario 1.0-SNAPSHOT
[INFO] --- javafx-maven-plugin:0.0.8:run (default-cli) @ ami-pseudoternario ---
[INFO] Changes detected - recompiling the module!
# Aplicação iniciada
# Clicar em "▶ Iniciar Servidor"

# Ubuntu - Terminal 2 (monitorar)
$ watch -n 1 'sudo ss -tnp | grep :5555'
LISTEN  0  50  0.0.0.0:5555  0.0.0.0:*  users:(("java",pid=12345))

# Quando Windows enviar:
ESTAB   0  0  192.168.1.100:5555  192.168.1.50:54321  users:(("java",pid=12345))
```

---

## ✅ Checklist Final

Antes de testar, verifique no Ubuntu:

- [ ] UFW instalado e habilitado: `sudo ufw status`
- [ ] Porta 5555 liberada: `sudo ufw status | grep 5555`
- [ ] Aplicação rodando: `ps aux | grep java`
- [ ] Servidor escutando: `sudo ss -tlnp | grep :5555`
- [ ] IP anotado: `hostname -I`
- [ ] Firewall do Windows também configurado
- [ ] Ambos na mesma rede

**Se tudo estiver ✅, vai funcionar!**

---

## 📚 Recursos Adicionais

### Documentação UFW:
```bash
man ufw
ufw --help
```

### Testar conectividade:
```bash
# Instalar nmap
sudo apt install nmap

# Escanear porta
nmap -p 5555 192.168.1.100
```

### Logs em tempo real:
```bash
# UFW
sudo tail -f /var/log/ufw.log

# Syslog
sudo tail -f /var/log/syslog | grep -i port
```

---

## 🆘 Última Opção: Desativar Firewall (TESTE)

⚠️ **APENAS PARA TESTAR! Não deixe desativado!**

```bash
# Desativar
sudo ufw disable

# Testar aplicação
# ...

# Reativar IMEDIATAMENTE
sudo ufw enable
sudo ufw allow 5555/tcp
```

---

## 📞 Resumo de 3 Comandos

```bash
# 1. Liberar porta
sudo ufw allow 5555/tcp

# 2. Ver IP
hostname -I

# 3. Executar
mvn javafx:run
```

**Pronto! Ubuntu configurado para receber mensagens! 🎉**


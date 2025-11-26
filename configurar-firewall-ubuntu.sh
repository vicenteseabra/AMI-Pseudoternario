# Configurar Firewall no Ubuntu para AMI Pseudoternário

## 🐧 Configuração do Receptor Linux (Ubuntu)

Este guia mostra como liberar a porta 5555 no Ubuntu para receber mensagens do projeto AMI.

---

## 🎯 Método Rápido (Recomendado)

### Script Automático

Salve este conteúdo como `configurar-firewall-ubuntu.sh`:

```bash
#!/bin/bash

echo "========================================"
echo "  Configurador de Firewall - Ubuntu    "
echo "  Projeto AMI Pseudoternário           "
echo "========================================"
echo ""

# Verifica se está executando como root
if [ "$EUID" -ne 0 ]; then
    echo "❌ ERRO: Este script precisa ser executado como root/sudo"
    echo ""
    echo "Execute:"
    echo "  sudo bash configurar-firewall-ubuntu.sh"
    echo ""
    exit 1
fi

echo "📋 Verificando firewall instalado..."

# Verifica se UFW está instalado
if command -v ufw &> /dev/null; then
    echo "✓ UFW encontrado"
    FIREWALL="ufw"
elif command -v firewall-cmd &> /dev/null; then
    echo "✓ Firewalld encontrado"
    FIREWALL="firewalld"
else
    echo "⚠ Nenhum firewall detectado (UFW ou Firewalld)"
    echo "Instalando UFW..."
    apt-get update && apt-get install -y ufw
    FIREWALL="ufw"
fi

echo ""
echo "🔧 Configurando firewall ($FIREWALL)..."
echo ""

if [ "$FIREWALL" == "ufw" ]; then
    # Configuração UFW
    echo "[1/4] Habilitando UFW..."
    ufw --force enable

    echo "[2/4] Permitindo porta 5555/tcp..."
    ufw allow 5555/tcp comment 'AMI Pseudoternario'

    echo "[3/4] Verificando status..."
    ufw status numbered | grep 5555

    echo "[4/4] Recarregando regras..."
    ufw reload

else
    # Configuração Firewalld
    echo "[1/4] Habilitando Firewalld..."
    systemctl start firewalld
    systemctl enable firewalld

    echo "[2/4] Permitindo porta 5555/tcp..."
    firewall-cmd --permanent --add-port=5555/tcp

    echo "[3/4] Recarregando regras..."
    firewall-cmd --reload

    echo "[4/4] Verificando status..."
    firewall-cmd --list-ports | grep 5555
fi

echo ""
echo "========================================"
echo "  ✅ SUCESSO! Porta 5555 liberada!     "
echo "========================================"
echo ""

echo "📊 Informações de Rede:"
echo ""

# Mostra IPs disponíveis
echo "Seus endereços IP:"
ip -4 addr show | grep -oP '(?<=inet\s)\d+(\.\d+){3}' | grep -v '127.0.0.1' | while read ip; do
    echo "  → $ip"
done

echo ""
echo "📝 Próximos Passos:"
echo ""
echo "1. Execute o aplicativo AMI no Ubuntu:"
echo "   mvn javafx:run"
echo ""
echo "2. Clique em 'Iniciar Servidor'"
echo ""
echo "3. No Windows (emissor), configure:"
echo "   Servidor Destino: [um dos IPs acima]"
echo "   Porta: 5555"
echo ""
echo "4. Processe e envie a mensagem!"
echo ""


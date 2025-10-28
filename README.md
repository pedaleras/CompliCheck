# 📋 CompliCheck

**CompliCheck** é uma API RESTful desenvolvida para auxiliar empresas no gerenciamento de normas de conformidade ESG (Ambiental, Social e Governança). A aplicação permite cadastrar empresas, vincular normas e gerar alertas de vencimento, promovendo a organização e controle de obrigações corporativas de forma simples e automatizada.

## 🚀 Tecnologias Utilizadas

- **Java 21**
- **Spring Boot**
- **Spring Security com JWT**
- **JPA (Hibernate) + Banco de Dados MySQL**
- **Lombok**
- **Docker e Docker Compose**
- **Insomnia (coleção de testes incluída)**
- **Maven** para build e gerenciamento de dependências

## 🧱 Estrutura Principal

- `Empresa` – Cadastro de empresas com CNPJ, setor e normas.
- `Norma` – Regras de conformidade associadas a empresas.
- `Alerta` – Notificações com base na data limite de normas.
- `Usuário` – Acesso autenticado via token JWT (roles: ADMIN e USER).

---

Este projeto faz parte de uma atividade prática avaliativa, com foco na construção de uma solução backend segura e containerizada.

## ⚙️ Arquivo .env — Configuração de Ambiente

Antes de rodar o container, é necessário criar um arquivo chamado .env na raiz do projeto.
Ele define as variáveis sensíveis utilizadas pela aplicação, como credenciais do banco e o segredo JWT.

📄 Exemplo de .env
```bash
# Banco de dados Oracle
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL
SPRING_DATASOURCE_USERNAME=USUARIO
SPRING_DATASOURCE_PASSWORD=SENHA

# JWT Secret
JWT_SECRET=SECRET
```

## 🐳 Instruções para Testar o CompliCheck no Docker

Estas instruções permitem que avaliadores validem rapidamente a imagem Docker `pedaleras/complicheck:v0.01`.

---

### 1. Puxar a Imagem
Baixe a tag `latest` do CompliCheck:

```bash
docker pull pedaleras/complicheck:latest
```
O Docker busca a imagem e a armazena localmente

---

### 2. Criar o arquivo .env

Antes de iniciar o container, crie o arquivo `.env` conforme o exemplo acima.

### 3. Iniciar o Container
Execute o container em segundo plano, expondo a porta 8080:

```bash
docker run --name compli-check-test \
  --env-file .env \                  # carrega variáveis de ambiente  
  -d \                               # modo detached  
  -p 8080:8080 \                     # mapeia porta host → container  
  pedaleras/complicheck:latest        # imagem a executar
```
- `--env-file` garante que as variáveis configuradas sejam passadas para o container.
- `-d` inicia o container em background.
- `-p host:container` publica a porta para acesso externo .

---

### 4. Verificar Containers em Execução
Liste os containers ativos:

```bash
docker ps
```

Você deverá ver `compli-check-test` com STATUS “Up” e `0.0.0.0:8080->8080/tcp`

---

### 5. Testar a API

Importe o [arquivo da collection](complicheck-collection) no Insomnia e faça os testes

---

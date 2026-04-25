# 🛒 OLX Clone API

API REST completa para um sistema de marketplace, com gerenciamento de usuários, autenticação, produtos, carrinho, favoritos e pagamentos.

---

## 📌 funcionalidades

### 👤 usuários

* atualização de dados do usuário

### 🔐 autenticação

* cadastro de usuário
* login
* verificação de autenticação
* recuperação e redefinição de senha

### 📦 produtos 

* CRUD completo de produtos
* upload de imagem
* listagem por usuário
* marcação como vendido/inativo

### 🛒 carrinho

* adicionar produtos ao carrinho
* listar itens
* calcular subtotal
* remover itens e limpar carrinho

### ❤️ favoritos

* adicionar/remover produtos dos favoritos
* listar favoritos
* verificar se produto está favoritado

### 💳 pagamento

* criação de checkout

### 🔍 busca avançada

* busca por termo
* filtro por categoria
* filtro por preço
* filtro por localização (UF)
* combinação de múltiplos filtros

---

## 🛠️ tecnologias

* Java
* Spring Boot
* JPA / Hibernate
* PostgreSQL / MySQL
* API REST
* Swagger (OpenAPI)

---

## 📖 documentação da API

A documentação interativa está disponível via Swagger:

http://localhost:8080/swagger-ui/index.html

---

## 🔗 principais endpoints

### 🔐 autenticação

| Método | Endpoint                       |
| ------ | ------------------------------ |
| POST   | `/api/auth/register/comprador` |
| POST   | `/api/auth/register/vendedor`  |
| POST   | `/api/auth/login`              |
| POST   | `/api/auth/esqueci-senha`      |
| POST   | `/api/auth/resetar-senha`      |
| GET    | `/api/auth/verify`             |

---

### 📦 produtos

| Método | Endpoint                            |
| ------ | ----------------------------------- |
| POST   | `/api/produtos/usuario/{usuarioId}` |
| GET    | `/api/produtos`                     |
| GET    | `/api/produtos/{id}`                |
| PUT    | `/api/produtos/{id}`                |
| DELETE | `/api/produtos/{id}`                |
| PUT    | `/api/produtos/{id}/vendido`        |
| PUT    | `/api/produtos/{id}/inativo`        |
| POST   | `/api/produtos/{id}/imagem`         |

---

### 🔍 busca

| Método | Endpoint                              |
| ------ | ------------------------------------- |
| GET    | `/api/produtos/pesquisar`             |
| GET    | `/api/produtos/pesquisar-avancado`    |
| GET    | `/api/produtos/categoria/{categoria}` |

---

### ❤️ favoritos

| Método | Endpoint                                       |
| ------ | ---------------------------------------------- |
| POST   | `/api/favoritos/produto/{produtoId}`           |
| DELETE | `/api/favoritos/produto/{produtoId}`           |
| GET    | `/api/favoritos`                               |
| GET    | `/api/favoritos/produto/{produtoId}/verificar` |

---

### 🛒 carrinho

| Método | Endpoint                            |
| ------ | ----------------------------------- |
| POST   | `/api/carrinho/adicionar`           |
| GET    | `/api/carrinho`                     |
| GET    | `/api/carrinho/subtotal`            |
| GET    | `/api/carrinho/contar`              |
| DELETE | `/api/carrinho/remover/{produtoId}` |
| DELETE | `/api/carrinho/limpar`              |

---

### 💳 pagamento

| Método | Endpoint                |
| ------ | ----------------------- |
| POST   | `/api/pagamento/create` |

---

## ▶️ como executar


```bash
git clone https://github.com/debbtrbl/olx-pp1-api.git

cd olx-pp1-api

./mvnw spring-boot:run
```
---

## ✨ sobre o projeto

Este projeto simula um sistema real de marketplace (inspirado na OLX), com arquitetura REST completa e múltiplos módulos integrados.

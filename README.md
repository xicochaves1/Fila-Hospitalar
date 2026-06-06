# 🏥 MediQueue — Fila Digital Hospitalar

Sistema de fila digital desenvolvido com **Java Spring Boot** para a disciplina de Sistemas Distribuídos.

---

## 🚀 Como Rodar

### Pré-requisitos
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### 1. Configure o banco
```sql
CREATE DATABASE fila_hospitalar;
```

### 2. Edite as credenciais
Em `src/main/resources/application.properties`:
```properties
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

### 3. Execute
```bash
mvn spring-boot:run
```

### 4. Acesse
| Recurso | URL |
|---|---|
| Painel principal | http://localhost:8080 |
| Swagger/Docs API | http://localhost:8080/swagger-ui.html |
| API base | http://localhost:8080/api/fila |

---

## 📡 Endpoints da API

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/api/fila/checkin` | Paciente entra na fila |
| `GET` | `/api/fila` | Lista fila atual (ordenada por prioridade) |
| `POST` | `/api/fila/chamar` | Chama próximo paciente |
| `PUT` | `/api/fila/{id}/status` | Atualiza status do atendimento |
| `GET` | `/api/fila/senha/{senha}` | Consulta posição pela senha |
| `GET` | `/api/fila/estatisticas` | Dashboard em tempo real |
| `GET` | `/api/fila/historico/paciente/{id}` | Histórico de um paciente |

---

## 🌐 Conceitos de Sistemas Distribuídos Implementados

### 1. Comunicação via HTTP (REST)
> Protocolo stateless de requisição/resposta. Cada endpoint representa um recurso identificado por URI.

### 2. WebSocket (Tempo Real)
> O painel se conecta via WebSocket STOMP. Quando qualquer paciente entra na fila ou é chamado, **todos os painéis** conectados recebem a atualização instantaneamente — sem polling.
> 
> → **Transparência de localização**: o cliente não sabe onde o servidor está.

### 3. Concorrência e Sincronização
> O método `chamarProximo()` usa `synchronized` para evitar que dois atendentes chamem o mesmo paciente simultânamente — controle de acesso a recurso compartilhado.

### 4. Consistência com `@Transactional`
> Operações críticas (check-in, chamar próximo) são transacionais: ou tudo é gravado, ou nada é — garantindo consistência do banco mesmo com múltiplos clientes.

### 5. Middleware
> Spring Boot age como middleware: abstrai HTTP, banco de dados, WebSocket e segurança, permitindo que os módulos se comuniquem sem acoplamento direto.

### 6. Banco de Dados Distribuído (MySQL)
> O MySQL centraliza o estado da fila, permitindo que múltiplos servidores/instâncias Spring acessem os mesmos dados — base para escalabilidade horizontal.

### 7. Prioridade de Recursos
> A fila implementa ordenação por prioridade (Emergência > Urgente > Prioritário > Normal) — conceito de escalonamento em sistemas distribuídos.

---

## 👥 Usuários Padrão

| Username | Senha | Role |
|---|---|---|
| admin | admin123 | Administrador |
| atendente1 | senha123 | Atendente |
| medico1 | senha123 | Médico |

---

## 🏗️ Arquitetura

```
┌─────────────────────────────────────────┐
│           CLIENTES (Browser)            │
│  Painel HTML + WebSocket + REST calls   │
└──────────────┬──────────────────────────┘
               │ HTTP / WebSocket
┌──────────────▼──────────────────────────┐
│         SPRING BOOT (Middleware)        │
│  Controllers → Services → Repositories  │
│  WebSocket Broker (STOMP)               │
└──────────────┬──────────────────────────┘
               │ JDBC
┌──────────────▼──────────────────────────┐
│              MySQL 8.0                  │
│  pacientes | fila_atendimento | usuarios│
└─────────────────────────────────────────┘
```

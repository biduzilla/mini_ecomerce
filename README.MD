# 🚀 Mini E-Commerce - Ecossistema de Microsserviços Distribuídos

Este repositório contém a implementação de uma arquitetura orientada a microsserviços para um ecossistema de e-commerce resiliente e de alta performance, desenvolvido inteiramente em **Kotlin** e **Spring Cloud**. O projeto foca no isolamento completo de domínios, auditoria transparente de dados e consistência eventual orientada a eventos.

---

## 📐 Arquitetura de Infraestrutura & Fluxo

O ecossistema é composto por quatro microsserviços de negócio apoiados por duas ferramentas centrais de infraestrutura: o **API Gateway** e o **Config Server**.


```

```
               ┌───────────────────────────────────┐
               │  Config Server (ms_config: 8888)  │
               └─────────────────┬─────────────────┘
                                 │ (Provê propriedades .yml)
                                 ▼
               ┌───────────────────────────────────┐
               │     API Gateway (Porta 9000)      │
               └──────┬───┬─────────────┬───┬──────┘
      ┌───────────────┘   │             │   └───────────────┐
/api/auth/** /api/products/** /api/orders/** /api/stocks/**
      │                   │             │                   │
      ▼                   ▼             ▼                   ▼

```

┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐
│     ms_auth      │ │    ms_product    │ │     ms_order     │ │     ms_stock     │
│   (Porta 8081)   │ │   (Porta 8083)   │ │   (Porta 8082)   │ │   (Porta 8084)   │
└─────────┬────────┘ └─────────┬────────┘ └─────────┬────────┘ └─────────┬────────┘
│                    │                    │                    │
▼                    ▼                    │                    ▼
[ db_auth ]          [ db_product ]             │              [ db_stock ]
│                    ▲
│     OpenFeign      │
└────────────────────┘
(Check Availability)
│
Kafka Message Bus
(Async Event: OrderCreated)

```

### 🎛️ Componentes Centrais de Infraestrutura
1. **Config Server (`ms_config` - Porta 8888):** Atua como o repositório centralizado de configurações do sistema através da anotação `@EnableConfigServer`. Ele centraliza e distribui os arquivos de propriedades (`.yml`) para todos os outros microsserviços em tempo de inicialização.
2. **API Gateway (Porta 9000):** Único ponto de entrada para o tráfego externo. Realiza o roteamento inteligente, repasse de headers HTTP (`Authorization` JWT) e isola falhas locais usando **Resilience4j Circuit Breaker**.

---

## 🧬 1. Padrões de Persistência & Auditoria

Todas as tabelas do ecossistema compartilham uma fundação robusta de governança de dados através do reaproveitamento de componentes:

### ⏱️ Auditoria e Soft Delete Transparente (`BaseModel`)
Cada entidade herda de uma classe abstrata comum mapeada (`@MappedSuperclass`), que aciona automaticamente o ciclo de vida do JPA (`AuditingEntityListener`):
* **Campos Gerenciados:** `createdAt`, `createdBy`, `updatedAt`, `updatedBy` e `deleted`.
* **Soft Delete Nativo:** A exclusão física de registros é desativada. Ao chamar o método `.deleteById()`, o Hibernate intercepta a requisição usando `@SQLDelete` e executa um comando de atualização lógica:
  ```sql
  UPDATE table_name SET deleted = true, updated_at = NOW() WHERE id = ?

```

* **Filtro Automático:** Registros deletados logicamente são invisíveis para consultas de busca gerais graças à diretiva `@SQLRestriction("deleted <> true")`.

---

## 🗄️ 2. Detalhes Técnicos dos Domínios

### 🔐 Auth Service (`ms_auth` - Porta 8081)

Centraliza o controle de usuários (`User`) e as regras corporativas de segurança.

* **Modelo:** Implementa `UserDetails` do Spring Security. Garante restrição única multi-colunar composta para re-cadastro dinâmico de chaves clonadas: `uniqueConstraints = [columnNames = ["email", "deleted", "updatedAt"]]`.
* **Regras de Serviço (`UserService`):** Implementa criptografia forte na senha (`PasswordEncoder`). Aplica validação de segurança rígida: mínimo de 8 caracteres, contendo obrigatoriamente pelo menos uma letra e um dígito.
* **Emissão de Tokens:** O `AuthService` gera e gerencia ciclos de vida de chaves através de pares assimétricos de `AccessToken` e `RefreshToken`.

### 📦 Product Service (`ms_product` - Porta 8083)

Catálogo puro de itens expostos ao cliente.

* **Modelo:** `Product` mapeia estritamente `name` e `price`.
* **Regras de Serviço:** Suporta pesquisas paginadas e filtradas.

### 📊 Stock Service (`ms_stock` - Porta 8084)

Camada de inventário física. Isola as volumetrias do catálogo.

* **Modelo:** `Stock` armazena `productId` e `availableQuantity`. Possui um atributo `@Version var version: Int` que implementa **Optimistic Locking**, blindando o banco de dados contra inconsistências numéricas induzidas por concorrências massivas.
* **Comunicação Direta (`ProductClient`):** Durante a criação de um lote de estoque, o serviço valida sincronamente se o produto realmente existe consultando o `ms_product` antes de efetivar o registro.

### 🛒 Order Service (`ms_order` - Porta 8082)

Orquestrador das intenções de compra do ecossistema.

* **Modelo:** Relacionamento clássico `OneToMany` entre `Order` e `OrderItem`.
* **Regras Históricas:** Persiste imutavelmente o campo `unitPrice` no instante exato do fechamento do pedido, protegendo o histórico financeiro do cliente contra flutuações futuras de preço efetuadas no catálogo de produtos.

---

## ⚙️ 3. O Fluxo de Compira Híbrido

O momento de fechamento de uma compra utiliza uma arquitetura híbrida (**Síncrona + Assíncrona**) para maximizar a consistência e a vazão do sistema:

### Fase 1: Validação Síncrona Implacável (OpenFeign)

1. O cliente submete a compra ao Gateway, que a encaminha ao `ms_order`.
2. O `OrderService` intercepta a requisição e aciona o `StockClient` (gerado via **OpenFeign**), disparando uma chamada síncrona para:
```http
POST http://localhost:8084/api/stocks/check-availability

```


3. O `ms_stock` avalia o cenário do inventário em bloco (`findAllByProductIdIn`). Se a quantidade solicitada for maior que a disponível, retorna um relatório detalhado das falhas, fazendo o `ms_order` abortar imediatamente a transação e lançar um erro `400 Bad Request` customizado ao cliente.

### Fase 2: Persistência e Notificação por Eventos (Kafka)

1. Se o estoque validar positivamente o lote, o `ms_order` salva o registro local com o status inicial `OrderStatusEnum.PENDING`.
2. O serviço aciona imediatamente o `OrderEventProducers`, empacotando e enviando um evento rico (`OrderCreatedEvent`) para o broker **Apache Kafka (Porta 9094)**.
3. O `ms_stock` escuta o tópico em background através do `stock-service-group`, consome o evento e executa a dedução atômica das quantidades em seu banco de dados (`deductStock`), validando a concorrência via controle de versão.

---

## 🌐 4. Mapeamento de Endpoints Unificados no Gateway

O API Gateway centraliza todas as chamadas expostas à internet sob a porta **9000**:

| Rota Exposta no Gateway | Alvo Interno | Microsserviço | Resiliência (Circuit Breaker) |
| --- | --- | --- | --- |
| `POST /api/auth/**` | `/api/auth/**` | `ms_auth` (8081) | `msAuthCircuitBreaker` |
| `POST /api/user/**` | `/api/user/**` | `ms_auth` (8081) | `msAuthCircuitBreaker` |
| `/api/products/**` | `/api/products/**` | `ms_product` (8083) | `msProductCircuitBreaker` |
| `/api/orders/**` | `/api/orders/**` | `ms_order` (8082) | `msOrderCircuitBreaker` |
| `/api/stocks/**` | `/api/stocks/**` | `ms_stock` (8084) | `msStockCircuitBreaker` |

### 🔍 Agregação Central de Documentação (Swagger UI)

A interface unificada do Swagger pode ser acessada de forma consolidada no endereço `http://localhost:9000/swagger-ui.html`. O Gateway redistribui e serve as definições de documentação das APIs através das rotas configuradas de redirecionamento interno:

* **Auth & User Specification:** `/docs/ms-auth/v3/api-docs` -> `ms_auth` `/v3/api-docs`
* **Product Specification:** `/docs/ms-product/v3/api-docs` -> `ms_product` `/api-docs`
* **Order Specification:** `/docs/ms-order/v3/api-docs` -> `ms_order` `/api-docs`
* **Stock Specification:** `/docs/ms-stock/v3/api-docs` -> `ms_stock` `/api-docs`
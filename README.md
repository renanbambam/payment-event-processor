# payment-event-processor

Serviço de processamento de eventos de pagamento usando arquitetura orientada a eventos.

Precisava entender na prática como sistemas financeiros lidam com consistência eventual — a
teoria de Kafka é simples, mas conectar producer, consumer, DLQ e persistência com tratamento
de falhas levou alguns dias de ajuste. O que mais me tomou tempo não foi o Kafka em si, e sim
decidir onde cada responsabilidade morava pra não vazar detalhe de infra pro domínio.

## Como funciona

O fluxo é: REST → producer publica no Kafka → consumer consome → persiste no PostgreSQL. Um
POST cria o pagamento como `PENDING`, grava no banco e publica um evento. O consumer lê esse
evento e move o pagamento pra `PROCESSING`.

O ponto interessante é o tratamento de falhas no consumer: evento que não consegue ser lido ou
processado vai pra uma dead letter queue em vez de travar o tópico principal.

## Stack

Kotlin 1.9 · Spring Boot 3.2 · Apache Kafka · PostgreSQL 16 · Flyway · Docker Compose

## Subindo local

pré-requisito: Docker rodando

    docker compose -f docker/docker-compose.yml up -d
    # aguarda os serviços subirem (~30 segundos)

    # criar um pagamento
    curl -X POST http://localhost:8080/api/v1/payments \
      -H "Content-Type: application/json" \
      -d '{"amount": 150.00, "currency": "BRL", "senderId": "user-1", "receiverId": "user-2"}'

    # consultar o status
    curl http://localhost:8080/api/v1/payments/{id}

A documentação da API fica em `http://localhost:8080/swagger-ui.html`.

## Arquitetura

Hexagonal (ports and adapters): o domínio não conhece Spring, Kafka ou JPA. O modelo de
domínio não tem uma anotação de framework sequer, e os services dependem só de interfaces.
Qualquer adapter pode ser trocado sem tocar nas regras de negócio.

    REST Controller → PublishPaymentPort → PaymentPublisherService → PaymentEventPublisher → Kafka
                                                                    ↘ PaymentRepository → PostgreSQL

    Kafka → PaymentConsumer → QueryPaymentPort → PaymentQueryService → PaymentRepository → PostgreSQL

## Limitações que eu melhoraria

- Sem autenticação nos endpoints (deixei de fora pra não esconder a lógica de evento).
- Salvo no banco e publico no Kafka em sequência, sem transação entre os dois: se o processo
  cai entre o commit e o publish, o evento se perde. O **Outbox pattern** (gravar o evento numa
  tabela na mesma transação do pagamento e publicar depois a partir dela) resolveria essa
  consistência entre banco e Kafka — foi o trade-off consciente que aceitei pro escopo.
- O consumer marca `PROCESSING` mas não simula o processamento real até `COMPLETED`/`FAILED` —
  o ciclo de vida completo do pagamento ficou de fora.
- A DLQ recebe as mensagens mas não tem reprocessamento automático; hoje seria manual.
- Retry do producer está configurado no yml, podia ser backoff exponencial explícito.
- Testcontainers só no teste de integração; os demais usam mocks.

## O que faria diferente

Hoje serializo o evento como JSON string na mão. Num sistema de verdade usaria um schema
registry (Avro ou Protobuf) pra versionar o contrato do evento — do jeito que está, mudar um
campo do `Payment` quebra consumer antigo sem nenhum aviso em tempo de build.

## Testes

    ./gradlew test

Unitários cobrem domínio, service, controller (`@WebMvcTest`) e consumer. O fluxo completo
(REST → Kafka → banco) roda com Testcontainers, subindo Kafka e Postgres de verdade — então
esse teste precisa de Docker disponível.

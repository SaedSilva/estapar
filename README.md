# Teste para vaga Júnior Estapar

## Sistema de gestão de estacionamentos, que controla o número de vagas em aberto, entrada, saida e faturameto do setor.

### Tecnologias utilizadas

- Kotlin
- Spring Boot
- PostgreSQL

Atualmente o projeto recria o banco de dados a cada inicialização.

### Requisitos

- [X] Importar dados do simulador e salvar no banco de dados.
- [X] Regra de preço dinâmico, onde o valor do estacionamento é calculado de acordo com o tempo de permanência do
  veículo.
- [X] Regra de lotação, fechar o setor quando estiver cheio.
- [X] Webhook - entrada na garagem
- [X] Webhook - entrada na vaga
- [X] Webhook - saida da garagem
- [X] Api - Consulta de placa
- [X] Api - Consulta de vaga
- [X] Api - Consulta de faturamento

### Extras:

Diagrama do banco de dados:
![Diagrama do banco de dados](Estapar_DB_DIAGRAM.png)

Documentação da api disponível no swagger: http://localhost:3003/swagger-ui/index.html

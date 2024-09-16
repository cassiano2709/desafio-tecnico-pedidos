# Desafio Técnico - API de Pedidos
Este projeto é uma API REST desenvolvida em Spring Boot para gerenciar pedidos de clientes.
A API permite realizar o upload de arquivos, processá-los e retornar os pedidos em um formato JSON estruturado, conforme solicitado no desafio técnico.

# Funcionalidades
A API oferece as seguintes funcionalidades

- Upload de Arquivos: Processa arquivos contendo informações de pedidos e usuários.
- Listagem de Pedidos: Retorna todos os pedidos armazenados.
- Detalhes de um Pedido: Retorna os detalhes de um pedido específico.

# Tecnologias Utilizadas
- Java 21
- Spring Boot 3.x
- Spring Data JPA
- PostgreSQL
- Hibernate
- Swagger 3.x (Documentação da API)
- Lombok (para simplificar o código com getters/setters automáticos)
- Maven (Gerenciador de dependências)
- Docker

# Pré-requisitos
Antes de iniciar, certifique-se de ter as seguintes ferramentas instaladas em seu ambiente:

- JDK 17 ou superior
- Maven 3.6 ou superior
- PostgreSQL
- Git
- Postman ou cURL (para testar a API)

# Configuração do Banco de Dados
O banco de dados PostgreSQL será configurado automaticamente pelo Docker Compose.
Não é necessário criar a base de dados manualmente.
Os dados de configuração do banco de dados estão no docker-compose.yml, e a 
aplicação se conectará ao banco de dados automaticamente ao subir.
Se precisar alterar os dados de conexão, modifique as variáveis de ambiente no 
docker-compose.yml:

# Documentação da API
A documentação da API gerada pelo Swagger está disponível em:
 - http://localhost:8080/swagger-ui/index.html

# Dependências Principais
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-data-jpa
- org.postgresql:postgresql
- io.springfox:springfox-boot-starter:3.0.0
- org.projectlombok:lombok
- org.springdoc


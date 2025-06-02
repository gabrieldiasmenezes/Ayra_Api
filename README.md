# 🌪️ Projeto Ayra - Backend em Java

O **Ayra** é um aplicativo voltado à prevenção de desastres naturais, utilizando sensores inteligentes, dados em tempo real e mapas interativos para identificar áreas de risco e alertar usuários. Esta parte do repositório é dedicada ao backend desenvolvido em **Java**, responsável pelo processamento de dados, integração com banco de dados e envio de alertas para o app mobile.

## 📌 Funcionalidades do Backend

- Cadastro e autenticação de usuários
- Consulta e atualização de dados de sensores
- Classificação de áreas de risco por gravidade (vermelho, amarelo, verde)
- Geração e envio de alertas para usuários conforme localização
- API REST para integração com o aplicativo mobile
- Integração com banco de dados relacional e não relacional

## 🧱 Tecnologias Utilizadas

- Java 17
- Spring Boot
- JPA/Hibernate
- MySQL
- MongoDB
- Postman (testes de API)
- Maven

## 🗂️ Estrutura do Projeto
```
/src
└── main
├── java
│ └── com.ayra.backend
│ ├── controller
│ ├── service
│ ├── repository
│ ├── model
│ └── AyraApplication.java
└── resources
├── application.properties
└── static
```
---
### Rodando localmente com Maven

1. Clone o repositório:

```bash
https://github.com/gabrieldiasmenezes/Ayra_Api.git
cd Ayra_Api
```

2. Execute a aplicação:

```bash
./mvnw spring-boot:run
```

3. Acesse a API em:

```
http://localhost:8080
```

4. Para acessar a documentação Swagger (UI interativa):

```
http://localhost:8080/swagger-ui.html
```

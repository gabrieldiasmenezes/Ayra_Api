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

## 🧪 Testes e Validação

- Todos os endpoints foram testados via Postman após o deploy.
- Erros comuns como CORS, autenticação ou erro 500 foram tratados no ambiente de produção.
- O vídeo de demonstração mostra a aplicação em funcionamento real na nuvem.

---
## 📡EndPoints por Entidades

### **🧍‍♂️ User**

| Método | Endpoint          | Descrição                 |
| ------ | ----------------- | ------------------------- |
| GET    | `/user`      | Retorna todos os usuários |
| GET    | `/user/{id}` | Retorna um usuário por ID |
| POST   | `/user`      | Cria um novo usuário      |
| PUT    | `/user/{id}` | Atualiza um usuário       |
| DELETE | `/user/{id}` | Deleta um usuário         |


### **🌍 Coordinate**

| Método | Endpoint                | Descrição                         |
| ------ | ----------------------- | --------------------------------- |
| GET    | `/coordinates`      | Retorna todas as coordenadas      |

### **🗺️ Map Marker**

| Método | Endpoint               | Descrição                           |
| ------ | ---------------------- | ----------------------------------- |
| GET    | `/map-marker`      | Retorna todos os marcadores do mapa |
| GET    | `/map-marker/{id}` | Retorna um marcador específico      |       |

### **🚨 Alert**

| Método | Endpoint           | Descrição                    |
| ------ | ------------------ | ---------------------------- |
| GET    | `/alert`      | Retorna todos os alertas     |

### **🛣️ Safe Route**

| Método | Endpoint               | Descrição                      |
| ------ | ---------------------- | ------------------------------ |
| GET    | `/safe-routes`      | Retorna todas as rotas seguras |
| GETBYALERTID    | `/safe-routes/by-alert/{id_alert}` | Retorna rotas especificas de um alerta        |

### **🏠 Safe Location**

| Método | Endpoint                  | Descrição                          |
| ------ | ------------------------- | ---------------------------------- |
| GET    | `/safe-location`      | Retorna todos os locais seguros    |
| GETBYALERTID | `/safe-location/by-alert/{id_alert}` | Retorna locais seguros específicos de um alerta |

### **💡 Safe Tip**

| Método | Endpoint             | Descrição                           |
| ------ | -------------------- | ----------------------------------- |
| GET    | `/safe-tip`      | Retorna todas as dicas de segurança |
| GETBYALERTID    | ` /safe-tip/by-alert/{id_alert} ` | Retorna dicas específicas de um alerta       |

---

### **📌 Testes da API – JSONs**

| Entidade              | Método | Endpoint                 | Exemplo de JSON (POST)                                                                                                                                                                                                                                                             |
| --------------------- | ------ | ------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **User**              | POST   | `/users`             | `{"name": "Isabela Santos","email": "isabela.santos@exemplo.com","password": "senhaForte456","phone": "11987654321","coordinates": {"latitude": -23.560520,"longitude": -46.643308,"dateCoordinate": "2025-06-02"}} `   |
| **Map Marker**              | POST   | `/map-marker`             | `{"title": "Inundação Detectada","description": "Água acumulada detectada pelo sensor.","intensity":"high","radius": 50.0,"coordinates": {"latitude": -23.5505,"longitude": -46.6333,"dateCoordinate": "2023-10-01"}} `   |
---

# Configuração do Banco de Dados

Antes de executar a aplicação, é necessário configurar o banco de dados Oracle manualmente. Siga as instruções abaixo:

## 1. Crie as Tabelas

Execute os seguintes scripts SQL no seu banco de dados Oracle para criar as tabelas necessárias:

```sql
/*
DROP TABLE safe_tip;
DROP TABLE safe_location;
DROP TABLE safe_routes;
DROP TABLE alert;
DROP TABLE emergency_user;
DROP TABLE user_table;
DROP TABLE emergency_contact;
DROP TABLE map_marker;
DROP TABLE coordinates;
*/
-- Tabela: Coordinates
CREATE TABLE coordinates (
 id_cor INTEGER GENERATED BY DEFAULT AS IDENTITY,
 latitude NUMBER(9,6) NOT NULL,
 longitude NUMBER(9,6) NOT NULL,
 date_coordinate DATE NOT NULL,
 CONSTRAINT coordinates_pk PRIMARY KEY (id_cor)
);
-- Tabela: EmergencyContact
CREATE TABLE emergency_contact (
 id_contact INTEGER GENERATED BY DEFAULT AS IDENTITY,
 name VARCHAR2(100) NOT NULL,
 phone VARCHAR2(11) NOT NULL UNIQUE,
 CONSTRAINT emergency_contact_pk PRIMARY KEY (id_contact)
);
-- Tabela: User
CREATE TABLE user_table (
 id_user INTEGER GENERATED BY DEFAULT AS IDENTITY,
 name VARCHAR2(100) NOT NULL,
 email VARCHAR2(150) NOT NULL UNIQUE,
 password VARCHAR2(255) NOT NULL,
 phone VARCHAR2(11) NOT NULL UNIQUE,
 coordinates_id INTEGER NOT NULL,
 CONSTRAINT user_pk PRIMARY KEY (id_user),
 CONSTRAINT user_coordinates_fk FOREIGN KEY (coordinates_id) REFERENCES 
coordinates(id_cor)
);
-- Tabela Associativa: EmergencyUser
CREATE TABLE emergency_user (
 id_emergency INTEGER NOT NULL,
 id_user INTEGER NOT NULL,
 CONSTRAINT emergency_user_pk PRIMARY KEY (id_emergency, id_user),
 CONSTRAINT emergency_user_emergency_fk FOREIGN KEY (id_emergency) REFERENCES 
emergency_contact(id_contact),
 CONSTRAINT emergency_user_user_fk FOREIGN KEY (id_user) REFERENCES 
user_table(id_user)
);
-- Tabela: MapMarker
CREATE TABLE map_marker (
 id_maker INTEGER GENERATED BY DEFAULT AS IDENTITY,
 title VARCHAR2(200) NOT NULL,
 description CLOB NOT NULL,
 intensity VARCHAR2(10) NOT NULL CHECK (intensity IN ('high', 'medium', 'low')),
 radius NUMBER(5,2) NOT NULL CHECK (radius > 0), 
 id_cor INTEGER NOT NULL,
 CONSTRAINT map_marker_pk PRIMARY KEY (id_maker),
 CONSTRAINT map_marker_coordinates_fk FOREIGN KEY (id_cor) REFERENCES 
coordinates(id_cor)
);
-- Tabela: Alert
CREATE TABLE alert (
 id_alert INTEGER GENERATED BY DEFAULT AS IDENTITY,
 title VARCHAR2(200) NOT NULL,
 description CLOB NOT NULL,
 intensity VARCHAR2(10) NOT NULL CHECK (intensity IN ('high', 'medium', 'low')),
 alert_datetime TIMESTAMP WITH TIME ZONE NOT NULL,
 location VARCHAR2(200) NOT NULL,
 radius NUMBER(5,2) NOT NULL,
 evacuation_time VARCHAR2(100),
 coordinates_id INTEGER NOT NULL,
 id_map INTEGER NOT NULL,
 CONSTRAINT alert_pk PRIMARY KEY (id_alert),
 CONSTRAINT alert_coordinates_fk FOREIGN KEY (coordinates_id) REFERENCES 
coordinates(id_cor),
 CONSTRAINT alert_map_marker_fk FOREIGN KEY (id_map) REFERENCES 
map_marker(id_maker)
);
-- Tabela: SafeRoutes
CREATE TABLE safe_routes (
 id_routes INTEGER GENERATED BY DEFAULT AS IDENTITY,
 routes VARCHAR2(200) NOT NULL,
 alert_id_alert INTEGER NOT NULL,
 CONSTRAINT safe_routes_pk PRIMARY KEY (id_routes),
 CONSTRAINT safe_routes_alert_fk FOREIGN KEY (alert_id_alert) REFERENCES alert(id_alert)
);
-- Tabela: SafeLocation
CREATE TABLE safe_location (
 id_location INTEGER GENERATED BY DEFAULT AS IDENTITY,
 location VARCHAR2(200) NOT NULL,
 alert_id_alert INTEGER NOT NULL,
 CONSTRAINT safe_location_pk PRIMARY KEY (id_location),
 CONSTRAINT safe_location_alert_fk FOREIGN KEY (alert_id_alert) REFERENCES 
alert(id_alert)
);
-- Tabela: SafeTip
CREATE TABLE safe_tip (
 id_tip INTEGER GENERATED BY DEFAULT AS IDENTITY,
 tip VARCHAR2(200) NOT NULL,
 alert_id_alert INTEGER NOT NULL,
 CONSTRAINT safe_tip_pk PRIMARY KEY (id_tip),
 CONSTRAINT safe_tip_alert_fk FOREIGN KEY (alert_id_alert) REFERENCES alert(id_alert)
);
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

# SimpleOrderManager

Backend for a simple Order Management System developed with Spring boot and MySQL.
It has REST api with CRUD operations for orders.

## Configuration
App requires MySQL server with created database and user that has access to this database.

User credentials, server address and database name should be set in [application.properties](src/main/resources/application.properties)

## Running
You can start server with command:
 `./mvnw spring-boot:run`
 
 You can connect with it on localhost:8080
 
## API
- GET /api/order - get all orders
- POST /api/order (with Order in body) - create new order
- PUT /api/order (with Order in body) - update existing order. Id of the given order is the one to update
- GET /api/order/{id} - get specified order
- DELETE /api/order/{id} - delete specified order

## Order - Data Structure
id : Long

order_name: String

buyer_name: String

buyer_surname: String

date: Date
  

## Tests
You can execute tests with command:
`./mvnw test`

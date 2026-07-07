# WS Bank API

Simple API to manage bank account balances through deposit, withdraw, and transfer events.

## Tech Stack

- Java 25
- Spring Boot 4.1.0
- Spring Web MVC
- Spring Data JPA
- H2 in-memory database
- Maven

## Running The Application

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

Or, if Maven is installed:

```powershell
mvn spring-boot:run
```

The API runs at:

```text
http://localhost:8080
```

The default profile is `dev`, using an in-memory H2 database.

## Running Tests

```powershell
mvn test
```

To run only the full API flow integration test:

```powershell
mvn "-Dtest=ApiFlowIntegrationTest" test
```

## Endpoints

### Reset State

```http
POST /reset
```

Response: `200 OK`

### Get Balance

```http
GET /balance?account_id=100
```

Response when the account exists:

```http
200 OK
20
```

Response when the account does not exist:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Account not found: 100",
  "path": "/balance"
}
```

### Process Event

```http
POST /event
Content-Type: application/json
```

Deposit creates the account when it does not exist. If the account already exists, it increases the current balance.

```json
{
  "type": "deposit",
  "destination": "100",
  "amount": 10
}
```

Response:

```json
{
  "destination": {
    "id": "100",
    "balance": 10
  }
}
```

Withdraw requires the origin account to exist and have enough balance.

```json
{
  "type": "withdraw",
  "origin": "100",
  "amount": 5
}
```

Response:

```json
{
  "origin": {
    "id": "100",
    "balance": 15
  }
}
```

Transfer requires both origin and destination accounts to exist.

```json
{
  "type": "transfer",
  "origin": "100",
  "destination": "300",
  "amount": 15
}
```

Response:

```json
{
  "origin": {
    "id": "100",
    "balance": 0
  },
  "destination": {
    "id": "300",
    "balance": 20
  }
}
```

## curl Examples

```powershell
$base = "http://localhost:8080"

curl.exe -i -X POST "$base/reset"

curl.exe -i -X POST "$base/event" `
  -H "Content-Type: application/json" `
  -d '{"type":"deposit","destination":"100","amount":10}'

curl.exe -i -X POST "$base/event" `
  -H "Content-Type: application/json" `
  -d '{"type":"withdraw","origin":"100","amount":5}'

curl.exe -i "$base/balance?account_id=100"
```

## Error Response Format

Handled errors return JSON in this format:

```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Amount must be positive: -10",
  "path": "/event"
}
```

Main error messages:

- `Account not found: 200`
- `Insufficient balance for account: 100`
- `Amount is required`
- `Amount must be positive: -10`
- `Event type is required`
- `Unsupported event type: unknown`
- `Origin account is required`
- `Destination account is required`

## Main Decisions

- Business logic lives in services and strategies, not in controllers.
- `GET /balance` only reads state and does not change balances.
- `POST /reset` clears accounts and events.
- Deposit creates an account when needed.
- Withdraw fails when the origin account does not exist or has insufficient balance.
- Transfer fails when either origin or destination does not exist.
- Transfer runs inside a transaction to avoid partial state changes.
- Errors return a structured body to make debugging and Postman usage easier.

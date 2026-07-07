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

Body:

```text
OK
```

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

```http
404 Not Found
0
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

Transfer requires the origin account to exist and have enough balance. The destination account is credited and created when it does not exist.

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
    "balance": 15
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

Handled errors return a plain `0` body with the appropriate HTTP status:

```http
404 Not Found
0
```

Examples:

- Missing account: `404 0`
- Invalid event payload: `400 0`
- Insufficient balance: `400 0`
- Unknown event type: `400 0`

## Main Decisions

- Business logic lives in services and strategies, not in controllers.
- `GET /balance` only reads state and does not change balances.
- `POST /reset` clears accounts and events.
- Deposit creates an account when needed.
- Withdraw fails when the origin account does not exist or has insufficient balance.
- Transfer fails when the origin account does not exist.
- Transfer creates or credits the destination account.
- Transfer runs inside a transaction to avoid partial state changes.
- Handled errors follow the challenge contract and return `0` as response body.

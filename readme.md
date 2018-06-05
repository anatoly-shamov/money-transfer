### MoneyTransfer

Demo web application for money transfers between accounts with REST API.

#### How to run

```sh
gradle run
```

#### REST API

Root API path is http://localhost:8080

##### Endpoints

| Method | Path | Description | On success returns |
|------|------|------|------|
|POST|/account/create|create new account|201 Created + Location header|
|GET|/account/all|receive list of all accounts|200 OK + list of all account|
|GET|/account/{id}|receive account by id|200 OK + requested account|
|DELETE|/account/{id}|delete account by id|204 No Content|
|PATCH|/account/{id}/deposit/{amount}|deposit money on account|200 OK|
|PATCH|/account/{id}/withdraw/{amount}|withdraw money from account|200 OK|
|POST|/transfer|transfer money between accounts|200 OK|

##### Sample request payloads

POST /account/create
```sh
{
    "holderName":"John Dow",
    "currencyCode":"USD",
    "balance":1000
}
```

POST /transfer
```sh
{
    "senderAccountId":1,
    "receiverAccountId":3,
    "amount":500
}
```

##### Sample response payloads

GET /account/all
```sh
[
    {
        "id": 1,
        "holderName": "John Dow",
        "currencyCode": "EUR",
        "balance": 1000
    },
    {
        "id": 2,
        "holderName": "John Dow",
        "currencyCode": "USD",
        "balance": 2000
    }
]
```

GET /account/{id}
```sh
{
    "id": 3,
    "holderName": "Mary Jane",
    "currencyCode": "EUR",
    "balance": 3000
}
```
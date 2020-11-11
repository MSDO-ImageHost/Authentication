# API Specification




## return authentication token

Request
```json
{
  "user-id": "<user-id>"  
}
```

Response
```json
{
    "user-id": "<user-id>",
    "token": "<positive integer>"
}
```

## confirm set password

Request
```json
{
  "user-id": "<user-id>",
  "old-password": "<password>",
  "new-password": "<password>"
}
```

Response
```json
{
    "password-set": "<Boolean>"
}
```

## confirm invalidate token

Request
```json
{
  "user-id": "<user-id>",
  "token": "<positive integer>"
}
```

Response
```json
{
    "token-invalid": "<Boolean>"
}
```

## confirm account creation

Request
```json
{
  "username": "<username>",
  "user-email": "<email>",
  "password": "<password>"
}
```

Response
```json
{
    "account-created": "<Boolean>",
    ("user-id": "<user-id>")
}
```

## confirm account deletion

Request
```json
{
  "user-id": "<user-id>",
  "password": "<password>"
}
```

Response
```json
{
    "account-deleted": "<Boolean>"
}
```

## confirm account update

Request
```json
{
  "user-id": "<user-id>",
  "old-username": "<username>",
  "new-username": "<username>",
  "old-email": "<email>",
  "new-email": "<email>"
}
```

Response
```json
{
    "account-updated": "<Boolean>"
}
```

## return account info

Request
```json
{
  "user-id": "<user-id>",
  ("password": "<password>")
}
```

Response
```json
{
    "user-id": "<user-id>",
    "username": "<username>",
    "user-email": "<email>",
    "last-login": "<time-format>"
}
```

## request likes for user

Request
```json
{
  "user-id": "<user-id>"  
}
```

Response
```json
{
    "user-id": "<user-id>",
    "number-of-likes": "<positive integer>"
}
```

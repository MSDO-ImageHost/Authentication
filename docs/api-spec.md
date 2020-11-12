# API Specification




## LoginToken

Request
```json
{
  "user-id": "<user-id>",
  "login-time": "<ISO8601 timestamp>"
}
```

Response
```json
{
    "user-id": "<user-id>",
    "token": "<Integer>"
}
```

## InvalidateLoginToken

Request
```json
{
  "user-id": "<user-id>",
  "token": "<Integer>"
  
}
```

Response
```json
{
    "token-invalid": "<Boolean>"
}
```

## AccountCreate

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

## AccountReset

Request
```json
{
  
}
```

Response
```json
{
    "account-reset": "<Boolean>",
    ("user-id": "<user-id>")
}
```

## AccountPasswordUpdate

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
    "password-updated": "<Boolean>",
    ("user-id": "<user-id>"
}
```

## AccountDeletion

Request
```json
{
  "user-id": "<user-id>",
  ("token": "<Integer>")
}
```

Response
```json
{
   "account-deleted": "<Boolean>",
  ("user-id": "<user-id>")
}
```


## UpdateAccountPrivileges

Request
```json
{
  "user-id": "<user-id>"
  "new-privileges": "<privileges>"
}
```

Response
```json
{
    "privi-updated": "<Boolean>",
    "user-id": "<user-id>"
}
```

## AccountData

Request
```json
{
  "user-id": "<user-id>"
  "token": "<Integer>"
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

## ReturnLikesForUser

Request
```json
{
    "user-id": "<user-id>",
    "number-of-likes": "<Integer>"
}
```

Response
```json
{
   
}
```

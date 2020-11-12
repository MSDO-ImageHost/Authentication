# API Specification




## RequestLoginToken

Request
```json
{
  "user-id": "<user-id>",
  "login-time": "<ISO8601 timestamp>"
}
```

Response: ReturnAuthenticationToken
```json
{
    "user-id": "<user-id>",
    "token": "<token>"
}
```

## RequestInvalidateLoginToken

Request
```json
{
  "user-id": "<user-id>",
  "token": "<token>"
  
}
```

Response: ConfirmInvalidateToken
```json
{
    "token-invalid": "<Boolean>"
}
```

## RequestAccountCreate

Request
```json
{
  "username": "<username>",
  "user-email": "<email>",
  "password": "<password>"
}
```

Response: ConfirmAccountCreation
```json
{
    "account-created": "<Boolean>",
    ("user-id": "<user-id>")
}
```

## RequestAccountReset

Request
```json
{
  
}
```

Response: 
```json
{
    "account-reset": "<Boolean>",
    ("user-id": "<user-id>")
}
```

## RequestAccountPasswordUpdate

Request
```json
{
  "user-id": "<user-id>",
  "old-password": "<password>",
  "new-password": "<password>"
}
```

Response: ConfirmInvalidateToken
```json
{
    "password-updated": "<Boolean>",
    ("user-id": "<user-id>"
}
```

## ConfirmAccountDeletion

Request: RequestAccountDelete
```json
{
  "user-id": "<user-id>",
  ("token": "<token>")
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
  "user-id": "<user-id>",
  "new-privileges": "<privileges>"
}
```

Response: ConfirmAccountUpdate
```json
{
    "privi-updated": "<Boolean>",
    "user-id": "<user-id>"
}
```

## RequestAccountData

Request
```json
{
  "user-id": "<user-id>",
  "token": "<token>"
}
```

Response: ReturnAccountInfo
```json
{
    "user-id": "<user-id>",
    "username": "<username>",
    "user-email": "<email>",
    "last-login": "<time-format>"
}
```

## ReturnLikesForUser

Request: RequestLikesForUser
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

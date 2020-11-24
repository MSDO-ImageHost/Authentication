# API Specification




## RequestLoginToken

Request
```json
{
  "username": "<username>",
  "password": "<password>",
  "role": "role"
}
```

Response: ReturnAuthenticationToken
```json
{
    "data": "<jwt>",
    "status_code": "<Number: HTTP status code",
    "message": "<String>",
    "processing_time": "<Number: Processing time of the request in ms>",
    "node_respondant": "<NodeID: ID of the node handling the request>"
}
```

## RequestInvalidateLoginToken

Request
```json
{
  "authentication_token": "<jwt>"
  
}
```

Response: ConfirmInvalidateToken
```json
{
    "invalidated_at": "<ISO8601 timestamp>",
    "data": "<Object: requested data>",
    "status_code": "<Number: HTTP status code",
    "message": "<String>",
    "processing_time": "<Number: Processing time of the request in ms>",
    "node_respondant": "<NodeID: ID of the node handling the request>"
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
    "data": {
        "user-id": "<user-id>",
        "created_at": "<ISO8601 timestamp>"
        },
    "status_code": "<Number: HTTP status code",
    "message": "<String>",
    "processing_time": "<Number: Processing time of the request in ms>",
    "node_respondant": "<NodeID: ID of the node handling the request>"
}
```

## RequestAccountReset

Request
```json
{
  "authentication_token": "<jwt>", 
}
```

Response: ConfirmAccountReset 
```json
{
    "data": {
        "reset_code": "<Integer/String>",
        "default-password": "<password>"
        },
    "status_code": "<Number: HTTP status code",
    "message": "<String>",
    "processing_time": "<Number: Processing time of the request in ms>",
    "node_respondant": "<NodeID: ID of the node handling the request>"
}
```
Then the user should write the code sent to its email and that code is compared with the code from ConfirmAccountReset. It they are equal a RequestAccountPasswordUpdate is sent with the default as the old password and the new password is the one the user had written.  

## RequestAccountPasswordUpdate

Request
```json
{
  "authentication_token": "<jwt>",
  "old-password": "<password>",
  "new-password": "<password>"
}
```

Response: ConfirmSetPassword
```json
{
    "data": "<Object: requested data>",
    "status_code": "<Number: HTTP status code",
    "message": "<String>",
    "processing_time": "<Number: Processing time of the request in ms>",
    "node_respondant": "<NodeID: ID of the node handling the request>"
}
```

## ConfirmAccountDeletion

Request: RequestAccountDelete
```json
{
  "authentication_token": "<jwt>"
}
```

Response
```json
{
   "data": "<Object: requested data>",
   "status_code": "<Number: HTTP status code",
   "message": "<String>",
   "processing_time": "<Number: Processing time of the request in ms>",
   "node_respondant": "<NodeID: ID of the node handling the request>"
}
```


## UpdateAccountPrivileges

Request
```json
{
  "authentication_token": "<jwt>",
  "new-privileges": ["<privileges>"]
}
```

Response: ConfirmAccountUpdate
```json
{
    "data": "<Object: requested data>",
    "status_code": "<Number: HTTP status code",
    "message": "<String>",
    "processing_time": "<Number: Processing time of the request in ms>",
    "node_respondant": "<NodeID: ID of the node handling the request>"
}
```

## RequestAccountData

Request
```json
{
  "authentication_token": "<jwt>"
}
```

Response: ReturnAccountInfo
```json
{
    "data": {
        "username": "<username>", 
        "user-email": "<email>", 
        "role": "<role>",
        "last-login": "<ISO8601 timestamp>",
        "created_at": "<ISO8601 timestamp>",
        "updated_at": "<ISO8601 timestamp>"
        },
    "status_code": "<Number: HTTP status code",
    "message": "<String>",
    "processing_time": "<Number: Processing time of the request in ms>",
    "node_respondant": "<NodeID: ID of the node handling the request>"
}
```

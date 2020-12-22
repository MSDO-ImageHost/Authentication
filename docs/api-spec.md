# API Specification

# Header

Request
```json
{
  "jwt":"<jwt token>"
}
```

Response
```json
{
  "status_code": "<status code>", 
  "status_message": "<status message>", 
  "processing_time_ns": "<processing time of the request in nano seconds>"
}
```

# Properties

Request
```json
{
  "ContentType": "application/json"
  "CorrelationId": "<id>"
  "Headers": {"jwt":"<jwt token>"}
}
```

Response
```json
{
  "ContentType": "application/json"
  "CorrelationId": "<id>"
  "Headers": {
    "status_code": "<status code>", 
    "status_message": "<status message>", 
    "processing_time_ns": "<processing time of the request in nano seconds>" 
  }
}
```

The below definitions are just the body of the different requests and respones

## RequestLoginToken

Request
```json
{
  "username": "<username>",
  "password": "<password>",
  "ttl": "<time-in-millis>"
}
```

Response: ReturnAuthenticationToken
```json
{
    "data": "<jwt>"
}
```

## RequestInvalidateLoginToken

Request
```json
{
 
  
}
```

Response: ConfirmInvalidateToken
```json
{
    "data": "<Object: requested data>"
}
```

## RequestAccountCreate

Request
```json
{
  "username": "<username>",
  "user_email": "<email>",
  "password": "<password>",
  "role": "<role>"
}
```

Response: ConfirmAccountCreation
```json
{
    "data": {
        "user-id": "<user-id>",
        "created_at": "<ISO8601 timestamp>"
        }
}
```

## RequestAccountReset

Request
```json
{

}
```

Response: ConfirmAccountReset 
```json
{
    "data": {
        "reset_code": "<Integer/String>",
        "default_password": "<password>"
        }
}
```
Then the user should write the code sent to its email and that code is compared with the code from ConfirmAccountReset. It they are equal a RequestAccountPasswordUpdate is sent with the default as the old password and the new password is the one the user had written.  

## RequestAccountPasswordUpdate

Request
```json
{
  "old_password": "<password>",
  "new_password": "<password>"
}
```

Response: ConfirmSetPassword
```json
{
    "data": null
}
```

## ConfirmAccountDeletion

Request: RequestAccountDelete
```json
{
  "user_id": "<userid>"
}
```

Response
```json
{
   "data": null
}
```


## UpdateAccountPrivileges

Request
```json
{
  "new_role": "<role>",
  "user_id": "<user_id>"
}
```

Response: ConfirmAccountUpdate
```json
{
    "data": null
}
```

## UpdateAccount

Request
```json
{
  "username": "<username>",
  "user_email": "<email>"
  
}
```

Response: ConfirmAccountUpdate
```json
{
    "data": {
              "username": "<username>",
              "user_email": "<email>",
              "updated_at": "<ISO8601 timestamp>"
            }
}
```
If one of the two options should not be changed the JSON request should contain the old info on that key e.g. if the username should not be changed the old username is sent in the JSON. 

## RequestAccountData

Request
```json
{
  "user_id": "<userid>"
}
```

Response: ReturnAccountInfo
```json
{
    "data": {
        "username": "<username>", 
        "user_email": "<email>", 
        "role": "<role>",
        "last_login": "<ISO8601 timestamp>",
        "created_at": "<ISO8601 timestamp>",
        "updated_at": "<ISO8601 timestamp>"
        }
}
```

## RequestBanUser
Request
```json
{
  "user_id": "<userid>",
  "permanent": "<boolean>"
}
```

Response: ConfirmBanUser
```json
{
    "data": null
}
```

## RequestFlagUser
Request
```json
{
  "user_id": "<userid>"
}
```

Response: ConfirmFlagUser
```json
{
    "data": null
}
```

## RequestAllFlagged
Request
```json
{

}
```

Response: ReturnAllFlagged
```json
{
    "data": {
        "users": ["<list of users>"]
        }
}
```

## RequestUsername
Request
```json
{
  "user_id": "<userid>"
}
```

Response: ReturnAllFlagged
```json
{
    "data": "username": "<username>"
}
```

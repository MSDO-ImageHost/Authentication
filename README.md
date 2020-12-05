# Authentication
The service can be run by using docker-compose up. This will start the service in a container, a rabbitmq container and a mySQL container.
It is necessary to add a .env file. The .env files needs to contain:

```bash
RABBITMQ_DEFAULT_USER=<user>
RABBITMQ_DEFAULT_PASS=<password>
MYSQL_ROOT_PASSWORD=<password>
MYSQL_DATABASE=authentication
```


MYSQL_DATABASE needs to be authentication as this is used in the code.

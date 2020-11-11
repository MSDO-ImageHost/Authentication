FROM java:15  
COPY . . 
WORKDIR /src  
RUN javac -cp amqp-client-5.7.1.jar Send.java Receive.java mySQL.java
CMD ["java", "Hello"]  

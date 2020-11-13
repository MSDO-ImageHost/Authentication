FROM openjdk:16-jdk-alpine3.12  
COPY . . 
WORKDIR .  
RUN javac -classpath lib/\* src/*.java
#Change RUN to include JSON jar
CMD ["java", "-cp", ".:\*", "Receive"]
#CMD ["java", "-cp", ".:amqp-client-5.7.1.jar:slf4j-api-1.7.26.jar:slf4j-simple-1.7.26.jar", "Main"] 

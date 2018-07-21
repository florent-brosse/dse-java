# Simple project with DSE (cassandra) driver and Spring

This project use Spring, DSE driver and maven. It creates 2 sessions one for normal queries and another one for SOLR.
`@ConfigurationProperties` provides either plugins or built-in features for code completion in the application.properties files. 
It shoes example with accessor, mapper and prepared statement.

It also use docker for Integration Test.

To launch IT use `mvn verify`

To launch the app (we use the IT profile to create the keyspace, the user table and the SOLR index):
```
mvn docker:start 
mvn spring-boot:run -Dspring-boot.run.profiles=IT
```
go to http://localhost:8080

To stop the app:
```
Control-C to kill `mvn spring-boot:run`
mvn docker:stop 
```

`docker exec -it 9a67c0b21e2f bash` to get bash into the running container

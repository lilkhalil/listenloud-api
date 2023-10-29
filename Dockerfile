FROM openjdk:17-alpine

WORKDIR /app
COPY . /app

RUN [ "./mvnw", "clean", "package" ]

CMD [ "-jar", "/app/target/listenloud-0.0.1-SNAPSHOT.jar" ]
ENTRYPOINT [ "java" ]
EXPOSE 8080
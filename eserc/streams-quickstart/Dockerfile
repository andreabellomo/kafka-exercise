# Creating first container to compile Java code and create jar file
FROM maven:3-openjdk-8 AS build
WORKDIR /app

# Copying pom.xml file to container
COPY pom.xml .

# Downloading dependencies
RUN mvn -f ./pom.xml clean package

COPY src src

RUN mvn -f ./pom.xml package

FROM apache/kafka:latest
ENV PATH /opt/kafka/bin:$PATH
WORKDIR /opt/kafka/
COPY --from=build /app/target/*.jar  /opt/kafka/libs
ENTRYPOINT [ "kafka-run-class.sh"]  
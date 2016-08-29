FROM maven:3.3.9-jdk-8

WORKDIR /usr/src/app
COPY . .

RUN mvn package

ENV TOPIC_NAME "build-contract-test"
ENV NUM_PARTITIONS 1
ENV NUM_REPLICAS 1

# ENTRYPOINT ["jar", "tvf", "target/kafka-topic-client-1.0-SNAPSHOT.jar"]
ENTRYPOINT ["java", "-jar", "target/kafka-topic-client-1.0-SNAPSHOT-jar-with-dependencies.jar"]

# CMD ls -l target


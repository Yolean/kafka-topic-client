FROM solsson/kafka-jre@sha256:06dabfc8cacd0687c8f52c52afd650444fb6d4a8e0b85f68557e6e7a5c71667c \
  as build

ENV GRADLE_VERSION=4.3.1

RUN set -ex; \
  export DEBIAN_FRONTEND=noninteractive; \
  runDeps='curl'; \
  buildDeps='ca-certificates unzip'; \
  apt-get update && apt-get install -y $runDeps $buildDeps --no-install-recommends; \
  \
  cd /opt; \
  curl -SLs -o gradle-$GRADLE_VERSION-bin.zip https://services.gradle.org/distributions/gradle-$GRADLE_VERSION-bin.zip; \
  unzip gradle-$GRADLE_VERSION-bin.zip; \
  rm gradle-$GRADLE_VERSION-bin.zip; \
  ln -s /opt/gradle-$GRADLE_VERSION/bin/gradle /usr/local/bin/gradle; \
  gradle -v

WORKDIR /opt/src/kafka-topic-client
COPY build.gradle ./

RUN set -ex; \
  mkdir -p src/main/java; \
  echo "public class Dummy {}" > src/main/java/Dummy.java; \
  gradle build; \
  rm src/main/java/Dummy.java

COPY . .

RUN set -ex; \
  gradle build

FROM solsson/kafka-jre@sha256:06dabfc8cacd0687c8f52c52afd650444fb6d4a8e0b85f68557e6e7a5c71667c

COPY --from=build /opt/src/kafka-topic-client/build/libs /usr/share/java/kafka-topic-client

ENTRYPOINT [ "java", \
  "-cp", "/usr/share/java/kafka-topic-client/*:/etc/kafka-topic-client/*", \
  "se.yolean.kafka.topic.client.cli.Client" ]

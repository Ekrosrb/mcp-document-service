FROM eclipse-temurin:26-jdk-noble AS build
WORKDIR /workspace
COPY gradlew settings.gradle build.gradle ./
COPY gradle gradle
COPY hl-document-service-IT/build.gradle hl-document-service-IT/
COPY src src
RUN --mount=type=cache,target=/root/.gradle \
    sh ./gradlew --no-daemon bootJar \
    && find build/libs -name '*.jar' ! -name '*-plain.jar' -exec cp {} app.jar \;

FROM eclipse-temurin:26-jre-noble
RUN groupadd --system app && useradd --system --gid app --no-create-home app
WORKDIR /app
COPY --from=build /workspace/app.jar app.jar
USER app
ENV JAVA_OPTS=""
EXPOSE 55535
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]

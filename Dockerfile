# --- Estágio 1: Build (Compilação) ---
# O projeto usa Java 25 (definido em pom.xml).
FROM maven:3.9.11-eclipse-temurin-25 AS build
WORKDIR /app

# Copia o pom.xml e baixa as dependências primeiro para aproveitar o cache do Docker.
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e gera o pacote .jar.
COPY src ./src
RUN mvn clean package -DskipTests

# --- Estágio 2: Runtime (Execução) ---
FROM eclipse-temurin:25-jre-alpine
WORKDIR /app

# Copia apenas o .jar gerado no estágio de build.
COPY --from=build /app/target/*.jar app.jar

# O Render injeta PORT; o padrão 8080 facilita a execução local.
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java -XX:MaxRAMPercentage=60.0 -XX:+UseSerialGC -Xss256k -Dserver.port=${PORT:-8080} -jar app.jar"]
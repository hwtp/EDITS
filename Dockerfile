# Dockerfile multi-stage pour optimiser la taille de l'image

# Stage 1: Build de l'application
FROM gradle:8.5-jdk17-alpine AS builder

# Informations sur l'image
LABEL maintainer="memoires@example.com"
LABEL description="Système de Gestion des Mémoires de Soutenance"
LABEL version="1.0.0"

# Définir le répertoire de travail
WORKDIR /app

# Copier les fichiers de configuration Gradle
COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle/ gradle/
COPY gradlew ./

# Rendre le script gradlew exécutable
RUN chmod +x gradlew

# Télécharger les dépendances (cache Docker layer)
RUN ./gradlew dependencies --no-daemon

# Copier le code source
COPY src/ src/

# Compiler et packager l'application
RUN ./gradlew bootJar --no-daemon --info

# Vérifier que le JAR a été créé
RUN ls -la build/libs/

# Stage 2: Image de production légère
FROM openjdk:17-jdk-alpine AS runtime

# Installer les outils nécessaires
RUN apk add --no-cache \
    curl \
    tzdata \
    fontconfig \
    ttf-dejavu

# Configurer le timezone
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# Créer un utilisateur non-root pour la sécurité
RUN addgroup -g 1001 -S memoires && \
    adduser -S -D -H -u 1001 -h /app -s /sbin/nologin -G memoires -g memoires memoires

# Créer les répertoires nécessaires
RUN mkdir -p /app/uploads /app/generated /app/logs && \
    chown -R memoires:memoires /app

# Définir le répertoire de travail
WORKDIR /app

# Copier le JAR depuis l'étape de build
COPY --from=builder /app/build/libs/*-boot.jar app.jar

# Changer les permissions
RUN chown memoires:memoires app.jar

# Basculer vers l'utilisateur non-root
USER memoires

# Exposer le port de l'application
EXPOSE 8080

# Variables d'environnement par défaut
ENV JAVA_OPTS="-Xmx1024m -Xms512m -XX:+UseG1GC -XX:+UseContainerSupport" \
    SPRING_PROFILES_ACTIVE=docker \
    SERVER_PORT=8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# Point d'entrée de l'application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

# Métadonnées de l'image
LABEL org.opencontainers.image.title="Memoires Soutenance"
LABEL org.opencontainers.image.description="Application de gestion des mémoires de soutenance"
LABEL org.opencontainers.image.version="1.0.0"
LABEL org.opencontainers.image.created="2024-01-01"
LABEL org.opencontainers.image.source="https://github.com/votre-repo/memoires-soutenance"
LABEL org.opencontainers.image.licenses="MIT"
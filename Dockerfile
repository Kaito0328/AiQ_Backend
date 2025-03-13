FROM eclipse-temurin:21-jdk

WORKDIR /workspace
COPY . .
SHELL ["/bin/bash", "-c"]
ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

RUN apt-get update && apt-get install -y \
    curl \
    unzip \
    zip \
    coreutils \
    findutils

# Gradle のセットアップ
RUN curl -sLo gradle.zip https://services.gradle.org/distributions/gradle-8.4-bin.zip \
    && unzip gradle.zip \
    && mv gradle-8.4 /opt/gradle \
    && rm gradle.zip

ENV GRADLE_HOME=/opt/gradle
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

RUN gradle wrapper
# gradlew の実行権限を付与
RUN chmod +x ./gradlew

# 依存関係をキャッシュ
RUN ./gradlew dependencies --no-daemon

# ビルド実行
RUN ./gradlew build --no-daemon

EXPOSE 8080

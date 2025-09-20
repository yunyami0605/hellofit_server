plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.hellofit"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // 자동 리로딩 DevTools
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // ⚙application.yml 자동완성용 Configuration Processor
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // Swagger - Springdoc OpenAPI
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    implementation("org.springframework.boot:spring-boot-starter-security")

    // env 설정 추가
    implementation("me.paulschwarz:spring-dotenv:3.0.0")


    // BOM (버전 관리)
    implementation(platform("io.awspring.cloud:spring-cloud-aws-dependencies:3.4.0"))

    // 모듈
    implementation("io.awspring.cloud:spring-cloud-aws-starter-s3")

    // aws env대신 secret 키 설정
    implementation("io.awspring.cloud:spring-cloud-aws-starter-secrets-manager:3.4.0")

    // 스키마 변경 관리
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

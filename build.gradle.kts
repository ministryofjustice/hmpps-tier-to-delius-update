plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "3.1.1"
  kotlin("plugin.spring") version "1.4.30"
  id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

configurations {
  testImplementation { exclude(group = "org.junit.vintage") }
}

dependencies {
  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  implementation("org.springframework.boot:spring-boot-starter-webflux")

  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
  implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

  implementation("com.google.code.gson:gson:2.8.6")
  implementation("org.springframework:spring-jms")

  implementation(platform("com.amazonaws:aws-java-sdk-bom:1.11.959"))
  implementation("com.amazonaws:amazon-sqs-java-messaging-lib:1.0.8")

  testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.awaitility:awaitility-kotlin:4.0.3")
}

tasks.register("fix") {
  dependsOn(":ktlintFormat")
}


plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "3.3.4"
  kotlin("plugin.spring") version "1.5.0"
  id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
  jacoco
  id("io.gitlab.arturbosch.detekt").version("1.17.1")
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

  implementation("org.springframework.cloud:spring-cloud-aws-messaging")

  testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.mock-server:mockserver-netty:5.11.1")
  testImplementation("org.awaitility:awaitility-kotlin:4.0.3")
}

extra["springCloudVersion"] = "Hoxton.SR8"

dependencyManagement {
  imports {
    mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
  }
}

jacoco {
  toolVersion = "0.8.7"
}

tasks {
  getByName<JacocoReport>("jacocoTestReport") {
    afterEvaluate {
      classDirectories.setFrom(
        files(
          classDirectories.files.map {
            fileTree(it) {
              exclude("**/config/**")
            }
          }
        )
      )
    }
    dependsOn("test")
    reports {
      xml.isEnabled = false
      csv.isEnabled = false
      html.destination = file("$buildDir/reports/coverage")
    }
  }
}

tasks {
  getByName<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
    afterEvaluate {
      classDirectories.setFrom(
        files(
          classDirectories.files.map {
            fileTree(it) {
              exclude("**/config/**")
            }
          }
        )
      )
    }
    dependsOn("jacocoTestReport")
    violationRules {
      rule {
        limit {
          counter = "BRANCH"
          minimum = BigDecimal(0.99)
        }
        limit {
          counter = "COMPLEXITY"
          minimum = BigDecimal(0.88)
        }
      }
    }
  }
  getByName("check") {
    dependsOn(":ktlintCheck", "detekt")
  }

  compileKotlin {
    kotlinOptions {
      jvmTarget = "16"
    }
  }
}

tasks.named("check") {
  dependsOn(":ktlintCheck")
  finalizedBy("jacocoTestCoverageVerification")
}

detekt {
  config = files("src/test/resources/detekt-config.yml")
  buildUponDefaultConfig = true
  ignoreFailures = true
}

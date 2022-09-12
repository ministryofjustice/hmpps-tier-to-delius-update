
plugins {
  id("uk.gov.justice.hmpps.gradle-spring-boot") version "4.5.0-beta"
  kotlin("plugin.spring") version "1.7.10"
  jacoco
  id("io.gitlab.arturbosch.detekt").version("1.20.0")
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

  implementation("com.google.code.gson:gson:2.9.1")

  implementation("uk.gov.justice.service.hmpps:hmpps-sqs-spring-boot-starter:1.1.8")

  testAnnotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.mock-server:mockserver-netty:5.14.0")
  testImplementation("org.awaitility:awaitility-kotlin:4.2.0")
}

jacoco {
  toolVersion = "0.8.8"
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
      xml.required.set(false)
      csv.required.set(false)
      html.outputLocation.set(file("$buildDir/reports/coverage"))
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
      jvmTarget = "18"
    }
  }
}

java {
  toolchain.languageVersion.set(JavaLanguageVersion.of(18))
}

tasks.named("check") {
  dependsOn(":ktlintCheck")
  finalizedBy("jacocoTestCoverageVerification")
}
repositories {
  mavenCentral()
}

detekt {
  config = files("src/test/resources/detekt-config.yml")
  buildUponDefaultConfig = true
  ignoreFailures = false
}

plugins {
	kotlin("jvm") version "2.3.21"
	kotlin("plugin.spring") version "2.3.21"
	id("org.springframework.boot") version "4.1.1"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.3.21"
	jacoco
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	// In-memory database for hermetic tests (no Docker / no external Postgres needed).
	testRuntimeOnly("com.h2database:h2")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// --- Code coverage (JaCoCo) ---
jacoco {
	toolVersion = "0.8.13"
}

// Exclude only the Spring Boot bootstrap `main` launcher, which is exercised by
// the framework, not by application tests.
val coverageExclusions = listOf("com/example/products/ProductsApplicationKt.class")

tasks.test {
	finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)
	reports {
		html.required.set(true)
		xml.required.set(true)
	}
	classDirectories.setFrom(
		files(classDirectories.files.map { dir ->
			fileTree(dir) { exclude(coverageExclusions) }
		})
	)
}

tasks.jacocoTestCoverageVerification {
	dependsOn(tasks.test)
	classDirectories.setFrom(
		files(classDirectories.files.map { dir ->
			fileTree(dir) { exclude(coverageExclusions) }
		})
	)
	violationRules {
		rule {
			limit {
				counter = "LINE"
				minimum = "0.90".toBigDecimal()
			}
		}
	}
}

// `./gradlew check` (and `build`) enforce the coverage threshold.
tasks.check {
	dependsOn(tasks.jacocoTestCoverageVerification)
}

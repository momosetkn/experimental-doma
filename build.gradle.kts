import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

val kotlin_version: String by project
val logback_version: String by project
val koin_version: String by project
val kotest_version: String by project
val jackson_version: String by project
val mockk_version: String by project
val detekt_version: String by project
val testcontainersVersion: String by project

plugins {
    kotlin("jvm") version "1.9.24"
    id("com.github.johnrengelman.shadow") version "8.1.1"

    // annotation
    // https://github.com/google/ksp/issues/1445#issuecomment-1763422067
    id("com.google.devtools.ksp") version "1.9.24-1.0.20"
    kotlin("kapt") version "1.9.24"

    id("org.liquibase.gradle") version "2.2.0"
    kotlin("plugin.serialization") version "1.9.24"
    id("io.gitlab.arturbosch.detekt").version("1.23.6")
    id("org.komapper.gradle") version "1.14.0"
    id("org.domaframework.doma.codegen") version "2.0.0"

    application
}

group = "momosetkn"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        // Only required if using EAP releases
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
}

dependencies {
    // reflection
    implementation("org.jetbrains.kotlin:kotlin-reflect:${kotlin_version}")

    // test
    testImplementation(kotlin("test"))
    testImplementation("io.kotest:kotest-framework-engine-jvm:$kotest_version")
    testImplementation("io.kotest:kotest-runner-junit5:$kotest_version")
    testImplementation("io.mockk:mockk:$mockk_version")

    // jdbc
    implementation("com.mysql:mysql-connector-j:8.1.0")
    // https://github.com/awslabs/aws-mysql-jdbc#mazon-web-services-aws-jdbc-driver-for-mysql
    implementation("software.aws.rds:aws-mysql-jdbc:1.1.9")

    // doma2
    implementation("org.seasar.doma:doma-core:2.56.0")
    kapt("org.seasar.doma:doma-processor:2.56.0")
    // https://mvnrepository.com/artifact/org.seasar.doma/doma-kotlin
    implementation("org.seasar.doma:doma-kotlin:2.56.0")
//    implementation("org.domaframework.doma.codegen:2.0.0")

    // connection pool
    implementation("com.zaxxer:HikariCP:5.0.1")

    // db-migration
    liquibaseRuntime("org.liquibase:liquibase-core:4.23.0")
    liquibaseRuntime("org.yaml:snakeyaml:2.0")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:3.0.3")
    liquibaseRuntime("com.mysql:mysql-connector-j:8.1.0")
    liquibaseRuntime("info.picocli:picocli:4.7.4")

    // jackson
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jackson_version")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jackson_version")

    // log
    implementation("ch.qos.logback:logback-classic:$logback_version")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4") // JSON形式でログを出力するために必要
    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.slf4j:jul-to-slf4j:2.0.9") // Domaなどがjulを使用しているため
    implementation("net.ttddyy:datasource-proxy:1.9")

    // HOCON type-safe Serialization
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-hocon:1.6.0")

    // openapi-generator-cli
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")
    ksp("com.squareup.moshi:moshi-kotlin-codegen:1.15.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")

    val komapperVersion = "1.18.1"
    platform("org.komapper:komapper-platform:$komapperVersion").let {
        implementation(it)
        ksp(it)
    }
    implementation("org.komapper:komapper-starter-jdbc")
    implementation("org.komapper:komapper-dialect-mysql-jdbc")
    implementation("org.komapper:komapper-jdbc")
    ksp("org.komapper:komapper-processor")

    // r2dbc
//    implementation("org.komapper:komapper-dialect-mysql-r2dbc")
//    implementation("org.komapper:komapper-r2dbc")
//    implementation("io.r2dbc:r2dbc-pool")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")

    // di
    implementation("io.insert-koin:koin-core:$koin_version")
    implementation("io.insert-koin:koin-ktor:$koin_version")
    implementation("io.insert-koin:koin-logger-slf4j:$koin_version")
    testImplementation("io.insert-koin:koin-test:$koin_version") {
        // https://github.com/InsertKoinIO/koin/issues/1526
        exclude("org.jetbrains.kotlin", "kotlin-test-junit")
    }

    // testcontainers
    implementation("org.testcontainers:testcontainers:${testcontainersVersion}")
    implementation("org.testcontainers:mysql:${testcontainersVersion}")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

kapt {
    arguments {
        // https://doma.readthedocs.io/ja/latest/annotation-processing/#options
        arg("doma.domain.converters", "momosetkn.infras.doma.UUIDTypeConverterProvider")
    }
}

tasks {
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.freeCompilerArgs += listOf("-opt-in=org.komapper.annotation.KomapperExperimentalAssociation")
        kotlinOptions.freeCompilerArgs += listOf("-Xcontext-receivers")
    }
}

tasks {
    // 複数のJARに、同じインターフェースを実装するクラスが存在する場合、
    // komapper内部で使っているServiceLoader#loadでうまくロードされないため、マージする
    // ここでの例) komapper-dialect-h2-jdbc,komapper-dialect-mysql-jdbc
    withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>() {
        mergeServiceFiles()
        // prevent below error
        // org.apache.tools.zip.Zip64RequiredException: archive contains more than 65535 entries.
        setProperty("zip64", "true")
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    // https://docs.gradle.org/current/dsl/org.gradle.api.tasks.testing.logging.TestLogging.html
    testLogging {
        showCauses = false // 冗長なため
        showStackTraces = true
        showExceptions = true
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        events("failed")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "21"
    kotlinOptions.freeCompilerArgs += listOf("-Xcontext-receivers")
}

application {
    mainClass.set("momosetkn.app.ApplicationKt")
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("com.mysql:mysql-connector-j:8.1.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin")
        classpath("org.jetbrains.kotlin:kotlin-serialization")
    }
}

val jdbcDriver = "com.mysql.cj.jdbc.Driver"

liquibase {
    activities {
        register("main") {
            this.arguments = mapOf(
                "logLevel" to "info",
                "changelogFile" to "src/main/resources/db/changelogs/db.changelog-main.yml",
                "url" to "jdbc:mysql://localhost:3306/dev_tenant_schema",
                "username" to "root",
                "driver" to jdbcDriver,
                // reference
                "referenceUrl" to "jdbc:mysql://localhost:3316/dev_tenant_schema",
                "referenceDriver" to jdbcDriver,
                "referenceUsername" to "root",
            )
        }
        runList = "main"
    }
}

// https://detekt.dev/docs/gettingstarted/gradle/#leveraging-gradles-sourcetask---excluding-and-including-source-files
tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    // _はじまりのpackageは自動生成というルールなので除外
    exclude("**/_*/**")
}

detekt {
    // 並列処理
    parallel = true
    autoCorrect = true

    // Detektの関する設定ファイル
    config.from("$rootDir/config/detekt/detekt.yml")

    // デフォルト設定の上に自分の設定ファイルを適用する
    buildUponDefaultConfig = true

    // レポートファイルに出力されるファイルパスのベースとなる
    // これが設定されてないとレポートファイルのパスは絶対パスになる
    basePath = rootDir.absolutePath
}

// Komapperプラグインに関する設定を行う
komapper {
    generators {
        // 利用するデータベースごとにregisterブロックに適当な名前をつけてブロック内に設定を記述する
        register("mysql") {
            jdbc {
                driver.set("com.mysql.cj.jdbc.Driver")
                url.set("jdbc:mysql://localhost:3306")
                user.set("root")
                password.set("")
            }
            packageName.set("org.komapper.example.postgresql")
            overwriteEntities.set(true)
            overwriteDefinitions.set(true)
            useSelfMapping.set(true)
        }
    }
}

/**
 * https://github.com/domaframework/doma-codegen-plugin/blob/fc026b9b1fba43952f6b498d5eb3962909428fad/codegen/src/main/java/org/seasar/doma/gradle/codegen/desc/KotlinClassResolver.java
 * https://github.com/domaframework/doma-codegen-plugin/blob/d2b67e04f9b42f4db265105e660211e36970a407/codegen/src/main/java/org/seasar/doma/gradle/codegen/desc/EntityPropertyDescFactory.java#L139
 *
 * doma-codegen-pluginで、StringやLocalDateTimeがすべてnullable扱いになるのはなぜ…と思ったら、KotlinClassResolverで未定義はデフォルト値を"null"としていて、デフォルト値が"null"もnullableと判定する挙動のよう…
 * それの回避として、"null"以外のデフォルト値をダミーで設定する
 */
class MyKotlinClassResolver : org.seasar.doma.gradle.codegen.desc.LanguageClassResolver {
    override fun resolve(
        javaClassName: String,
        columnMeta: org.seasar.doma.gradle.codegen.meta.ColumnMeta
    ) = org.seasar.doma.gradle.codegen.desc.LanguageClass(javaClassName, "// NOT_USE_DEFAULT_VALUE")
}

domaCodeGen {
    // gradle domaCodeGenKotlinEntity
    register("kotlin") {
        url = "jdbc:mysql://localhost:3316"
        user = "root"
        password = ""

        languageType = org.seasar.doma.gradle.codegen.desc.LanguageType.KOTLIN
        templateDir = file("src/main/resources/doma_codegen_template/kotlin")
        languageClassResolver = MyKotlinClassResolver()
        entity {
            packageName = "momosetkn.infra.doma.kotlin._entites"
            useListener = false
            useMappedSuperclass = false
        }
//        dao {
//            packageName.set("org.example.dao")
//        }
    }
    // gradle domaCodeGenJavaEntity
    register("java") {
        url = "jdbc:mysql://localhost:3316"
        user = "root"
        password = ""
        languageType = org.seasar.doma.gradle.codegen.desc.LanguageType.JAVA
        templateDir = file("src/main/resources/doma_codegen_template/java")
        entity {
            packageName = "momosetkn.infra.doma.java._entites"
            useListener = false
            useMappedSuperclass = false
        }
//        dao {
//            packageName.set("org.example.dao")
//        }
    }
}

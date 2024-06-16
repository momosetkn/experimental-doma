package momosetkn

import org.slf4j.LoggerFactory
import org.testcontainers.containers.Container

class MySQL {
    private val log = LoggerFactory.getLogger(this.javaClass.name)
    private var container: org.testcontainers.containers.MySQLContainer<*>? = null
    private val startedContainer
        get() =
            requireNotNull(container) {
                "momosetkn.MySQL is not started"
            }
    var jdbcUrl: String? = null

    fun start(): String {
        val mysqlImage = org.testcontainers.utility.DockerImageName.parse("mysql:8.0.34")
        container =
            org.testcontainers.containers.MySQLContainer(mysqlImage)
                .withTmpFs(mapOf("/var/lib/mysql" to "rw,noexec,nosuid,size=200m"))
                .withCommand(
                    "mysqld",
                    "--sql-mode=TRADITIONAL,NO_AUTO_VALUE_ON_ZERO,ONLY_FULL_GROUP_BY",
                    "--skip-log-bin",
                )

        val launchTime =
            kotlin.system.measureTimeMillis {
                startedContainer.start()
            }

        log.info("momosetkn.MySQL started in $launchTime ms")
        jdbcUrl = startedContainer.jdbcUrl.removeSuffix("/test")
        log.info("momosetkn.MySQL jdbcUrl: $jdbcUrl")
        return jdbcUrl!!
    }

    fun executeCommand(vararg args: String): Container.ExecResult {
        return startedContainer.execInContainer(*args)
    }

    fun stop() {
        container?.stop()
        log.info("momosetkn.MySQL stop")
    }

    companion object {
        // testcontainersのデフォルト値
        // データベースを作成したい場合は、ROOT_USERを使う
        const val ROOT_USER = "root"
        const val USER = "test"
        const val PASSWORD = "test"
    }
}

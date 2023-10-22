package momosetkn.infras.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

class ConnectionPoolDatasource(
) : DataSource by momosetkn.infras.database.createHikariDataSource()

private fun createHikariDataSource(): DataSource {
    val hikariConfig = HikariConfig().apply {
        driverClassName = "com.mysql.cj.jdbc.Driver"
        jdbcUrl = "jdbc:mysql://localhost:3316"
        username = "root"
        password = ""
        maximumPoolSize = 2
        isAutoCommit = false
    }
    val hikariDataSource = HikariDataSource(hikariConfig)

    @Suppress("MagicNumber")
    val proxyDataSource: DataSource = ProxyDataSourceBuilder
        .create(hikariDataSource)
        .logQueryBySlf4j()
        .logSlowQueryBySlf4j(3_000, TimeUnit.MILLISECONDS)
        .build()

    return proxyDataSource
}

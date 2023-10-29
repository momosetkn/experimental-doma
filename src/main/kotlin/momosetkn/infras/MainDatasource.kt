package momosetkn.infras

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder
import java.util.concurrent.TimeUnit
import javax.sql.DataSource

class MainDatasource : DataSource by createHikariDataSource()

private fun createHikariDataSource(): DataSource {
    val hikariConfig = HikariConfig().apply {
        driverClassName = "com.mysql.cj.jdbc.Driver"
        jdbcUrl = "jdbc:mysql://localhost:3316/test"
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
        .logSlowQueryBySlf4j(10_000, TimeUnit.MILLISECONDS)
        .build()

    return proxyDataSource
}

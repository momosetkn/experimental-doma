package momosetkn.infras.database.doma

import org.seasar.doma.jdbc.Config
import org.seasar.doma.jdbc.JdbcLogger
import org.seasar.doma.jdbc.dialect.Dialect
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource

class DomaConfig(
    private val dialect: Dialect,
    private val dataSource: LocalTransactionDataSource,
    private val jdbcLogger: JdbcLogger,
) : Config {
    override fun getDataSource(): LocalTransactionDataSource {
        return dataSource
    }

    override fun getDialect(): Dialect {
        return dialect
    }

    override fun getJdbcLogger(): JdbcLogger {
        return jdbcLogger
    }
}

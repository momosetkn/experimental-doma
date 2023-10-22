package momosetkn.infras.database.doma.contexts

import momosetkn.infras.database.doma.DomaConfig
import org.seasar.doma.jdbc.JdbcLogger
import org.seasar.doma.jdbc.criteria.Entityql
import org.seasar.doma.jdbc.criteria.NativeSql
import org.seasar.doma.jdbc.dialect.Dialect
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource
import org.seasar.doma.jdbc.tx.LocalTransactionManager
import org.seasar.doma.jdbc.tx.TransactionManager
import javax.sql.DataSource

class Db(
    private val dialect: Dialect,
    private val hikariDatasource: momosetkn.infras.database.ConnectionPoolDatasource,
    private val jdbcLogger: JdbcLogger,
) {
    fun getContext(): DomaContext {
        val ltds = LocalTransactionDataSource(hikariDatasource)
        val domaConfig = DomaConfig(dialect, ltds, jdbcLogger)
        val transactionManager = LocalTransactionManager(ltds.getLocalTransaction(jdbcLogger))

        return MyDomaContext(
            tx = transactionManager,
            domaConfig = domaConfig,
            entityql = Entityql(domaConfig),
            nativeSql = NativeSql(domaConfig),
            datasource = ltds
        )
    }
}

fun <T : Any> Db.transactionWithContext(
    block: DomaContext.() -> T,
): T {
    val domaContext = getContext()
    return domaContext.tx.required<T> {
        block(domaContext)
    }
}

data class MyDomaContext(
    override val tx: TransactionManager,
    override val domaConfig: DomaConfig,
    override val entityql: Entityql,
    override val nativeSql: NativeSql,
    override val datasource: DataSource,
) : DomaContext()

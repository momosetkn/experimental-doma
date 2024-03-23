package momosetkn.infras.doma.doma.contexts

import momosetkn.infras.doma.doma.DomaConfig
import org.seasar.doma.jdbc.JdbcLogger
import org.seasar.doma.jdbc.criteria.Entityql
import org.seasar.doma.jdbc.criteria.NativeSql
import org.seasar.doma.jdbc.dialect.Dialect
import org.seasar.doma.jdbc.tx.LocalTransactionDataSource
import org.seasar.doma.jdbc.tx.LocalTransactionManager
import org.seasar.doma.jdbc.tx.TransactionManager
import org.seasar.doma.kotlin.jdbc.criteria.KEntityql
import org.seasar.doma.kotlin.jdbc.criteria.KNativeSql
import javax.sql.DataSource

class Db(
    private val dialect: Dialect,
    private val hikariDatasource: DataSource,
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
            kentityql = KEntityql(domaConfig),
            nativeSql = NativeSql(domaConfig),
            knativeSql = KNativeSql(domaConfig),
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
    override val kentityql: KEntityql,
    override val nativeSql: NativeSql,
    override val knativeSql: KNativeSql,
    override val datasource: DataSource,
) : DomaContext()

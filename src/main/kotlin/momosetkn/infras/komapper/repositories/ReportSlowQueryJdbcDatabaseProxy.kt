package momosetkn.infras.komapper.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import momosetkn.app.getLog
import org.komapper.core.dsl.query.Query
import org.komapper.core.dsl.query.QueryScope
import org.komapper.jdbc.JdbcDataFactory
import org.komapper.jdbc.JdbcDatabase
import org.komapper.jdbc.JdbcDatabaseConfig
import org.komapper.tx.core.TransactionAttribute
import org.komapper.tx.core.TransactionOperator
import org.komapper.tx.core.TransactionProperty

class ReportSlowQueryJdbcDatabaseProxy(
    private val original: JdbcDatabase
) : JdbcDatabase {
    private val log = getLog()

    override val config: JdbcDatabaseConfig
        get() = original.config
    override val dataFactory: JdbcDataFactory
        get() = original.dataFactory

    override fun <T> runQuery(query: Query<T>): T {
        val slowQueryError = RuntimeException("slow query found. threshold-time is $SLOW_QUERY_THRESHOLD_TIME")
        val job = CoroutineScope(Dispatchers.IO).launch {
            delay(SLOW_QUERY_THRESHOLD_TIME)
            log.warn("completed delay", slowQueryError)
        }
        return original.runQuery(query).also {
            job.cancel()
        }
    }

    override fun <T> runQuery(block: QueryScope.() -> Query<T>): T {
        val slowQueryError = RuntimeException("slow query found. threshold-time is $SLOW_QUERY_THRESHOLD_TIME")
        val job = CoroutineScope(Dispatchers.IO).launch {
            delay(SLOW_QUERY_THRESHOLD_TIME)
            log.warn("completed delay", slowQueryError)
        }
        return original.runQuery(block).also {
            job.cancel()
        }
    }

    override fun <R> withTransaction(
        transactionAttribute: TransactionAttribute,
        transactionProperty: TransactionProperty,
        block: (TransactionOperator) -> R
    ): R {
        return original.withTransaction(transactionAttribute, transactionProperty, block)
    }

}

private const val SLOW_QUERY_THRESHOLD_TIME = 1_000L

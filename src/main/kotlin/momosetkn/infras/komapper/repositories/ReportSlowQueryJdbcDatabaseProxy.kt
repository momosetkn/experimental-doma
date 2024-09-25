package momosetkn.infras.komapper.repositories

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import momosetkn.app.getLog
import org.komapper.core.dsl.query.Query
import org.komapper.core.dsl.query.QueryScope
import org.komapper.jdbc.JdbcDatabase

class ReportSlowQueryJdbcDatabaseProxy(
    private val original: JdbcDatabase
) : JdbcDatabase by original {
    private val log = getLog()

    override fun <T> runQuery(query: Query<T>): T {
        val slowQueryError = RuntimeException("slow query found. threshold-time is $SLOW_QUERY_THRESHOLD_TIME")
        val job = CoroutineScope(Dispatchers.IO).launch {
            delay(SLOW_QUERY_THRESHOLD_TIME)
            log.warn("slow query found", slowQueryError)
        }
        return original.runQuery(query).also {
            job.cancel()
        }
    }

    override fun <T> runQuery(block: QueryScope.() -> Query<T>): T {
        val slowQueryError = RuntimeException("slow query found. threshold-time is $SLOW_QUERY_THRESHOLD_TIME")
        val job = CoroutineScope(Dispatchers.IO).launch {
            delay(SLOW_QUERY_THRESHOLD_TIME)
            log.warn("slow query found", slowQueryError)
        }
        return original.runQuery(block).also {
            job.cancel()
        }
    }

}

private const val SLOW_QUERY_THRESHOLD_TIME = 1_000L

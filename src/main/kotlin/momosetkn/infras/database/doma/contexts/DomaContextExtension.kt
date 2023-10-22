package momosetkn.infras.database.doma.contexts

import java.sql.Connection

object DomaContextExtension {
    fun <T> DomaContext.transaction(block: DomaContext.() -> T): T {
        return tx.required<T> {
            block()
        }
    }

    fun DomaContext.getConnection(): Connection {
        return datasource.getConnection()
    }
}

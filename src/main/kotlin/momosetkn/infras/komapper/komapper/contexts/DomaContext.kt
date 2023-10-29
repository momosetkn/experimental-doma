package momosetkn.infras.komapper.komapper.contexts

import org.komapper.jdbc.JdbcDatabase

/**
 * Komapperの機能を提供するコンテキスト
 */
open class KomapperContext {
    open val db: JdbcDatabase
        get() {
            throw NotImplementedError()
        }
}

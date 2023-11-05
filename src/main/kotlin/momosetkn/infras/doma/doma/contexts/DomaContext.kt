package momosetkn.infras.doma.doma.contexts

import momosetkn.infras.doma.doma.DomaConfig
import org.seasar.doma.jdbc.criteria.Entityql
import org.seasar.doma.jdbc.criteria.NativeSql
import org.seasar.doma.jdbc.tx.TransactionManager
import org.seasar.doma.kotlin.jdbc.criteria.KEntityql
import org.seasar.doma.kotlin.jdbc.criteria.KNativeSql
import javax.sql.DataSource

/**
 * Domaの機能を提供するコンテキスト
 */
open class DomaContext {
    open val tx: TransactionManager
        get() {
            throw NotImplementedError()
        }
    open val domaConfig: DomaConfig
        get() {
            throw NotImplementedError()
        }
    open val entityql: Entityql
        get() {
            throw NotImplementedError()
        }
    open val nativeSql: NativeSql
        get() {
            throw NotImplementedError()
        }

    open val kentityql: KEntityql
        get() {
            throw NotImplementedError()
        }
    open val knativeSql: KNativeSql
        get() {
            throw NotImplementedError()
        }
    open val datasource: DataSource
        get() {
            throw NotImplementedError()
        }
}

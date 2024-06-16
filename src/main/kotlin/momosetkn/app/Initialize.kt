package momosetkn.app

import momosetkn.infras.doma.doma.contexts.transactionWithContext
import momosetkn.infras.doma.repositories.DatabaseDaoImpl
import org.koin.core.component.get

fun initialize() {
    ConfigureLog.configLog()

    val log = getLog()

    log.info("hello world")

    val db = Koin.get<momosetkn.infras.doma.doma.contexts.Db>()

    db.transactionWithContext {
        DatabaseDaoImpl(domaConfig).initialize()
    }
}

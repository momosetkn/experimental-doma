package momosetkn.app

import momosetkn.domain.Company
import momosetkn.infras.doma.doma.contexts.Db
import momosetkn.infras.doma.doma.contexts.transactionWithContext
import momosetkn.infras.doma.repositories.CompaniesRepository
import momosetkn.infras.doma.repositories.DatabaseDaoImpl
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

/**
 * エントリーポイント
 */
fun main() {
    ConfigureLog.configLog()

    val log = getLog()

    log.info("hello world")

    startKoin { modules(appDependencies) }
    val db = Koin.get<Db>()
    val companiesRepository = Koin.get<CompaniesRepository>()

    val list = db.transactionWithContext {
        DatabaseDaoImpl(domaConfig).switchDb()

        val items = listOf(
            Koin.get<Company>(),
            Koin.get<Company>(),
            Koin.get<Company>(),
            Koin.get<Company>(),
            Koin.get<Company>(),
        )

        companiesRepository.createList(items)

        companiesRepository.findListALL()
    }
    log.info("companies: {}", list)
}

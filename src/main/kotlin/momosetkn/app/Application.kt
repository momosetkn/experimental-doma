package momosetkn.app

import momosetkn.domain.Company
import momosetkn.infras.database.doma.contexts.Db
import momosetkn.infras.database.doma.contexts.transactionWithContext
import momosetkn.infras.repositories.CompaniesRepository
import momosetkn.infras.repositories.DatabaseDaoImpl
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

/**
 * エントリーポイント
 */
fun main(args: Array<String>) {
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

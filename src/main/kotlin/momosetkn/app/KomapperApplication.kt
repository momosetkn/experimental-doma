package momosetkn.app

import momosetkn.domain.Company
import momosetkn.infras.komapper.komapper.contexts.Db
import momosetkn.infras.komapper.repositories.CompaniesRepository
import momosetkn.infras.komapper.repositories.DatabaseDao
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
    val databaseDao = Koin.get<DatabaseDao>()

    val list = db.getContext().db.withTransaction {
        databaseDao.switchDb()

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

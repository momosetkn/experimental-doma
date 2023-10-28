package momosetkn.app

import momosetkn.domain.Company
import momosetkn.domain.Employee
import momosetkn.domain.News
import momosetkn.domain.Product
import momosetkn.domain.ProductDetail
import momosetkn.infras.database.MainDatasource
import momosetkn.infras.database.MigrateDatasource
import momosetkn.infras.database.doma.contexts.Db
import momosetkn.infras.repositories.CompaniesRepository
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.seasar.doma.jdbc.JdbcLogger
import org.seasar.doma.jdbc.dialect.Dialect
import org.seasar.doma.jdbc.dialect.MysqlDialect
import org.seasar.doma.jdbc.momosetkn.Slf4jJdbcLogger
import java.time.LocalDateTime
import java.util.*

val infrasDependencies = module {
    // Doma
    single { Db(get(), MainDatasource(), get()) }
    single(named("MIGRATE_DB")) { Db(get(), MigrateDatasource(), get()) }
    single<Dialect> { MysqlDialect() }
    single<JdbcLogger> { Slf4jJdbcLogger() }
}
val repositoryDependencies = module {
    single { CompaniesRepository() }
}
val dummyDataDependencies = module {
    factory {
        Company(
            id = get(),
            name = get(),
            employees = get(named("List<Employee>")),
            news = get(named("List<News><")),
            products = get(named("<Product>")),
            updatedBy = get(),
            updatedAt = get(),
            createdBy = get(),
            createdAt = get(),
        )
    }
    factory {
        Product(
            id = get(),
            name = get(),
            details = get(named("List<ProductDetail>")),
            updatedBy = get(),
            updatedAt = get(),
            createdBy = get(),
            createdAt = get(),
        )
    }
    factoryOf(::News)
    factoryOf(::Employee)
    factoryOf(::ProductDetail)
    factory<List<News>>(named("List<News><")) { listOf(get(), get(), get()) }
    factory<List<Product>>(named("<Product>")) { listOf(get(), get(), get()) }
    factory<List<Employee>>(named("List<Employee>")) { listOf(get(), get(), get()) }
    factory<List<ProductDetail>>(named("List<ProductDetail>")) { listOf(get(), get(), get()) }
    factory<String> { UUID.randomUUID().toString() }
    factory<LocalDateTime> {
        LocalDateTime.now().plusDays((Math.random() * 100).toLong())
    }
}

val appDependencies =
    listOf(
        infrasDependencies,
        repositoryDependencies,
        dummyDataDependencies,
    )

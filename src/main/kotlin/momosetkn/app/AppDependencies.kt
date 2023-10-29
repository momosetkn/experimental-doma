package momosetkn.app

import momosetkn.domain.Company
import momosetkn.domain.Employee
import momosetkn.domain.News
import momosetkn.domain.Product
import momosetkn.domain.ProductDetail
import momosetkn.infras.MainDatasource
import momosetkn.infras.MigrateDatasource
import org.koin.core.module.dsl.factoryOf
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.time.LocalDateTime
import java.util.*

val infrasDependencies = module {
    // Doma
    single { momosetkn.infras.doma.doma.contexts.Db(get(), MainDatasource(), get()) }
    single(named("MIGRATE_DB")) { momosetkn.infras.doma.doma.contexts.Db(get(), MigrateDatasource(), get()) }
    single<org.seasar.doma.jdbc.dialect.Dialect> { org.seasar.doma.jdbc.dialect.MysqlDialect() }
    single<org.seasar.doma.jdbc.JdbcLogger> { org.seasar.doma.jdbc.momosetkn.Slf4jJdbcLogger() }
    // komapper
    single { momosetkn.infras.komapper.komapper.contexts.Db(get(), MainDatasource()) }
    single { get<momosetkn.infras.komapper.komapper.contexts.Db>().jdbcDatabase }
    single(named("MIGRATE_DB")) { momosetkn.infras.komapper.komapper.contexts.Db(get(), MigrateDatasource()) }
    single<org.komapper.jdbc.JdbcDialect> { org.komapper.jdbc.JdbcDialects.get("mysql") }
}
val repositoryDependencies = module {
    single { momosetkn.infras.doma.repositories.CompaniesRepository() }
    single { momosetkn.infras.komapper.repositories.CompaniesRepository(get()) }
    single { momosetkn.infras.komapper.repositories.DatabaseDao(get()) }
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

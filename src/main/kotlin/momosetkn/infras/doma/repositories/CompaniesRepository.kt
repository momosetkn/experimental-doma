package momosetkn.infras.doma.repositories

import momosetkn.domain.Company
import momosetkn.infras.doma.doma.contexts.DomaContext
import momosetkn.infras.doma.entities.Extension
import momosetkn.infras.doma.entities.Extension.fetchAndSelectinload
import momosetkn.infras.doma.entities.InfraCompanies
import momosetkn.infras.doma.entities.InfraCompaniesJavaRecord
import momosetkn.infras.doma.entities.InfraCompaniesJavaRecord_
import momosetkn.infras.doma.entities.converter.CompanyConverter.toInfra
import momosetkn.infras.doma.entities.converter.CompanyConverter.toModel
import momosetkn.infras.doma.entities.meta.Meta
import momosetkn.infras.doma.entities.meta.companies
import momosetkn.infras.doma.entities.meta.employees
import momosetkn.infras.doma.entities.meta.news
import momosetkn.infras.doma.entities.meta.productDetails
import momosetkn.infras.doma.entities.meta.products
import org.seasar.doma.kotlin.jdbc.criteria.statement.KEntityqlSelectStarting

class CompaniesRepository {
    context(DomaContext)
    @OptIn(Extension.ExperimentalApi::class)
    fun findListALL(): List<Company> {
        val items = entityql.from(Meta.companies)
            .fetchAndSelectinload {
                hasMany(
                    Meta.news,
                    Meta.companies.id to Meta.news.companyId,
                ) { f, s ->
                    f.news.addAll(s)
                }
                hasMany(
                    Meta.products,
                    Meta.companies.id to Meta.products.companyId,
                ) { f, s ->
                    f.products.addAll(s)
                }
                hasMany(
                    Meta.employees,
                    Meta.companies.id to Meta.employees.companyId,
                ) { f, s ->
                    f.employees.addAll(s)
                }
            }

        return items.map { it.toModel() }
    }

    context(DomaContext)
    @OptIn(Extension.ExperimentalApi::class)
    fun kfindListALL(): KEntityqlSelectStarting<InfraCompanies> {
        return kentityql.from(Meta.companies)
    }

    context(DomaContext)
    @OptIn(Extension.ExperimentalApi::class)
    fun findListALL_JavaRecord(): List<InfraCompaniesJavaRecord> {
        val c = InfraCompaniesJavaRecord_()
        return kentityql.from(c).fetch()
    }

    context(DomaContext)
    @OptIn(Extension.ExperimentalApi::class)
    fun insert_JavaRecord(items: List<InfraCompaniesJavaRecord>) {
        val c = InfraCompaniesJavaRecord_()
        kentityql.insert(c, items).execute()
    }

    context(DomaContext)
    fun createList(companies: List<Company>) {
        val items = companies.map { it.toInfra() }
        entityql.insert(Meta.companies, items)
        entityql.insert(Meta.news, items.flatMap { it.news })
        entityql.insert(Meta.products, items.flatMap { it.products })
        entityql.insert(Meta.productDetails, items.flatMap { it.products }.flatMap { it.productDetails })
        entityql.insert(Meta.employees, items.flatMap { it.employees })
    }
}

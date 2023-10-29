package momosetkn.infras.doma.repositories

import momosetkn.domain.Company
import momosetkn.infras.doma.doma.contexts.DomaContext
import momosetkn.infras.doma.entities.Extension
import momosetkn.infras.doma.entities.Extension.fetchAndSelectinload
import momosetkn.infras.doma.entities.converter.CompanyConverter.toInfra
import momosetkn.infras.doma.entities.converter.CompanyConverter.toModel
import momosetkn.infras.doma.entities.meta.Meta
import momosetkn.infras.doma.entities.meta.companies
import momosetkn.infras.doma.entities.meta.employees
import momosetkn.infras.doma.entities.meta.news
import momosetkn.infras.doma.entities.meta.productDetails
import momosetkn.infras.doma.entities.meta.products

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
    fun createList(companies: List<Company>) {
        val items = companies.map { it.toInfra() }
        entityql.insert(Meta.companies, items)
        entityql.insert(Meta.news, items.flatMap { it.news })
        entityql.insert(Meta.products, items.flatMap { it.products })
        entityql.insert(Meta.productDetails, items.flatMap { it.products }.flatMap { it.productDetails })
        entityql.insert(Meta.employees, items.flatMap { it.employees })
    }
}

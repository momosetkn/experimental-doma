package momosetkn.infras.entities.converter

import momosetkn.domain.Company
import momosetkn.domain.Employee
import momosetkn.domain.News
import momosetkn.domain.Product
import momosetkn.infras.entities.InfraCompanies
import momosetkn.infras.entities.InfraEmployees
import momosetkn.infras.entities.InfraNews
import momosetkn.infras.entities.InfraProducts

object CompanyConverter {
    fun InfraCompanies.toModel(): Company {
        return Company(
            id = id,
            name = name,
            news = news.map { it.toModel() },
            products = products.map { it.toModel() },
            employees = employees.map { it.toModel() },
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        )
    }

    fun InfraNews.toModel(): News {
        return News(
            id = id,
            name = name,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        )
    }

    fun InfraProducts.toModel(): Product {
        return Product(
            id = id,
            name = name,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        )
    }

    fun InfraEmployees.toModel(): Employee {
        return Employee(
            id = id,
            name = name,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        )
    }

    fun Company.toInfra(): InfraCompanies {
        return InfraCompanies(
            id = id,
            name = name,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        ).also { company ->
            company.news.addAll(news.map { it.toInfra(id) })
            company.products.addAll(products.map { it.toInfra(id) })
            company.employees.addAll(employees.map { it.toInfra(id) })
        }
    }

    fun News.toInfra(companyId: String): InfraNews {
        return InfraNews(
            id = id,
            name = name,
            companyId = companyId,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        )
    }

    fun Product.toInfra(companyId: String): InfraProducts {
        return InfraProducts(
            id = id,
            name = name,
            companyId = companyId,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        )
    }

    fun Employee.toInfra(companyId: String): InfraEmployees {
        return InfraEmployees(
            id = id,
            name = name,
            companyId = companyId,
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        )
    }
}

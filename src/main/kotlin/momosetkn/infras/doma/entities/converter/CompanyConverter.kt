package momosetkn.infras.doma.entities.converter

import momosetkn.domain.Company
import momosetkn.domain.Employee
import momosetkn.domain.News
import momosetkn.domain.Product
import momosetkn.domain.ProductDetail
import momosetkn.infras.doma.entities.InfraCompanies
import momosetkn.infras.doma.entities.InfraEmployees
import momosetkn.infras.doma.entities.InfraNews
import momosetkn.infras.doma.entities.InfraProductDetails
import momosetkn.infras.doma.entities.InfraProducts
import java.util.UUID

object CompanyConverter {
    fun InfraCompanies.toModel(): Company {
        return Company(
            id = id,
            uuid = UUID.fromString(uuid),
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
            details = productDetails.map { it.toModel() },
            updatedBy = updatedBy,
            updatedAt = updatedAt,
            createdBy = createdBy,
            createdAt = createdAt,
        )
    }

    fun InfraProductDetails.toModel(): ProductDetail {
        return ProductDetail(
            id = id,
            description = description,
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
            uuid = uuid.toString(),
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
        ).also { products ->
            products.productDetails.addAll(details.map { it.toInfra(id) })
        }
    }

    fun ProductDetail.toInfra(productId: String): InfraProductDetails {
        return InfraProductDetails(
            id = id,
            description = description,
            productId = productId,
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

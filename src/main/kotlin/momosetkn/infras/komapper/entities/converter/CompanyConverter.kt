package momosetkn.infras.komapper.entities.converter

import momosetkn.domain.Company
import momosetkn.domain.Employee
import momosetkn.domain.News
import momosetkn.domain.Product
import momosetkn.domain.ProductDetail
import momosetkn.infras.komapper.entities.InfraCompanies
import momosetkn.infras.komapper.entities.InfraEmployees
import momosetkn.infras.komapper.entities.InfraNews
import momosetkn.infras.komapper.entities.InfraProductDetails
import momosetkn.infras.komapper.entities.InfraProducts
import org.seasar.doma.jdbc.criteria.tuple.Tuple5

object CompanyConverter {
    fun List<InfraCompanies>.toModels(
        news: List<InfraNews>,
        products: List<InfraProducts>,
        productDetails: List<InfraProductDetails>,
        employees: List<InfraEmployees>,
    ): List<Company> {
        val newsMap = news.groupBy { it.companyId }
        val productsMap = products.groupBy { it.companyId }
        val productDetailsMap = productDetails.groupBy { it.productId }
        val employeesMap = employees.groupBy { it.companyId }

        return map {
            val individualProductDetails = products.flatMap {
                productDetailsMap.getOrDefault(it.id, emptyList())
            }

            it.toModel(
                news = newsMap.getOrDefault(it.id, emptyList()),
                products = productsMap.getOrDefault(it.id, emptyList()),
                productDetails = individualProductDetails,
                employees = employeesMap.getOrDefault(it.id, emptyList()),
            )
        }
    }

    fun InfraCompanies.toModel(
        news: List<InfraNews>,
        products: List<InfraProducts>,
        productDetails: List<InfraProductDetails>,
        employees: List<InfraEmployees>,
    ): Company {
        val productDetailsMap = productDetails.groupBy { it.productId }

        return Company(
            id = id,
            name = name,
            news = news.map { it.toModel() },
            products = products.map { it.toModel(productDetailsMap.getOrDefault(it.id, emptyList())) },
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

    fun InfraProducts.toModel(productDetails: List<InfraProductDetails>): Product {
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

    fun Company.toInfra():
        Tuple5<
            InfraCompanies,
            List<InfraNews>,
            List<InfraProducts>,
            List<InfraProductDetails>,
            List<InfraEmployees>,
            > {
        val p = products.map { it.toInfra(id) }
        return Tuple5(
            InfraCompanies(
                id = id,
                name = name,
                updatedBy = updatedBy,
                updatedAt = updatedAt,
                createdBy = createdBy,
                createdAt = createdAt,
            ),
            news.map { it.toInfra(id) },
            p.map { it.first },
            p.flatMap { it.second },
            employees.map { it.toInfra(id) },
        )
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

    fun Product.toInfra(companyId: String): Pair<InfraProducts, List<InfraProductDetails>> {
        return Pair(
            InfraProducts(
                id = id,
                name = name,
                companyId = companyId,
                updatedBy = updatedBy,
                updatedAt = updatedAt,
                createdBy = createdBy,
                createdAt = createdAt,
            ),
            details.map { it.toInfra(id) },
        )
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

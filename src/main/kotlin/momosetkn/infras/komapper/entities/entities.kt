package momosetkn.infras.komapper.entities

import org.komapper.annotation.KomapperAggregateRoot
import org.komapper.annotation.KomapperColumn
import org.komapper.annotation.KomapperEntity
import org.komapper.annotation.KomapperExperimentalAssociation
import org.komapper.annotation.KomapperId
import org.komapper.annotation.KomapperOneToMany
import org.komapper.annotation.KomapperTable
import java.util.UUID

@OptIn(KomapperExperimentalAssociation::class)
@KomapperEntity
@KomapperTable(name = "companies")
@KomapperAggregateRoot(navigator = "companies")
@KomapperOneToMany(InfraNews::class)
@KomapperOneToMany(InfraProducts::class, navigator = "products")
@KomapperOneToMany(InfraEmployees::class)
data class InfraCompanies(
    @KomapperId
    @KomapperColumn(name = "id")
    val id: String,
    @KomapperColumn(name = "uuid")
    val uuid: UUID,
    @KomapperColumn(name = "name") val name: String,

    @KomapperColumn(name = "updated_by") val updatedBy: String,
    @KomapperColumn(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @KomapperColumn(name = "created_by") val createdBy: String,
    @KomapperColumn(name = "created_at") val createdAt: java.time.LocalDateTime,
)

@KomapperEntity
@KomapperTable(name = "news")
data class InfraNews(
    @KomapperId
    @KomapperColumn(name = "id")
    val id: String,
    @KomapperColumn(name = "name") val name: String,
    @KomapperColumn(name = "company_id") val companyId: String,

    @KomapperColumn(name = "updated_by") val updatedBy: String,
    @KomapperColumn(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @KomapperColumn(name = "created_by") val createdBy: String,
    @KomapperColumn(name = "created_at") val createdAt: java.time.LocalDateTime,
)

@OptIn(KomapperExperimentalAssociation::class)
@KomapperEntity
@KomapperTable(name = "products")
@KomapperOneToMany(InfraProductDetails::class)
data class InfraProducts(
    @KomapperId
    @KomapperColumn(name = "id")
    val id: String,
    @KomapperColumn(name = "name") val name: String,
    @KomapperColumn(name = "company_id") val companyId: String,

    @KomapperColumn(name = "updated_by") val updatedBy: String,
    @KomapperColumn(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @KomapperColumn(name = "created_by") val createdBy: String,
    @KomapperColumn(name = "created_at") val createdAt: java.time.LocalDateTime,
)

@KomapperEntity
@KomapperTable(name = "product_details")
data class InfraProductDetails(
    @KomapperId
    @KomapperColumn(name = "id")
    val id: String,
    @KomapperColumn(name = "description") val description: String,
    @KomapperColumn(name = "product_id") val productId: String,

    @KomapperColumn(name = "updated_by") val updatedBy: String,
    @KomapperColumn(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @KomapperColumn(name = "created_by") val createdBy: String,
    @KomapperColumn(name = "created_at") val createdAt: java.time.LocalDateTime,
)

@KomapperEntity
@KomapperTable(name = "employees")
data class InfraEmployees(
    @KomapperId
    @KomapperColumn(name = "id")
    val id: String,
    @KomapperColumn(name = "name") val name: String,
    @KomapperColumn(name = "company_id") val companyId: String,

    @KomapperColumn(name = "updated_by") val updatedBy: String,
    @KomapperColumn(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @KomapperColumn(name = "created_by") val createdBy: String,
    @KomapperColumn(name = "created_at") val createdAt: java.time.LocalDateTime,
)

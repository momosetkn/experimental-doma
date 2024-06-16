package momosetkn.infras.doma.entities

import org.seasar.doma.Column
import org.seasar.doma.Entity
import org.seasar.doma.Id
import org.seasar.doma.Metamodel
import org.seasar.doma.Table
import org.seasar.doma.Transient


@Entity(immutable = true, metamodel = Metamodel())
@Table(name = "companies")
data class InfraCompanies(
    @Id @Column(name = "id") val id: String,
    @Column(name = "uuid") val uuid: java.util.UUID,
    @Column(name = "name") val name: String,

    @Column(name = "updated_by") val updatedBy: String,
    @Column(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @Column(name = "created_by") val createdBy: String,
    @Column(name = "created_at") val createdAt: java.time.LocalDateTime,
) {
    @Transient
    val news: ArrayList<InfraNews> = ArrayList()

    @Transient
    val products: ArrayList<InfraProducts> = ArrayList()

    @Transient
    val employees: ArrayList<InfraEmployees> = ArrayList()
}

@Entity(immutable = true, metamodel = Metamodel())
@Table(name = "news")
data class InfraNews(
    @Id @Column(name = "id") val id: String,
    @Column(name = "name") val name: String,
    @Column(name = "company_id") val companyId: String,

    @Column(name = "updated_by") val updatedBy: String,
    @Column(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @Column(name = "created_by") val createdBy: String,
    @Column(name = "created_at") val createdAt: java.time.LocalDateTime,
)

@Entity(immutable = true, metamodel = Metamodel())
@Table(name = "products")
data class InfraProducts(
    @Id @Column(name = "id") val id: String,
    @Column(name = "name") val name: String,
    @Column(name = "company_id") val companyId: String,

    @Column(name = "updated_by") val updatedBy: String,
    @Column(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @Column(name = "created_by") val createdBy: String,
    @Column(name = "created_at") val createdAt: java.time.LocalDateTime,
) {

    @Transient
    val productDetails: ArrayList<InfraProductDetails> = ArrayList()
}

@Entity(immutable = true, metamodel = Metamodel())
@Table(name = "product_details")
data class InfraProductDetails(
    @Id @Column(name = "id") val id: String,
    @Column(name = "description") val description: String,
    @Column(name = "product_id") val productId: String,

    @Column(name = "updated_by") val updatedBy: String,
    @Column(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @Column(name = "created_by") val createdBy: String,
    @Column(name = "created_at") val createdAt: java.time.LocalDateTime,
)

@Entity(immutable = true, metamodel = Metamodel())
@Table(name = "employees")
data class InfraEmployees(
    @Id @Column(name = "id") val id: String,
    @Column(name = "name") val name: String,
    @Column(name = "company_id") val companyId: String,

    @Column(name = "updated_by") val updatedBy: String,
    @Column(name = "updated_at") val updatedAt: java.time.LocalDateTime,
    @Column(name = "created_by") val createdBy: String,
    @Column(name = "created_at") val createdAt: java.time.LocalDateTime,
)
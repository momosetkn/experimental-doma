package momosetkn.infras.komapper.repositories

import momosetkn.domain.Company
import momosetkn.infras.komapper.entities.InfraCompanies
import momosetkn.infras.komapper.entities.converter.CompanyConverter.toInfra
import momosetkn.infras.komapper.entities.converter.CompanyConverter.toModel
import momosetkn.infras.komapper.entities.converter.CompanyConverter.toModels
import momosetkn.infras.komapper.entities.infraCompanies
import momosetkn.infras.komapper.entities.infraEmployees
import momosetkn.infras.komapper.entities.infraNews
import momosetkn.infras.komapper.entities.infraProductDetails
import momosetkn.infras.komapper.entities.infraProducts
import momosetkn.infras.komapper.entities.products
import org.komapper.core.dsl.Meta
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.context.SelectContext
import org.komapper.core.dsl.element.Relationship
import org.komapper.core.dsl.expression.ColumnExpression
import org.komapper.core.dsl.expression.OnDeclaration
import org.komapper.core.dsl.expression.Operand
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.operator.columnExpression
import org.komapper.core.dsl.operator.countDistinct
import org.komapper.core.dsl.query.EntitySelectQuery
import org.komapper.core.dsl.scope.FilterScope
import org.komapper.jdbc.JdbcDatabase
import java.time.LocalDateTime

class CompaniesRepository(
    private val db: JdbcDatabase,
) {
    fun findListALL(): List<Company> {
        val companies = run {
            val mainQuery = QueryDsl.from(Meta.infraCompanies)
            db.runQuery(mainQuery)
        }

        val companyIds = companies.map { it.id }
        val news = run {
            val newsQuery = QueryDsl.from(Meta.infraNews)
                .where { Meta.infraNews.companyId inList companyIds }
            db.runQuery(newsQuery)
        }
        val products = run {
            val productsQuery = QueryDsl.from(Meta.infraProducts)
                .where { Meta.infraProducts.companyId inList companyIds }
            db.runQuery(productsQuery)
        }
        val employees = run {
            val employeesQuery = QueryDsl.from(Meta.infraEmployees)
                .where { Meta.infraEmployees.companyId inList companyIds }
            db.runQuery(employeesQuery)
        }

        val productDetails = run {
            val productDetailsQuery = QueryDsl.from(Meta.infraProductDetails)
                .where { Meta.infraProductDetails.productId inList products.map { it.id } }
            db.runQuery { productDetailsQuery }
        }

        return companies.toModels(
            news = news,
            products = products,
            productDetails = productDetails,
            employees = employees,
        )
    }

    fun findListALL2(): List<Company> {
        val mainQuery = QueryDsl.from(Meta.infraCompanies).includeAll()

        val companieStore = db.runQuery {
            mainQuery
        }

        val companiesIds = companieStore[Meta.infraCompanies].map { it.id }

        val newsQuery = QueryDsl.from(Meta.infraCompanies)
            .innerJoin(Meta.infraNews) { Meta.infraCompanies.id eq Meta.infraNews.companyId }
            .where { Meta.infraNews.companyId inList companiesIds }.includeAll()
        val productsQuery = QueryDsl.from(Meta.infraProducts)
            .where { Meta.infraProducts.companyId inList companiesIds }.includeAll()
//        val productDetailsQuery = QueryDsl.from(Meta.infraProductDetails)
//            .where { Meta.infraProducts.companyId inList companiesIds }.includeAll()
        val employeesQuery = QueryDsl.from(Meta.infraEmployees)
            .where { Meta.infraEmployees.companyId inList companiesIds }.includeAll()

        val newsStore = db.runQuery { newsQuery }
        val productsStore = db.runQuery { productsQuery }
//        val productDetailsStore = db.runQuery { productDetailsQuery }
        val employeesStore = db.runQuery { employeesQuery }

        return companieStore[Meta.infraCompanies].map { company ->
            company.toModel(
                news = company.infraNews(newsStore).toList(),
                products = company.products(productsStore).toList(),
//                productDetails = company.productDetails(productDetailsStore).toList(),
                productDetails = emptyList(),
                employees = company.infraEmployees(employeesStore).toList(),
            )
        }
    }

    fun find_countDistinct(): Long? {
        val metaCompany = Meta.infraCompanies

        val mainQuery = QueryDsl.from(metaCompany)
            .select(countDistinct(metaCompany.id))

        val result = db.runQuery {
            mainQuery
        }

        return result
    }


    fun findIdAndSameCreatorCountList(): List<Pair<String?, Long?>> {
        val metaCompany = Meta.infraCompanies
        val metaSameCreatorCompany = Meta.infraCompanies.clone()

        fun countDistinctMultiple(
            vararg expressions: ColumnExpression<*, *>,
        ): ColumnExpression<Long, *> {
            val name = "countDistinct"
            val columns = expressions.map { Operand.Column(it) }
            return columnExpression(Long::class, name, columns) {
                // for MySQL
                append("count(distinct ")
//                append("count(distinct (") // for PostgreSQL
                columns.forEach {
                    visit(it)
                    append(", ")
                }
                cutBack(2)
                append(")")
//                append("))") // for PostgreSQL
            }
        }

        fun <ENTITY2 : Any, ID2 : Any, META2 : EntityMetamodel<ENTITY2, ID2, META2>> EntitySelectQuery<ENTITY2>.ensureLeftJoin(
            metamodel: META2,
            on: OnDeclaration,
        ): EntitySelectQuery<ENTITY2> {
            val joins = (this.context as SelectContext<*, *, *>).joins
            return if (joins.find { it.target == metamodel } != null) {
                this
            } else {
                leftJoin(Relationship(metamodel, on))
            }
        }

        // get same creator companies count
        val mainQuery = QueryDsl.from(metaCompany)
            .leftJoin(metaSameCreatorCompany) {
                metaCompany.createdBy eq metaSameCreatorCompany.createdBy
            }
            .ensureLeftJoin(metaSameCreatorCompany) {
                metaCompany.createdBy eq metaSameCreatorCompany.createdBy
            }
            .groupBy(metaCompany.id)
            .select(metaCompany.id, countDistinctMultiple(metaCompany.id, metaSameCreatorCompany.id))


        val mainQuery2 = QueryDsl.from(metaCompany)
            .ensureLeftJoin(metaSameCreatorCompany) {
                metaCompany.createdBy eq metaSameCreatorCompany.createdBy
            }
            .ensureLeftJoin(metaSameCreatorCompany) {
                metaCompany.createdBy eq metaSameCreatorCompany.createdBy
            }
            .groupBy(metaCompany.id)
            .select(metaCompany.id, countDistinctMultiple(metaCompany.id, metaSameCreatorCompany.id))

        println(mainQuery2)
        // select t0_.id, (count(distinct t0_.id, t1_.id))
        // from companies as t0_ left outer join companies as t1_ on (t0_.created_by = t1_.created_by) group by t0_.id

        val result = db.runQuery {
            mainQuery
        }

        return result
    }

    fun findIdAndSameCreatorCountList2(
        fromTo: Pair<LocalDateTime?, LocalDateTime?>,
    ): List<InfraCompanies> {
        val metaCompany = Meta.infraCompanies
        val metaSameCreatorCompany = Meta.infraCompanies.clone()

        class ConditionExtension<F : FilterScope<F>>(
            val filterScope: FilterScope<F>
        ) {
            infix fun <T : Comparable<T>, S : Any> ColumnExpression<T, S>.range(
                fromTo: Pair<T?, T?>,
            ) {
                val column = this
                val from = fromTo.first
                val to = fromTo.second
                return with(filterScope) {
                    if (from != null && to == null) {
                        column greaterEq from
                    } else if (from == null && to != null) {
                        column lessEq to
                    } else if (from != null && to != null) {
                        column between from..to
                    }
                }
            }

            infix fun <T : Any, S : Any> ColumnExpression<T, S>.notInIfNotEmpty(
                values: List<T>
            ) {
                val column = this
                return with(filterScope) {
                    if (values.isNotEmpty()) {
                        column notInList values
                    }
                }
            }

            infix fun <T : Any, S : Any> ColumnExpression<T, S>.inOrIsNull(
                values: List<T>,
            ) {
                val column = this
                this.orIsNull(values) {
                    column inList it
                }
            }

            fun <T : Any, S : Any> ColumnExpression<T, S>.orIsNull(
                values: List<T?>,
                orDeclaration: FilterScope<F>.(List<T>) -> Unit,
            ) {
                val column = this
                return with(filterScope) {
                    val (nonNullItems, nullItems) = values
                        .distinct()
                        .partition { it != null }
                    and {
                        if (nullItems.isNotEmpty()) {
                            or {
                                column.isNull()
                            }
                        }
                        if (nonNullItems.isNotEmpty()) {
                            or {
                                orDeclaration(nonNullItems as List<T>)
                            }
                        }
                    }
                }
            }
        }

        fun FilterScope<*>.conditionExtension(
            block: ConditionExtension<*>.() -> Unit,
        ) {
            ConditionExtension(this).apply {
                block()
            }
        }

        // get same creator companies count
        val mainQuery = QueryDsl.from(metaCompany)
            .leftJoin(metaSameCreatorCompany) {
                metaCompany.createdBy eq metaSameCreatorCompany.createdBy
            }
            .where {
                conditionExtension {
                    metaCompany.createdAt range fromTo
                }
            }
            .groupBy(metaCompany.id)
        // select t0_.id, (count(distinct t0_.id, t1_.id))
        // from companies as t0_ left outer join companies as t1_ on (t0_.created_by = t1_.created_by) group by t0_.id

        val result = db.runQuery {
            mainQuery
        }

        return result
    }

    fun createList(companies: List<Company>) {
        val items = companies.map { it.toInfra() }

        val queries = listOf(
            QueryDsl.insert(Meta.infraCompanies).batch(items.map { it.item1 }),
            QueryDsl.insert(Meta.infraNews).batch(items.flatMap { it.item2 }),
            QueryDsl.insert(Meta.infraProducts).batch(items.flatMap { it.item3 }),
            QueryDsl.insert(Meta.infraProductDetails).batch(items.flatMap { it.item4 }),
            QueryDsl.insert(Meta.infraEmployees).batch(items.flatMap { it.item5 }),
        )

        queries.forEach { db.runQuery(it) }
    }
}

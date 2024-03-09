package momosetkn.infras.komapper.repositories

import momosetkn.domain.Company
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
import org.komapper.core.dsl.expression.ColumnExpression
import org.komapper.core.dsl.expression.Operand
import org.komapper.core.dsl.operator.columnExpression
import org.komapper.core.dsl.operator.countDistinct
import org.komapper.jdbc.JdbcDatabase

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
        // select (count(distinct t0_.id, t0_.name)) from companies as t0_

        val result = db.runQuery {
            mainQuery
        }

        return result
    }


    fun findList_countDistinctMultiple(): List<Pair<String?, Long?>> {
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

        // get same creator companies count
        val mainQuery = QueryDsl.from(metaCompany)
            .leftJoin(metaSameCreatorCompany) {
                metaCompany.createdBy eq metaSameCreatorCompany.createdBy
            }
            .groupBy(metaCompany.id)
            .select(metaCompany.id, countDistinctMultiple(metaCompany.id, metaSameCreatorCompany.id))
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

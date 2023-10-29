package momosetkn.infras.komapper.repositories

import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.query.Query
import org.komapper.core.dsl.query.bind
import org.komapper.core.dsl.query.getNotNull
import org.komapper.jdbc.JdbcDatabase

/**
 * テストで使うもの
 * testディレクトリでは生成できなかったため、ここに置いている
 */
class DatabaseDao(
    val db: JdbcDatabase,
) {
    fun initialize() {
        val query = QueryDsl.executeScript(
            """
create database if not exists test;
use test;

CREATE TABLE companies (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL
);

CREATE TABLE news (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    company_id VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE products (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    company_id VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies(id)
);

CREATE TABLE product_details (
    id VARCHAR(255) PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE TABLE employees (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    company_id VARCHAR(255) NOT NULL,
    updated_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    FOREIGN KEY (company_id) REFERENCES companies(id)
);

select 1
"""
                .trimIndent(),
        )

        db.runQuery(query)
    }

    fun switchDb() {
        val query = QueryDsl.executeScript(
            """
            use test;
            select 1
            """.trimIndent(),
        )

        db.runQuery(query)
    }

    fun showTables(): List<String> {
        val query: Query<List<String>> = QueryDsl.fromTemplate(
            """
            select table_name
            from INFORMATION_SCHEMA.TABLES
            where TABLE_SCHEMA = DATABASE()
              and table_type = 'BASE TABLE'
            """.trimIndent(),
        )
            .bind("table_name", "table_name")
            .select { it.getNotNull("table_name") }

        return db.runQuery(query)
    }
}

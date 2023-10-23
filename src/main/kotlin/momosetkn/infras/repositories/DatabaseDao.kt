package momosetkn.infras.repositories

import org.seasar.doma.Dao
import org.seasar.doma.Script
import org.seasar.doma.Sql

/**
 * テストで使うもの
 * testディレクトリでは生成できなかったため、ここに置いている
 */
@Dao
interface DatabaseDao {

    @Sql(
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
""",
    )
    @Script
    fun initialize()

    @Sql(
        """
use test;
select 1
""",
    )
    @Script
    fun switchDb()
}

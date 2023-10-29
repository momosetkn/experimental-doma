package momosetkn.infras.komapper.komapper.contexts

import org.komapper.jdbc.DefaultJdbcDatabaseConfig
import org.komapper.jdbc.JdbcDatabase
import org.komapper.jdbc.JdbcDatabaseConfig
import org.komapper.jdbc.JdbcDialect
import javax.sql.DataSource

class Db(
    private val dialect: JdbcDialect,
    private val hikariDatasource: DataSource,
) {
    fun getContext(): KomapperContext {
        return MyKomapperContext(
            db = jdbcDatabase,
        )
    }

    private val config: JdbcDatabaseConfig = object : DefaultJdbcDatabaseConfig(hikariDatasource, dialect) {}
    val jdbcDatabase =  JdbcDatabase(config)
}

data class MyKomapperContext(
    override val db: JdbcDatabase,
) : KomapperContext()

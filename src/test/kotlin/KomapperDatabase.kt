import momosetkn.infras.komapper.repositories.DatabaseDao
import org.komapper.core.dsl.QueryDsl
import org.komapper.core.dsl.metamodel.EntityMetamodel
import org.komapper.core.dsl.metamodel.PropertyMetamodel
import org.komapper.core.dsl.query.EntitySelectQuery
import org.komapper.core.dsl.query.Query
import org.komapper.jdbc.JdbcDatabase
import java.util.*
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions

object KomapperDatabase {
    private val IGNORE_TABLE_LIST = listOf(
        // Liquibase
        "DATABASECHANGELOG",
        "DATABASECHANGELOGLOCK",
    )

    fun <ENTITY : Any, ID : Any, META : EntityMetamodel<ENTITY, ID, META>>
            JdbcDatabase.createList(meta: META, items: List<ENTITY>): List<ENTITY> {
        return exec(
            QueryDsl.insert(meta).batch(items),
        )
    }

    fun <ENTITY : Any, ID : Any, META : EntityMetamodel<ENTITY, ID, META>>
            JdbcDatabase.create(meta: META, item: ENTITY): ENTITY {
        return exec(
            QueryDsl.insert(meta).single(item),
        )
    }

    fun <ENTITY : Any, ID : Any, META : EntityMetamodel<ENTITY, ID, META>>
            JdbcDatabase.reloadList(meta: META, items: List<ENTITY>): List<ENTITY> {
        val propertyMetaToValues = getIdPropertyMeta<ENTITY>(items[0]::class).map { (propertyMeta, valueKCallable) ->
            Pair(
                propertyMeta,
                items.map { valueKCallable.call(it)!! },
            )
        }

        val results = propertyMetaToValues.fold(QueryDsl.from(meta) as EntitySelectQuery<ENTITY>) { acc, metaToValue ->
            acc.where { metaToValue.first inList metaToValue.second }
        }

        return exec(results)
    }

    fun <ENTITY : Any, ID : Any, META : EntityMetamodel<ENTITY, ID, META>> JdbcDatabase.reload(
        meta: META,
        item: ENTITY,
    ): ENTITY {
        return reloadList(meta, listOf(item))[0]
    }

    fun <ENTITY : Any, ID : Any, META : EntityMetamodel<ENTITY, ID, META>> JdbcDatabase.all(meta: META): List<ENTITY> {
        return exec(QueryDsl.from(meta))
    }

    fun JdbcDatabase.reset() {
        withTransaction {
            runQuery(QueryDsl.executeScript(("set foreign_key_checks = 0")))
            DatabaseDao(this).showTables().filter { !IGNORE_TABLE_LIST.contains(it) }.forEach {
                runQuery(QueryDsl.executeScript(("truncate table $it")))
            }
            runQuery(QueryDsl.executeScript("set foreign_key_checks = 1"))
        }
    }

    private fun <ENTITY: Any> getIdPropertyMeta(
        kclazz: KClass<out Any>
    ): List<Pair<PropertyMetamodel<ENTITY, Any, Any>, KCallable<*>>> {
        val entityMetaClass = Class.forName(kclazz.java.packageName + "._" + kclazz.simpleName).kotlin
        val metaObject = metaObject(entityMetaClass, kclazz)
        val idPropertiesFunc = entityMetaClass.functions.find { it.name == "idProperties" }!!
        val idPropertyMetaList =  idPropertiesFunc.call(metaObject) as List<PropertyMetamodel<ENTITY, Any, Any>>
        return idPropertyMetaList.map {
            Pair(it, getMemberByName(kclazz, it.name)!!)
        }
    }

    private fun metaObject(
        entityMetaClass: KClass<out Any>,
        kclazz: KClass<out Any>
    ): Any? {
        val entityMetaCompanionObject = entityMetaClass.companionObjectInstance!!
        val metaObject = entityMetaCompanionObject::class.members.find {
            it.name == kclazz.simpleName!!.replaceFirstChar { it.lowercase(Locale.getDefault()) }
        }!!
        return metaObject.call(entityMetaCompanionObject)
    }

    private fun getMemberByName(kclass: KClass<out Any>, memberName: String): KCallable<*>? {
        return kclass.members.find {
            it.name == memberName
        }
    }

    private fun <R> JdbcDatabase.exec(query: Query<R>): R {
        return withTransaction {
            runQuery(query)
        }
    }
}

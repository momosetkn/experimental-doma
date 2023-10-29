import momosetkn.infras.doma.doma.contexts.DomaContext
import momosetkn.infras.doma.doma.contexts.DomaContextExtension.getConnection
import momosetkn.infras.doma.doma.contexts.DomaContextExtension.transaction
import momosetkn.infras.doma.repositories.DatabaseDaoImpl
import org.seasar.doma.jdbc.criteria.metamodel.EntityMetamodel
import org.seasar.doma.jdbc.criteria.metamodel.PropertyMetamodel
import kotlin.reflect.KCallable
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible

object Database {
    private val IGNORE_TABLE_LIST = listOf(
        // Liquibase
        "DATABASECHANGELOG",
        "DATABASECHANGELOGLOCK",
    )

    fun <ENTITY> DomaContext.createList(meta: EntityMetamodel<ENTITY>, items: List<ENTITY>): List<ENTITY> {
        return transaction {
            entityql.insert(meta, items).execute().entities
        }
    }

    fun <ENTITY> DomaContext.create(meta: EntityMetamodel<ENTITY>, item: ENTITY): ENTITY {
        return transaction {
            entityql.insert(meta, item).execute().entity
        }
    }

    fun <ENTITY : Any> DomaContext.reloadList(meta: EntityMetamodel<ENTITY>, items: List<ENTITY>): List<ENTITY> {
        val propertyMetaToValues = getIdFields(items[0]::class).map { field ->

            val member = requireNotNull(getMemberByName(meta::class, field.name))
            val metaMember = member.call(meta) as PropertyMetamodel<Any>

            Pair(
                metaMember,
                items.map { field.call(it) },
            )
        }

        val results = transaction {
            var query = entityql.from(meta)

            query = propertyMetaToValues.fold(query) { acc, metaToValue ->
                acc.where {
                    it.`in`(
                        metaToValue.first,
                        metaToValue.second,
                    )
                }
            }

            query.fetch()
        }

        return results
    }

    fun <ENTITY : Any> DomaContext.reload(meta: EntityMetamodel<ENTITY>, item: ENTITY): ENTITY {
        return reloadList(meta, listOf(item))[0]
    }

    fun <ENTITY : Any> DomaContext.all(meta: EntityMetamodel<ENTITY>): List<ENTITY> {
        return transaction {
            entityql.from(meta).fetch()
        }
    }

    fun DomaContext.reset() {
        transaction {
            getConnection().use { connection ->
                connection.prepareStatement("set foreign_key_checks = 0").execute()
                DatabaseDaoImpl(domaConfig).showTables().filter { !IGNORE_TABLE_LIST.contains(it) }.forEach {
                    connection.prepareStatement("truncate table $it").execute()
                }
                connection.prepareStatement("set foreign_key_checks = 1").execute()
                connection.commit()
            }
        }
    }

    private fun getIdFields(kclazz: KClass<out Any>): List<KCallable<*>> {
        return kclazz.members.filter { declaredField ->
            declaredField.hasAnnotation<org.seasar.doma.Id>()
        }.map {
            it.isAccessible = true
            it
        }
    }

    private fun getMemberByName(kclass: KClass<out Any>, memberName: String): KCallable<*>? {
        return kclass.members.find {
            it.name == memberName
        }
    }
}

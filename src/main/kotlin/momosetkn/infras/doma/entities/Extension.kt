package momosetkn.infras.doma.entities

import momosetkn.infras.doma.doma.contexts.DomaContext
import org.seasar.doma.jdbc.criteria.metamodel.EntityMetamodel
import org.seasar.doma.jdbc.criteria.metamodel.PropertyMetamodel
import org.seasar.doma.jdbc.criteria.statement.EntityqlSelectStarting
import org.seasar.doma.jdbc.criteria.statement.Listable
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.isAccessible

object Extension {
    // fetchOneはnull-safeではないため、singleOrNullかsingleを使用してください
    fun <ELEMENT> Listable<ELEMENT>.singleOrNull(): ELEMENT? {
        @Suppress("ForbiddenApiUsage")
        return fetchOptional().orElseGet { null }
    }

    fun <ELEMENT> Listable<ELEMENT>.single(): ELEMENT {
        @Suppress("ForbiddenApiUsage")
        return requireNotNull(fetchOne())
    }

    @MustBeDocumented
    @RequiresOptIn(
        level = RequiresOptIn.Level.WARNING,
        message = "実験的に実装"
    )
    public annotation class ExperimentalApi

    data class LoadContext<STORE_ENTITY>(
        val storeEntities: List<STORE_ENTITY>,
    ) {
        context(DomaContext)
        fun <LOAD_ENTITY, ID_TYPE> hasMany(
            loadEntity: EntityMetamodel<LOAD_ENTITY>,
            assocCondition: Pair<PropertyMetamodel<ID_TYPE>, PropertyMetamodel<ID_TYPE>>,
            store: (STORE_ENTITY, List<LOAD_ENTITY>) -> Unit,
        ): List<STORE_ENTITY> {
            return selectinloadHasMany(
                storeEntities,
                loadEntity,
                assocCondition,
                store,
            )
        }

        context(DomaContext)
        fun <LOAD_ENTITY, ID_TYPE> hasOne(
            loadEntity: EntityMetamodel<LOAD_ENTITY>,
            assocCondition: Pair<PropertyMetamodel<ID_TYPE>, PropertyMetamodel<ID_TYPE>>,
            store: (STORE_ENTITY, List<LOAD_ENTITY>) -> Unit,
        ): List<STORE_ENTITY> {
            return selectinloadHasMany(
                storeEntities,
                loadEntity,
                assocCondition,
                store,
            )
        }

        context(DomaContext)
        private fun <LOAD_ENTITY, ID_TYPE> selectinloadHasMany(
            storeEntities: List<STORE_ENTITY>,
            loadEntity: EntityMetamodel<LOAD_ENTITY>,
            assocCondition: Pair<PropertyMetamodel<ID_TYPE>, PropertyMetamodel<ID_TYPE>>,
            store: (STORE_ENTITY, List<LOAD_ENTITY>) -> Unit,
        ): List<STORE_ENTITY> {
            val assocCondition_ = run {
                val entityType = getPrivateValue(assocCondition.first, "entityType")!!
                val name = getPrivateValue(entityType, "getName")
                if (loadEntity.asType().name == name) {
                    // swap
                    assocCondition.second to assocCondition.first
                } else {
                    assocCondition
                }
            }

            val storeEntityIdCllable: KCallable<ID_TYPE> =
                storeEntities.getOrNull(0)?.let {
                    getIdTypeCallable(it, assocCondition_.first.name)
                } ?: return storeEntities

            val loadEntities = run {
                val inValues = storeEntities.map { storeEntityIdCllable.call(it) }
                entityql.from(loadEntity)
                    .where { it.`in`(assocCondition_.second, inValues) }
                    .fetch()
            }

            val loadEntityIdCllable: KCallable<ID_TYPE> =
                loadEntities.getOrNull(0)?.let { getIdTypeCallable(it, assocCondition_.second.name) }
                    ?: return storeEntities

            val loadEntitiesMap = loadEntities
                .groupBy { loadEntityIdCllable.call(it) }
            storeEntities.forEach { storeEntity ->
                val hasManyEntities = loadEntitiesMap[storeEntityIdCllable.call(storeEntity)]
                hasManyEntities?.also {
                    store(storeEntity, hasManyEntities)
                }
            }

            return storeEntities
        }

        context(DomaContext)
        private fun <LOAD_ENTITY, ID_TYPE: Any> selectinloadHasOne(
            storeEntities: List<STORE_ENTITY>,
            loadEntity: EntityMetamodel<LOAD_ENTITY>,
            assocCondition: Pair<PropertyMetamodel<ID_TYPE>, PropertyMetamodel<ID_TYPE>>,
            store: (STORE_ENTITY, LOAD_ENTITY) -> Unit,
        ): List<STORE_ENTITY> {
            val assocCondition_ = run {
                val entityType = getPrivateValue(assocCondition.first, "entityType")!!
                val name = getPrivateValue(entityType, "getName")
                if (loadEntity.asType().name == name) {
                    // swap
                    assocCondition.second to assocCondition.first
                } else {
                    assocCondition
                }
            }

            val storeEntityIdCllable: KCallable<ID_TYPE> =
                storeEntities.getOrNull(0)?.let {
                    getIdTypeCallable(it, assocCondition_.first.name)
                } ?: return storeEntities

            val loadEntities = run {
                val inValues = storeEntities.map { storeEntityIdCllable.call(it) }
                entityql.from(loadEntity)
                    .where { it.`in`(assocCondition_.second, inValues) }
                    .fetch()
            }

            val loadEntityIdCllable: KCallable<ID_TYPE> =
                loadEntities.getOrNull(0)?.let { getIdTypeCallable(it, assocCondition_.second.name) }
                    ?: return storeEntities

            val loadEntitiesMap = loadEntities
                .groupBy { loadEntityIdCllable.call(it) }
            storeEntities.forEach { storeEntity ->
                val hasManyEntities = loadEntitiesMap[storeEntityIdCllable.call(storeEntity)]
                hasManyEntities?.also {
                    check(hasManyEntities.size == 1)
                    store(storeEntity, hasManyEntities[0])
                }
            }

            return storeEntities
        }

        @Suppress("UNCHECKED_CAST")
        private fun <IDTYPE> getIdTypeCallable(entity: Any, memberName: String): KCallable<IDTYPE>? {
            return entity::class.members.find {
                it.name == memberName
            } as KCallable<IDTYPE>?
        }

        private fun getPrivateValue(entity: Any, memberName: String): Any? {
            val member = entity::class.members.find {
                it.name == memberName
            }!!
            member.isAccessible = true
            return member.call(entity)
        }
    }

    @ExperimentalApi
    fun <ENTITY> EntityqlSelectStarting<ENTITY>.fetchAndSelectinload(
        block: LoadContext<ENTITY>.() -> Unit,
    ): List<ENTITY> {
        val list = this.fetch()
        return list.apply {
            val loadContext = LoadContext(list)
            block(loadContext)
        }
    }

    @ExperimentalApi
    fun <ENTITY> EntityqlSelectStarting<ENTITY>.singleAndSelectinload(
        block: LoadContext<ENTITY>.() -> Unit,
    ): ENTITY {
        val entity = this.single()
        return entity.apply {
            val loadContext = LoadContext(listOf(entity))
            block(loadContext)
        }
    }

    @ExperimentalApi
    fun <ENTITY> EntityqlSelectStarting<ENTITY>.singleOrNullAndSelectinload(
        block: LoadContext<ENTITY>.() -> Unit,
    ): ENTITY? {
        val entity = this.singleOrNull()
        return entity?.apply {
            val loadContext: LoadContext<ENTITY> = LoadContext(listOf(entity))
            block(loadContext)
        }
    }
}

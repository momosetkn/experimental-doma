
import KomapperDatabase.create
import KomapperDatabase.reload
import KomapperDatabase.reset
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import momosetkn.app.Koin
import momosetkn.app.appDependencies
import momosetkn.infras.komapper.entities.InfraCompanies
import momosetkn.infras.komapper.entities.infraCompanies
import momosetkn.infras.komapper.komapper.contexts.Db
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.komapper.core.dsl.Meta

class KomapperExampleTest : FunSpec({
    val db = Koin.get<Db>().jdbcDatabase

    beforeEach {
        db.reset()
    }
    test("test") {
        val item = InfraCompanies(
            id = "1",
            uuid = java.util.UUID.randomUUID(),
            name = "name1",
            updatedBy = "updatedBy",
            updatedAt = java.time.LocalDateTime.of(2021, 1, 1, 1, 1, 1),
            createdBy = "createdBy",
            createdAt = java.time.LocalDateTime.of(2021, 1, 1, 1, 1, 2),
        )
        db.create(Meta.infraCompanies, item)
        db.reload(Meta.infraCompanies, item) shouldBe item
    }
})

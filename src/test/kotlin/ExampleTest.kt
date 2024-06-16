import Database.create
import Database.reload
import Database.reset
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import momosetkn.app.Koin
import momosetkn.app.appDependencies
import momosetkn.infras.doma.doma.contexts.Db
import momosetkn.infras.doma.entities.InfraCompanies
import momosetkn.infras.doma.entities.meta.Meta
import momosetkn.infras.doma.entities.meta.companies
import org.koin.core.component.get
import org.koin.core.context.startKoin
import java.util.UUID

class ExampleTest : FunSpec({
    val db = Koin.get<Db>()
    val context = db.getContext()

    beforeEach {
        context.reset()
    }
    test("test") {
        val item = InfraCompanies(
            id = "1",
            uuid = UUID.randomUUID(),
            name = "name1",
            updatedBy = "updatedBy1",
            updatedAt = java.time.LocalDateTime.of(2021, 1, 1, 1, 1, 1),
            createdBy = "createdBy",
            createdAt = java.time.LocalDateTime.of(2021, 1, 1, 1, 1, 2),
        )
        context.create(Meta.companies, item)
        context.reload(Meta.companies, item) shouldBe item
    }
})

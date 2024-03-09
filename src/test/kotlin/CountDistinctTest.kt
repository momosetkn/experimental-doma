import Database.createList
import Database.reset
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import momosetkn.app.Koin
import momosetkn.infras.doma.doma.contexts.Db
import momosetkn.infras.doma.entities.InfraCompanies
import momosetkn.infras.doma.entities.meta.Meta
import momosetkn.infras.doma.entities.meta.companies
import org.koin.core.component.get
import java.time.LocalDateTime

class CountDistinctTest : FunSpec({
    val db = Koin.get<Db>()
    val context = db.getContext()
    val companiesRepository = Koin.get<momosetkn.infras.komapper.repositories.CompaniesRepository>()

    beforeEach {
        context.reset()

        var id = 0
        val items = listOf(
            InfraCompanies(
                id = "${id++}",
                name = "name1",
                updatedBy = "updatedBy1",
                updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                createdBy = "createdBy1",
                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 2),
            ),
            InfraCompanies(
                id = "${id++}",
                name = "name1",
                updatedBy = "updatedBy1",
                updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                createdBy = "createdBy1",
                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 2),
            ),
            InfraCompanies(
                id = "${id++}",
                name = "name1",
                updatedBy = "updatedBy1",
                updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                createdBy = "createdBy2",
                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 2),
            ),
            InfraCompanies(
                id = "${id++}",
                name = "name1",
                updatedBy = "updatedBy1",
                updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                createdBy = "createdBy2",
                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 2),
            ),
            InfraCompanies(
                id = "${id++}",
                name = "name1",
                updatedBy = "updatedBy1",
                updatedAt = LocalDateTime.of(2021, 1, 1, 1, 1, 1),
                createdBy = "createdBy2",
                createdAt = LocalDateTime.of(2021, 1, 1, 1, 1, 2),
            ),
        )
        context.createList(Meta.companies, items)
    }
    context("#countDistinctMultiple") {
        test("createdByが同じ会社が何件あるかの件数が、行毎に付与されること") {
            val actual = companiesRepository.findIdAndSameCreatorCountList()
            actual shouldBe listOf(
                "0" to 2L,
                "1" to 2L,
                "2" to 3L,
                "3" to 3L,
                "4" to 3L,
            )
        }
    }
})

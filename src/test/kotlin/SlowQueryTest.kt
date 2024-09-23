import Database.reset
import io.kotest.core.spec.style.FunSpec
import momosetkn.app.Koin
import momosetkn.infras.doma.doma.contexts.Db
import org.koin.core.component.get

class SlowQueryTest : FunSpec({
    val db = Koin.get<Db>()
    val context = db.getContext()
    val companiesRepository = Koin.get<momosetkn.infras.komapper.repositories.CompaniesRepository>()

    beforeEach {
        context.reset()
    }
    context("#slowQuery") {
        test("slowQueryのログが出ること") {
            val actual = companiesRepository.slowQuery()
        }
    }
    context("#notSlowQuery") {
        test("slowQueryのログが出ないこと") {
            val actual = companiesRepository.notSlowQuery()
        }
    }
})

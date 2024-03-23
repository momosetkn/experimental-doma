package momosetkn.infras.doma.repositories

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import momosetkn.app.Koin
import momosetkn.infras.doma.doma.contexts.Db
import momosetkn.infras.doma.doma.contexts.transactionWithContext
import momosetkn.infras.doma.entities.InfraCompaniesJavaRecord
import org.koin.core.component.get
import java.time.LocalDateTime

class CompaniesRepositorySpec : FunSpec({
    val subject = CompaniesRepository()
    context("kfindListALLJavaRecord") {
        test("should return list of companies") {
            val db = Koin.get<Db>()

            db.transactionWithContext {
                val list = listOf(
                    InfraCompaniesJavaRecord("1", "name1",
                        "address1", LocalDateTime.now(),
                        "address1", LocalDateTime.now(),
                        ),
                    InfraCompaniesJavaRecord("2", "name2",
                        "address2", LocalDateTime.now(),
                        "address2", LocalDateTime.now(),
                        ),
                )
                subject.insert_JavaRecord(list)
                val actual = subject.findListALL_JavaRecord()
                actual.size shouldBe 2
            }
        }
    }
})

package support

import io.kotest.core.config.AbstractProjectConfig
import momosetkn.app.Koin
import momosetkn.app.appDependencies
import momosetkn.infras.doma.doma.contexts.Db
import org.koin.core.component.get
import org.koin.core.context.startKoin

class KotestProjectConfig : AbstractProjectConfig() {
    init {
        startKoin { modules(appDependencies) }

        momosetkn.app.initialize()
        momosetkn.app.insertData()
    }
}

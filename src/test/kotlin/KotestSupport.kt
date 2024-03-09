package support

import io.kotest.core.config.AbstractProjectConfig
import momosetkn.app.appDependencies
import org.koin.core.context.startKoin

class KotestProjectConfig : AbstractProjectConfig() {
    init {
        startKoin { modules(appDependencies) }
    }
}

@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION")

package momosetkn.app

import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.KoinContext
import org.koin.core.error.ApplicationAlreadyStartedException
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

// KoinのKtorプラグインがGlobalContextを使用するため、
// GlobalContextの起動と重複（同一Contextを二重起動ができない）しないようにContextを作成
// 以下クラスのコピペ
// @see org.koin.core.context.GlobalContext
@Suppress("TooManyFunctions")
class IsolationKoinContext : KoinContext {

    private var _koin: Koin? = null
    private var _koinApplication: KoinApplication? = null

    override fun get(): Koin = _koin ?: error("KoinApplication has not been started")

    override fun getOrNull(): Koin? = _koin

    fun getKoinApplicationOrNull(): KoinApplication? = _koinApplication

    private fun register(koinApplication: KoinApplication) {
        if (_koin != null) {
            throw ApplicationAlreadyStartedException("A Koin Application has already been started")
        }
        _koinApplication = koinApplication
        _koin = koinApplication.koin
    }

    override fun stopKoin() = synchronized(this) {
        _koin?.close()
        _koin = null
    }

    override fun startKoin(koinApplication: KoinApplication): KoinApplication = synchronized(this) {
        register(koinApplication)
        koinApplication.createEagerInstances()
        return koinApplication
    }

    override fun startKoin(appDeclaration: KoinAppDeclaration): KoinApplication = synchronized(this) {
        val koinApplication = KoinApplication.init()
        register(koinApplication)
        appDeclaration(koinApplication)
        koinApplication.createEagerInstances()
        return koinApplication
    }

    override fun loadKoinModules(module: Module, createEagerInstances: Boolean) = synchronized(this) {
        get().loadModules(listOf(module), createEagerInstances = createEagerInstances)
    }

    override fun loadKoinModules(modules: List<Module>, createEagerInstances: Boolean) = synchronized(this) {
        get().loadModules(modules, createEagerInstances = createEagerInstances)
    }

    override fun unloadKoinModules(module: Module) = synchronized(this) {
        get().unloadModules(listOf(module))
    }

    override fun unloadKoinModules(modules: List<Module>) = synchronized(this) {
        get().unloadModules(modules)
    }
}

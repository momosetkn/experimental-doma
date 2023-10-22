package momosetkn.app

import org.slf4j.bridge.SLF4JBridgeHandler
import java.util.logging.LogManager

object ConfigureLog : Log {
    fun configLog() {
        configureCoroutineLog()
        configureLogback()
        bridgeOtherLog()
    }

    private fun configureCoroutineLog() {
        // スタックトレースに、withContextを呼び出した箇所も出るようになる
        System.setProperty("kotlinx.coroutines.debug", "on")
    }

    private fun configureLogback() {
        val configProfile = run {
            val e = System.getenv()["CONFIG_PROFILE"] ?: "local"
            if (System.getProperty("sun.java.command").lowercase().contains("test")) {
                "$e-test"
            } else {
                e
            }
        }
        // システムプロパティの値logback.configurationFileの設定だと、
        // fatJarにした際、ファイルとして読み取れないため、クラスローダーからリソースとして読み込む
        val classLoader = Thread.currentThread().getContextClassLoader()
        val filename = "logback-$configProfile.xml"
        val resourceUrl = classLoader.getResource(filename)
        if (resourceUrl === null) {
            log.error("logback config file not found: $filename")
            return
        }

        val loggerContext = org.slf4j.LoggerFactory.getILoggerFactory() as ch.qos.logback.classic.LoggerContext
        loggerContext.reset()

        val configurator = ch.qos.logback.classic.joran.JoranConfigurator()
        configurator.context = loggerContext
        configurator.doConfigure(resourceUrl)
    }

    // JUL(java.util.logging)を無効にし、slf4jへ流す
    private fun bridgeOtherLog() {
        val rootLogger = LogManager.getLogManager().getLogger("")
        val handlers = rootLogger.handlers
        for (handler in handlers) {
            rootLogger.removeHandler(handler)
        }
        SLF4JBridgeHandler.install()
    }
}

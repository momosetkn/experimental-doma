package momosetkn.app

import momosetkn.app.ConfigureLog.configLog
import org.slf4j.Logger

interface Log {
    val log: Logger get() = org.slf4j.LoggerFactory.getLogger(this::class.java)
}

// getLog()を呼び出した時点のメソッドの名前をログにセットします
fun getLog(): Logger {
    configLog()
    return org.slf4j.LoggerFactory.getLogger(getCallClassName())
}

private fun getCallClassName(): String {
    val stackTrace = Thread.currentThread().stackTrace
    // 呼び出した関数名を取得する
    // 呼び出し場所に依存する。関数をラップすると意図しない挙動になるので注意。
    // FIXME: Don't rely on bad hacks like this.
    @Suppress("MagicNumber")
    val caller = stackTrace[3]
    return caller.className
}

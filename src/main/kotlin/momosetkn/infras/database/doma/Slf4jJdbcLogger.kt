@file:Suppress("InvalidPackageDeclaration")

package org.seasar.doma.jdbc.momosetkn

import momosetkn.app.Log
import org.seasar.doma.jdbc.JdbcLogger
import org.seasar.doma.jdbc.Sql
import org.seasar.doma.jdbc.SqlExecutionSkipCause
import java.sql.SQLException

@Suppress("TooManyFunctions")
class Slf4jJdbcLogger : JdbcLogger, Log {
    override fun logDaoMethodEntering(callerClassName: String, callerMethodName: String, vararg args: Any) {
        log.debug("Entering DAO method: {}.{}", callerClassName, callerMethodName)
    }

    override fun logDaoMethodExiting(callerClassName: String, callerMethodName: String, result: Any?) {
        log.debug("Exiting DAO method: {}.{}", callerClassName, callerMethodName)
    }

    override fun logDaoMethodThrowing(callerClassName: String?, callerMethodName: String?, e: RuntimeException?) {
        log.error("Exception in DAO method: {}.{}", callerClassName, callerMethodName, e)
    }

    override fun logSqlExecutionSkipping(
        callerClassName: String?,
        callerMethodName: String?,
        cause: SqlExecutionSkipCause?
    ) {
        if (cause != null) {
            log.debug(
                "Skipping SQL execution in method {}.{} due to: {}",
                callerClassName,
                callerMethodName,
                cause
            )
        } else {
            log.debug("Skipping SQL execution in method {}.{}", callerClassName, callerMethodName)
        }
    }

    override fun logSql(callerClassName: String, callerMethodName: String, sql: Sql<*>) {
        if (log.isDebugEnabled) {
            log.debug("Executing SQL in method {}.{}: {}", callerClassName, callerMethodName, sql.formattedSql)
        } else {
            // bindされたパラメーターは?として表示
            log.info("Executing SQL in method {}.{}: {}", callerClassName, callerMethodName, sql.rawSql)
        }
    }

    override fun logTransactionBegun(callerClassName: String?, callerMethodName: String?, transactionId: String?) {
        log.debug("Transaction begun in method {}.{}", callerClassName, callerMethodName)
    }

    override fun logTransactionEnded(callerClassName: String?, callerMethodName: String?, transactionId: String?) {
        log.debug("Transaction ended in method {}.{}", callerClassName, callerMethodName)
    }

    override fun logTransactionCommitted(callerClassName: String?, callerMethodName: String?, transactionId: String?) {
        log.debug("Transaction committed in method {}.{}", callerClassName, callerMethodName)
    }

    override fun logTransactionSavepointCreated(
        callerClassName: String?,
        callerMethodName: String?,
        transactionId: String?,
        savepointName: String?
    ) {
        log.debug(
            "Savepoint {} created in transaction {} in method {}.{}",
            savepointName,
            transactionId,
            callerClassName,
            callerMethodName
        )
    }

    override fun logTransactionRolledback(callerClassName: String?, callerMethodName: String?, transactionId: String?) {
        log.debug("Transaction rolled back in method {}.{}", callerClassName, callerMethodName)
    }

    override fun logTransactionSavepointRolledback(
        callerClassName: String?,
        callerMethodName: String?,
        transactionId: String?,
        savepointName: String?
    ) {
        log.debug(
            "Savepoint {} rolled back in transaction {} in method {}.{}",
            savepointName,
            transactionId,
            callerClassName,
            callerMethodName
        )
    }

    override fun logTransactionRollbackFailure(
        callerClassName: String?,
        callerMethodName: String?,
        transactionId: String?,
        e: SQLException?
    ) {
        log.error(
            "Transaction rollback failed in method {}.{}. Transaction ID: {}",
            callerClassName,
            callerMethodName,
            transactionId,
            e
        )
    }

    override fun logAutoCommitEnablingFailure(callerClassName: String?, callerMethodName: String?, e: SQLException?) {
        log.error(
            "Auto-commit enabling failed in method {}.{}",
            callerClassName,
            callerMethodName,
            e
        )
    }

    override fun logTransactionIsolationSettingFailure(
        callerClassName: String?,
        callerMethodName: String?,
        transactionIsolationLevel: Int,
        e: SQLException?
    ) {
        log.error(
            "Transaction isolation setting failed in method {}.{}. Isolation Level: {}",
            callerClassName,
            callerMethodName,
            transactionIsolationLevel,
            e
        )
    }

    override fun logConnectionClosingFailure(callerClassName: String?, callerMethodName: String?, e: SQLException?) {
        log.error(
            "Connection closing failed in method {}.{}",
            callerClassName,
            callerMethodName,
            e
        )
    }

    override fun logStatementClosingFailure(callerClassName: String?, callerMethodName: String?, e: SQLException?) {
        log.error(
            "Statement closing failed in method {}.{}",
            callerClassName,
            callerMethodName,
            e
        )
    }

    override fun logResultSetClosingFailure(callerClassName: String?, callerMethodName: String?, e: SQLException?) {
        log.error(
            "ResultSet closing failed in method {}.{}",
            callerClassName,
            callerMethodName,
            e
        )
    }
}

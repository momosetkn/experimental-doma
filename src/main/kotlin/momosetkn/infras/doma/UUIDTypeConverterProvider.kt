package momosetkn.infras.doma

import org.seasar.doma.DomainConverters

@Suppress("MaxLineLength")
/**
 * UUIDとvarbinaryの変換を行うDomainConverter
 * @see https://doma.readthedocs.io/ja/latest/domain/#external-domain-classes
 * 以下設定が必要
 * ```bash
 * echo "doma.domain.converters=momosetkn.infras.doma.UUIDTypeConverterProvider" > src/main/resources/doma.compile.config
 * ```
 */
@DomainConverters(UUIDTypeConverter::class)
class UUIDTypeConverterProvider

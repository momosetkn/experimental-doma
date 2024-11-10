package momosetkn.infras.komapper

import org.komapper.core.spi.DataTypeConverter
import java.nio.ByteBuffer
import java.util.UUID
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("MaxLineLength")
/**
 * UUIDとvarbinaryの変換を行うDataTypeConverter
 *
 * 以下設定が必要
 * https://www.komapper.org/ja/docs/reference/data-type/#user-defined-data-types-for-jdbc
 * ```bash
 * echo "momosetkn.infras.komapper.UUIDTypeConverter" > src/main/resources/META-INF/services/org.komapper.core.spi.DataTypeConverter
 * ```
 */
class UUIDTypeConverter : DataTypeConverter<UUID, ByteArray> {
    override val exteriorType: KType = typeOf<UUID>()
    override val interiorType: KType = typeOf<ByteArray>()

    // https://qiita.com/moaikids/items/f987b4d5c1736335d103
    @Suppress("MagicNumber")
    override fun unwrap(exterior: UUID): ByteArray {
        val buffer = ByteBuffer.allocate(16)
        buffer.putLong(exterior.mostSignificantBits)
        buffer.putLong(exterior.leastSignificantBits)
        return buffer.array()
    }

    override fun wrap(interior: ByteArray): UUID {
        val buffer = ByteBuffer.wrap(interior)
        return UUID(buffer.long, buffer.long)
    }
}

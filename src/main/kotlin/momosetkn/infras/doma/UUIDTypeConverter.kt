package momosetkn.infras.doma

import org.seasar.doma.ExternalDomain
import org.seasar.doma.jdbc.domain.DomainConverter
import java.nio.ByteBuffer
import java.util.UUID

// https://doma.readthedocs.io/ja/latest/basic/
@ExternalDomain
class UUIDTypeConverter : DomainConverter<UUID, ByteArray> {
    @Suppress("MagicNumber")
    override fun fromDomainToValue(exterior: UUID?): ByteArray? {
        if (exterior == null) {
            return null
        }
        val buffer = ByteBuffer.allocate(16)
        buffer.putLong(exterior.mostSignificantBits)
        buffer.putLong(exterior.leastSignificantBits)
        return buffer.array()
    }

    override fun fromValueToDomain(interior: ByteArray?): UUID? {
        if (interior == null) {
            return null
        }
        val buffer = ByteBuffer.wrap(interior)
        return UUID(buffer.long, buffer.long)
    }
}

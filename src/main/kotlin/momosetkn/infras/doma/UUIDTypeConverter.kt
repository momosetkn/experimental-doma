package momosetkn.infras.doma

import org.seasar.doma.ExternalDomain
import org.seasar.doma.jdbc.domain.DomainConverter
import java.util.UUID

// https://doma.readthedocs.io/en/2.19.2/basic/
@ExternalDomain
class UUIDTypeConverter : DomainConverter<UUID, Object> {
    @Suppress("MagicNumber")
    override fun fromDomainToValue(exterior: UUID?): Object {
        if (exterior == null) {
            return null
        }
        val a = (exterior.mostSignificantBits).toByte()
        val b = (exterior.leastSignificantBits).toByte()
        return arrayOf(a, b) as Object
    }

    override fun fromValueToDomain(interior: Object): UUID? {
        if (interior == null) {
            return null
        }
        val interior = interior as Array<Byte>
        return UUID(interior[0].toLong(), interior[1].toLong())
    }
}

package momosetkn.domain

import java.time.LocalDateTime


data class ProductDetail(
    val id: String,
    val description: String,
    val updatedBy: String,
    val updatedAt: LocalDateTime,
    val createdBy: String,
    val createdAt: LocalDateTime,
)


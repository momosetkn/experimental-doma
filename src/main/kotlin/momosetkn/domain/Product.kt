package momosetkn.domain

import java.time.LocalDateTime


data class Product(
    val id: String,
    val name: String,
    val details: List<ProductDetail>,
    val updatedBy: String,
    val updatedAt: LocalDateTime,
    val createdBy: String,
    val createdAt: LocalDateTime,
)


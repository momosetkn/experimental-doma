package momosetkn.domain

import java.time.LocalDateTime

data class Company(
    val id: String,
    val name: String,
    val news: List<News>,
    val products: List<Product>,
    val employees: List<Employee>,
    val updatedBy: String,
    val updatedAt: LocalDateTime,
    val createdBy: String,
    val createdAt: LocalDateTime,
)

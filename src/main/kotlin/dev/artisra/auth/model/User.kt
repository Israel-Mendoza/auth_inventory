package dev.artisra.auth.model

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(unique = true, nullable = false)
    val uuid: UUID = UUID.randomUUID(),

    @Column(nullable = false)
    var firstName: String,

    @Column(nullable = false)
    var lastName: String,

    @Column(unique = true, nullable = false)
    var email: String,

    @Column(nullable = false)
    var password: String
)

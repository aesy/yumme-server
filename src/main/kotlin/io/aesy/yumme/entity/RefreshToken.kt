package io.aesy.yumme.entity

import io.aesy.yumme.converter.InstantIntPersistenceConverter
import org.hibernate.annotations.*
import java.time.Instant
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "refresh_token")
@Where(clause = "revoked_at IS NULL")
class RefreshToken(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "value", nullable = false)
    var value: String,

    @field:ManyToOne
    @field:JoinColumn(name = "user", nullable = false)
    var user: User
) {
    @field:Column(name = "created_at", nullable = false)
    @field:Convert(converter = InstantIntPersistenceConverter::class)
    @field:Generated(GenerationTime.INSERT)
    var createdAt: Instant? = null

    @Column(name = "modified_at", nullable = false)
    @Convert(converter = InstantIntPersistenceConverter::class)
    @Generated(GenerationTime.ALWAYS)
    var modifiedAt: Instant = Instant.now()

    @Column(name = "revoked_at", nullable = true)
    @Convert(converter = InstantIntPersistenceConverter::class)
    var revokedAt: Instant? = null

    @field:Column(name = "last_used_at", nullable = true)
    @field:Convert(converter = InstantIntPersistenceConverter::class)
    var lastUsedAt: Instant? = null

    override fun toString(): String {
        return "RefreshToken(id=$id, value='$value')"
    }
}

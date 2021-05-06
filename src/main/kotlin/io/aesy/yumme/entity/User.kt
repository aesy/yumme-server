package io.aesy.yumme.entity

import io.aesy.yumme.converter.InstantIntPersistenceConverter
import org.hibernate.annotations.*
import java.time.Instant
import javax.persistence.*
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "user")
@Where(clause = "deleted_at IS NULL AND suspended_at IS NULL")
@SQLDelete(sql = "proc_user_delete", callable = true)
class User(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_name", unique = true, nullable = false)
    var userName: String,

    @Column(name = "display_name", nullable = false)
    var displayName: String,

    @Column(name = "password", nullable = false)
    var passwordHash: String,

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_has_role",
        joinColumns = [JoinColumn(name = "user")],
        inverseJoinColumns = [JoinColumn(name = "role")]
    )
    var roles: MutableSet<Role> = mutableSetOf()
) {
    @Column(name = "created_at", nullable = false)
    @Convert(converter = InstantIntPersistenceConverter::class)
    @Generated(GenerationTime.INSERT)
    var createdAt: Instant = Instant.now()

    @Column(name = "modified_at", nullable = false)
    @Convert(converter = InstantIntPersistenceConverter::class)
    @Generated(GenerationTime.ALWAYS)
    var modifiedAt: Instant = Instant.now()

    override fun toString(): String {
        return "User(id=$id, name='$userName')"
    }
}

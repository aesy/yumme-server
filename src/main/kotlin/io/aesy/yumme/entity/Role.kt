package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "role")
class Role(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String
) {
    @ManyToMany(fetch = FetchType.LAZY)
    val permissions: Set<Permission> = setOf()

    override fun toString(): String {
        return "Role(id=$id, name='$name')"
    }

    companion object {
        const val USER = "USER"
        const val ADMIN = "ADMIN"
    }
}

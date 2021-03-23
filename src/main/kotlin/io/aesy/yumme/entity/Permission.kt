package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "permission")
class Permission(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String
) {
    @ManyToMany(fetch = FetchType.LAZY)
    val roles: Set<Role> = setOf()

    override fun toString(): String {
        return "Permission(id=$id, name='$name')"
    }

    companion object {
        const val READ_OWN_USER = "READ_OWN_USER"
        const val READ_OTHER_USER = "READ_OTHER_USER"
        const val WRITE_OWN_USER = "WRITE_OWN_USER"
        const val WRITE_OTHER_USER = "WRITE_OTHER_USER"
    }
}

package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "role")
class Role(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", unique = true, nullable = false)
    val name: String
) {
    override fun toString(): String {
        return "Role(id=$id, name='$name')"
    }

    companion object {
        const val USER: String = "USER"
        const val ADMIN: String = "ADMIN"
    }
}

package io.aesy.yumme.entity

import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import javax.persistence.*

@Entity
@Table(name = "user")
@Where(clause = "deleted_at = 0 AND suspended_at = 0")
@SQLDelete(sql = "proc_user_delete", callable = true)
class User(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "email", nullable = false)
    val email: String,

    @Column(name = "password", nullable = false)
    val password: String
) {
    override fun toString(): String {
        return "User(id=$id, email='$email')"
    }
}

package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "tag")
class Tag(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val name: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe", nullable = false)
    val recipe: Recipe
) {
    override fun toString(): String {
        return "Tag(id=$id, name='$name')"
    }
}

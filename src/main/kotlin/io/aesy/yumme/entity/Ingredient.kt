package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "ingredient")
class Ingredient(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    @JoinTable(
        name = "recipe_has_ingredient",
        joinColumns = [JoinColumn(name = "ingredient")],
        inverseJoinColumns = [JoinColumn(name = "recipe")]
    )
    val recipe: Set<Recipe> = setOf()
) {
    override fun toString(): String {
        return "Ingredient(id=$id, name='$name')"
    }
}

package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "category")
class Category(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String
) {
    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    @JoinTable(
        name = "recipe_belong_to_category",
        joinColumns = [JoinColumn(name = "category")],
        inverseJoinColumns = [JoinColumn(name = "recipe")]
    )
    val recipes: Set<Recipe> = setOf()

    override fun toString(): String {
        return "Category(id=$id, name='$name')"
    }
}

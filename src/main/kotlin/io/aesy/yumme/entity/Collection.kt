package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "collection")
class Collection(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    val title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", nullable = false)
    val owner: User
) {
    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    @JoinTable(
        name = "collection_has_recipe",
        joinColumns = [JoinColumn(name = "collection")],
        inverseJoinColumns = [JoinColumn(name = "recipe")]
    )
    val recipes: Set<Recipe> = setOf()

    override fun toString(): String {
        return "Collection(id=$id, title='$title')"
    }
}

package io.aesy.yumme.entity

import io.aesy.yumme.converter.InstantIntPersistenceConverter
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "collection")
class Collection(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var title: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner", nullable = false)
    var owner: User
) {
    @Column(name = "public", nullable = false)
    var public: Boolean = false

    @Column(name = "created_at", nullable = false)
    @Convert(converter = InstantIntPersistenceConverter::class)
    @Generated(GenerationTime.INSERT)
    var createdAt: Instant = Instant.now()

    @ManyToMany(
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL]
    )
    @JoinTable(
        name = "collection_has_recipe",
        joinColumns = [JoinColumn(name = "collection")],
        inverseJoinColumns = [JoinColumn(name = "recipe")]
    )
    var recipes: MutableSet<Recipe> = mutableSetOf()

    override fun toString(): String {
        return "Collection(id=$id, title='$title')"
    }
}

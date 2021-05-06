package io.aesy.yumme.entity

import io.aesy.yumme.converter.DurationLongPersistenceConverter
import io.aesy.yumme.converter.InstantIntPersistenceConverter
import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import java.time.Duration
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "recipe")
class Recipe(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description", nullable = false)
    var description: String,

    @Column(name = "directions", nullable = false)
    var directions: String,

    @Convert(converter = DurationLongPersistenceConverter::class)
    @Column(name = "prep_time", nullable = false)
    var prepTime: Duration,

    @Convert(converter = DurationLongPersistenceConverter::class)
    @Column(name = "cook_time", nullable = false)
    var cookTime: Duration,

    @Column(name = "yield", nullable = false)
    var yield: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    var author: User
) {
    @Column(name = "public", nullable = false)
    var public: Boolean = false

    @Column(name = "created_at", nullable = false)
    @Convert(converter = InstantIntPersistenceConverter::class)
    @Generated(GenerationTime.INSERT)
    var createdAt: Instant = Instant.now()

    @OneToMany(
        mappedBy = "recipe",
        fetch = FetchType.EAGER,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var tags: MutableSet<Tag> = mutableSetOf()

    @ManyToMany(
        fetch = FetchType.EAGER,
        cascade = [CascadeType.ALL]
    )
    @JoinTable(
        name = "recipe_belong_to_category",
        joinColumns = [JoinColumn(name = "recipe")],
        inverseJoinColumns = [JoinColumn(name = "category")]
    )
    var categories: MutableSet<Category> = mutableSetOf()

    @OneToMany(
        mappedBy = "recipe",
        fetch = FetchType.LAZY,
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    var imageUploadMappings: MutableSet<RecipeHasImageUpload> = mutableSetOf()

    override fun toString(): String {
        return "Recipe(id=$id, title='$title')"
    }
}

package io.aesy.yumme.entity

import java.awt.Dimension
import javax.persistence.*

@Entity
@Table(name = "recipe_has_image_upload")
class RecipeHasImageUpload(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "name", nullable = false)
    var name: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    var type: Type,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe", nullable = false)
    val recipe: Recipe,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "image_upload", nullable = false)
    val upload: ImageUpload
) {
    override fun toString(): String {
        return "RecipeHasImageUpload(id=$id, name='$name')"
    }

    enum class Type(
        val dimensions: Dimension
    ) {
        ORIGINAL(Dimension(1200, 800)), // Used as a minimum
        LARGE(Dimension(1200, 800)),
        MEDIUM(Dimension(600, 400)),
        THUMBNAIL(Dimension(300, 200));
    }
}

package io.aesy.yumme.entity

import io.aesy.yumme.conversion.DurationLongPersistenceConverter
import io.aesy.yumme.conversion.InstantIntPersistenceConverter
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
    val id: Long? = null,

    @Column(name = "title", nullable = false)
    var title: String,

    @Column(name = "description", nullable = false)
    var description: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    val author: User
) {
    @Column(name = "public", nullable = false)
    var public: Boolean = false

    @Convert(converter = DurationLongPersistenceConverter::class)
    @Column(name = "approximate_completion_time", nullable = false)
    var completionTime: Duration = Duration.ZERO

    override fun toString(): String {
        return "Recipe(id=$id, title='$title')"
    }
}

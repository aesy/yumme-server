package io.aesy.yumme.entity

import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "rating")
class Rating(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "score", nullable = false)
    var score: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe", nullable = false)
    var recipe: Recipe,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user", nullable = false)
    var user: User
) {
    @Column(name = "created_at", nullable = false)
    @Generated(GenerationTime.INSERT)
    var createdAt: Instant = Instant.now()

    @Column(name = "modified_at", nullable = false)
    @Generated(GenerationTime.ALWAYS)
    var modifiedAt: Instant = Instant.now()

    override fun toString(): String {
        return "Rating(id=$id, score=$score)"
    }
}

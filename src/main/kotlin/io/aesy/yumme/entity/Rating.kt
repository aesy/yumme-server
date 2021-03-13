package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "rating")
data class Rating(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "score", nullable = false)
    val score: Int = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe", nullable = false)
    val recipe: Recipe
)

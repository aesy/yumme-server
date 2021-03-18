package io.aesy.yumme.entity

import javax.persistence.*

@Entity
@Table(name = "recipe")
class Recipe(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "title", nullable = false)
    val title: String,

    @Column(name = "description", nullable = false)
    val description: String
)

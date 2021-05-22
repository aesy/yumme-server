package io.aesy.yumme.entity

import org.hibernate.annotations.Generated
import org.hibernate.annotations.GenerationTime
import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "image_upload")
class ImageUpload(
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "file_name", nullable = false)
    var fileName: String,

    @Column(name = "hash", nullable = false)
    var hash: String,

    @Column(name = "width", nullable = false)
    var width: Int,

    @Column(name = "height", nullable = false)
    var height: Int,
) {
    @Column(name = "created_at", nullable = false)
    @Generated(GenerationTime.INSERT)
    var createdAt: Instant = Instant.now()

    @Column(name = "modified_at", nullable = false)
    @Generated(GenerationTime.ALWAYS)
    var modifiedAt: Instant = Instant.now()

    override fun toString(): String {
        return "ImageUpload(id=$id, fileName='$fileName')"
    }
}

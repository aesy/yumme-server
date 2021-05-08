package io.aesy.yumme.service

import io.aesy.yumme.util.Strings
import org.springframework.stereotype.Service
import java.text.Normalizer
import java.text.Normalizer.Form
import java.util.*
import java.util.regex.Pattern
import kotlin.math.max
import kotlin.math.min

@Service
class SlugService {
    companion object {
        private val NONLATIN = Pattern.compile("[^\\w-]")
        private val SEPARATORS = Pattern.compile("[\\s\\p{Punct}&&[^-]]")
    }

    fun generateSlug(name: String, limit: Int): String {
        var slug = normalize(name)

        if (!slug.endsWith("-")) {
            slug += "-"
        }

        val padding = max(0, limit - slug.length)

        slug += Strings.random(padding)

        return slug.substring(0, min(limit, slug.length))
    }

    private fun normalize(input: String): String {
        var output = SEPARATORS.matcher(input).replaceAll("-")
        output = Normalizer.normalize(output, Form.NFD)
        output = NONLATIN.matcher(output).replaceAll("")

        return output.toLowerCase(Locale.ENGLISH)
            .replace("-{2,}", "-")
            .replace("^-|-$", "")
    }
}

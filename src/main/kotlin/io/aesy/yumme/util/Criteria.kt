package io.aesy.yumme.util

import javax.persistence.criteria.*
import kotlin.reflect.KProperty1

/**
 * Criteria API extension methods to make it more type-safe
 */
object Criteria {
    fun <T, R> Path<T>.get(property: KProperty1<T, R>): Path<R> {
        return get(property.name)
    }

    fun <T, R> From<*, T>.join(
        property: KProperty1<T, Collection<R>>,
        type: JoinType = JoinType.INNER
    ): CollectionJoin<T, R> {
        return joinCollection(property.name, type)
    }

    fun <T, R> From<*, T>.join(
        property: KProperty1<T, Set<R>>,
        type: JoinType = JoinType.INNER
    ): SetJoin<T, R> {
        return joinSet(property.name, type)
    }

    fun <T, R> From<*, T>.join(
        property: KProperty1<T, List<R>>,
        type: JoinType = JoinType.INNER
    ): ListJoin<T, R> {
        return joinList(property.name, type)
    }

    fun <T, K, V> From<*, T>.join(
        property: KProperty1<T, Map<K, V>>,
        type: JoinType = JoinType.INNER
    ): MapJoin<T, K, V> {
        return joinMap(property.name, type)
    }

    fun <T, R> From<*, T>.join(
        property: KProperty1<T, R>,
        type: JoinType = JoinType.INNER
    ): Join<T, R> {
        return join(property.name, type)
    }
}

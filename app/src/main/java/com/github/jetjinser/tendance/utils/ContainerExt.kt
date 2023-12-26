package com.github.jetjinser.tendance.utils

internal fun <E> Set<E>.addOrRemove(element: E): Set<E> {
    return this.toMutableSet().apply {
        if (!add(element)) {
            remove(element)
        }
    }.toSet()
}

internal fun <E> Set<E>.mutableAdd(element: E): Set<E> {
    return this.toMutableSet().apply {
        add(element)
    }.toSet()
}

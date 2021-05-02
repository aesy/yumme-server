package io.aesy.yumme.util

import io.aesy.yumme.entity.*
import io.aesy.yumme.entity.Collection

object AccessControl {
    fun User.isAuthor(recipe: Recipe): Boolean {
        return recipe.author.id == id
    }

    fun User.isOwner(collection: Collection): Boolean {
        return collection.owner.id == id
    }

    fun User.isAdmin(): Boolean {
        return roles.map(Role::name).contains(Role.ADMIN)
    }

    fun User.canRead(recipe: Recipe): Boolean {
        return isAdmin() or isAuthor(recipe) or recipe.public
    }

    fun User.canWrite(recipe: Recipe): Boolean {
        return isAdmin() or isAuthor(recipe)
    }

    fun User.canRead(collection: Collection): Boolean {
        return isAdmin() or isOwner(collection)
    }

    fun User.canWrite(collection: Collection): Boolean {
        return isAdmin() or isOwner(collection)
    }
}

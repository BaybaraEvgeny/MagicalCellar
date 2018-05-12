package com.magicalcellar

class Book(val name: Int, val author: Int, val imageResource: Int, val imageUrl: String) {
    var isFavorite = false

    fun toggleFavorite() {
        isFavorite = !isFavorite
    }
}